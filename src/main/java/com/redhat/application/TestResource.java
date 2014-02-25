package com.redhat.application;

import com.redhat.application.hystrix.DecrementCommand;
import com.redhat.application.hystrix.IncrementCommand;
import com.redhat.application.hystrix.SleepCommand;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Simple service to test things.
 *
 * @author nmalik
 */
@Path("/test")
public class TestResource {
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
     * Sleep for the given number of milliseconds.
     *
     * @param milliseconds the number of milliseconds
     * @return the input seconds
     */
    @GET
    @Path("/sleep/{milliseconds:.+}")
    public String sleep(@PathParam("milliseconds") String milliseconds) {
        return new SleepCommand(milliseconds).execute();
    }
}
