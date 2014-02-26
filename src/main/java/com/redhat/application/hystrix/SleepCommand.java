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
public class SleepCommand extends HystrixCommand<String> {
    private final String msec;

    public SleepCommand(String msec) {
        super(HystrixConfiguration.Setter(SleepCommand.class, "test"));
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
