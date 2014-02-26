/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix.test;

import com.netflix.hystrix.HystrixCommand;

/**
 * Utility class for doing tests of concurrent command execution.
 *
 * @author nmalik
 */
public class HystrixCommandTestThread extends Thread {
    private final HystrixCommand command;

    public HystrixCommandTestThread(HystrixCommand command) {
        this.command = command;
    }

    @Override
    public void run() {
        command.execute();
    }

    public HystrixCommand getCommand() {
        return command;
    }
}
