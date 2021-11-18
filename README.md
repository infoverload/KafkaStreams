# Kafka Producer for Twitter Tweets

For Hackathon

## To run

Download Apache Kafka 3.0 from [here](https://www.apache.org/dyn/closer.cgi?path=/kafka/3.0.0/kafka-3.0.0-src.tgz), extract the archive, and navigate into the folder.

Start Zookeeper:

./bin/zookeeper-server-start.sh config/zookeeper.properties

Start Kafka:

./bin/kafka-server-start.sh config/server.properties

Create a topic for "twitter_tweets":

./bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter_tweets --create --partitions 3 --replication-factor 1

Subscribe to the "twitter_tweets" topic:

./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter_tweets

Run the TwitterProducer Java program