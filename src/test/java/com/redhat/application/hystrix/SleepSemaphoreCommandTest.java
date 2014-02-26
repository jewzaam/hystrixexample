/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix;

import com.redhat.application.hystrix.test.HystrixCommandTestThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author nmalik
 */
public class SleepSemaphoreCommandTest {
    private Properties config;

    @Before
    public void setup() throws IOException {
        config = new Properties();
        config.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
    }

    @Test
    public void single() {
        SleepSemaphoreCommand command = new SleepSemaphoreCommand("0");
        command.execute();
    }

    @Test
    public void concurrent() throws InterruptedException, ExecutionException {
        // configuration
        Assert.assertEquals("SEMAPHORE", config.get(SleepSemaphoreCommand.class.getName() + ".executionIsolationStrategy"));
        int maxConcurrent = Integer.valueOf((String) config.get(SleepSemaphoreCommand.class.getName() + ".executionIsolationSemaphoreMaxConcurrentRequests"));

        // setup
        List<HystrixCommandTestThread> threads = new ArrayList<HystrixCommandTestThread>();
        for (int i = 0; i < maxConcurrent * 5; i++) {
            threads.add(new HystrixCommandTestThread(new SleepSemaphoreCommand("400")));
        }

        // execute
        for (Thread thread : threads) {
            thread.start();
        }

        // join
        int count = 0;
        int failedCount = 0;
        for (HystrixCommandTestThread thread : threads) {
            thread.join();
            count++;
            if (!thread.getCommand().isSuccessfulExecution()) {
                failedCount++;
            }
        }

        // validate
        Assert.assertEquals(threads.size(), count);
        Assert.assertEquals(threads.size() - maxConcurrent, failedCount);
    }

}
