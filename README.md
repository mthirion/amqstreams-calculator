# A tool to compute the initial size of an AMQ Streams cluster

## Disclaimer
The tool is a fork of :   https://github.com/AndyYuen/kafka-sizing/  
,which is available here:  https://kafkasizing.azurewebsites.net/size

### Reason of the fork
The above tool leverages Spring MVC.  
The  present fork is a refactored version that uses a javascript UI and a Quarkus backend.  
It also integrates a few new capabilities.  


## Purpose
Defining the exact sizing of an AMQ Streams cluster can really only be the result of a thorough testing exercise.  
However, it would really be nice to be able to anticipate what an AMQ Streams-based solution would cost.  
This tool what this tool is about.  It's aimed at providing a relatively good estimate of what a target AMQ Streams solution would look like in terms of infrastuctre.


## Compute rational
The rational behind the computation, described with an example, can be found in the docs directory or here:  
[guide](guide.md)


## Running the tool

The calculator is composed of 2 parts:  
 - a ReactJS-based UI
 - a quarkus-based ReST API backend  

The UI opens the port 3000 locally, and contact the backend on the port 8090, (still locally).

To run the tool:
- clone this repository
- cd into the calculator-backend directory and run:  
    - _mvn quarkus:dev_  
- cd into the calculator-ui directory and run: 
    - _npm install_   
    - _npm start_  
- open a web browser on 'localhost:3000'

The form comes prefilled so a simple demo computation can be triggered immediately.


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


----- Computation started -----  
base throughput : 100.0 MB/s  
throughput (at peak, with deviation) : 180.0 MB/s  
peak throughput with 3 replicas : 540.0 MB/s  
max network speed (with saturation) : 1024.0 MB/s  
max disk speed (with saturation) : 320.0 MB/s  
computation against disk (540.0 // 320.0) returns : 1.6875 nodes  
computation against network (900.0 // 1024.0) returns : 0.87890625 nodes  
disk and network comparation (max) gives : 1.6875 nodes  
adjust against security margin (0.25) gives : 2.109375 nodes  
adjust with throughput tolerance of 1 and rounded up gives : 4.0 nodes  
adjusting to ensure each replica can be on a dedicated node -> nb nodes = 4.0  
ensuring the number of partitions per node is below the hard limit (500)...  
ensuring the number of nodes per cluster is below the partitions hard limit (50)...  
Computed memory to support the lagging time (rounded) : 16.0  
computed CPU : 1.0  
checking proper ratio between CPU and memory (1 CPU / 6 GB)...  
=> CPU setting changed to : 2.6666666666666665 to respect the ratio  
Checking CPU requirement is not below the minimum default (4)...  
=> CPU below the default and restored to : 4  
.....Parallel Disk CPU addition is skipped in this version  
Adjusting CPU as a multiple of 2 => new value = 4  
Double checking CPU to memory ratio is still ok...  
Setting the number of zookeeper nodes (for HA tolerance of 0) : 3 nodes  
Reverse-computing lag time based on adjusted memory...  
lag = 91 sec  
maximum lag = 163 sec  
Computing storage retention : 124 TB  
Storage requirement per broker node : 42188 GB   
----- Computation complete -----

