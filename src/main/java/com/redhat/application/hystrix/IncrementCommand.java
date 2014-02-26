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
public class IncrementCommand extends HystrixCommand<Integer> {
    private final String number;

    public static IncrementCommand instance(String number, String clientId) {
        if (null == clientId || clientId.isEmpty()) {
            return new IncrementCommand(number);
        } else {
            return new IncrementCommand(number, clientId);
        }
    }

    private IncrementCommand(String number) {
        this(number, "test");
    }

    private IncrementCommand(String number, String clientId) {
        super(HystrixConfiguration.Setter(IncrementCommand.class, clientId));
        this.number = number;
    }

    @Override
    protected Integer run() throws Exception {
        return Integer.valueOf(number) + 1;
    }
}
