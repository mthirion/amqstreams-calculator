package com.redhat.integration.amqstreams.quarkus;

import com.redhat.integration.amqstreams.quarkus.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaCalculator {

    private static Logger logger = LoggerFactory.getLogger(KafkaCalculator.class);
    
    public static KafkaClusterDesc compute(KafkaRequirements req) {

        logger.info("----- Input parameters -----");
        logger.info(req.toString());

        KafkaClusterDesc desc = new KafkaClusterDesc();
        int replicas = req.getReplicas();
       
        logger.info("----- Computation started -----");
        
        // getting the throughput at peak time
        Double dev = Double.parseDouble(req.getDeviation().toString());
        double peakthroughput = req.getInthroughput() * (1.0 + (dev/100) ) ;
        logger.info("base throughput : " + req.getInthroughput() + " MB/s");
        logger.info("throughput (at peak, with deviation) : " + peakthroughput + " MB/s");
        logger.info("peak throughput with " + replicas + " replicas : " + peakthroughput * replicas + " MB/s");

        // getting the usable disk and network speed
        double netusable = Double.parseDouble(req.getNetspeed().toString() );
        double nsat = Double.parseDouble(req.getNetsat().toString() );
        netusable = netusable * 1024 / 8;  // convert Gbps to MB/s
        netusable = netusable * (nsat/100);
        logger.info("max network speed (with saturation) : " + netusable + " MB/s");

        double diskusable = Double.parseDouble(req.getDiskspeed().toString());
        double dsat = Double.parseDouble(req.getDisksat().toString() );
        diskusable = diskusable * (dsat/100) * req.getNbdisks();
        logger.info("max disk speed (with saturation) : " + diskusable + " MB/s");


        /*  **************************
            Computing the broker nodes 
            */
        KafkaNodeDesc ndesc = new KafkaNodeDesc();

        double ndsto = computeDisk(req.getReplicas(), peakthroughput, (int)diskusable);
        double ndnet = computeNetwork(req.getConsumers(), peakthroughput, (int)netusable);

        logger.info("computation against disk (" + peakthroughput * replicas + " // "+ diskusable + ") returns : " + ndsto + " nodes");
        logger.info("computation against network (" + peakthroughput * req.getConsumers() + " // "+ netusable + ") returns : " + ndnet + " nodes");

        double nbNodes = Math.max(ndsto, ndnet);
        logger.info("disk and network comparation (max) gives : " + nbNodes + " nodes");
        nbNodes = nbNodes * (1 + ((double)req.getMargin()/100) );
        logger.info("adjust against security margin (" + (double)req.getMargin()/100 + ") gives : " + nbNodes + " nodes");
        nbNodes = Math.ceil(nbNodes) + req.getThroughputtolerance();
        logger.info("adjust with throughput tolerance of " + req.getThroughputtolerance() + " and rounded up gives : " + nbNodes + " nodes");

            // adjusting the number of node so that each replica is on a dedicated node (best practices check)
        if (nbNodes<replicas) {
            nbNodes=replicas;
            logger.info("adjusting to ensure each replica can be on a dedicated node -> nb nodes = " + nbNodes);
        }
        else
            logger.info("each replica can be on a dedicated node... OK");

            // adjusting the number of node to maintain < x partitions per node (best practices check)
        double topicPerNode = Math.ceil((double)req.getNbtopics() / nbNodes);
        double partitionPerNode = Math.ceil((double)req.getNbpartitions() / nbNodes);

        logger.info("ensuring the number of partitions per node is below the hard limit ("+req.getLimit()+")...");
        if (partitionPerNode > req.getLimit()) {
            nbNodes = (int) Math.ceil( (double)req.getNbpartitions() / req.getLimit() );
            logger.info("adjusting the number of nodes to respect the hard limit --> " + nbNodes);
            partitionPerNode = Math.ceil((double)req.getNbpartitions() / nbNodes);
        }
        if (req.getLimit()>500) 
            desc.addOverload("The number of partitions per broker is > 500.");

            // adjusting the broker limit (best practices check)
        logger.info("ensuring the number of nodes per cluster is below the hard limit (" + desc.MAX_BROKERS_PER_CLUSTER + ")...");
        if (nbNodes > desc.MAX_BROKERS_PER_CLUSTER) {
            nbNodes = desc.MAX_BROKERS_PER_CLUSTER;
            logger.info("INCROOECT => number of nodes exceeding hard limit and reset to the hard limit (" + nbNodes + ")");
            desc.addOverload("The total number of brokers in the cluster is > 50");
        }

        ndesc.setNumnodes((int)nbNodes);

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
        logger.info("Computed memory to support the lagging time (rounded) : " + memory);
        if (memory <= ndesc.BROKER_MEMORY_DEFAULT) { 
            memory = ndesc.BROKER_MEMORY_DEFAULT;
            logger.info("Memory requirement below the minimum default -> reseting to default : " + ndesc.BROKER_MEMORY_DEFAULT);
        }
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
            logger.info("Computed CPU (based on input fixed CPU share parameter) : " + cpu);
        } else {
            cpu = Math.ceil(req.getAvgpartitions() / 2);
            logger.info("computed CPU : " + cpu);
        }

        // checking the ratio between cpu and memory
        logger.info("checking proper ratio between CPU and memory (1 CPU / 6 GB)...");
        double mincpu = memory / 6; // min 1 cpu per 6G of memory
        if (cpu < mincpu) {
            cpu = mincpu;   
            logger.info("=> CPU setting changed to : " + cpu + " to respect the ratio");
        }

        logger.info("Checking CPU requirement is not below the minimum default (" + ndesc.BROKER_CPU_DEFAULT + ")...");
        int brokercpu = (int) Math.round(cpu); 
        if (brokercpu < ndesc.BROKER_CPU_DEFAULT) {
            brokercpu=ndesc.BROKER_CPU_DEFAULT;
            logger.info("=> CPU below the default and restored to : " + brokercpu);
        }

        // adding CPU for SSL and/or compaction
        if (req.isCompaction() || req.isSsl()) {
            brokercpu++;
            logger.info("CPU adjusted for extra SSL and file compaction support : " + brokercpu);
        }

        logger.info(".....Parallel Disk CPU addition is skipped in this version");
        // adding CPU for parallel disks ???
        //brokercpu += (req.getNbdisks()-1);
        //logger.info("Adding CPU for disk management : " + brokercpu);

        // rounded up to a multiple of 2 up to 16, and to a multiple of 4 otherwise
        brokercpu = brokercpu + (brokercpu % 2);
        if (brokercpu > 16) brokercpu = brokercpu + (brokercpu % 4); 
        logger.info("Adjusting CPU as a multiple of 2 => new value = " + brokercpu);
 
        // checking the ration between memory and cpu
        logger.info("Double checking CPU to memory ratio is still ok...");
        if (memory < (2 * brokercpu) ) {
            memory = (double) 2 * brokercpu;
            ndesc.setMemory(memory.intValue() + " GB");
            logger.info("re-Adjusting memory based on CPU : " + memory.intValue() + " GB");
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
        desc.setNode(ndesc);


        /*  **********************
            Computing the zk nodes 
            */

        
        KafkaZkDesc zkdesc = new KafkaZkDesc(req.getNbpartitions());
        int zknodes = zkdesc.ZK_NUM_NODE_DEFAULT + 2 * req.getFaulttolerance();
        zkdesc.setNumnodes(zknodes);   // odd number

        logger.info("Setting the number of zookeeper nodes (for HA tolerance of " + req.getFaulttolerance() + ") : " + zknodes + " nodes");
        /* Filling up the cluster with the computed zk nodes */ 
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

        if (req.isUseshare()) tdesc.setCpushare(req.getCpushare());
        else tdesc.setCpushare(Double.valueOf(String.format("%.2f", (double)brokercpu/partitionPerNode) ) );

        /* Filling up the cluster with the topic related data */ 
        desc.setTopic(tdesc);

        /*  **************************************
            Completing the cluster-wide parameters 
            */

        logger.info("Reverse-computing lag time based on adjusted memory...");   
        int maxlag = (int) (Math.round(memory) * 1024 / peakthroughput );   
        int avglag = (int) (Math.round(memory) * 1024 / req.getInthroughput() );     
        desc.setAvglag(avglag);logger.info("lag = " + avglag + " sec");
        desc.setMaxlag(maxlag);logger.info("maximum lag = " + maxlag + " sec");                    

        desc.setInthroughput(req.getInthroughput());
        desc.setPeakthroughput(peakthroughput);
        double assertNodes = computeNetwork(req.getConsumers(), req.getOutthroughput(), (int)netusable);
        if ( assertNodes > nbNodes) {
            desc.addOverload("Network not sufficient to deliver expected outbound throughput.  Cluster requires extra " + (int)(assertNodes - nbNodes) + " nodes to satisfies outbound throughput");
            desc.setMaxthroughput(assertNodes * netusable);
        } else
            desc.setMaxthroughput(nbNodes * netusable);
        

        // set storage retention
        
        double sto = computeStorage(req.getRetention(), req.getInthroughput(), req.getReplicas());
        double nodesto = sto / (nbNodes - req.getThroughputtolerance()) ;
        String desc_nodesto = (int)Math.ceil(nodesto) + " GB";
        desc.setNodeStorage(desc_nodesto);
        String desc_sto;
        if (sto > 100000) {   // going to TB instead of GB as from 100TB
            desc_sto=(int)Math.ceil(sto/1024) + " TB";
            desc.setStorage(desc_sto);
        } 
        else {
            desc_sto=(int)Math.ceil(sto) + " GB";
            desc.setStorage(desc_sto);
        }
        logger.info("Computing storage retention : " + desc_sto);
        logger.info("Storage requirement per broker node : " + desc_nodesto);

        // identify possible bottleneck
        if (ndsto > ndnet) desc.setBottleneck("Network");
        else desc.setBottleneck("Disk");

        // setting ssl and compaction
        desc.setSsl(req.isSsl());
        desc.setCompaction(req.isCompaction());


        /*  *********************************
            Computing the Mirror Maker sizing
            Same CPU as for the broker
            */
        
        if (req.getMm()) {
            logger.info("Mirror Maker will be sized");
            KafkaConnectDesc mm = new KafkaConnectDesc();

            int mmcpu = ndesc.getCpu() * ndesc.getNumnodes();
            int mmlimit= req.getMmcpu();

            logger.info("Mirror Maker requires " + mmcpu + " vcpu with an upper bound of " + mmlimit);
            int mmnodes = (int)Math.ceil((double)mmcpu / mmlimit);
            if (mmnodes < 2) mmnodes=2;
            mmcpu = (int)Math.ceil((double)mmcpu / mmnodes); 
            if (mmcpu % 2 == 1) mmcpu++;
            logger.info("Mirro Maker will have " + mmnodes + " nodes of " + mmcpu + " each, totalizing " + mmnodes*mmcpu + " vpcu");
            
            mm.setWorkers(mmnodes + req.getMmtol());
            mm.setCpu(mmcpu);
            mm.setMem(computeMM(mmcpu));
            mm.setTasks(req.getNbpartitions());

            desc.setMirror(mm);
        }
        

        /*  **************************************
            Evaluating a KafkaConnect cluster size
            */    
        
        if (req.getKc()) {
            logger.info("Kafka Connect will be sized");
            KafkaConnectDesc kc = new KafkaConnectDesc();

            int tasks = req.getKctasks();
            int kcpu = tasks / 2;  // 2 tasks per vcpu
            int kclimit= req.getKccpu();
            
            logger.info("KafkaConnect is configured for " + tasks + " tasks => requires " + kcpu + " vCPU with an upper bound of " +kclimit);
            int knodes = (int)Math.ceil((double)kcpu / kclimit);
            if (knodes < 2) knodes=2;
            kcpu = (int)Math.ceil((double)kcpu / knodes); 
            if (kcpu % 2 == 1) kcpu++;
            logger.info("KafkaConnect cluster will have " + knodes + " nodes of " + kcpu + " each, totalizing " + knodes*kcpu + " vpcu");

            kc.setWorkers(knodes + req.getKctol());
            kc.setCpu(kcpu);
            kc.setMem(computeMM(kcpu));

            desc.setConnect(kc);
        }        

        logger.info("----- Computation complete -----" );
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

    private static int computeMM(int cpu) {
        int memory = 500;
        if (cpu >= 2) memory = 1024;
        if (cpu >= 4) memory = 2048;
        if (cpu >= 8) memory = 4096;
        if (cpu > 16) memory = 8192;

        return memory;
    }
    
}