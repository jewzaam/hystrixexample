package com.redhat.application.hystrix;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nmalik
 */
public class EnvTest {

    @Test
    public void hostname() {
        String hostname = System.getenv("HOSTNAME");
        Assert.assertNotNull(hostname);
    }
}
