package app.beachvolleyball.client;

import app.beachvolleyball.entity.Ball;
import app.beachvolleyball.entity.Net;
import app.beachvolleyball.entity.Player;
import app.beachvolleyball.messenger.MessengerController;
import app.beachvolleyball.server.ServerMessage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

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
    private int clientID;

    private MessengerController messengerController;

    private boolean responded = false;
    final Object lock = new Object();

    private final Player[] players = new Player[2];
    private Net net;
    private Ball ball;

    private String key = "";
    private boolean dPressed;
    private boolean aPressed;
    private boolean spacePressed;

    @Override
    public void start(Stage stage) throws IOException {

        if (!initConnection()) return;

        GraphicsContext gc = initGraphics(stage);

        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), x->run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
        receive();
    }

    private boolean initConnection() throws IOException {
        try {
            socket = new Socket("localhost", 9797);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            if (!ois.readObject().equals("server ready")){
                System.out.println("Error: server is not ready");
                return false;
            }
            oos.writeObject("client ready");
            clientID = (Integer) ois.readObject();
            players[0] = (Player) ois.readObject();
            players[1] = (Player) ois.readObject();
            net = (Net) ois.readObject();
            ball = (Ball) ois.readObject();
            if (!ois.readObject().equals("done")){
                System.out.println("Error: server did not respond properly");
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            close();
            e.printStackTrace();
        }
        return true;
    }

    private GraphicsContext initGraphics(Stage stage) throws IOException {

        stage.setTitle("Beach Volleyball");
        stage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("/messenger.fxml"));
        Node node = fxmlLoader.load();
        messengerController = fxmlLoader.getController();
        messengerController.setClientID(clientID);

        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        canvas.setFocusTraversable(true);

        aPressed = dPressed = spacePressed = false;
        canvas.setOnKeyPressed(keyEvent -> {
            key = keyEvent.getCode().toString();
            switch (key) {
                case "A" -> aPressed = true;
                case "D" -> dPressed = true;
                case "SPACE" -> spacePressed = true;
            }
        });
        canvas.setOnKeyReleased(keyEvent -> {
            key = keyEvent.getCode().toString();
            switch (key) {
                case "A" -> aPressed = false;
                case "D" -> dPressed = false;
                case "SPACE" -> spacePressed = false;
            }
        });
        canvas.setOnMouseClicked(e -> canvas.requestFocus());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        HBox hbox = new HBox(canvas);
        hbox.getChildren().add(node);
        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.show();

        return gc;
    }

    public void run(GraphicsContext gc){
        display(gc);
        send();
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

    public void send(){
        try {
            oos.writeObject(new ClientMessage(dPressed, aPressed, spacePressed, messengerController.getCurrentMessage()));
            messengerController.setCurrentMessage("");
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
        ball.setCoordinateX(message.getBallPosition().x);
        ball.setCoordinateY(message.getBallPosition().y);
        String msg = message.getVerifiedMessage();
        if(!msg.isEmpty())
            Platform.runLater(() -> messengerController.receiveMessage(msg));
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
