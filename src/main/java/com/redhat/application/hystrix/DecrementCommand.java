/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix;

/**
 *
 * @author nmalik
 */
public class DecrementCommand extends AbstractCommand<Integer> {
    private final String number;

    public DecrementCommand(String number) {
        super("test");
        this.number = number;
    }

    @Override
    protected Integer run() throws Exception {
        METRICS.incrementCounter("decrement");
        return toInteger(number) - 1;
    }
}
