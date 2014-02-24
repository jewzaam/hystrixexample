/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.application.hystrix;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author nmalik
 */
public class SleepCommand extends AbstractCommand<String> {
    private final String seconds;

    public SleepCommand(String seconds) {
        super("test");
        this.seconds = seconds;
    }

    @Override
    protected String run() throws Exception {
        METRICS.incrementCounter("sleep");

        try {
            int i = toInteger(seconds);
            METRICS.incrementGauge("sleep");
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            Response r = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity("Timed out sleeping!").build();
            throw new WebApplicationException(e, r);
        } finally {
            METRICS.decrementGauge("sleep");
        }

        return seconds;
    }

}
