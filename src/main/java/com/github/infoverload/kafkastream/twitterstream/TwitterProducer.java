package com.github.infoverload.kafkastream.twitterstream;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {

    Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());

    String consumerKey = "";
    String consumerSecret = "";
    String accessToken = "";
    String accessSecret = "";

    String bootstrapServers = "127.0.0.1:9092";

    public TwitterProducer(){}

    public static void main(String[] args) throws InterruptedException {
        new TwitterProducer().run();
    }

    public void run() {

        logger.info("Setup");
        // set up blocking queues - size these properly based on expected TPS of stream
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(1000);
        // create a twitter client
        Client client = createTwitterClient(msgQueue);
        client.connect();

        Producer<String, String> producer = createKafkaProducer();

        while(!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
            if (msg != null) {
                logger.info(msg);
                producer.send(new ProducerRecord<>("twitter_tweets", null, msg), (metadata, exception) -> {
                    if (exception != null) {
                        logger.error("Error!", exception);
                    }
                });
            }
            logger.info("End of application");
        }

    }

    public Client createTwitterClient(BlockingQueue<String> msgQueue) {

        // declare host you want to connect to, the endpoint, and authentication (basic auth or oauth)
        Hosts twitterHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint twitterEndpoint = new StatusesFilterEndpoint();

        // these secrets should be read from a config file
        Authentication twitterAuth = new OAuth1(consumerKey, consumerSecret, accessToken, accessSecret);

        // generate the OAuth Access token first and then set it with twitter handle
        //twitter = new TwitterFactory().getInstance();
        //twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET_KEY);
        //AccessToken oauthAccessToken = new AccessToken(getSavedAccessToken(), getSavedAccessTokenSecret());
        //twitter.setOAuthAccessToken(oauthAccessToken);

        ClientBuilder builder = new ClientBuilder()
                .name("Twitter-Client-01")
                .hosts(twitterHosts)
                .authentication(twitterAuth)
                .endpoint(twitterEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        Client twitterClient = builder.build();
        return twitterClient;
    }

    public KafkaProducer<String, String> createKafkaProducer(){

            // create producer properties
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            // create the producer
            KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
            return producer;
    }
}
