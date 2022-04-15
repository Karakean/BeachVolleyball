package app.beachvolleyball;

import app.beachvolleyball.communication.Client;
import app.beachvolleyball.communication.ClientHandler;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import static app.beachvolleyball.communication.Server.SCREEN_HEIGHT;
import static app.beachvolleyball.communication.Server.SCREEN_WIDTH;


public class GameClient extends Application{

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Player player1;
    private Player player2;
    private Net net;

    @Override
    public void start(Stage stage) throws IOException {

        //init stage etc
        stage.setTitle("Beach Volleyball");
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        stage.setScene(new Scene(new StackPane(canvas)));
        stage.show();

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

        //handling key events
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

        //GameClient gameClient = new GameClient();
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), x->display(gc, player1, player2, net)));
        tl.setCycleCount(Timeline.INDEFINITE);
        new Thread(tl::play);
        receive(gc);
        send();
    }

    public void send() throws IOException {
        try {
            while (socket.isConnected()) {
                oos.writeObject(player1.getVelocityX());
                oos.writeObject(player1.getVelocityY());
            }
        }catch (IOException e){
            close();
        }
    }

    public void receive(GraphicsContext gc) {
        new Thread(() -> {
            try {
                player1.setCoordinateX((Integer) ois.readObject());
                player1.setCoordinateY((Integer) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                try {
                    close();
                } catch (IOException ex) {
                    ex.printStackTrace();
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

//
//        stage.setTitle("Beach Volleyball");
//        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        canvas.setFocusTraversable(true);
//
//        stage.setScene(new Scene(new StackPane(canvas)));
//        stage.show();
//
//        try (Socket client = new Socket("localhost", 9797)) {
//            try(ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
//                ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){
//
//                String response = (String) ois.readObject();
//                if (!response.equals("ready")){
//                    System.out.println("Error: server not ready");
//                    return;
//                }
//                response = "me2";
//                oos.writeObject(response);
//
//                Player player1 = (Player) ois.readObject();
//                Player player2 = (Player) ois.readObject();
//                Net net = (Net) ois.readObject();
//
//                canvas.setOnKeyPressed(keyEvent -> {
//                    switch (keyEvent.getCode().toString()) {
//                        case "W" -> player1.setVelocityY(-2);
//                        case "A" -> player1.setVelocityX(-2);
//                        case "S" -> player1.setVelocityY(2);
//                        case "D" -> player1.setVelocityX(2);
//                    }
//                });
//                canvas.setOnKeyReleased(keyEvent -> {
//                    switch (keyEvent.getCode().toString()) {
//                        case "W", "S" -> player1.setVelocityY(0);
//                        case "A", "D" -> player1.setVelocityX(0);
//                    }
//                });
//                //Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), x->run(gc)));
//                //tl.setCycleCount(Timeline.INDEFINITE);
//                display(gc, player1, player2, net);
//                while(client.isConnected()){
//                    oos.writeObject(player1.getVelocityX());
//                    oos.writeObject(player1.getVelocityY());
//                    player1.setCoordinateX((Integer)ois.readObject());
//                    player1.setCoordinateY((Integer)ois.readObject());
//                    display(gc, player1, player2, net);
//                }
//
//            }
//            catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException ex) {
//            System.err.println(ex);
//        }


//        Stage secondStage = new Stage();
//        firstStage.setTitle("Beach Volleyball: " + player1);
//        secondStage.setTitle("Beach Volleyball: " + player2);
//        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
//        Canvas canvas2 = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        GraphicsContext gc2 = canvas2.getGraphicsContext2D();
//        canvas.setFocusTraversable(true);
//        canvas2.setFocusTraversable(true);
//        canvas.setOnKeyPressed(keyEvent -> {
//            switch (keyEvent.getCode().toString()) {
//                case "W" -> player1.setVelocityY(-2);
//                case "A" -> player1.setVelocityX(-2);
//                case "S" -> player1.setVelocityY(2);
//                case "D" -> player1.setVelocityX(2);
//            }
//        });
//        canvas2.setOnKeyPressed(keyEvent -> {
//            switch (keyEvent.getCode().toString()) {
//                case "W" -> player1.setVelocityY(-2);
//                case "A" -> player1.setVelocityX(-2);
//                case "S" -> player1.setVelocityY(2);
//                case "D" -> player1.setVelocityX(2);
//            }
//        });
//        canvas.setOnKeyReleased(keyEvent -> {
//            switch (keyEvent.getCode().toString()) {
//                case "W", "S" -> player1.setVelocityY(0);
//                case "A", "D" -> player1.setVelocityX(0);
//            }
//        });
//        canvas2.setOnKeyReleased(keyEvent -> {
//            switch (keyEvent.getCode().toString()) {
//                case "W", "S" -> player1.setVelocityY(0);
//                case "A", "D" -> player1.setVelocityX(0);
//            }
//        });
        //Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), x->run(gc)));
        //tl.setCycleCount(Timeline.INDEFINITE);
//        display(gc);
//        firstStage.setScene(new Scene(new StackPane(canvas)));
//        firstStage.show();
//        display(gc2);
//        secondStage.setScene(new Scene(new StackPane(canvas2)));
//        secondStage.show();


//        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("game-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT);
//        stage.setTitle("Beach Volleyball");
//        stage.setScene(scene);
//        stage.show();





    public static void main(String[] args) {
        launch();
    }

}
