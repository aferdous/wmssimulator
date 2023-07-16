# wmssimulator

## How to run the simulator?
1. The simulator needs an installation of Apache Kafka installed on the local machine.
- Download apache kafka. Unzip it to your suitable location on your development machine.
- Open a terminal. run zookeeper. Assuming that you have installed kafka inside your home directory. 
  - cd ~/kafka
  - bin/zookeeper-server-start.sh config/zookeeper.properties
  - Check console logs to ensure that it started properly.
- Open a second terminal. run kafka. 
  - cd ~/kafka
  - bin/kafka-server-start.sh config/server.properties
  - Check console logs.

2. Open the project on IntelliJIdea (I am using the free community edition). 
   - As an alternate you can just use maven to build the project.
3. Run com.sunpower.scale.wmssimulator application. 
   - check console. 
   - There should be logs showing that simulated messages being sent to the kafka topic. 

## Notes to help with navigating code

1. Key entry point classes
   - WmsSimulatorApplication - the springboot application
   - InventoryMoveEventProducer - a springboot Component that is scheduled to run once every second to send a message to the kafka topic
   - KafkaTopicConfiguration - a springboot Configuration with Bean that is loaded at startup and creates the topics with partitions.
2. resources/application.properties - contains configuration for Apacke Kafka consumer.
3. Lombok is used to avoid boilerplate codes. 

   