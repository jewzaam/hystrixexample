package com.redhat.application;

import com.redhat.application.hystrix.DecrementCommand;
import com.redhat.application.hystrix.IncrementCommand;
import com.redhat.application.hystrix.SleepCommand;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Simple service to test out NewRelic custom metrics.
 *
 * @author nmalik
 */
@Path("/test")
public class TestResource {

    private static ServiceMetrics metrics = ServoServiceMetrics.getInstance();

    private int toInteger(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            Response r = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity("Invalid numeric value: " + number).build();
            throw new WebApplicationException(e, r);
        }
    }

    /**
     * Increment the given number
     *
     * @param number the number to increment
     * @return the new value
     */
    @GET
    @Path("/increment/{number:.+}")
    public Number increment(@PathParam("number") String number) {
        return new IncrementCommand(number).execute();
    }

    /**
     * Decrement the given number.
     *
     * @param number the number to decrement
     * @return the new value
     */
    @GET
    @Path("/decrement/{number:.+}")
    public Number decrement(@PathParam("number") String number) {
        return new DecrementCommand(number).execute();
    }

    /**
     * Sleep for the given number of seconds, to force stuff to show up on newrelic metrics...
     *
     * @param seconds the number of seconds
     * @return the input seconds
     */
    @GET
    @Path("/sleep/{seconds:.+}")
    public String sleep(@PathParam("seconds") String seconds) {
        return new SleepCommand(seconds).execute();
    }
}
