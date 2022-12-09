package com.redhat.integration.myquickstarts.quarkus;

import com.redhat.integration.myquickstarts.quarkus.model.*;

public class KafkaCalculator {

    public static KafkaClusterDesc compute(KafkaRequirements req) {

        KafkaClusterDesc desc = new KafkaClusterDesc();

        
        // getting the throughput at peak time
        Double dev = Double.parseDouble(req.getDeviation().toString());
        double peakthroughput = req.getInthroughput() * (1.0 + (dev/100) ) ;

        // getting the usable disk and network speed
        double netusable = Double.parseDouble(req.getNetspeed().toString() );
        double nsat = Double.parseDouble(req.getNetsat().toString() );
        netusable = netusable * 1024 / 8;  // convert Gbps to MB/s
        netusable = netusable * (nsat/100);

        double diskusable = Double.parseDouble(req.getDiskspeed().toString());
        double dsat = Double.parseDouble(req.getDisksat().toString() );
        diskusable = diskusable * dsat * req.getNbdisks();

        /*  **************************
            Computing the broker nodes 
            */
        KafkaNodeDesc ndesc = new KafkaNodeDesc();

        double ndsto = computeDisk(req.getReplicas(), peakthroughput, (int)diskusable);
        double ndnet = computeNetwork(req.getConsumers(), peakthroughput, (int)netusable);

        double nbNodes = Math.max(ndsto, ndnet);
        nbNodes = nbNodes * (1 + ((double)req.getMargin()/100) );
        nbNodes = Math.ceil(nbNodes) + req.getThroughputtolerance();

            // adjusting the number of node so that each replica is on a dedicated node (best practices check)
            // doing if only for replicas up to 4
        if (req.getReplicas()<4) {
            if (nbNodes<req.getReplicas()) nbNodes=req.getReplicas();
            ndesc.setNumnodes((int)nbNodes);
        }

            // adjusting the number of node to maintain < x partitions per node (best practices check)
        double topicPerNode = Math.ceil((double)req.getNbtopics() / nbNodes);
        double partitionPerNode = Math.ceil((double)req.getNbpartitions() / nbNodes);

        if (partitionPerNode > req.getLimit()) 
            nbNodes = partitionPerNode / req.getLimit();
        if (req.getLimit()>500) 
            desc.addOverload("The number of partitions per broker is > 500.");

            // adjusting the broker limit (best practices check)
        if (nbNodes > desc.MAX_BROKERS_PER_CLUSTER) {
            nbNodes = desc.MAX_BROKERS_PER_CLUSTER;
            desc.addOverload("The total number of brokers in the cluster is > 50");
        }

        /* Computing the size of the broker nodes: memory */
            // Memory is 6 for the broker + lagging time requirements + 2G counted for the OS
            // If the total is less than 16, we let it at the default 16 GB
        Double memory = 0.0;
        if (req.getLagtime() > 0) {
            // getting the memory in GB 
            memory = ndesc.BROKER_MEMORY_BASE_DEFAULT + computeLagging(peakthroughput, req.getLagtime()) + 2; 
            // rounding the memory to a multiplier of 8 -> 8, 16, 24, 32, with the exception of 12
            if (memory > 8.0 && memory <= 12.0) 
                memory=12.0;
            else {
                int rem = (int) (memory % 8);
                if (rem != 0) {
                    rem = 8-rem;
                    memory = memory + rem;
                }
            }
        }    
        if (memory <= ndesc.BROKER_MEMORY_DEFAULT) memory = ndesc.BROKER_MEMORY_DEFAULT;
        ndesc.setMemory((int)Math.ceil(memory) + " GB");        

        /* Computing the size of the broker nodes: cpu */
        /* CPU depends on
            cpu share is based on the average number of topic per node and partitions per topic 
            we assume a cpu share of max 1/10th
             
            adding for the number of disks in parallel
            assert based on the amount of memory 
            adding 1 for SSL and 1 for compaction
            rounded up to a multiple of 2
        */

        double cpu = 0.0;
        if (req.isUseshare()) {
            cpu = req.getAvgpartitions() * topicPerNode;
            cpu = cpu / (req.getCpushare() * 100);
            System.out.println("computed cpu share for partitions = " + cpu);
        } else
            cpu = Math.ceil(req.getAvgpartitions() / 2);

        // checking the ration between cpu and memory
        double mincpu = memory / 6; // min 1 cpu per 6G of memory
        if (cpu < mincpu) cpu = mincpu;   

        int brokercpu = (int) Math.round(cpu); 
        if (brokercpu < ndesc.BROKER_CPU_DEFAULT) brokercpu=ndesc.BROKER_CPU_DEFAULT;

        // adding CPU for SSL and/or compaction
        if (req.isCompaction() || req.isSsl()) brokercpu++;

        // adding CPU for parallel disks ???
        //brokercpu += (req.getNbdisks()-1);

        // rounded up to a multiple of 2 up to 16, and to a multiple of 4 otherwise
        brokercpu = brokercpu + (brokercpu % 2);
        if (brokercpu > 16) brokercpu = brokercpu + (brokercpu % 4); 
        System.out.println("broker cpu rounded = " + brokercpu);
 
        // checking the ration between memory and cpu
        if (memory < (2 * brokercpu) ) {
            memory = (double) 2 * brokercpu;
            ndesc.setMemory(memory.intValue() + " GB");
        }  

        ndesc.setCpu(brokercpu);


        /* Completing the settings of the broker nodes, filling up the input disk and network values */
        switch (req.getNetspeed()) {
            case 1: ndesc.setNetcard(ndesc.NET_SPEED_1);break;
            case 10: ndesc.setNetcard(ndesc.NET_SPEED_10);break;
            default: ndesc.setNetcard("unknown");break;
        }
        ndesc.setDiskspeed(req.getDiskspeed().toString() + " MB/s"); 
        ndesc.setDisktype(req.getDisktype());
        ndesc.setNbdisks(req.getNbdisks());         

        /* Filling up the cluster with the computed broker nodes */ 
        System.out.println("Broker Node : " + ndesc.toString());
        desc.setNode(ndesc);


        /*  **********************
            Computing the zk nodes 
            */

        KafkaZkDesc zkdesc = new KafkaZkDesc(req.getNbpartitions());
        zkdesc.setNumnodes(zkdesc.ZK_NUM_NODE_DEFAULT + 2 * req.getFaulttolerance());   // odd number

        /* Filling up the cluster with the computed zk nodes */ 
        System.out.println("Zk Node : " + zkdesc.toString());
        desc.setZk(zkdesc);


        /*  **************************************
            Computing the Topic-related parameters 
            */

        KafkaTopicDesc tdesc = new KafkaTopicDesc();
        tdesc.setReplicas(req.getReplicas());
        tdesc.setInsync(req.getReplicas()-1);
        tdesc.setTopics(req.getNbtopics());
        tdesc.setPartitions(req.getNbpartitions());
        tdesc.setPnode((int)Math.round(partitionPerNode));
        tdesc.setParallel(req.getAvgpartitions());

        //System.out.println("cpu = "+brokercpu + " partitions = " + partitionPerNode + " round = " + (double)brokercpu/partitionPerNode);
        if (req.isUseshare()) tdesc.setCpushare(req.getCpushare());
        else tdesc.setCpushare(Double.valueOf(String.format("%.2f", (double)brokercpu/partitionPerNode) ) );

        /* Filling up the cluster with the topic related data */ 
        System.out.println("Topic configuration : " + tdesc.toString());
        desc.setTopic(tdesc);

        /*  **************************************
            Completing the cluster-wide parameters 
            */

        desc.setInthroughput(req.getInthroughput());
        desc.setPeakthroughput(peakthroughput);
        System.out.println("mem = " + Math.round(memory));
        desc.setMaxlag( (int) (Math.round(memory) * 1024 / peakthroughput ));
        desc.setAvglag( (int) (Math.round(memory) * 1024 / req.getInthroughput() ));
        double assertNodes = computeNetwork(req.getConsumers(), req.getOutthroughput(), (int)netusable);
        if ( assertNodes > nbNodes) {
            desc.addOverload("Network not sufficient to deliver expected outbound throughput.  Cluster requires extra " + (int)(assertNodes - nbNodes) + " nodes");
            desc.setMaxthroughput(assertNodes * netusable);
        } else
            desc.setMaxthroughput(nbNodes * netusable);
        

        // set storage retention
        //    + TODO: recommendation per node, based on fault tolerance and margin (unbalanced cluster)
        
        double sto = computeStorage(req.getRetention(), req.getInthroughput(), req.getReplicas());
        if (sto > 100000)   // going to TB instead of GB as from 100TB
            desc.setStorage( (int)Math.ceil(sto/1024) + " TB");
        else
            desc.setStorage( (int)Math.ceil(sto) + " GB");

        // identify possible bottleneck
        if (ndsto > ndnet) desc.setBottleneck("Network");
        else desc.setBottleneck("Disk");

        // setting ssl and compaction
        desc.setSsl(req.isSsl());
        desc.setCompaction(req.isCompaction());


        System.out.println("Cluster : " + desc.toString());
        return desc;


    }

