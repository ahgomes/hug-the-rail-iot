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

import java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.GroupLayout.*;
import javax.swing.Timer;
import javax.swing.text.html.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class IoT {
    private IoT system = this;

    private Display display;
    private TSNR tsnr;
    private Log log;
    private State currentState;

    private String[] opCreds = {"o", "p"}; //{"operator", "password"};
    private String[] techCreds = {"t", "p"};  //{"technician", "password"};

    // For parsed sensor data
    private double temperature;
    private int precipitate;
    private int precipitateIntensity;
    private double obstacleDist;
    private int barrDown;
    private double curveDeg;
    private double acceleration;
    private ArrayList<Double> instantSpeeds;

    private static ArrayList<String> runInstructions = new ArrayList<String>();
    private static int currLineInd = 0;

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
        this.temperature= -1000;
        this.precipitate = -1;
        this.precipitateIntensity = -1;
        this.obstacleDist = -1;
        this.barrDown = -1;
        this.curveDeg = 0;
        this.acceleration = 0;
        this.instantSpeeds = new ArrayList<Double>();
    }

    public static void main(String[] args) {
        // read in isntructions file
        readRun();

        // Start IoT engine
        IoT iot = new IoT();
        iot.display.open();
        iot.currentState = State.LOGIN;
    }

    public static void readRun() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("run.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String text = line.split("#")[0].trim();
                if (!text.isEmpty()) runInstructions.add(text);
            }

        } catch (Exception e) {
            System.out.println(e);
            return;
        }
    }

    public static boolean update(IoT iot) {
        if (currLineInd == runInstructions.size()) {
            System.out.println("----- RUN FILE END ------");
            currLineInd++;
            return false;
        }

        if (currLineInd > runInstructions.size()) return false;

        String currLine = runInstructions.get(currLineInd);

        if (currLine.indexOf(">") != 0) {
            System.out.println(currLine);
            currLineInd++;
            return false;
        }

        while (currLine.indexOf(">") == 0) {
            String data = currLine.substring(1).trim();
            String name = data.split("[+]")[1];
            iot.tsnr.sensors.get(name).data = data;
            if (++currLineInd >= runInstructions.size()) break;
            currLine = runInstructions.get(currLineInd);
        }
        return true;
    }

    class Display {
        private JFrame frame;

        public Display() {
            frame = new JFrame("HRT IoT System");
        }

        public void open() {
            int width = 800, height = 600;
    		frame.setSize(width, height);
            frame.setResizable(false);
    		frame.add(new Window(width, height));
    		frame.setVisible(true);
    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        class Window extends JPanel implements ActionListener {
            private Timer timer;
            private Window mainPanel;
            private JTextPane loginPane;
            private JTextPane dashPane;
            private JPanel logPanel;

            public Window(int width, int height) {
                mainPanel = this;
                setSize(width, height);
                setBackground(Color.WHITE);
                setFocusable(true);
                timer = new Timer(1000, this);
                timer.start();
                loginPane = createLoginPane();
                this.add(loginPane);
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (currentState == State.STATION) {
                    System.out.println(currentState);
                }

                if (currentState == State.WARNING) {
                    System.out.println(currentState);
                }

                if (currentState == State.DANGER) {
                    System.out.println(currentState);
                }
            }

            @Override
        	public void actionPerformed(ActionEvent e) {
        		Object source = e.getSource();

        		if (source == timer)
                     system.update();

                // ... collect ??
                // ... analize ??

        		repaint();

        	}

            public JTextPane createLoginPane() {
                JTextPane textPane = new JTextPane();
                textPane.setContentType("text/html");
                textPane.setEditable(false);

                String imgsrc = this.getClass().getClassLoader()
                    .getResource("img/tracks.png").toString();

                String[] html = {""
                  + "<html>"
                    + "<head>"
                    + "<style>"
                      + "body {"
                        + "width: 650px; height: 500px;"
                        + "background: #fff; color: #000;"
                        + "padding: 100px;"
                        + "box-sizing: border-box;"
                      + "}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<img src=\"" + imgsrc +"\">"
                    + "<form action=\"#\">"
                      + "<label for=\"id\">Id:</label><br>"
                      + "<input type=\"text\" name=\"id\"><br>"
                      + "<label for=\"pass\">Password:</label><br>"
                      + "<input type=\"password\" name=\"pass\">"
                      + "<input type=\"submit\" value=\"Log In\">"
                    + "</form>"
                    + "<span>", "" ,"</span>"
                    + "</body>"
                  + "</html>"
                };

                textPane.setText(String.join("", html));

                HTMLEditorKit kit = (HTMLEditorKit)textPane.getEditorKit();
                kit.setAutoFormSubmission(false);
                textPane.addHyperlinkListener(new HyperlinkListener() {
                    @Override
                        public void hyperlinkUpdate(HyperlinkEvent e) {
                            if (e instanceof FormSubmitEvent) {
                                String data = ((FormSubmitEvent)e).getData();
                                String[] creds = data.split("[=&]");
                                if (login(creds[1], creds[3])) {
                                    log.open();
                                    log.hline();
                                    log.write("Login by " + creds[1], true);
                                    mainPanel.remove(loginPane);
                                    if (currentState == State.TLOG) {
                                        log.flush();
                                        logPanel = createLogPanel();
                                        mainPanel.add(logPanel);
                                        mainPanel.revalidate();
                                    } else {
                                        dashPane = createDashboardPane();
                                        mainPanel.add(dashPane);
                                    }
                                } else {
                                    html[1] = "Unable to log in. Invalid Id or Password.";
                                    textPane.setText(String.join("", html));
                                }
                            }
                        }
                });

                return textPane;
            } // endof createLoginPane

            public JTextPane createDashboardPane() {
                JTextPane textPane = new JTextPane();
                textPane.setContentType("text/html");
                textPane.setEditable(false);

                String[] html = {""
                  + "<html>"
                    + "<head>"
                    + "<style>"
                      + "body {"
                        + "width: 650px; height: 500px;"
                        + "background: #fff; color: #000;"
                        + "padding: 10px 50px;"
                        + "box-sizing: border-box;"
                      + "}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                      + "<h1>DASHBOARD</h1>"
                      + "<br>"
                    + "</body>"
                  + "</html>"
                };

                textPane.setText(String.join("", html));

                JButton logout = new JButton("Log Out");
                logout.addActionListener(logoutAction);
                textPane.insertComponent(logout);

                return textPane;

            } // endof createDashboardPane

            public JPanel createLogPanel() {
                JPanel panel = new JPanel();
                panel.setBackground(Color.WHITE);
                panel.setSize(800, 600);
                panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

                JTextArea logArea = new JTextArea();
                try {
                    FileReader reader = new FileReader(log.path);
                    logArea.read(reader, log.path);
                } catch (IOException e) {
                    logArea = new JTextArea("Error: Unable to load '" + log.path + "'.");
                }

                JScrollPane scrollPane = new JScrollPane(logArea);
                scrollPane.setBackground(Color.YELLOW);
                scrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.setPreferredSize(new Dimension(800, 500));
                scrollPane.setBorder(
                    BorderFactory.createCompoundBorder(
                      BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Log"),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                      ),
                    scrollPane.getBorder())
                );

                JButton logout = new JButton("Log Out");
                logout.addActionListener(logoutAction);

                panel.add(scrollPane);
                panel.add(Box.createRigidArea(new Dimension(0,10)));
                panel.add(logout);

                return panel;

            } // endof createLogPanel

            public ActionListener logoutAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (currentState == State.TLOG) {
                        mainPanel.remove(logPanel);
                    } else {
                        mainPanel.remove(dashPane);
                    }

                    log.hline();
                    log.close();
                    loginPane = createLoginPane();
                    mainPanel.add(loginPane);
                    mainPanel.revalidate();

                    currentState = State.LOGIN;
                }
            };

        } // endof Window
    } // endof Display

    class TSNR { // FIXME might not need map if have id
        private HashMap<String, Sensor> sensors;
        private Sensor weather;
	    private Sensor inclinometer;
	    private Sensor speedS;
	    private Sensor accelerometer;
	    private Sensor infrared;
	    private Sensor weight;

        public TSNR() {
            int id = 0;
            sensors = new HashMap<String, Sensor>();
            weather = new Sensor("Weather", id++);
            inclinometer = new Sensor("Inclinometer", id++);
            speedS = new Sensor("Speed", id++);
            accelerometer = new Sensor("Accelerometer", id++);
            infrared = new Sensor("Infrared", id++);
            weight = new Sensor("Weight", id++);
            sensors.put(weather.name, weather);
            sensors.put(inclinometer.name, inclinometer);
            sensors.put(speedS.name, speedS);
            sensors.put(accelerometer.name, accelerometer);
            sensors.put(infrared.name, infrared);
            sensors.put(weight.name, weight);
        }

        public String getSensorReport() {
            String report = "";
            for (Sensor s : sensors.values())
                report += s.data + ";";
            return report;
        }

        public void collect() { // FIXME: Needs to set up data for update()
            for (Sensor s : sensors.values()) {
                parse(s);
            }
        }

	    public void parse(Sensor sensor) { // data format ID+NAME+TYPE:DATA
            String[] tokens = sensor.data.split("[+:]");
            if (tokens.length <= 3) {
                System.out.println("Error: S-" + sensor.id + " " + sensor.name + " missing data.");
                return;
            }
            switch (tokens[2]) {
                case "W" :
                    String[] comps = tokens[3].split("[;>]");
                    temperature= Double.parseDouble(comps[0]);
                    precipitate = (
                        comps[1].equals("R") ? 1 :
                        comps[1].equals("S") ? 2 : 0
                    );
                    precipitateIntensity = (
                        comps[1].equals("D") ? 0 : Integer.parseInt(comps[2])
                    );
                    break;
                case "O" :
                    obstacleDist = Double.parseDouble(tokens[3]);
                    break;
                case "L" :
                    barrDown = tokens[3].equals("S") ? 1 : 0;
                    break;
                case "S" :
                    instantSpeeds.add(
                        Double.parseDouble(tokens[3]) * 50 * Math.PI
                        * 60 / 63360
                    );
                    if (instantSpeeds.size() > 10) instantSpeeds.remove(0);
                    break;
                case "I" :
                    curveDeg = Double.parseDouble(tokens[3]);
                    break;
                case "A" :
                    acceleration = Double.parseDouble(tokens[3]);
                    break;
            }
        } // endof parse
    } // endof TSNR

    class Sensor { // FIXME
        private String name;
        private int id;
        private String data;

        public Sensor(String name, int id) {
            this.name = name;
            this.id = id;
            this.data = "";
        }

        public String toString() {
            // TODO
            return "";
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
    } // endof Log

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

    public int[] analize() {
        int dangerLevel = 0;
        int sensorDL = 0;
        int[] sensorDLList = new int[tsnr.sensors.size() + 1];

        // Weather check
        if (temperature > -1000) { //FIXME change variable names too long
            int psum = precipitate + precipitateIntensity;
            if (psum > 3 || temperature < -30 || temperature > 120)
                sensorDL = ladd(sensorDL);
            if (psum > 5 || temperature < -50 || temperature > 150)
                sensorDL = ladd(sensorDL);
            dangerLevel = Math.max(dangerLevel, sensorDL);
            sensorDLList[tsnr.sensors.get("Weather").id] = sensorDL;
            sensorDL = 0;
        }

        // Infrared obstacle check
        if (obstacleDist > -1) {

            if (obstacleDist <= 500)
                sensorDL = ladd(sensorDL);
            if (obstacleDist <= 250)
                sensorDL = ladd(sensorDL);

            dangerLevel = Math.max(dangerLevel, sensorDL);
            sensorDLList[tsnr.sensors.get("Infrared").id] = sensorDL;
            sensorDL = 0;
        }

        // Weight sensor barrier check
        if (barrDown > -1) {

            if (barrDown == 0)
                sensorDL = 2;

            dangerLevel = Math.max(dangerLevel, sensorDL);
            sensorDLList[tsnr.sensors.get("Weight").id] = sensorDL;
            sensorDL = 0;
        }

        // Inclinometer curve angle check
        if (curveDeg != 0) {

            if (curveDeg > 8)
                sensorDL = ladd(sensorDL);
            if (curveDeg > 20)
                sensorDL = ladd(sensorDL);

            dangerLevel = Math.max(dangerLevel, sensorDL);
            sensorDLList[tsnr.sensors.get("Inclinometer").id] = sensorDL;
            sensorDL = 0;
        }

        // Accelerometer check
        if (acceleration != 0) {

            if (acceleration > 12)
                sensorDL = ladd(sensorDL);
            if (acceleration > 20)
                sensorDL = ladd(sensorDL);

            dangerLevel = Math.max(dangerLevel, sensorDL);
            sensorDLList[tsnr.sensors.get("Accelerometer").id] = sensorDL;
            sensorDL = 0;
        }

        // Wheel slippage check
        if (instantSpeeds.size() >= 2) {

            double wslip = wheelSlippage();

            if (wslip > 2)
                sensorDL = ladd(sensorDL);
            if (wslip > 5)
                sensorDL = ladd(sensorDL);

            dangerLevel = Math.max(dangerLevel, sensorDL);
            sensorDLList[tsnr.sensors.size()] = sensorDL;
            sensorDL = 0;
        }

        currentState = State.values()[dangerLevel + 4];
        return sensorDLList;

    } // endof analize

    public int ladd(int n) {
        return n + (n <= 1 ? 1 : 0);
    }

    public double wheelSlippage() {
        double avgWheelAcc = 0;
        for (int i = 1; i < instantSpeeds.size(); i++) {
            avgWheelAcc += instantSpeeds.get(i) - instantSpeeds.get(i - 1);
        }
        avgWheelAcc /= instantSpeeds.size();

        return avgWheelAcc - acceleration;
    }

    public void update() {
        if (!IoT.update(system))
            return;

        tsnr.collect();
        int[] analizedData = analize();
        for (int i = 0; i < analizedData.length; i++) {
            System.out.println("i: " + i + " => dl: " + analizedData[i]);
        }

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

} // endof IoT
