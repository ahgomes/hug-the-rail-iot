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

import java.util.*;
import java.util.regex.Pattern;
import java.util.Timer;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.GroupLayout.*;

public class IoT {
    private Display display;
    private TSNR tsnr;
    private Log log;
    private State currentState;

    private String[] opCreds = {"o", "p"}; //{"operator", "password"};
    private String[] techCreds = {"t", "p"};  //{"technician", "password"};

    private int temp = -1;
    private int precipitate = -1;
    private int precipitateIntensity = -1;
    private int obstacleDist = -1;
    private boolean barrDown = false;
    private double speed = 0;
    private int curveDeg = 0;
    private int acceleration = 0;

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

    /*public boolean warning() {
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
    }*/

    public static void main(String[] args) {
        IoT iot = new IoT();

        iot.log.clear();
        iot.display.open();
        iot.currentState = State.LOGIN;
    }


        ////////////// TODO //////////////
        // Log in state -> TLOG                               x
            // Change display to Log File                     x
            // Flush log file                                 x
            // Display log file                               x
            // Log out                                        x
        // Log in state -> STATION                            x
            // Change display to Dashboard                    x
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
        ArrayList<JLabel> dataLabels;
        JLabel collectLabel;

        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);

        public Display() {
            frame = new JFrame("Hug The Rails IoT System");
            panel = new JPanel();
            panel.setPreferredSize(new Dimension(800, 600));
            panel.setLayout(new BorderLayout());
            panel.setBackground(Color.white);

            dataLabels = new ArrayList<JLabel>();
            collectLabel = new JLabel();
        }

        public void open() {
            panel.add(createLoginPanel());
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }

        public void update() {
            // TODO
        }

        public JPanel createLoginPanel() {
            loginP = new JPanel();
            loginP.setBorder(padding);

            JLabel imgLabel = new JLabel(new ImageIcon("img/tracks.png"));
            JLabel title = new JLabel("Login");
            JLabel idLabel = new JLabel("Id");
            idLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            JTextField idText = new JTextField(1);
            idText.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            JLabel passLabel = new JLabel("Password");
            passLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            JPasswordField passText = new JPasswordField(2);
            passText.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            passText.setPreferredSize(new Dimension(1000, 24));
            JButton enter = new JButton("Login");
            JLabel error = new JLabel("Welcome!! Login to access Hug The Rails IoT System.");
            enter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (login(
                        idText.getText(),
                        String.valueOf(passText.getPassword()))
                    ) {
                        log.open();
                        log.hline();
                        log.write("Login by " + idText.getText(), true);
                        panel.remove(loginP);

                        if (currentState == State.TLOG) {
                            log.flush();
                            panel.add(createLogPanel());
                        } else panel.add(createDashboardPanel());

                        panel.revalidate();
                        panel.repaint();

                        /*SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (currentState == State.TLOG) return;
                                tsnr.collect();
                            }
                        });*/
                        return;
                    }
                    error.setText("Unable to login. Id or Password is incorrect.");
                    idText.setText("");
                    passText.setText("");
                }
            });

            GroupLayout layout = new GroupLayout(loginP);
            loginP.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup (
                    layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(idLabel)
                    .addComponent(passLabel)
                )
                .addGroup (
                    layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(imgLabel)
                    .addComponent(idText, 200, 300, 400)
                    .addComponent(passText, 200, 300, 400)
                    .addComponent(title)
                    .addComponent(enter)
                    .addComponent(error)
                )
            );

            layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(imgLabel)
                .addComponent(title)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(idLabel)
                    .addComponent(idText)
                )
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passLabel)
                    .addComponent(passText)
                )
                .addComponent(enter)
                .addComponent(error)
            );

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

            JLabel state = new JLabel("State: " + currentState);
	        JLabel title = new JLabel("Sensor Data");
	        JLabel weather = new JLabel("Weather/Precipitation");
            JLabel weatherData = new JLabel("");
            dataLabels.add(weatherData);
	        if (tsnr.weather.data == "0")
                weatherData.setText("False");
	        else if (tsnr.weather.data == "1")
                weatherData.setText("True");
	        JLabel inclin = new JLabel("Inclination");
            JLabel inclinData = new JLabel(tsnr.inclinometer.data);
            dataLabels.add(inclinData);
	        JLabel speedL = new JLabel("Speed");
            JLabel speedData = new JLabel(tsnr.speedS.data);
            dataLabels.add(speedData);
	        JLabel acc = new JLabel("Acceleration");
            JLabel accData = new JLabel(tsnr.accelerometer.data);
            dataLabels.add(accData);
	        JLabel obst = new JLabel("Current Obstacles");
	        JLabel obstData = new JLabel("");
            dataLabels.add(obstData);
            if(tsnr.infrared.data == "0")
                obstData.setText("False");
	        else if(tsnr.infrared.data == "1")
                obstData.setText("True");
	        JLabel trigger = new JLabel("Barrier Triggered");
	        JLabel triggerData = new JLabel("");
            dataLabels.add(triggerData);
	        if (tsnr.weight.data == "0")
		        triggerData.setText("False");
	        else if (tsnr.weight.data == "1")
                triggerData.setText("True");
            JButton logout = new JButton("Logout");
            logout.addActionListener(logoutAction);
            JLabel collectLabel = new JLabel("Collecting sensor data ...");

            dashP.add(state);
	        dashP.add(title);
            dashP.add(weather);
            dashP.add(weatherData);
            dashP.add(inclin);
            dashP.add(inclinData);
            dashP.add(speedL);
            dashP.add(speedData);
            dashP.add(acc);
            dashP.add(accData);
            dashP.add(obst);
            dashP.add(obstData);
            dashP.add(trigger);
            dashP.add(triggerData);
            dashP.add(logout);
            dashP.add(collectLabel);
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

                log.hline();
                log.close();
                panel.add(createLoginPanel());
                panel.revalidate();
                panel.repaint();

                currentState = State.LOGIN;
            }
        };
    }

    class TSNR {
        private HashMap<String, Sensor> sensors;
        private Sensor weather;
	    private Sensor inclinometer;
	    private Sensor speedS;
	    private Sensor accelerometer;
	    private Sensor infrared;
	    private Sensor weight;

        public TSNR() {
            sensors = new HashMap<String, Sensor>();
            weather = new Sensor("Weather");
            inclinometer = new Sensor("Inclinometer");
            speedS = new Sensor("Speed");
            accelerometer = new Sensor("Accelerometer");
            infrared = new Sensor("Infrared");
            weight = new Sensor("Weight");
            sensors.put(weather.name, weather);
            sensors.put(inclinometer.name, inclinometer);
            sensors.put(speedS.name, speedS);
            sensors.put(accelerometer.name, accelerometer);
            sensors.put(infrared.name, infrared);
            sensors.put(weight.name, weight);
        }

        public String getSensorReport() {
            String report = "";
            for (Sensor value : sensors.values())
                report += value.data + ";";
            return report;
        }

        public void collect() { // FIXME: Needs to set up data for update()
            if (currentState == State.LOGIN) return;
            //System.out.print("Sensor Data -> ");
            display.collectLabel.setText("Collecting sensor data...");

            String str = "";
            /*try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                str = reader.readLine();
            } catch (IOException e) {

            }*/

            parse(str);
            display.update();
            display.panel.revalidate();
            display.panel.repaint();

            /*Timer timer = new Timer();
        	timer.schedule(new TimerTask(){
        		public void run(){
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (currentState == State.TLOG) return;
                            tsnr.collect();
                        }
                    });
        		}
        	}, 1, 500);*/
        }

	    public void parse(String data) {

            // data = Name+T:Data
            //     W:[-50..150];[D|R|S]>[0..5]; < -40 || >120 || R 4 || S 3 warn
            //     O:[0..1000]; < 500 warn
            //     L:[S|F]; F warn
            //     S:[0..2000]; -> s = rpm * d * Math.PI * 60 / 63360
            //     I:[0..180]; > 8deg warin
            //     A:[-2..15]; > 12mph warn
            //

            String[] tokens = data.split("[+:;]");
            switch (tokens[1]) {
                case "W" :
                    String[] comps = tokens[2].split("[;>]");
                    temp = Integer.parseInt(comps[0]);
                    precipitate = (
                        comps[1].equals("R") ? 1 :
                        comps[1].equals("S") ? 2 : 0
                    );
                    precipitateIntensity = (
                        comps[1].equals("D") ? 0 : Integer.parseInt(comps[2])
                    );
                    break;
                case "O" :
                    obstacleDist = Integer.parseInt(tokens[2]);
                    break;
                case "L" :
                    barrDown = tokens[2].equals("S");
                    break;
                case "S" :
                    speed =
                        Integer.parseInt(tokens[2]) * 50 * Math.PI * 60/63360;
                    break;
                case "I" :
                    curveDeg = Integer.parseInt(tokens[2]);
                    break;
                case "A" :
                    acceleration = Integer.parseInt(tokens[2]);
                    break;
            }
        }

    }

    class Sensor {
        private String name;
        private String data;
        private String issue;
        private State state;

        public Sensor(String name) {
            this.name = name;
            this.state = State.SAFE;
            this.data = "1";
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
            open();
        }

        public Log() {
            this("iot.log");
        }

        public String fmessage(String sysState, String sensorReport) {
            String spacedSR = String.join("\n  ", sensorReport.split(";"));
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

        public void clear() {
            try {
                bw.close();
                bw = new BufferedWriter(new FileWriter(this.path, false));
                bw.append("");
            } catch (IOException e) {
                System.err.println("Error: Unable to clear '" + this.path + "'.");
            }

        }

        public void open() {
            try {
                bw = new BufferedWriter(new FileWriter(this.path, true));
            } catch (IOException e) {
                System.err.println("Error: Failed to open '" + this.path + "'.");
            }
        }

        public void close() {
            try {
                bw.close();
            } catch (IOException e) {
                System.err.println("Error: Unable to close '" + this.path + "'.");
            }
        }

        public void flush() {
            try {
                bw.flush();
            } catch (IOException e) {
                System.err.println("Error: Unable to flush '" + this.path + "'.");
            }
        }

        public void hline() {
            try {
                bw.append("------------------------------------------------\n");
            } catch (IOException e) {

            }
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

    public String getWeatherData() {
        return this.tsnr.weather.data;
    }

    public String getInclinationData() {
        return this.tsnr.inclinometer.data;
    }

    public String getSpeedData() {
        return this.tsnr.speedS.data;
    }

    public String getAccelerationData() {
        return this.tsnr.accelerometer.data;
    }

    public String getInfraredData() {
        return this.tsnr.infrared.data;
    }

}
