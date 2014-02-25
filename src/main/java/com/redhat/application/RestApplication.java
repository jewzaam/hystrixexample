package com.redhat.application;

import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.servo.publish.BasicMetricFilter;
import com.netflix.servo.publish.MetricObserver;
import com.netflix.servo.publish.MonitorRegistryMetricPoller;
import com.netflix.servo.publish.PollRunnable;
import com.netflix.servo.publish.PollScheduler;
import com.netflix.servo.publish.graphite.GraphiteMetricObserver;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationPath("/rest")
public class RestApplication extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApplication.class);

    static {
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

        String addr = "metrics11.devlab.redhat.com:8080";
        LOGGER.info("ServoConfig: prefix={}, address={}", prefix, addr);
        MetricObserver observer = new GraphiteMetricObserver(prefix, addr);

        // create poller
        PollScheduler.getInstance().start();

        // create poller on observer
        PollRunnable task = new PollRunnable(new MonitorRegistryMetricPoller(), BasicMetricFilter.MATCH_ALL, observer);

        // register poller with scheduler
        PollScheduler.getInstance().addPoller(task, 5, TimeUnit.SECONDS);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(TestResource.class));
    }
}
