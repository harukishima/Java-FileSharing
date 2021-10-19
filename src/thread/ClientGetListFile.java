package thread;

import model.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ClientGetListFile implements Callable<Message> {
    String address;
    int port;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public ClientGetListFile(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public Message call() throws Exception {
        Socket socket = new Socket(address, port);
        Message request = new Message("GET_LIST", "");
        MessageController.send(socket, request);
        Message response = MessageController.receive(socket);
        Message stop_connection = new Message("STOP_CONNECTION", "");
        MessageController.send(socket, stop_connection);
        return response;
    }
}
