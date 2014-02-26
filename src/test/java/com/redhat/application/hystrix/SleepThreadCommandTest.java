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
        SleepThreadCommand command = new SleepThreadCommand("0");
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
            threads.add(new HystrixCommandTestThread(new SleepThreadCommand("0")));
        }

        for (int i = 0; i < expectedFailCount; i++) {
            threads.add(new HystrixCommandTestThread(new SleepThreadCommand(timeoutMillis + "0")));
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
