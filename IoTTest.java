/**
 *  IoTTest.java tests IoT.java against unit tests that correspond to the
 *  use cases defined in the IoT HRT Project Document.
 *
 *  I pledge my honor that I have abided by the Stevens Honor System.
 *
 *  @author Adrian Gomes, Aliya Iqbal, Amraiza Naz, and Matthew Cunningham
 *  @versio 1.0
 *  @since 2021-04-19
 */

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class IoTTest {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(IoTTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        if (result.wasSuccessful())
           System.out.println("All tests passed.");
    }

    @Test
    public void test1() {
        IoT iot = new IoT();
        assertFalse(iot.login("id", "pass"));
        assertFalse(iot.login("id", "password"));
        assertFalse(iot.login("operator", "pass"));
        assertFalse(iot.login("technician", "pass"));
        assertTrue(iot.login("operator", "password"));
        assertTrue(iot.login("technician", "password"));
    }

    @Test
    public void test2() {
        IoT iot = new IoT();
        iot.login("operator", "password");
        assertEquals(iot.getCurrentState().toString(), "STATION");
        iot = new IoT();
        iot.login("technician", "password");
        assertEquals(iot.getCurrentState().toString(), "TLOG");
        iot = new IoT();
        iot.login("tec", "password");
        assertEquals(iot.getCurrentState().toString(), "LOGIN");
    }

    // TODO Add more test cases. Look at document requirements and use cases. I put some examples but idk if they match with document i didnt check.
}
