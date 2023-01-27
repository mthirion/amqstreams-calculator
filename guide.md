# AMQ Streams sizing guidance
## Introduction
This document describes an approach to evaluate the size of an AMQ Streams cluster.  

## Method
A running AMQ Cluster receives messages from producers and delivers them to consumers.  
We propose to divide the problem into those 2 sub-problems.  
At the instant 0, there is no message in the cluster. The consuming applications are idle and only producers are active.  The messages can be consumed only once they have been acknowledged by the cluster and replicated according to the replication factor.   
As a result, the method described in this document will consist of 4 steps:  
- First, we look at the provider side only, and we try to evaluate how the cluster could
absorb the inbound throughput
- Secondly, we look at the consumer side, and we try to evaluate how the cluster could
deliver the same throughput to the consumers
- Then, we’ll confront the result to the best practices
- Also, we’ll apply a security margin to take insto account peak times and lack of uniformity in the cluster
- Finally, we'll check that the cluster is not out of bound  

Through this document, we will sometimes use "node" for an AMQ Streams broker, with the assumption of a one-to-one mapping between a node and either a physical host, a VM or a Container/Pod.  

### Inbound throughput
The principal information needed is the inbound throughput: the result of the average number of messages published to the cluster per unit of time by the average size of a message.  

#### Example

| Parameter               | Value         |
| ----------------------- | ------------- |
| Average msg per second  | 20000         |
| Average message size    | 40 KB         |
|
| Throughput              | 800 MB/s      |  

#### Note
The max size for a kafka message is 1MB.  
A Kafka message can have a header in addition to the payload.  
When headers are not in use, we can estimate the overhead to be 9 bytes per message.  

In our above example, this overhead represents only 0.0002% of the message.  
The total overhead is therefore 180KB and is negligible.

### Step 1: Message ingestion
Any message received by an AMQ Streams node is written to disk and is therefore directly dependent on the performance of the disks.  
As a best practice, it’s preferred to use a storage architecture consisting of several disks in parallel rather than a single disk to increase the total throughput.  
Technology such as Raid or Jbod can help with this.  

Each message is stored in a partition that is persisted to the local disk but that is also replicated to other nodes (based on the replication factor).  
The replication factor and the disk throughput are therefore 2 critical information to know for the computation.  
Notice that it's recommended to withdraw a percentage of the disk throughput as a part of the bandwith can be required by other processes such as the operating system itself.  

#### Example

| Parameter                     | Value         |
| -----------------------       | ------------- |
| Disk throughput               |400 MB/s       |
| Disk throughput usable (80%)  |320 MB/s       |
| Inbound throughput            |800 MB/s       |
| Replication factor            |3              |
| Total throughput              |2400 MB/s      |
| 
| number of nodes               | => 8          |

* nb nodes = inbound throughput * replication factor / usable disk throughput  


![workflow](docs/img1.png?raw=true)


### Message delivery
Messages stored in partitions must be delivered over the network to the consumers.  
Among the consumers, we can identify:
- the nodes hosting the replicas
- MirrorMaker (we’ll leave that aside for now)
- consuming appliations, including KafkaConnect sources   

 To be able to perform the computation, the 2 critical elements of information
that we need are:
- the network bandwidth
- the replication factor
- potentially the number of MirrorMaker nodes (left aside for now)
- and the average number of consumers per topic (= the total number of consuming applications divided by the total number of topics).  

Again, we should take into account that a percentage of the network bandwidth might not be usable and should be reserved for other administrative processes.  

#### Example

| Parameter                     | Value         |
| -----------------------       | ------------- |
| Number of replicas (r)        |3              |
| Adapter speed                 |10 Gbps        |
| Adapter speed (bytes)         |1250 MB/s      |
| Usable bandwidth (80%)        |1000 MB/s      |
| Number of topics              |1362           |
| Number of consumers           |1985           |
| Consumers per group (c)       |3              |
| Total consumers ()            |4              |
|
| number of nodes               | => 4          |
* nb nodes = total throughput * total consumers / usable bandwith  


![workflow](docs/img2.png?raw=true)

### Best practices
From the 2 examples listed above, we can conclude that the target cluster would require a minimum of 8 nodes. We also can deduce that the disk is the constraining factor.  

No we need to ensure that the best practices are respected.  
We’ve already assmed one of them, which is to have a one-to-one mapping between brokers
and their underlying physical resources (VM/Pod).   
Another one is about having each replicas on separate nodes.  For instance, if the result had been 2 nodes, we would have taken 3 as the final answer as the replication factor is 3 in our example.  
A replication factor of 3 and an in-sync replicas of 2 are other best practices that have also implicitly been assumed in the example.  

Also thing to consider is the number of active partitions per node.  Some benchmarks have concluded to a hard limit of 4000 partitions per broker.  However, it's recommended to take a lower value.  Indeed, in case of the crash of a node, all the partition leaders need to be moved to another node, and this operation, unfortunately sequential by design, implies a 10ms delay per leader migration.  For that reason, we like to try to keep the total number of partitions per node under 1500.  

Notice that Cruise Control should periodically be used to keep a proper balancing of the resources across the cluster over time.  

### Tolerance factor
The above figures were computed with some assumptions.  Some adjustments might be required,  should those assumptions not always be met.  

1. Projection for the future  
The computation leads to the MINIMUM size of the cluster and it's wise to add some extra resources to support an initial increasing of the load.  

2. Fault tolerance  
We might also want that the cluster is able to support the entire workload in the case of the failure of one or more nodes, which would simply means adding one or two extra nodes to the result.  

