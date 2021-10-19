package thread;

import model.FileModel;
import model.Message;
import view.ClientForm;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class ClientUploadRunnable implements Runnable {
    String address;
    int port;
    JProgressBar progressBar;
    ClientForm clientForm;
    Socket socket;
    File file;
    int count = 0;

    public ClientUploadRunnable(File file, String address, int port, JProgressBar progressBar, ClientForm clientForm) {
        this.address = address;
        this.port = port;
        this.progressBar = progressBar;
        this.clientForm = clientForm;
        this.file = file;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            FileModel fileModel = new FileModel(file.getName(), file.length());
            Message request = new Message("UPLOAD", fileModel);
            MessageController.send(socket, request);
            Message response = MessageController.receive(socket);
            if (response.getType().equals("ACCEPT_UPLOAD")) {
                SwingUtilities.invokeLater(() -> progressBar.setMaximum((int) file.length()));
                UploadFile(file.getAbsolutePath(), 0);
                Message success = MessageController.receive(socket);
                if (success.getType().equals("COMPLETE_UPLOAD"))
                    clientForm.showDialog("Upload completed");
            }
            Message disconnect = new Message("STOP_CONNECTION", "");
            MessageController.send(socket, disconnect);
            clientForm.getList();
        } catch (IOException | ExecutionException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    protected void UploadFile(String file,long serverFileSize) throws IOException{
        FileInputStream fis = new FileInputStream(file);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        fis.skip(serverFileSize);
        byte[] buffer = new byte[1024];
        while(fis.read(buffer) > 0){
            dos.write(buffer);
            count += 1024;
            SwingUtilities.invokeLater(() -> progressBar.setValue(count));
        }
        dos.flush();
        fis.close();
    }
}
