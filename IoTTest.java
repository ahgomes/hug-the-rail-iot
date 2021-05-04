/**
 *  IoTTest.java tests IoT.java against unit tests that correspond to the
 *  use cases defined in the IoT HRT Project Document.
 *
 *  I pledge my honor that I have abided by the Stevens Honor System.
 *
 *  @author Adrian Gomes, Aliya Iqbal, Amraiza Naz, and Matthew Cunningham
 *  @version 1.0
 *  @since 2021-05-03
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

    /**
     * Tests Login validation.
     */
    @Test
    public void testLogin() {
        IoT iot = new IoT();
        iot.login("o", "p"); //operator login
        assertEquals("STATION", iot.getCurrentState().toString());
        iot = new IoT();
        iot.login("t", "p"); // technician login
        assertEquals("TLOG", iot.getCurrentState().toString());
        iot = new IoT();
        iot.login("tec", "password"); // failed login
        assertEquals("LOGIN", iot.getCurrentState().toString());
    }

    /**
     * Tests weather warning.
     */
    @Test
    public void testWeather() {
        IoT iot = new IoT();
        iot.setCurrentState(3); // 3 represents safe state
        iot.setWeatherData("0+Weather+W:50;D"); // dry
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        iot.setWeatherData("0+Weather+W:50;R>3"); // rain
        iot.handleData();
        assertEquals("WARNING", iot.getCurrentState().toString());
        iot.setWeatherData("0+Weather+W:50;S>4"); // snow
        iot.handleData();
        assertEquals("DANGER", iot.getCurrentState().toString());
    }

    /**
     * Tests curve degree warning.
     */
    @Test
    public void testInclinometer() {
        IoT iot = new IoT();
        iot.setCurrentState(3); // 3 represents safe state
        iot.setInclinometerData("1+Inclinometer+I:0"); // no curve
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        iot.setInclinometerData("1+Inclinometer+I:3"); // small curve
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        iot.setInclinometerData("1+Inclinometer+I:9"); // harsh curve
        iot.handleData();
        assertEquals("WARNING", iot.getCurrentState().toString());
        iot.setInclinometerData("1+Inclinometer+I:30"); // extreme curve
        iot.handleData();
        assertEquals("DANGER", iot.getCurrentState().toString());
    }

    /**
     * Tests wheel slippage warning.
     */
    @Test
    public void testSlippage() {
        IoT iot = new IoT();
        iot.setCurrentState(3); // 3 represents safe state
        // no slippage
        iot.setSpeedData("2+Speed+S:500");
        iot.handleData();
        iot.setSpeedData("2+Speed+S:500");
        iot.setAccelerometerData("3+Accelerometer+A:0");
        iot.handleData();
        iot.setSpeedData("2+Speed+S:500");
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        // potential slippage
        iot.setSpeedData("2+Speed+S:500");
        iot.handleData();
        iot.setSpeedData("2+Speed+S:510");
        iot.setAccelerometerData("3+Accelerometer+A:0");
        iot.handleData();
        iot.setSpeedData("2+Speed+S:520");
        iot.handleData();
        iot.setSpeedData("2+Speed+S:530");
        iot.handleData();
        assertEquals("WARNING", iot.getCurrentState().toString());
        // definite slippage
        iot.setSpeedData("2+Speed+S:500");
        iot.handleData();
        iot.setSpeedData("2+Speed+S:560");
        iot.setAccelerometerData("3+Accelerometer+A:2");
        iot.handleData();
        iot.setSpeedData("2+Speed+S:700");
        iot.handleData();
        assertEquals("DANGER", iot.getCurrentState().toString());
    }

    /**
     * Tests accelration warning.
     */
    @Test
    public void testAccelerometer() {
        IoT iot = new IoT();
        iot.setCurrentState(3); // 3 represents safe state
        iot.setAccelerometerData("3+Accelerometer+A:0"); // no acceleration
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        iot.setAccelerometerData("3+Accelerometer+A:5"); // low acceleration
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        iot.setAccelerometerData("3+Accelerometer+A:13"); // high acceleration
        iot.handleData();
        assertEquals("WARNING", iot.getCurrentState().toString());
        iot.setAccelerometerData("3+Accelerometer+A:50"); //extreme acceleration
        iot.handleData();
        assertEquals("DANGER", iot.getCurrentState().toString());
    }

    /**
     * Tests obstacle warning.
     */
    @Test
    public void testInfrared() {
        IoT iot = new IoT();
        iot.setCurrentState(3); // 3 represents safe state
        iot.setInfraredData("4+Infrared+O:-1"); // no obstacle
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        iot.setInfraredData("4+Infrared+O:1000"); // far obstacle
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        iot.setInfraredData("4+Infrared+O:500"); // near obstacle
        iot.handleData();
        assertEquals("WARNING", iot.getCurrentState().toString());
        iot.setInfraredData("4+Infrared+O:100"); // too close obstacle
        iot.handleData();
        assertEquals("DANGER", iot.getCurrentState().toString());
    }

    /**
     * Tests barrier crossing warning.
     */
    @Test
    public void testWeight() {
        IoT iot = new IoT();
        iot.setCurrentState(3); // 3 represents safe state
        iot.setWeightData("5+Weight+L:S"); // closed barrier
        iot.handleData();
        assertEquals("SAFE", iot.getCurrentState().toString());
        iot.setWeightData("5+Weight+L:F"); // failed to closed barrier
        iot.handleData();
        assertEquals("DANGER", iot.getCurrentState().toString());
    }

}
