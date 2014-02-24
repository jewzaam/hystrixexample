/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.redhat.application.ServiceMetrics;
import com.redhat.application.ServoServiceMetrics;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author nmalik
 */
public class SleepCommand extends HystrixCommand<String> {
    private static final int TIMEOUT_THRESHOLD_MILLISECONDS = 2000;
    protected static ServiceMetrics METRICS = ServoServiceMetrics.getInstance();

    protected int toInteger(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            Response r = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity("Invalid numeric value: " + number).build();
            throw new WebApplicationException(e, r);
        }
    }

    private final String msec;
    private Exception exception;

    public SleepCommand(String msec) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("test"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationThreadTimeoutInMilliseconds(TIMEOUT_THRESHOLD_MILLISECONDS)));
        this.msec = msec;
    }

    @Override
    protected String run() throws Exception {
        METRICS.incrementCounter("sleep");

        try {
            int i = toInteger(msec);
            METRICS.incrementGauge("sleep");
            Thread.sleep(i);
        } finally {
            METRICS.decrementGauge("sleep");
        }

        return msec;
    }

    @Override
    protected String getFallback() {
        return msec + " TIMEOUT";
    }
}
