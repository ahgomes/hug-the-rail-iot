/**
 *  IoT.java runs a simulation of the Hug the Rails IoT system as defined in
 *  the IoT HRT Project Document.
 *
 *  I pledge my honor that I have abided by the Stevens Honor System.
 *
 *  @author Adrian Gomes, Aliya Iqbal, Amraiza Naz, and Matthew Cunningham
 *  @version 1.0
 *  @since 2021-04-26
 */

// Imports for lists and parsing
import java.util.*;
import java.util.regex.Pattern;

// Imports for logging
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Imports for GUI
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

    /**
     * Reads in the instructions for the running of IoT from run.txt.
     */
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

    /**
     * Updates sensor data based on run instructions and prints to console.
     * @param IoT the current IoT system
     * @return boolean whether or not sensor data was updated
     */
    public static boolean update(IoT iot) {
        if (iot.currentState == State.TLOG || iot.currentState == State.LOGIN)
            return false;

        if (currLineInd == runInstructions.size()) {
            System.out.println("----- RUN FILE END ------");
            currLineInd++;
            return false;
        }

        if (currLineInd > runInstructions.size())
            return false;

        if (currLineInd == 0)
            System.out.println("----- RUN FILE START ------");

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

    /**
     * Creates GUI and used to present data to user.
     */
    class Display {
        private JFrame frame;

        public Display() {
            frame = new JFrame("HRT IoT System");
        }

        /**
         * Sets up properties of the JFrame and adds a Window to it. Then opens * the GUI to be visible by the user.
         */
        public void open() {
            int width = 800, height = 600;
    		frame.setSize(width, height);
            frame.setResizable(false);
    		frame.add(new Window(width, height));
    		frame.setVisible(true);
    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        /**
         * Creates the window to be added to the Display.
         */
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

            /**
             * Tells repaint what to do when run.
             */
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

                // TODO

            }

            /**
             * Updates and repaints window.
             */
            @Override
        	public void actionPerformed(ActionEvent e) {
        		Object source = e.getSource();

        		if (source == timer)
                     system.update();

        		repaint();
        	}

            /**
             * Creates Login page with full login functionality.
             * @return JTextPane the page to be added to the Window.
             */
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
                    + "<img src=\"" + imgsrc + "\">"
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

                /*
                 * Actived when user hits login. Decides whether to open log
                 * log file, dashboard, or send authorization failure to user.
                 */
                HTMLEditorKit kit = (HTMLEditorKit)textPane.getEditorKit();
                kit.setAutoFormSubmission(false);
                textPane.addHyperlinkListener(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        if (e instanceof FormSubmitEvent) {
                            String data = ((FormSubmitEvent)e).getData();
                            String[] creds = data.split("[=&]");
                            if (
                                creds.length > 3
                                && login(creds[1], creds[3])
                            ) {
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
                                    tsnr.diagnostic();
                                }
                            } else {
                                html[1] = "Unable to log in. Invalid Id or Password.";
                                textPane.setText(String.join("", html));
                            }
                        }
                    }
                }); // endof textPane.addHyperlinkListener
                return textPane;
            } // endof createLoginPane

            /**
             * Creates Dashboard page with list of sensors to be used for
             * displaying warnings to user.
             * @return JTextPane the page to be added to the Window.
             */
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

            /**
             * Creates log file page from iot.log for technician use.
             * @return JPanel the page to be added to the Window.
             */
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

            /*
             * Actived when user hits logout on either the dashboard or the
             * log file page. Brings the user back to the login page.
             */
            public ActionListener logoutAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (currentState == State.TLOG) {
                        mainPanel.remove(logPanel);
                    } else {
                        mainPanel.remove(dashPane);
                    }

                    loginPane = createLoginPane();
                    mainPanel.add(loginPane);
                    mainPanel.revalidate();

                    currentState = State.LOGIN;
                }
            };
        } // endof Window
    } // endof Display

    /**
     * Processes and sends data from Sensors to IoT
     */
    class TSNR {
        private LinkedHashMap<String, Sensor> sensors;
        private Sensor weather;
	    private Sensor inclinometer;
	    private Sensor speedS;
	    private Sensor accelerometer;
	    private Sensor infrared;
	    private Sensor weight;

        public TSNR() {
            int id = 0;
            sensors = new LinkedHashMap<String, Sensor>();
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

        /**
         * Checks the state of each sensor and logs its state.
         */
        public void diagnostic() {
            int[] data = analize();
            log.write("Ran diagnostic.", true);
            int len = data.length;
            for (int i = 0; i < len - 1; i++) {
                String logMsg = "  * ";
                Sensor s = (Sensor)tsnr.sensors.values().toArray()[i];
                logMsg += "S-" + s.id + " " + s.name + ": ";
                logMsg += State.values()[data[i] + 4];
                log.write(logMsg, false);
            }
            log.flush();
        }

        /**
         * Collects data from sensors and sends to the the parser.
         */
        public void collect() {
            for (Sensor s : sensors.values())
                parse(s);
        }

        /**
         * Parses sensor data and updates IoT variables for each piece of data.
         */
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

    /**
     * Houses Sensor information: name, id, and its data.
     */
    class Sensor {
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

    /**
     * Used for all interations with log file: iot.log or other path from input.
     */
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

        /**
         * Writes to the log file from message with or without time stamp.
         * @param String message to be written to log
         * @param boolean if message needs timestamp
         */
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

        /**
         * Clears the log file.
         */
        public void clear() {
            try {
                bw.close();
                bw = new BufferedWriter(new FileWriter(this.path, false));
                bw.append("");
            } catch (IOException e) {
                System.err.println("Error: Unable to clear '" + this.path + "'.");
            }
        }

        /**
         * Opens the log file for reading.
         */
        public void open() {
            try {
                bw = new BufferedWriter(new FileWriter(this.path, true));
            } catch (IOException e) {
                System.err.println("Error: Failed to open '" + this.path + "'.");
            }
        }

        /**
         * Closes the log file.
         */
        public void close() {
            try {
                bw.close();
            } catch (IOException e) {
                System.err.println("Error: Unable to close '" + this.path + "'.");
            }
        }

        /**
         * Flushes log file.
         */
        public void flush() {
            try {
                bw.flush();
            } catch (IOException e) {
                System.err.println("Error: Unable to flush '" + this.path + "'.");
            }
        }

        /**
         * Writes a horizontal line divider to log file.
         */
        public void hline() {
            try {
                bw.append("------------------------------------------------\n");
            } catch (IOException e) {

            }
        }
    } // endof Log

    /**
     * Validates credentials againts those stored in IoT for
     * the operator and technician.
     * @param String[] credentials to be checked
     * @return int for credential match
     */
    protected int checkCreds(String[] creds) {
        if (creds[0].equals(techCreds[0]) && creds[1].equals(techCreds[1]))
            return 0;
        if (creds[0].equals(opCreds[0]) && creds[1].equals(opCreds[1]))
            return 1;
        return -1;
    }

    /**
     * Validates whether login was successful and updates IoT current state.
     * @param String user id
     * @param String user password
     * @return boolean login successful or not
     */
    public boolean login(String id, String pass) {
        String[] creds = {id, pass};
        int account = checkCreds(creds);
        if (account == -1) return false;
        this.currentState = State.values()[account + 1];
        return true;
    }

    /**
     * Checks the safety of each data point from sensors and updates state of
     * IoT depending on certain criteria.
     * @return int[] danger levels of each sensor or event
     */
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

        if (currentState != State.LOGIN)
            currentState = State.values()[dangerLevel + 4];
        return sensorDLList;

    } // endof analize

    /**
     * Limit add where increments until n is 2.
     * @param int number to be add to
     * @return int sum
     */
    public int ladd(int n) {
        return n + (n <= 1 ? 1 : 0);
    }

    /**
     * Uses data from multiple senors to decide the potential of wheel slippage.
     * @return double difference between acceleration of wheels and train
     */
    public double wheelSlippage() {
        double avgWheelAcc = 0;
        for (int i = 1; i < instantSpeeds.size(); i++)
            avgWheelAcc += instantSpeeds.get(i) - instantSpeeds.get(i - 1);
        avgWheelAcc /= instantSpeeds.size();
        return avgWheelAcc - acceleration;
    }

    /**
     * Upadates IoT based on collected and analized data from sensors and logs
     * any issues or warnings.
     */
    public void update() {
        if (!IoT.update(system))
            return;

        tsnr.collect();
        int[] analizedData = analize();

        // Log warning
        System.out.println("logging");
        log.write(currentState.toString(), true);

        int len = analizedData.length;
        for (int i = 0; i < len; i++) {
            if (analizedData[i] > 0) {
                String logMsg = "  * ";
                if (i < len - 1) {
                    Sensor s = (Sensor)tsnr.sensors.values().toArray()[i];
                    logMsg += "S-" + s.id + " " + s.name + ": ";
                } else {
                    logMsg += "Potential wheel slippage: ";
                }

                logMsg += State.values()[analizedData[i] + 4];
                log.write(logMsg, false);
            }
        }
        log.flush();

        // TODO deal with crashing

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
