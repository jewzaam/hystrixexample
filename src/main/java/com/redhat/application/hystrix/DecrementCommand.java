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
public class DecrementCommand extends HystrixCommand<Integer> {
    private final String number;

    public static DecrementCommand instance(String number, String clientId) {
        if (null == clientId || clientId.isEmpty()) {
            return new DecrementCommand(number);
        } else {
            return new DecrementCommand(number, clientId);
        }
    }

    private DecrementCommand(String number) {
        this(number, "test");
    }

    private DecrementCommand(String number, String clientId) {
        super(HystrixConfiguration.Setter(DecrementCommand.class, clientId));
        this.number = number;
    }

    @Override
    protected Integer run() throws Exception {
        return Integer.parseInt(number) - 1;
    }
}
