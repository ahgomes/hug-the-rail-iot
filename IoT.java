/**
 *  IoT.java runs a simulation of the Hug the Rails IoT system as defined in
 *  the IoT HRT Project Document.
 *
 *  I pledge my honor that I have abided by the Stevens Honor System.
 *
 *  @author Adrian Gomes, Aliya Iqbal, Amraiza Naz, and Matthew Cunningham
 *  @versio 1.0
 *  @since 2021-04-19
 */

import java.util.ArrayList;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Timer;
import java.util.TimerTask;

public class IoT {
    private Display display;
    private TSNR tsnr;
    private Log log;
    private State currentState;

    private String[] opCreds = {"o", "p"}; //{"operator", "password"};
    private String[] techCreds = {"t", "p"};  //{"technician", "password"};

    public enum State {
        LOGIN,
        TLOG,
        STATION,
        CRASH,
        SAFE,
        WARNING,
        DANGER
    }

    public IoT() {
        this.display = new Display();
        this.tsnr = new TSNR();
        this.log = new Log();
        this.currentState = State.LOGIN;
    }

    public boolean warning() {
    	int weather = tsnr.weather.data;
    	int inclin = tsnr.inclinometer.data;
    	int speed = tsnr.speed.data;
    	int acc = tsnr.accelerometer.data;
    	int infrared = tsnr.infrared.data;

    	if (
            weather == 1
            || inclin > 8
            || speed == 1
            || acc > 12
            || infrared == 1
        ) {
    	    currentState = State.WARNING;
    	    return true;
    	}
	    currentState = State.SAFE;
	    return false;
    }

    public static void main(String[] args) throws IOException {
        IoT iot = new IoT();

        iot.display.open();
        iot.currentState = State.LOGIN;

    	//not entirely sure this is right?
    	//currentState = State.TLOG;
    	//iot.log.flush();
    	//createLogPanel();

    	//LocalTime time = LocalTime.now();
    	//iot.log.write("Beginning of log: " + time + "\n", false);

    	//createLoginPanel();
    	//createDashboardPanel();
    	//JLabel warning = new JLabel("");
    	//String warningMessage = "";

    	/*currentState = State.SAFE;
    	Timer timer = new Timer();
    	timer.schedule(new TimerTask(){
    		@Override
    		public void update(){
    		    sensorData();
    		    createDashboardPanel();
    		    if(iot.tsnr.getData(5) = "1"){
    			triggerMessage = "Railroad crossing barrier has been triggered";
    			warning.setText(triggerMessage);
    			iot.log.write(triggerMessage, true);
    		    }

    		    //not sure if we wanna have a warning message specific to each sensor
    		    if(warning()){
    			warningMessage = "Detected unsafe conditions, decrease in speed recommended";
    			warning.setText(warningMessage);
    			iot.log.write(warningMessage, true);
    		    }
    		    if(iot.tsnr.getData(2) == "0" && iot.tsnr.getData(3) = "0")
    			currentState = State.STATION;
    		}
    	    }, 0, 1000);

    	//also double check pls
    	if(currentState = State.STATION){
    	    timer.cancel();
    	    iot.log.write("Train stopped", true);
    	    module.addWindowListener(new java.awt.event.WindowAdapter() {
    		    @Override
    		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
    			if (JOptionPane.showConfirmDialog(module, "Are you sure you want to close this window?", "Close Window?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
    			    iot.log.close();
    			    currentState = State.LOGIN;
    			    createLoginPanel();
    			}
    		    }
    		});
    	   }*/
        }


        ////////////// TODO //////////////
        // Log in state -> TLOG
            // Change display to Log File
            // Flush log file
            // Display log file
            // Log out
        // Log in state -> STATION
            // Change display to Dashboard
            // Start train state -> SAFE
            // Write to log operator id
            // Write log line
            // Send simulation info to sensors
            // Process sensor data in TSNR
            // Get info from TSNR
            // Update & Display state/data
            // State -> WARNING || DANGER
                // Add to log
            // Stop train
            // If no warning/danger -> Log write "safe run"
            // else -> why stopped CRASH or STATION
            // Close log file
            // Log out
            // Change Display to Login

