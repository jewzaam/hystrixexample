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
public class SleepThreadCommandTest {
    private Properties config;

    @Before
    public void setup() throws IOException {
        config = new Properties();
        config.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
    }

    @Test
    public void single() {
        SleepThreadCommand command = SleepThreadCommand.instance("0", null);
        command.execute();
    }

    @Test
    public void concurrent() throws InterruptedException, ExecutionException {
        // configuration
        Assert.assertEquals("THREAD", config.get(SleepThreadCommand.class.getName() + ".executionIsolationStrategy"));
        Assert.assertEquals("true", config.get(SleepThreadCommand.class.getName() + ".executionIsolationThreadInterruptOnTimeout"));
        String timeoutMillis = (String) config.get(SleepThreadCommand.class.getName() + ".executionIsolationThreadTimeoutInMilliseconds");

        // setup for 25% fail rate
        int expectedFailCount = 3;  // keep total thread count under 10 or things start failing in the unit tests
        List<HystrixCommandTestThread> threads = new ArrayList<HystrixCommandTestThread>();
        for (int i = 0; i < expectedFailCount * 2; i++) {
            threads.add(new HystrixCommandTestThread(SleepThreadCommand.instance("0", null)));
        }

        for (int i = 0; i < expectedFailCount; i++) {
            threads.add(new HystrixCommandTestThread(SleepThreadCommand.instance(timeoutMillis + "0", null)));
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
            if (thread.getCommand().isResponseFromFallback()) {
                failedCount++;
            }
        }

        // validate
        Assert.assertEquals(threads.size(), count);
        Assert.assertEquals(expectedFailCount, failedCount);
    }

    @Test
    public void concurrentTwoThreadGroups() throws InterruptedException, ExecutionException {
        // configuration
        Assert.assertEquals("THREAD", config.get(SleepThreadCommand.class.getName() + ".executionIsolationStrategy"));
        Assert.assertEquals("true", config.get(SleepThreadCommand.class.getName() + ".executionIsolationThreadInterruptOnTimeout"));
        String timeoutMillis = (String) config.get(SleepThreadCommand.class.getName() + ".executionIsolationThreadTimeoutInMilliseconds");

        List<HystrixCommandTestThread> threads = new ArrayList<HystrixCommandTestThread>();

        // keep total thread count under 10 for each thread group or things start failing in the unit tests
        int expectedFailCount = 0;

        // setup for 25% fail rate for client1
        {
            int x = 3;
            expectedFailCount += x;
            for (int i = 0; i < x * 2; i++) {
                threads.add(new HystrixCommandTestThread(SleepThreadCommand.instance("0", "client1")));
            }

            for (int i = 0; i < x; i++) {
                threads.add(new HystrixCommandTestThread(SleepThreadCommand.instance(timeoutMillis + "0", "client1")));
            }
        }

        // setup for 25% fail rate for client1
        {
            int x = 2;
            expectedFailCount += x;
            for (int i = 0; i < x * 3; i++) {
                threads.add(new HystrixCommandTestThread(SleepThreadCommand.instance("0", "client2")));
            }

            for (int i = 0; i < x; i++) {
                threads.add(new HystrixCommandTestThread(SleepThreadCommand.instance(timeoutMillis + "0", "client2")));
            }
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
            if (thread.getCommand().isResponseFromFallback()) {
                failedCount++;
            }
        }

        // validate
        Assert.assertEquals(threads.size(), count);
        Assert.assertEquals(expectedFailCount, failedCount);
    }
}
