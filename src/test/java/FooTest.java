
import junit.framework.Assert;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nmalik
 */
public class FooTest {

    @Test
    public void hostname() {
        String hostname = System.getenv("HOSTNAME");
        Assert.assertNotNull(hostname);
    }
}
