/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 *
 * @author nmalik
 */
public class DecrementCommand extends HystrixCommand<Integer> {
    private final String number;

    public DecrementCommand(String number) {
        super(HystrixCommandGroupKey.Factory.asKey("test"));
        this.number = number;
    }

    @Override
    protected Integer run() throws Exception {
        return Integer.parseInt(number) - 1;
    }
}
