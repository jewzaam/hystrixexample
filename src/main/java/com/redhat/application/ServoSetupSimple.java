package com.redhat.application;

import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.servo.publish.BasicMetricFilter;
import com.netflix.servo.publish.JvmMetricPoller;
import com.netflix.servo.publish.MetricObserver;
import com.netflix.servo.publish.PollRunnable;
import com.netflix.servo.publish.PollScheduler;
import com.netflix.servo.publish.graphite.GraphiteMetricObserver;
import java.util.concurrent.TimeUnit;

/**
 * A better implementation is ServoSetup. This class was written to provide a slightly less complicated example for
 * documentation.
 *
 * @author nmalik
 */
public class ServoSetupSimple {
    public static final void initialize() {
        // register hystrix servo metrics publisher
        HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());

        // try to get name from openshift.  assume it's scaleable app.
        // format: <app name>.  <namespace>.<gear dns>
        String prefix = System.getenv("HOSTNAME"); // default

        String host = System.getenv("GRAPHITE_HOSTNAME");
        String port = System.getenv("GRAPHITE_PORT");

        String addr = host + ":" + port;
        MetricObserver observer = new GraphiteMetricObserver(prefix, addr);

        // start poll scheduler
        PollScheduler.getInstance().start();

        // create and register jvm poller
        PollRunnable jvmTask = new PollRunnable(new JvmMetricPoller(), BasicMetricFilter.MATCH_ALL, observer);
        PollScheduler.getInstance().addPoller(jvmTask, 5, TimeUnit.SECONDS);
    }
}
