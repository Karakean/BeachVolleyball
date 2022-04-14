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
import java.net.ServerSocket;
import java.net.Socket;


public class GameServer extends Application{

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private boolean isRunning = true;
    Player player1 = new Player(1,(int)(0.25*SCREEN_WIDTH-50), SCREEN_HEIGHT - 100);
    Player player2 = new Player(2,(int)(0.75*SCREEN_WIDTH), SCREEN_HEIGHT - 100);
    Net net = new Net(SCREEN_WIDTH/2 - 20, SCREEN_HEIGHT/2);

    @Override
    public void start(Stage stage) {
//        try (ServerSocket server = new ServerSocket(9797)) {
//            Socket socket = server.accept();
//            System.out.println("Welcome: " + socket);
//            Thread thread = new Thread(new ClientHandler(socket));
//            thread.start();
//        } catch (IOException ex) {
//            System.err.println(ex);
//        }

//        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("game-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT);
//        stage.setTitle("Beach Volleyball");
//        stage.setScene(scene);
//        stage.show();

        stage.setTitle("Beach Volleyball");
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
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
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), x->run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);
        display(gc);
        stage.setScene(new Scene(new StackPane(canvas)));
        stage.show();
        tl.play();
    }

    public void run(GraphicsContext gc){
        player1.setCoordinateX(player1.getCoordinates().x + player1.getVelocityX());
        player1.setCoordinateY(player1.getCoordinates().y + player1.getVelocityY());
        display(gc);
    }

    public void display(GraphicsContext gc){
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gc.setFill(Color.GRAY);
        gc.fillRect(net.getCoordinates().x, net.getCoordinates().y, net.getWidth(), net.getHeight());
        gc.setFill(Color.RED);
        gc.fillRect(player1.getCoordinates().x, player1.getCoordinates().y, player1.getWidth(), player1.getHeight());
        gc.setFill(Color.GREEN);
        gc.fillRect(player2.getCoordinates().x, player2.getCoordinates().y, player2.getWidth(), player2.getHeight());
    }

    public static void main(String[] args) {
        launch();
    }

}
