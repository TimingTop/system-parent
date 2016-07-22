package com.tim.system.monitor.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import kafka.tools.ConsoleProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.*;
import org.apache.kafka.common.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.concurrent.*;

/**
 * Created by eminxta on 2016/07/01.
 */
public class KafkaReporter extends ScheduledReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReporter.class);

    String topic;
    ProducerConfig config;
    KafkaProducer<String,String> producer;
    ExecutorService kafkaExecutor;
    Properties props;

    private String prefix;
    private String hostName;
    private String ip;

    int count = 0;
    ObjectMapper mapper;


    private KafkaReporter(MetricRegistry registry, String name, TimeUnit rateUnit,TimeUnit durationUnit,boolean showSamples,
                          MetricFilter filter,String topic,Properties props,String prefix,String hostName,String ip){

        super(registry, name, filter, rateUnit, durationUnit);
        this.topic = topic;
        this.props = props;
        this.prefix = prefix;
        this.hostName = hostName;
        this.ip = ip;

        this.mapper = new ObjectMapper().registerModule(new MetricsModule(rateUnit,durationUnit,showSamples));
        this.producer = new KafkaProducer<String, String>(props);


        kafkaExecutor = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder().setNameFormat("kafka-producer-%d").build()
        );

    }

    private Map<String,Object> addPrefix(SortedMap<String,?> map){
        Map<String,Object> result = new HashMap<String, Object>(map.size());
        for(Map.Entry<String,?> entry : map.entrySet()){
            result.put(prefix + entry.getKey(),entry.getValue());
        }
        return result;
    }

    @Override
    public void report(SortedMap<String, Gauge> sortedMap,
                       SortedMap<String, Counter> sortedMap1,
                       SortedMap<String, Histogram> sortedMap2,
                       SortedMap<String, Meter> sortedMap3,
                       SortedMap<String, Timer> sortedMap4) {

        final Map<String,Object> result = new HashMap<String,Object>(16);

        result.put("hostName",hostName);
        result.put("ip",ip);
        result.put("rateUnit",getRateUnit());
        result.put("durationUnit",getDurationUnit());

        result.put("gauges",addPrefix(sortedMap));
        result.put("counters",addPrefix(sortedMap1));
        result.put("histograms",addPrefix(sortedMap2));
        result.put("meters",addPrefix(sortedMap3));
        result.put("timers",addPrefix(sortedMap4));

        result.put("clock",System.currentTimeMillis());

        kafkaExecutor.execute(new Runnable() {
            public void run() {
                try{
                    ProducerRecord<String,String> message = new ProducerRecord<String, String>(
                            topic,"" + count++, mapper.writeValueAsString(result)
                    );
                    producer.send(message);

                }catch(Exception e){
                    LOGGER.error("send metrics to kafka error!",e);
                }
            }
        });
    }
}