3. Peak deviation  
The inbound throughput we used in an example is assumed to come from the AVERAGE number of message per unit of time (day).  With such an input, the result tells the number of nodes required to support the average workload, and the cluster might be undersized for peak loads.  
The deviation factor is an indication of the difference between the average and the peak load, as displayed on the below diagram.

![workflow](docs/img3.png?raw=true)

4. Uniformity  
The computation made above assume an AMQ Streams cluster perfectly uniformly distributed.
Cruise Control can help achieve continuous uniformity, but from time to time (with scaling of upon incidents), the cluster can become unbalanced, and this is also something to correct at the end of the computation.  

## Physical characteristics of the nodes
### Memory
An AMQ Streams broker should behave correctly with a memory size of 6 GB but a much higher might be recommended.  
AMQStreams writes data to the PageCache (in-memory) before they are synchronized to disk.  
Retrieving data from the PageCache is obviously a much faster than reading them back from the disk.  
Now, Imagine a situation where 2 consumer groups, ‘C-A’ and ‘C-B’, both read data from a single partition and suddenly, consumer ‘C-A’ crashes.  The consumer ‘C-B’ continues reading data from the partition normally.  Thus, when consumer ‘C-A’ is back online, its index is behind the index of the consumer ‘C-B’.  We say that the consumer has a lag.  
If the broker had to query those "historical" data from the disk, that would greatly affect the general performance of the entire cluster.  However, the performance will be maintained as long as those historical data can still be found in the PageCache.  
This is the reason why production AMQ Streams nodes usually have large RAM settings (32GB, 64GB...).  

#### Maximum lagging time
The maximum lagging time would be the time limit behind which an issue on a consumer would force the cluster to read the data from the disk.  
It’s computed by:
 _[Maximum lag] = [Usable cache memory] / [inbound throughput]_  
For example, with a throughput of 800 MB/s, and evaluating the available cache
to 24G (32G total - 6G for the broker -2G for the OS):  
_max_lag = 24000 MB / 800 MB/s = 30 seconds_ 

A good value for the maximum lag time would be just above the consumer recovery time, which usually is around 5 sec for a function, 30 sec for a pod, and a coupld of min for a VM.  

### CPU
AMQ Streams does not make an intensive use of the CPU.  The only 2 activities that required higher CPU consumption is SSL encryption and file compaction.  

AMQ Streams is a system designed with parallelization in mind, which performs better with a large number of “small” CPU that with a few “high-performance" ones.  
Also, bear in mind that the number of partitions per topic is directly influenced by the number of CPU.  For example, with 10 or 12 CPU, it’s usually discouraged to go further than 20 partitions per topic, due to the increasing concurrency between the processes.  

Generally, we start with a production setup involving 8 vCPU, and sometimes go up to more,
depending on several factors such as the number of disks in parallel, the use of TLS...

### Physical storage
The physical storage capacity simply comes from the inbound throughput and the retention periods.  
_[total storage] = [Retention period] * [Inbound throughput] * [Replication factor]_  
Example for 5 days: 432000 * 800 * 3 = 1036800000 MB = 989 Terabytes  

#### Message overhead
The storage capacity taken as an example above represents the pure data, and we already took into account the overhead induced by the message header as it was negligible.  
However, we still need to take into account 2 more factors:
1) Partitions are indexed and the index information are also written to disk.  
The default configuration divides the storage file system into segments of 1GB to which are associated 2 indexes of 10MB each, leading to an overhead of 2%.  
2) The retention policy doesn’t apply to the last segment of the commit log which is still
active. A configuration with large log segments on partitions having a small amount of
data can lead to the data being retained much longer than the retention time for this last
segment.  
We could valuate this as a 3 to 5% overhead over the total.  
#### Storage distribution
The total capacity needed by the cluster is to be spread over the nodes. 
However, on a cluster of 10 nodes, we can’t simply allocate 1/10th of that quantity to each node, and for at least 2 reasons:  
1. The distribution might not be uniform across the cluster
2. We need to respect the fault tolerance factor, which means the ability to support the full workload with a reduced number of nodes

## KafkaConnect technology
KafkaConnect is a standardized technology to integrate with Kafka, which leverages the Kafak Producer and Consumer API.  
It follows an "application server clustering" pattern.  Connectors are deployed on a node that can be scaled out, so a KafkaConnect node can run one or more "connector", which themselves can run "tasks" (or processes).     
For high availability, a minimum of 2 nodes is therefore recommended.

To properly evaluate the resources required for a KafkaConnect-based client cluster is key because Mirror Maker 2, the native solution that allows the replication of data between multiple Kafka clusters, entirely relies on this technology.  It's a crucial computation because only the optimal performance of the Mirror Maker 2 cluster can (somehow) guarantee that the replication is following at near real time.

The replication of data between Kafak cluster is an important feature because Kafka relies on a Zookeeper database to maintain its state, which has very strict network latency requirements, most of the time preventing a Kafka cluster to be stretched across multiple data centers.

For the reasons exposed above, the sizing will be limited to MirrorMaker, which stands as a Source KafkaConnect connector.

### Mirror Maker 2
Contrary to the cluster itself, which is more memory and disk bound, Kafka Connect will be CPU bound.  

The rules used to evaluate the size of Mirror Maker and the underlying KafkaConnect cluster are:
- We always need a minimum of 2 nodes for high availability
- The number of Tasks equals the number of partitions to replicate
- 1 CPU thread is requird for each 2 tasks  
_A 2 sockets, 6 cores per socket, bi-threaded server would be able to process a maximum of 48 tasks_
- Each task requires 10MB of java memory, not counting the size of the in-transit data.  
_50 tasks requires 50*10M = 500M of java memory for threading only_

