package app.beachvolleyball;

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

import static app.beachvolleyball.Server.SCREEN_HEIGHT;
import static app.beachvolleyball.Server.SCREEN_WIDTH;


public class Client extends Application{

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private boolean responded = false;
    final Object lock = new Object();

    private Player player1;
    private Player player2;
    private Net net;

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
            player1 = (Player) ois.readObject();
            player2 = (Player) ois.readObject();
            net = (Net) ois.readObject();
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
            switch (keyEvent.getCode().toString()) {
                case "W" -> player1.setVelocityY(-2);
                case "A" -> player1.setVelocityX(-2);
                case "S" -> player1.setVelocityY(2);
                case "D" -> player1.setVelocityX(2);
            }
        });
        canvas.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode().toString()) {
                case "W", "S" -> player1.setVelocityY(0);
                case "A", "D" -> player1.setVelocityX(0);
            }
        });

        //real start
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), x->run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
        receive();
    }

    public void run(GraphicsContext gc){
        display(gc, player1, player2, net);
        send();
    }

    public void send(){
        try {
            oos.writeObject(player1.getVelocityX());
            oos.writeObject(player1.getVelocityY());
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
                    player1.setCoordinateX((Integer) ois.readObject());
                    player1.setCoordinateY((Integer) ois.readObject());
                    //System.out.println("dostalem mesydz ze trzeba robic pekydz");
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

    public static void display(GraphicsContext gc, Player player1, Player player2, Net net){
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gc.setFill(Color.GRAY);
        gc.fillRect(net.getCoordinates().x, net.getCoordinates().y, net.getWidth(), net.getHeight());
        gc.setFill(Color.RED);
        gc.fillRect(player1.getCoordinates().x, player1.getCoordinates().y, player1.getWidth(), player1.getHeight());
        gc.setFill(Color.GREEN);
        gc.fillRect(player2.getCoordinates().x, player2.getCoordinates().y, player2.getWidth(), player2.getHeight());
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
