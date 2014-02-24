package com.redhat.application.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.redhat.application.ServiceMetrics;
import com.redhat.application.ServoServiceMetrics;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author nmalik
 */
public abstract class AbstractCommand<T> extends HystrixCommand<T> {
    protected static ServiceMetrics METRICS = ServoServiceMetrics.getInstance();

    protected int toInteger(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            Response r = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity("Invalid numeric value: " + number).build();
            throw new WebApplicationException(e, r);
        }
    }

    public AbstractCommand(String commandGroup) {
        super(HystrixCommandGroupKey.Factory.asKey(commandGroup));
    }
}
