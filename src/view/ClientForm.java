package view;

import model.Message;
import thread.ClientDownloadRunnable;
import thread.ClientGetListFile;
import thread.ClientUploadRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

public class ClientForm extends JFrame {
    JPanel panel;
    JTextField serverField, portField, uploadField, downloadField;
    Button btnGetList, btnBrowseFileUpload, btnBrowseFolderDownload, btnUpload, btnDownload;
    JList fileList;
    JProgressBar uploadBar, downloadBar;
    ExecutorService executorService;
    JScrollPane scrollPane;

    public ClientForm() {
        InitComponent();
        executorService = Executors.newFixedThreadPool(5);
        this.add(panel);
        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setTitle("Client");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void InitComponent() {
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        serverField = new JTextField();
        serverField.setPreferredSize(new Dimension(100,20));
        serverField.setText("localhost");

        portField = new JTextField();
        portField.setPreferredSize(new Dimension(70,20));
        portField.setText("4000");

        uploadField = new JTextField();
        uploadField.setPreferredSize(new Dimension(200,20));
        uploadField.setEnabled(false);

        downloadField = new JTextField();
        downloadField.setPreferredSize(new Dimension(200,20));
        downloadField.setEnabled(false);

        btnGetList = new Button("Get list file");
        btnGetList.addActionListener((e) -> {
            try {
                getList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnBrowseFileUpload = new Button("Choose file");
        btnBrowseFileUpload.addActionListener(this::chooseFileUploadEvent);

        btnBrowseFolderDownload = new Button("Choose folder");
        btnBrowseFolderDownload.addActionListener(this::chooseFileDownloadEvent);

        btnUpload = new Button("Upload");
        btnUpload.addActionListener((e) -> {
            uploadBar.setValue(0);
            Runnable runnable = new ClientUploadRunnable(new File(uploadField.getText()), serverField.getText(), Integer.parseInt(portField.getText()), uploadBar, this);
            executorService.execute(runnable);
        });

        btnDownload = new Button("Download");
        btnDownload.addActionListener((e -> {
            if (fileList.getSelectedIndex() >= 0) {
                downloadBar.setValue(0);
                Runnable runnable = new ClientDownloadRunnable((String) fileList.getSelectedValue(), new File(downloadField.getText()), serverField.getText(), Integer.parseInt(portField.getText()), downloadBar, this);
                executorService.execute(runnable);
            }
        }));

        fileList = new JList();
        scrollPane = new JScrollPane();
        scrollPane.setSize(300, 300);
        scrollPane.add(fileList);

        uploadBar = new JProgressBar();
        uploadBar.setMinimum(0);

        downloadBar = new JProgressBar();
        downloadBar.setMinimum(0);

        panel.add(serverField);
        panel.add(portField);
        panel.add(btnGetList);
        panel.add(uploadBar);
        panel.add(uploadField);
        panel.add(btnBrowseFileUpload);
        panel.add(btnUpload);
        panel.add(downloadBar);
        panel.add(downloadField);
        panel.add(btnBrowseFolderDownload);
        panel.add(btnDownload);
        panel.add(scrollPane);
    }

    private void chooseFileUploadEvent(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int i = fileChooser.showOpenDialog(this);
        if (i == JFileChooser.APPROVE_OPTION) {
            uploadField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void chooseFileDownloadEvent(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int i = fileChooser.showOpenDialog(this);
        if (i == JFileChooser.APPROVE_OPTION) {
            downloadField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public void showDialog(String info) {
        JOptionPane.showMessageDialog(this, info);
    }

    public void getList() throws ExecutionException, InterruptedException, IOException, ClassNotFoundException {
        Callable<Message> getListCallable = new ClientGetListFile(serverField.getText(), Integer.parseInt(portField.getText()));
        Future<Message> future = executorService.submit(getListCallable);
        panel.remove(fileList);
        String[] list = (String[]) future.get().getPayload();
        DefaultListModel defaultListModel = new DefaultListModel();
        defaultListModel.addAll(Arrays.asList(list));
        fileList.setModel(defaultListModel);
        panel.add(fileList);
        panel.updateUI();
//        Socket socket = new Socket(serverField.getText(), Integer.parseInt(portField.getText()));
//        //ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//        //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//        Message request = new Message("GET_LIST", "");
//        MessageController.send(socket, request);
//        //oos.writeObject(request);
//        Message response = MessageController.receive(socket);
//        System.out.println(response);
//        panel.remove(fileList);
//        fileList= new JList((String[]) response.getPayload());
//        panel.add(fileList);
//        panel.updateUI();
//        Message stop_connection = new Message("STOP_CONNECTION", "");
//        //oos.writeObject(stop_connection);
//        MessageController.send(socket, stop_connection);
    }
}
