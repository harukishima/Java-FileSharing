package thread;

import model.FileModel;
import model.Message;
import run.ServerProgram;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ClientManageThread implements Runnable {
    Socket socket;
    Thread t;
    boolean isRunning;

    public ClientManageThread(Socket socket) {
        this.socket = socket;
        t = new Thread(this);
    }

    public void startThread() throws IOException {
        //initStream();
        isRunning = true;
        t.start();
    }

    public void stopThread() throws IOException {
        isRunning = false;
        socket.close();
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                System.out.println("Waiting for " + socket.toString());
                Message message = MessageController.receive(socket);
                System.out.println(message);
                switch (message.getType()) {
                    case "GET_LIST" -> sendListFile();
                    case "UPLOAD" -> uploadFile(message);
                    case "DOWNLOAD" -> downloadFile(message);
                    case "STOP_CONNECTION" -> stopThread();
                    default -> {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendListFile() throws IOException {
        File folder = ServerProgram.root;
        Message message = new Message("ACCEPT_SEND_LIST", folder.list());
        //oos.writeObject(message);
        MessageController.send(socket, message);
    }

    private void uploadFile(Message message) throws IOException {
        File folder = ServerProgram.root;
        String[] listFiles = folder.list();
        FileModel fileModel = (FileModel) message.getPayload();

        assert listFiles != null;
        if (!Arrays.asList(listFiles).contains(fileModel.filename)) {
            Message accept = new Message("ACCEPT_UPLOAD", fileModel);
            MessageController.send(socket, accept);
            File file = new File(folder, fileModel.filename);
            writeBytesToFile(fileModel.fileSize, file.getAbsolutePath(), 0);
            Message completed = new Message("COMPLETE_UPLOAD", "");
            MessageController.send(socket, completed);
        }
    }

    protected void writeBytesToFile(long fileSize, String fileName,long buploAded) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        FileOutputStream fos;
        if(buploAded == 0)
            fos = new FileOutputStream(fileName, false);
        else
            fos = new FileOutputStream(fileName, true);
        try {
            int rx = (int) fileSize;
            int read = 0;
            long tn = buploAded;
            byte[] cz = new byte[1024];
            while((read = dis.read(cz, 0, Math.min(cz.length, rx))) > 0) {
                tn += read;rx -= read;
                System.out.println("\r Uploading File - "+ (int)((double)(tn)/fileSize * 100)+"% complete");
                fos.write(cz,0,read);
            }
        }catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }finally {
            fos.flush();  fos.close();
        }
    }

    private void downloadFile(Message message) throws IOException {
        File folder = ServerProgram.root;
        String[] listFiles = folder.list();
        File requestFile = new File(folder, (String) message.getPayload());
        FileModel fileModel = new FileModel(requestFile.getName(), requestFile.length());

        assert listFiles != null;
        if (Arrays.asList(listFiles).contains(fileModel.filename)) {
            Message accept = new Message("ACCEPT_DOWNLOAD", fileModel);
            MessageController.send(socket, accept);
            File file = new File(folder, fileModel.filename);
            UploadFile(file.getAbsolutePath());
            Message completed = new Message("DOWNLOAD_COMPLETED", "");
            MessageController.send(socket, completed);
        }
    }

    protected void UploadFile(String f) throws IOException{
        FileInputStream fis = new FileInputStream(f);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        byte[] cz = new byte[1024];
        while(fis.read(cz) > 0)
            dos.write(cz);
        dos.flush();
        fis.close();
    }
}
