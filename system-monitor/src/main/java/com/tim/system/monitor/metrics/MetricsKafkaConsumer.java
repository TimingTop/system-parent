package com.tim.system.monitor.metrics;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import kafka.consumer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by eminxta on 2016/07/01.
 */
public class MetricsKafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsKafkaConsumer.class);

    String zookeeper;
    String group;
    String topic;

    int threadNumber = 1;

    int zookeeperSessionTimeoutMs = 4000;
    int zookeeperSyncTimeMs = 2000;
    int autoCommitIntervalMs = 1000;

    MessageListener messageListener;

    kafka.javaapi.consumer.ConsumerConnector consumer;

    ExecutorService executor;

    public void init(){
        Properties props = new Properties();

        ConsumerConfig config = new ConsumerConfig(props);

        consumer = Consumer.createJavaConsumerConnector(config);

        Map<String,Integer> topicCountMap = new HashMap<String, Integer>();

        topicCountMap.put(topic,threadNumber);
        Map<String,List<KafkaStream<byte[],byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[],byte[]>> streams = consumerMap.get(topic);

        executor = Executors.newFixedThreadPool(threadNumber,
                new ThreadFactoryBuilder().setNameFormat("kafka-metrics-consumer-%d").build());

        for(final KafkaStream stream : streams){
            executor.submit(new Runnable(){

                public void run() {
                    ConsumerIterator<byte[],byte[]> it = stream.iterator();
                    while(it.hasNext()){
                        try{
                            messageListener.onMessage(new String(it.next().message()));
                        }catch(RuntimeException e){
                            LOGGER.error("consumer kafka metrics message error!",e);
                        }
                    }
                }
            });
        }

    }

    public void desotry(){
        try{
            if(consumer != null){
                consumer.shutdown();
            }
        }finally {
            if(executor != null){
                executor.shutdown();
            }
        }
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public int getZookeeperSessionTimeoutMs() {
        return zookeeperSessionTimeoutMs;
    }

    public void setZookeeperSessionTimeoutMs(int zookeeperSessionTimeoutMs) {
        this.zookeeperSessionTimeoutMs = zookeeperSessionTimeoutMs;
    }

    public int getZookeeperSyncTimeMs() {
        return zookeeperSyncTimeMs;
    }

    public void setZookeeperSyncTimeMs(int zookeeperSyncTimeMs) {
        this.zookeeperSyncTimeMs = zookeeperSyncTimeMs;
    }

    public int getAutoCommitIntervalMs() {
        return autoCommitIntervalMs;
    }

    public void setAutoCommitIntervalMs(int autoCommitIntervalMs) {
        this.autoCommitIntervalMs = autoCommitIntervalMs;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
