# Play Kafka Demo

## Setup

First, you must download Kafka at https://kafka.apache.org/downloads.html and un-tar it.


Next, start the kafka server by running the following commands:

> ./bin/zookeeper-server-start.sh config/zookeeper.properties

> ./bin/kafka-server-start.sh config/server.properties

Now, create a topic named `topic1` in Kafka server:

> ./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic1

Check that the topic is well created:

> ./bin/kafka-topics.sh --list --zookeeper localhost:2181


Now, to start playing with Kafka, you must start the Play application and load the homepage:

> sbt run

> curl http://localhost:9000

At last, you should send a message:

> ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topic1

You will see the message from sbt/play output console.


## Playing with the EventBus from Play

Subscribe to a topic:

> curl http://localhost:9000/subscribe/{topic-name}

Unsubscribe to a topic:

> curl http://localhost:9000/unsubscribe/{topic-name}

Send a message:

> curl -X POST -H "Content-Type:text/plain" -d "Hello World" http://localhost:9000/send/{topic-name}
