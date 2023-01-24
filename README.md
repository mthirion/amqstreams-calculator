# A tool to compute the initial size of an AMQ Streams cluster


## Purpose
While asserting the performance of an AMQ Streams cluster can really only be done with real-life testing, it would still be nice to be able to anticipate what an AMQ Streams-based solution would cost in terms of hardware and subscriptions.
This tool is aimed at providing a relatively good estimate of what the target AMQ Streams cluster would look like, based on use case information.


## Compute rational
The rational behind the computation, which will also provide more information about all the parameters that the tool accept, are detailed in the pdf file located into the docs directory.


## Running the tool

The calculator is composed of 2 parts: a ReactJS-based UI and a quarkus-based ReST API backend.
The UI opens the port 3000 locally, and contact the backend on the port 8090, still locally.

To run the tool:
- clone this repository
- cd into the calculator-backend directory and launch the backend with 'mvn quarkus:dev'
- cd into the calculator-ui directory, ensure all dependencies are there running 'npm install'; then launch the UI with 'npm start'
- open a web browser on 'localhost:3000'.  
  the form is designed to be prefilled so that a simple computation can trigger immediately with the example parameters set.

## Current status
This is still a beta version under test, and all the features might not work.
The computation of the sizing of the KafkaConnect connectors and a full MirrorMaker still has to be implemented.

## Troubleshooting
The Quarkus backends prints the values of the input parameters it receives from the UI.
It then prints the result of each computation.

### Log example
----- Input parameters -----
inbound throunghput : 100.0
nb topics : 100
nb partitions :  300
nb replicas : 3
network : 10 (saturation = 80)
disk : 400 (saturation = 80 ; type = ssd ; number = 1)
nb consumers : 5
lag time : 30
inbound throunghput = 100.0
fault tolerannce on zookeeper : 0
throughput tolerance on brokers : 1
deviation factor : 80
security margin : 25
storage retention period : 5
mirror maker enabled : false
ssl enabled : false
compaction enabled : false
use CPU share enabled : false
CPU share value : 0.1
hard partition limit : 500

2023-01-24 17:20:29,429 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) ----- Computation started -----
2023-01-24 17:20:29,430 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) base throughput : 100.0 MB/s
2023-01-24 17:20:29,430 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) throughput (at peak, with deviation) : 180.0 MB/s
2023-01-24 17:20:29,430 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) peak throughput with 3 replicas : 540.0 MB/s
2023-01-24 17:20:29,430 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) max network speed (with saturation) : 1024.0 MB/s
2023-01-24 17:20:29,430 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) max disk speed (with saturation) : 320.0 MB/s
2023-01-24 17:20:29,430 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) computation against disk (540.0 // 320.0) returns : 1.6875 nodes
2023-01-24 17:20:29,430 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) computation against network (900.0 // 1024.0) returns : 0.87890625 nodes
2023-01-24 17:20:29,430 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) disk and network comparation (max) gives : 1.6875 nodes
2023-01-24 17:20:29,431 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) adjust against security margin (0.25) gives : 2.109375 nodes
2023-01-24 17:20:29,431 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) adjust with throughput tolerance of 1 and rounded up gives : 4.0 nodes
2023-01-24 17:20:29,431 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) adjusting to ensure each replica can be on a dedicated node -> nb nodes = 4.0
2023-01-24 17:20:29,431 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) ensuring the number of partitions per node is below the hard limit (500)...
2023-01-24 17:20:29,431 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) ensuring the number of nodes per cluster is below the partitions hard limit (50)...
2023-01-24 17:20:29,431 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) Computed memory to support the lagging time (rounded) : 16.0
2023-01-24 17:20:29,431 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) computed CPU : 1.0
2023-01-24 17:20:29,431 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) checking proper ratio between CPU and memory (1 CPU / 6 GB)...
2023-01-24 17:20:29,432 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) => CPU setting changed to : 2.6666666666666665 to respect the ratio
2023-01-24 17:20:29,432 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) Checking CPU requirement is not below the minimum default (4)...
2023-01-24 17:20:29,432 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) => CPU below the default and restored to : 4
2023-01-24 17:20:29,432 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) .....Parallel Disk CPU addition is skipped in this version
2023-01-24 17:20:29,432 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) Adjusting CPU as a multiple of 2 => new value = 4
2023-01-24 17:20:29,432 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) Double checking CPU to memory ratio is still ok...
2023-01-24 17:20:29,432 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) Setting the number of zookeeper nodes (for HA tolerance of 0) : 3 nodes
2023-01-24 17:20:29,432 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) Reverse-computing lag time based on adjusted memory...
2023-01-24 17:20:29,433 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) lag = 91 sec
2023-01-24 17:20:29,433 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) maximum lag = 163 sec
2023-01-24 17:20:29,433 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) Computing storage retention : 124 TB
2023-01-24 17:20:29,433 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) Storage requirement per broker node : 42188 GB
2023-01-24 17:20:29,433 INFO  [com.red.int.myq.qua.KafkaCalculator] (executor-thread-0) ----- Computation complete -----

