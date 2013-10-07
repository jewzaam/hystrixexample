package librato;

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
@Path("/newrelic")
public class LibratoResource {

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
     * @param number
     *            the number to increment
     * @return the new value
     */
    @GET
    @Path("/increment/{number:.+}")
    public Number increment(@PathParam("number") String number) {
        return toInteger(number) + 1;
    }

    /**
     * Decrement the given number.
     * 
     * @param number
     *            the number to decrement
     * @return the new value
     */
    @GET
    @Path("/decrement/{number:.+}")
    public Number decrement(@PathParam("number") String number) {
        return toInteger(number) - 1;
    }

    /**
     * Sleep for the given number of seconds, to force stuff to show up on
     * newrelic metrics...
     * 
     * @param seconds
     *            the number of seconds
     * @return the input seconds
     */
    @GET
    @Path("/sleep/{seconds:.+}")
    public String sleep(@PathParam("seconds") String seconds) {
        int i = toInteger(seconds);

        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            Response r = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity("Timed out sleeping!").build();
            throw new WebApplicationException(e, r);
        }

        return seconds;
    }
}
