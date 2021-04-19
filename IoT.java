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

public class IoT {
    private Display display;
    private TSNR tsnr;
    private Log log;
    private State currentState;

    private String[] opCreds = {"operator", "password"};
    private String[] techCreds = {"technician", "password"};

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

    public static void main(String[] args) throws IOException {
        IoT iot = new IoT();

        iot.display.open();

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

    }

    class Display {
        JFrame frame;
        JPanel panel;
        JPanel loginP;
        JPanel tlogP;
        JPanel dashP;

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

            Border paneEdge = BorderFactory.createEmptyBorder(10,10,10,10);
            loginP.setBorder(paneEdge);
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

            // TODO

            return dashP;
        }

        public JPanel createLogPanel() {
            tlogP = new JPanel();

            // TODO

            /*JTextArea logs = new JTextArea();
            FileReader reader = new FileReader("iot.log");
            logs.read(reader, "iot.log");*/

            return tlogP;
        }

        public void update() {
            // TODO
        }
    }

    class TSNR {
        private ArrayList<Sensor> sensors;

        public TSNR() {
            sensors = new ArrayList<Sensor>();

            // TODO add sensors
        }

        public String getSensorReport() {
            String report = "";
            int i = 0;
            for (; i < sensors.size() - 1; i++)
                report += sensors.get(i).toString() + ",";
            report += sensors.get(i).toString();
            return report;
        }

        // TODO Process sensor data

    }


    // TODO I'm not sure how we should set up Sensor class
    class Sensor {
        private String name;
        private String data;
        private String issue;
        private State state;

        public Sensor(String name) {
            this.name = name;
            this.state = State.SAFE;
            this.data = "";
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
            return "System State: " + sysState + ".\nSensor Report: {\n  " + spacedSR + "\n}";
        }

        public void write(String msg, boolean timed) {
            String start = "";
            if (timed) start = LocalDateTime.now().format(formatter)+"\t\t:: ";

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

    private int checkCreds(String[] creds) {
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

    // TODO add getters for testing
}
