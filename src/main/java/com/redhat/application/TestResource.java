package com.redhat.application;

import com.redhat.application.hystrix.DecrementCommand;
import com.redhat.application.hystrix.IncrementCommand;
import com.redhat.application.hystrix.SleepSemaphoreCommand;
import com.redhat.application.hystrix.SleepThreadCommand;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

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
    public Number increment(@PathParam("number") String number, @QueryParam("clientId") String clientId) {
        return IncrementCommand.instance(number, clientId).execute();
    }

    /**
     * Decrement the given number.
     *
     * @param number the number to decrement
     * @return the new value
     */
    @GET
    @Path("/decrement/{number:.+}")
    public Number decrement(@PathParam("number") String number, @QueryParam("clientId") String clientId) {
        return DecrementCommand.instance(number, clientId).execute();
    }

    /**
     * Sleep for the given number of milliseconds.
     *
     * @param milliseconds the number of milliseconds
     * @return the input seconds
     */
    @GET
    @Path("/sleep/{milliseconds:.+}")
    public String sleep(@PathParam("milliseconds") String milliseconds, @QueryParam("type") String type, @QueryParam("clientId") String clientId) {
        if (null == type || "semaphore".equalsIgnoreCase(type)) {
            return SleepSemaphoreCommand.instance(milliseconds, clientId).execute();
        } else {
            return new SleepThreadCommand(milliseconds).execute();
        }
    }
}
