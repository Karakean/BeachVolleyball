package app.beachvolleyball;

import app.beachvolleyball.entity.Ball;
import app.beachvolleyball.entity.Net;
import app.beachvolleyball.entity.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Application{

    public static int SCREEN_WIDTH = 800;
    public static int SCREEN_HEIGHT = 600;

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private boolean responded = false;
    final Object lock = new Object();

    private Player[] players = new Player[2];
    private Net net;
    private Ball ball;
    private String key = "";
    private boolean isPressed = false;

    @Override
    public void start(Stage stage) throws IOException {

        //init connection
        try {
            socket = new Socket("localhost", 9797);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            if (!ois.readObject().equals("server ready")){
                System.out.println("Error: server is not ready");
                return;
            }
            oos.writeObject("client ready");
            players[0] = (Player) ois.readObject();
            players[1] = (Player) ois.readObject();
            net = (Net) ois.readObject();
            ball = (Ball) ois.readObject();
            if (!ois.readObject().equals("done")){
                System.out.println("Error: server did not respond properly");
                return;
            }
        } catch (IOException | ClassNotFoundException e) {
            close();
            e.printStackTrace();
        }

        //init visuals
        stage.setTitle("Beach Volleyball");
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        stage.setScene(new Scene(new StackPane(canvas)));
        stage.show();
        canvas.setOnKeyPressed(keyEvent -> {
            key = keyEvent.getCode().toString();
            isPressed = true;
        });
        canvas.setOnKeyReleased(keyEvent -> {
            key = keyEvent.getCode().toString();
            isPressed = false;
        });

        //real start
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), x->run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
        receive();
    }

    public void run(GraphicsContext gc){
        display(gc);
        send();
    }

    public void send(){
        try {
            oos.writeObject(new ClientMessage(key, isPressed));
            //System.out.println("wysylam mesydz ze trzeba robic pekydz");
            synchronized (lock) {
                while (!responded) {
                    lock.wait();
                }
                responded = false;
            }
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public void receive() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    ServerMessage serverMessage = (ServerMessage) ois.readObject();
                    handleServerMessage(serverMessage);
                    synchronized (lock) {
                        responded = true;
                        lock.notifyAll();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    try {
                        close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void handleServerMessage(ServerMessage message){
        players[0].setCoordinateX(message.getPlayer1Position().x);
        players[0].setCoordinateY(message.getPlayer1Position().y);
        players[1].setCoordinateX(message.getPlayer2Position().x);
        players[1].setCoordinateY(message.getPlayer2Position().y);
    }

    public void display(GraphicsContext gc){
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gc.setFill(Color.RED);
        gc.fillRect(players[0].getCoordinates().x, players[0].getCoordinates().y, players[0].getWidth(), players[0].getHeight());
        gc.setFill(Color.GREEN);
        gc.fillRect(players[1].getCoordinates().x, players[1].getCoordinates().y, players[1].getWidth(), players[1].getHeight());
        gc.setFill(Color.GRAY);
        gc.fillRect(net.getCoordinates().x, net.getCoordinates().y, net.getWidth(), net.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(ball.getCoordinates().x, ball.getCoordinates().y, ball.getWidth(), ball.getHeight());
    }

    public void close() throws IOException {
        oos.close();
        ois.close();
        socket.close();
    }

    public static void main(String[] args) {
        launch();
    }

}
