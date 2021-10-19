package thread;

import model.FileModel;
import model.Message;
import view.ClientForm;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientDownloadRunnable implements Runnable{
    String address;
    int port;
    JProgressBar progressBar;
    ClientForm clientForm;
    Socket socket;
    String file;
    File folder;
    int count = 0;

    public ClientDownloadRunnable(String file, File folder, String address, int port, JProgressBar progressBar, ClientForm clientForm) {
        this.address = address;
        this.port = port;
        this.folder = folder;
        this.progressBar = progressBar;
        this.clientForm = clientForm;
        this.file = file;
    }


    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            Message request = new Message("DOWNLOAD", file);
            MessageController.send(socket, request);
            Message response = MessageController.receive(socket);
            if (response.getType().equals("ACCEPT_DOWNLOAD")) {
                FileModel fileModel = (FileModel) response.getPayload();
                File file = new File(folder, fileModel.filename);
                writeBytesToFile(fileModel.fileSize, file.getAbsolutePath(), 0);
                Message completed = MessageController.receive(socket);
                if (completed.getType().equals("DOWNLOAD_COMPLETED")) {
                    clientForm.showDialog("Download completed");
                }
            }
            Message disconnect = new Message("STOP_CONNECTION", "");
            MessageController.send(socket, disconnect);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void writeBytesToFile(long fileSize, String fileName,long buploAded) throws IOException {
        FileOutputStream fos;
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        if(buploAded == 0)
            fos = new FileOutputStream(fileName, false);
        else
            fos = new FileOutputStream(fileName, true);
        try {
            byte[] buffer = new byte[1024];
            int read = 0;
            long totalRead = buploAded;
            int remaining = (int) fileSize;
            //To determine the percentage remaining to download file
            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                count += read;
                SwingUtilities.invokeLater(() -> progressBar.setValue(count));
                remaining -= read;
                System.out.print("\rDownloading file - "+ (int)((double)(totalRead)/fileSize * 100)+"% complete");
                fos.write(buffer, 0, read);
            }
        }catch (Exception e){
            System.out.println("ERROR: " + e.getMessage());
        }finally {
            fos.flush();  fos.close();
        }
    }
}
