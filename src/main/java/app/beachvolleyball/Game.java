package app.beachvolleyball;

import app.beachvolleyball.entity.Net;
import app.beachvolleyball.entity.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Game extends Application{

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private boolean isRunning = true;
    Player player1 = new Player((int)(0.25*SCREEN_WIDTH-50), SCREEN_HEIGHT - 100);
    Player player2 = new Player((int)(0.75*SCREEN_WIDTH), SCREEN_HEIGHT - 100);
    Net net = new Net(SCREEN_WIDTH/2 - 20, SCREEN_HEIGHT/2);

    @Override
    public void start(Stage stage) {
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
                case "W" -> player1.setCoordinateY(player1.getCoordinates().y - 20);
                case "A" -> player1.setCoordinateX(player1.getCoordinates().x - 20);
                case "S" -> player1.setCoordinateY(player1.getCoordinates().y + 20);
                case "D" -> player1.setCoordinateX(player1.getCoordinates().x + 20);
            }
            display(gc);
            //stage.show();
        });
        //Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), x->run(gc)));
        display(gc);
        stage.setScene(new Scene(new StackPane(canvas)));
        stage.show();
        //tl.play();
        //test
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
