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

    public IncrementCommand(String number) {
        super(HystrixConfiguration.Setter(IncrementCommand.class, "test"));
        this.number = number;
    }

    @Override
    protected Integer run() throws Exception {
        return Integer.valueOf(number) + 1;
    }
}
