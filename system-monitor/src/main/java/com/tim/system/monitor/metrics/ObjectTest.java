package com.tim.system.monitor.metrics;

import com.codahale.metrics.*;

import java.io.IOException;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by eminxta on 2016/07/04.
 */
public class ObjectTest {
    static final MetricRegistry metrics = new MetricRegistry();
    static public Timer timer = new Timer();

    public static void main(String args[]) throws IOException{
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS).build();
        metrics.register("jvm.mem",new JvmAttributeGaugeSet());
       // metrics.register("jvm.gc",new GarbageCollectionMetricSet());

        final Histogram responseSizes = metrics.histogram("response-sizes");
        final com.codahale.metrics.Timer metricsTimer = metrics
                .timer("test-timer");

        /*
        timer.schedule(new TimerTask() {
            int i = 100;

            @Override
            public void run() {
                Timer.Context context = metricsTimer.time();
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                responseSizes.update(i++);
                context.stop();
            }

        }, 1000, 1000);
        */

        reporter.start(5, TimeUnit.SECONDS);
    }
}
