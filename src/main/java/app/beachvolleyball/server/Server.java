package app.beachvolleyball.server;

import app.beachvolleyball.entity.Ball;
import app.beachvolleyball.entity.Net;
import app.beachvolleyball.entity.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    private static final Player[] players = new Player[2];
    private static final Net net = new Net(SCREEN_WIDTH/2 - 10, SCREEN_HEIGHT/2);
    private static final Ball ball = new Ball((int)(0.25 * SCREEN_WIDTH - 50), SCREEN_HEIGHT/2);

    public static void main(String[] args) throws IOException {

        players[0] = new Player(1,(int)(0.25 * SCREEN_WIDTH - 50), SCREEN_HEIGHT - 100);
        players[1] = new Player(2,(int)(0.75 * SCREEN_WIDTH), SCREEN_HEIGHT - 100);

        try (ServerSocket server = new ServerSocket(9797)) {

            Socket socket = server.accept();
            Thread thread = new Thread(new ClientHandler(socket, players, net, ball, 0, ""));
            thread.start();

            Socket socket1 = server.accept();
            Thread thread1 = new Thread(new ClientHandler(socket1, players, net, ball,1, ""));
            thread1.start();

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}