/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix;

import com.netflix.hystrix.HystrixCommand;

/**
 *
 * @author nmalik
 */
public class SleepSemaphoreCommand extends HystrixCommand<String> {
    private final String msec;

    public static SleepSemaphoreCommand instance(String number, String clientId) {
        if (null == clientId || clientId.isEmpty()) {
            return new SleepSemaphoreCommand(number);
        } else {
            return new SleepSemaphoreCommand(number, clientId);
        }
    }

    private SleepSemaphoreCommand(String msec) {
        this(msec, "test");
    }

    private SleepSemaphoreCommand(String msec, String clientId) {
        super(HystrixConfiguration.Setter(SleepSemaphoreCommand.class, clientId));
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