    class Display {
        JFrame frame;
        JPanel panel;
        JPanel loginP;
        JPanel tlogP;
        JPanel dashP;

        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);

        public Display() {
            frame = new JFrame("Hug The Rails IoT System");
            panel = new JPanel();
            panel.setBackground(Color.white);
        }

        public void open() {
            panel.add(createLoginPanel());
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }

        public JPanel createLoginPanel() {  // TODO make the login look nicer
            loginP = new JPanel();
            loginP.setBorder(padding);
            loginP.setLayout(new BoxLayout(loginP, BoxLayout.Y_AXIS));

            JLabel imgLabel = new JLabel(new ImageIcon("img/tracks.png"));
            JLabel title = new JLabel("Login");
            JLabel idLabel = new JLabel("Id");
            JTextField idText = new JTextField(20);
            JLabel passLabel = new JLabel("Password");
            JPasswordField passText = new JPasswordField(20);
            JButton enter = new JButton("Login");
            JLabel error = new JLabel("");
            enter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (login(
                        idText.getText(),
                        String.valueOf(passText.getPassword()))
                    ) {
                        panel.remove(loginP);

                        if (currentState == State.TLOG)
                            panel.add(createLogPanel());
                        else panel.add(createDashboardPanel());

                        panel.revalidate();
                        panel.repaint();
                        return;
                    }
                    error.setText("Unable to login. Id or Password is incorrect.");
                    idText.setText("");
                    passText.setText("");
                }
            });
            loginP.add(imgLabel);
            loginP.add(title);
            loginP.add(idLabel);
            loginP.add(idText);
            loginP.add(passLabel);
            loginP.add(passText);
            loginP.add(enter);
            loginP.add(error);
            return loginP;
        }

        public JPanel createDashboardPanel() {
            dashP = new JPanel();
            dashP.setBorder(padding);
            dashP.setLayout(new BoxLayout(dashP, BoxLayout.Y_AXIS));

	        JLabel title = new JLabel("Sensor Data");
	        JLabel weather = new JLabel("Weather/Precipitation");
            JLabel weatherData = new JLabel("");
	        if (tsnr.weather.data == 0)
                weatherData.setText("False");
	        else if (tsnr.weather.data == 1)
                weatherData.setText("True");
	        JLabel inclin = new JLabel("Inclination");
            JLabel inclinData = new JLabel(String.valueOf(tsnr.inclinometer.data));
	        JLabel speed = new JLabel("Speed");
            JLabel speedData = new JLabel(String.valueOf(tsnr.speed.data));
	        JLabel acc = new JLabel("Acceleration");
            JLabel accData = new JLabel(String.valueOf(tsnr.accelerometer.data));
	        JLabel obst = new JLabel("Current Obstacles");
	        JLabel obstData = new JLabel("");
            if(tsnr.infrared.data == 0)
                obstData.setText("False");
	        else if(tsnr.infrared.data == 1)
                obstData.setText("True");
	        JLabel trigger = new JLabel("Barrier Triggered");
	        JLabel triggerData = new JLabel("");
	        if (tsnr.weight.data == 0)
		        triggerData.setText("False");
	        else if (tsnr.weight.data == 1)
                triggerData.setText("True");
            JButton logout = new JButton("Logout");
            logout.addActionListener(logoutAction);

	        dashP.add(title);
            dashP.add(weather);
            dashP.add(weatherData);
            dashP.add(inclin);
            dashP.add(inclinData);
            dashP.add(speed);
            dashP.add(speedData);
            dashP.add(acc);
            dashP.add(accData);
            dashP.add(obst);
            dashP.add(obstData);
            dashP.add(trigger);
            dashP.add(triggerData);
            dashP.add(logout);
            return dashP;
        }

        public JPanel createLogPanel() {
            tlogP = new JPanel();

            tlogP.setBorder(padding);
            tlogP.setLayout(new BoxLayout(tlogP, BoxLayout.Y_AXIS));

            JTextArea logArea = new JTextArea();
            logArea.setBorder(padding);

            try {
                FileReader reader = new FileReader(log.path);
                logArea.read(reader, log.path);
            } catch (IOException e) {
                logArea = new JTextArea("Error: Unable to load '" + log.path + "'.");
            }

            JButton logout = new JButton("Logout");
            logout.addActionListener(logoutAction);

            tlogP.add(logArea);
            tlogP.add(logout);

            return tlogP;
        }

        public ActionListener logoutAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentState == State.TLOG) {
                    panel.remove(tlogP);
                } else {
                    panel.remove(dashP);
                }

                panel.add(createLoginPanel());
                panel.revalidate();
                panel.repaint();

                currentState = State.LOGIN;
            }
        };
    }

    class TSNR {
        private ArrayList<Sensor> sensors;
        private Sensor weather;
	    private Sensor inclinometer;
	    private Sensor speed;
	    private Sensor accelerometer;
	    private Sensor infrared;
	    private Sensor weight;

        public TSNR() {
            sensors = new ArrayList<Sensor>();
            weather = new Sensor("Weather");
            inclinometer = new Sensor("Inclinometer");
            speed = new Sensor("Speed");
            accelerometer = new Sensor("Accelerometer");
            infrared = new Sensor("Infrared");
            weight = new Sensor("Weight");
            sensors.add(weather);
            sensors.add(inclinometer);
            sensors.add(speed);
            sensors.add(accelerometer);
            sensors.add(infrared);
            sensors.add(weight);
        }

        public String getSensorReport() {
            String report = "";
            int i = 0;
            for (; i < sensors.size() - 1; i++)
                report += sensors.get(i).toString() + ",";
            report += sensors.get(i).toString();
            return report;
        }

	    /*public ArrayList<Sensor> sensorData() {
            return sensors;
	    }*/

    }

    class Sensor {
        private String name;
        private int data;
        private String issue;
        private State state;

        public Sensor(String name) {
            this.name = name;
            this.state = State.SAFE;
            this.data = 0;
            this.issue = "";
        }

        public String toString() {
            return (state == State.SAFE ? "+" : "-") + "S" + this.name + ": State-" + this.state + ";" + (issue.equals("") ? "" : " Issue-") + this.issue + ";";
        }

    }

    class Log {
        private BufferedWriter bw;
        private String path;

        private DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss ::");

        public Log(String path) {
            this.path = path;
            try {
                bw = new BufferedWriter(new FileWriter(this.path, true));
            } catch (IOException e) {
                System.err.println("Error: Failed to open '" + this.path + "'.");
            }
        }

        public Log() {
            this("iot.log");
        }

        public String fmessage(String sysState, String sensorReport) {
            String spacedSR = String.join("\n  ", sensorReport.split(","));
            return "System State: " + sysState + "\nSensor Report: {\n  " + spacedSR + "\n}";
        }

        public void write(String msg, boolean istimed) {
            String start = "";
            if (istimed)
                start = LocalDateTime.now().format(formatter)+"\t\t:: ";

            try {
                bw.append(start).append(msg).append("\n");
            } catch (IOException e) {
                System.err.println("Error: Could not write to '" + this.path + "'.");
            }
        }

        public void clear() throws IOException {
            bw.close();
            bw = new BufferedWriter(new FileWriter(this.path, false));
            bw.append("");
        }

        public void close() throws IOException {
            bw.close();
        }

        public void flush() throws IOException {
            bw.flush();
        }

        public void hline() throws IOException {
            bw.append("--------------------------------------------");
        }
    }

    protected int checkCreds(String[] creds) {
        if (creds[0].equals(techCreds[0]) && creds[1].equals(techCreds[1]))
            return 0;
        if (creds[0].equals(opCreds[0]) && creds[1].equals(opCreds[1]))
            return 1;
        return -1;
    }

    public boolean login(String id, String pass) {
        String[] creds = {id, pass};
        int account = checkCreds(creds);
        if (account == -1) return false;
        this.currentState = State.values()[account + 1];
        return true;
    }

    public State getCurrentState() {
        return this.currentState;
    }

    public int getWeatherData() {
        return this.tsnr.weather.data;
    }

    public int getInclinationData() {
        return this.tsnr.inclinometer.data;
    }

    public int getSpeedData() {
        return this.tsnr.speed.data;
    }

    public int getAccelerationData() {
        return this.tsnr.accelerometer.data;
    }

    public int getInfraredData() {
        return this.tsnr.infrared.data;
    }

}
