package com.redhat.application;

import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.servo.publish.BasicMetricFilter;
import com.netflix.servo.publish.JvmMetricPoller;
import com.netflix.servo.publish.MetricObserver;
import com.netflix.servo.publish.MonitorRegistryMetricPoller;
import com.netflix.servo.publish.PollRunnable;
import com.netflix.servo.publish.PollScheduler;
import com.netflix.servo.publish.graphite.GraphiteMetricObserver;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServoSetup {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServoSetup.class);

    public static final void initialize() {
        // register hystrix servo metrics publisher
        HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());

        // try to get name from openshift.  assume it's scaleable app.
        // format: <app name>.  <namespace>.<gear dns>
        String prefix = System.getenv("HOSTNAME"); // default

        if (System.getenv("OPENSHIFT_APP_NAME") != null) {
            prefix = String.format(
                    "%s.%s.%s",
                    System.getenv("OPENSHIFT_APP_NAME"),
                    System.getenv("OPENSHIFT_NAMESPACE"),
                    System.getenv("OPENSHIFT_GEAR_DNS")
            );
        }

        String host = "metrics11.devlab.redhat.com";//System.getenv("GRAPHITE_HOSTNAME");
        String port = "8080";//System.getenv("GRAPHITE_PORT");

        String addr = host + ":" + port;
        LOGGER.info("ServoConfig: prefix={}, address={}", prefix, addr);
        MetricObserver observer = new GraphiteMetricObserver(prefix, addr);

        // start poll scheduler
        PollScheduler.getInstance().start();

        // create registry on observer                                                                                                          
        PollRunnable registeryTask = new PollRunnable(new MonitorRegistryMetricPoller(), BasicMetricFilter.MATCH_ALL, observer);
        PollScheduler.getInstance().addPoller(registeryTask, 5, TimeUnit.SECONDS);
        
        // create jvm poller
        PollRunnable jvmTask = new PollRunnable(new JvmMetricPoller(), BasicMetricFilter.MATCH_ALL, observer);
        PollScheduler.getInstance().addPoller(jvmTask, 5, TimeUnit.SECONDS);
    }
}
