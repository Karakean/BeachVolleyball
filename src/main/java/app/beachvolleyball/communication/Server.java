package app.beachvolleyball.communication;

import app.beachvolleyball.communication.ClientHandler;
import app.beachvolleyball.entity.Net;
import app.beachvolleyball.entity.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    private static Player player1 = new Player(1,(int)(0.25 * SCREEN_WIDTH - 50), SCREEN_HEIGHT - 100);
    private static Player player2 = new Player(2,(int)(0.75 * SCREEN_WIDTH), SCREEN_HEIGHT - 100);
    private static Net net = new Net(SCREEN_WIDTH/2 - 20, SCREEN_HEIGHT/2);

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(9797)) {
            Socket socket = server.accept();
            Thread thread = new Thread(new ClientHandler(socket, player1, player2, net));
            thread.start();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}