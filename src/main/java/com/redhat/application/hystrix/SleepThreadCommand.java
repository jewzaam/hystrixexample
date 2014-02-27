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
public class SleepThreadCommand extends HystrixCommand<String> {
    private final String msec;

    public static SleepThreadCommand instance(String number, String clientId) {
        if (null == clientId || clientId.isEmpty()) {
            return new SleepThreadCommand(number);
        } else {
            return new SleepThreadCommand(number, clientId);
        }
    }

    private SleepThreadCommand(String msec) {
        this(msec, "test");
    }

    private SleepThreadCommand(String msec, String clientId) {
        super(HystrixConfiguration.Setter(SleepThreadCommand.class, clientId));
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
