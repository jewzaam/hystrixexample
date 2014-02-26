/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix;

import com.netflix.hystrix.HystrixCommand;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nmalik
 */
public class HystrixConfigurationTest {
    @Test
    public void increment() {
        HystrixCommand.Setter setter = HystrixConfiguration.Setter(IncrementCommand.class, "test");
        Assert.assertNotNull(setter);
    }

    @Test
    public void decrement() {
        HystrixCommand.Setter setter = HystrixConfiguration.Setter(DecrementCommand.class, "test");
        Assert.assertNotNull(setter);
    }

    @Test
    public void sleep() {
        HystrixCommand.Setter setter = HystrixConfiguration.Setter(SleepCommand.class, "test");
        Assert.assertNotNull(setter);
    }
}
