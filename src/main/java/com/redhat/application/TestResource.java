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
     * Increment the given number. Uses semaphore isolation to limit concurrent requests to 5. Additional requests are
     * rejected.
     *
     * @param number the number to increment
     * @return the new value
     */
    @GET
    @Path("/increment/{number:.+}")
    public Number increment(@PathParam("number") String number) {
        Number out = IncrementCommand.instance(number, null).execute();
        ServoSetup.initialize();
        return out;
    }

    /**
     * Decrement the given number. Uses thread isolation to timeout long requests. Unexpected to see timeouts.
     *
     * @param number the number to decrement
     * @return the new value
     */
    @GET
    @Path("/decrement/{number:.+}")
    public Number decrement(@PathParam("number") String number) {
        return DecrementCommand.instance(number, null).execute();
    }

    /**
     * Sleep for the given number of milliseconds. For semaphore isolation (default) limits concurrent requests to 2 and
     * rejects additional concurrent requests. For thread isolation requests are timed out and rejected after 2000
     * milliseconds.
     *
     * @param milliseconds the number of milliseconds
     * @param type isolation strategy: semaphore or thread
     * @return the input seconds
     */
    @GET
    @Path("/sleep/{milliseconds:.+}")
    public String sleep(@PathParam("milliseconds") String milliseconds, @QueryParam("type") String type) {
        if (null == type || "semaphore".equalsIgnoreCase(type)) {
            return SleepSemaphoreCommand.instance(milliseconds, null).execute();
        } else {
            return SleepThreadCommand.instance(milliseconds, null).execute();
        }
    }
}
