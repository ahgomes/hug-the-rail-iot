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

        System.out.println(result.wasSuccessful());
    }

    @Test
    public void test1() {
      String str = "this is just a test";
      assertEquals("this is just a test", str);
   }
}
