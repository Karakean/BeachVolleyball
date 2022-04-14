package app.beachvolleyball.communication;

import app.beachvolleyball.communication.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(9797)) {
            Socket socket = server.accept();
            System.out.println("Welcome: " + socket);
            Thread thread = new Thread(new ClientHandler(socket));
            thread.start();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}