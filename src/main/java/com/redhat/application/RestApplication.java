package com.redhat.application;

import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class RestApplication extends Application {
    static {
        HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(TestResource.class));
    }
}
