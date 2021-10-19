package thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptThread implements Runnable {
    Thread t;
    ServerSocket serverSocket;
    int port;
    boolean isRunning = false;

    public AcceptThread(int port){
        this.port = port;
        t = new Thread(this);
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;
        t.start();
    }

    public void stopServer() throws IOException {
        serverSocket.close();
        isRunning = false;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                System.out.println("Server is running at " + port);
                Socket newSocket = serverSocket.accept();
                ClientManageThread clientManageThread = new ClientManageThread(newSocket);
                clientManageThread.startThread();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
