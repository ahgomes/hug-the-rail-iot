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
                timer = new Timer(100, this);
                timer.start();
                loginPane = createLoginPane();
                this.add(loginPane);
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (currentState == State.LOGIN) {


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

        		// if (source == timer)
        		// 	... update

                // ... collect ??

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
                        + "padding: 100px;"
                        + "box-sizing: border-box;"
                        + "}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                      + "<h1>HELLO</ht>"
                    + "</body>"
                  + "</html>"
                };

                textPane.setText(String.join("", html));

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

                JButton logout = new JButton("Logout");
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
            int id = 1;
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
            for (Sensor value : sensors.values())
                report += value.data + ";";
            return report;
        }

        public void collect() { // FIXME: Needs to set up data for update()
            if (currentState == State.LOGIN) return;

            // .. parse(str);
        }

	    public void parse(String data) {

            // data = ID+Name+T:Data
            //     W:[-50..150];[D|R|S]>[0..5]; < -40 || >120 || R 4 || S 3 warn
            //     O:[0..1000]; < 500 warn
            //     L:[S|F]; F warn
            //     S:[0..2000]; -> s = rpm * d * Math.PI * 60 / 63360
            //     I:[0..180]; > 8deg warin
            //     A:[-2..15]; > 12mph warn
            //

            String[] tokens = data.split("[+:;]");
            switch (tokens[2]) {
                case "W" :
                    String[] comps = tokens[3].split("[;>]");
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
                    obstacleDist = Integer.parseInt(tokens[3]);
                    break;
                case "L" :
                    barrDown = tokens[3].equals("S");
                    break;
                case "S" :
                    speed =
                        Integer.parseInt(tokens[3]) * 50 * Math.PI * 60/63360;
                    break;
                case "I" :
                    curveDeg = Integer.parseInt(tokens[3]);
                    break;
                case "A" :
                    acceleration = Integer.parseInt(tokens[3]);
                    break;
            }
        } // endof parse
    } // endof TSNR

    class Sensor { // FIXME maybe get rid of state
        private String name;
        private int id;
        private String data;
        private String issue;
        private State state;

        public Sensor(String name, int id) {
            this.name = name;
            this.id = id;
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
