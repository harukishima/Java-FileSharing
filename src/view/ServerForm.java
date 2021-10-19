package view;

import thread.AcceptThread;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ServerForm extends JFrame {
    JPanel panel;
    JTextField portField;
    Button btnStartServer, btnStopServer;
    AcceptThread acceptThread;
    public ServerForm() {
        InitComponent();
        this.add(panel);
        this.setSize(300,100);
        this.setTitle("Server");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void InitComponent() {
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        portField = new JTextField();
        portField.setPreferredSize(new Dimension(100, 40));
        portField.setText("4000");

        btnStartServer = new Button("Start");
        btnStartServer.addActionListener((e) -> {
            btnStartServer.setEnabled(false);
            btnStopServer.setEnabled(true);
            acceptThread = new AcceptThread(Integer.parseInt(portField.getText()));
            try {
                acceptThread.startServer();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        btnStopServer = new Button("Stop");
        btnStopServer.setEnabled(false);
        btnStopServer.addActionListener((e) -> {
            btnStopServer.setEnabled(false);
            btnStartServer.setEnabled(true);
            try {
                acceptThread.stopServer();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        panel.add(portField);
        panel.add(btnStartServer);
        panel.add(btnStopServer);
    }
}
