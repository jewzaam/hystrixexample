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
    private static final int TIMEOUT_THRESHOLD_SECONDS = 2;
    protected static ServiceMetrics METRICS = ServoServiceMetrics.getInstance();

    protected int toInteger(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            Response r = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity("Invalid numeric value: " + number).build();
            throw new WebApplicationException(e, r);
        }
    }

    private final String seconds;

    public SleepCommand(String seconds) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("test"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationThreadTimeoutInMilliseconds(TIMEOUT_THRESHOLD_SECONDS * 1000)));
        this.seconds = seconds;
    }

    @Override
    protected String run() throws Exception {
        METRICS.incrementCounter("sleep");

        try {
            int i = toInteger(seconds);
            METRICS.incrementGauge("sleep");
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            Response r = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity("Timed out sleeping!").build();
            throw new WebApplicationException(e, r);
        } finally {
            METRICS.decrementGauge("sleep");
        }

        return seconds;
    }

    @Override
    protected String getFallback() {
        return seconds + " TIMEOUT";
    }
}