    private static double computeNetwork(int consumers, double inrate, int netusable){
        /* Formula
            nb consumers * inbound_throughput / netspeed
            inbound_throughput and netspeed in MB/s
        */
        return inrate * consumers / netusable;
    }

    private static double computeDisk(int replicas, double inrate, int diskspeed){
        /* Formula
            replicas * inbound_throughput / diskspeed
            inbound_throughput and diskspeed in MB/s
        */
        return replicas * inrate / diskspeed;        
    }

    private static double computeLagging(double inrate, int lag){
        /* Formula
            lag time = memory / inbound throughput
            inbound_throughput in MB/s
            memory in MB

            return memory in GB
            between 0 and 500MB, we return 0 ; between 500M and 1G, we return 1 ; otherwise we return the amount in G
        */
        Double result = inrate;
        result = result * lag / 1024;
        if ( result < 0.5)  return 0;
        return Math.ceil(result);
    }

    private static double computeStorage(int retention, double inrate, int replicas){
        /* Formula
            replicas * inbound_throughput *retention
            inbound_throughput in MB/s
            retention in days

            return a number of GB
        */
        double total = inrate * 3600 * 24; // converting MB/s in amount per day
        total = total * replicas * retention;
        // returning GB instead of MB  
        return total/1024;
    }    
}