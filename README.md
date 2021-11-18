# Kafka Producer for Twitter Tweets

For Ververica Hackathon

## To run

Download the latest version of Apache Kafka from [here](https://kafka.apache.org/downloads), extract the archive, and navigate into the folder.

Start Zookeeper:

```
./bin/zookeeper-server-start.sh config/zookeeper.properties &
```

Start Kafka:

```
./bin/kafka-server-start.sh config/server.properties &
```

Create a topic for "twitter_tweets":

```
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter_tweets --create --partitions 3 --replication-factor 1
```

Subscribe to the "twitter_tweets" topic:

```
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter_tweets
```

Run the TwitterProducer Java program
