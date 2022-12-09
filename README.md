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

