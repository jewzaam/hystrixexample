package com.redhat.application.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * Basic sleep command without any fancy configuration.
 *
 * @author nmalik
 */
public class SleepCommand extends HystrixCommand<String> {
    private final String msec;

    public SleepCommand(String msec, String clientId) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("sleep")));

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
