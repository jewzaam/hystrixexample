/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 *
 * @author nmalik
 */
public class SleepCommand extends HystrixCommand<String> {
    private static final int TIMEOUT_THRESHOLD_MILLISECONDS = 2000;

    private final String msec;

    public SleepCommand(String msec) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("test"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationThreadTimeoutInMilliseconds(TIMEOUT_THRESHOLD_MILLISECONDS)));
        this.msec = msec;
    }

    @Override
    protected String run() throws Exception {
        int i = Integer.valueOf(msec);
        Thread.sleep(i);

        return msec;
    }

    @Override
    protected String getFallback() {
        return msec + " TIMEOUT";
    }
}
