package app.beachvolleyball;
import app.beachvolleyball.entity.Ball;
import app.beachvolleyball.entity.Net;
import app.beachvolleyball.entity.Player;
import app.beachvolleyball.chat.ChatController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;

public class Client extends Application{

    public static int SCREEN_WIDTH = 800;
    public static int SCREEN_HEIGHT = 600;

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int clientID;

    private ChatController chatController;

    private final Player[] players = new Player[2];
    private Ball ball;
    private Image redImage;
    private Image greenImage;
    private Image ballImage;

    private String key = "";
    private boolean left;
    private boolean right;
    private boolean jump;

    int gameState = 0;
    final Object lock = new Object();

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
            socket = new Socket("localhost", 1234);
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

        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("/app/beachvolleyball/messenger-view.fxml"));
        Node chat = fxmlLoader.load();
        chatController = fxmlLoader.getController();
        chatController.setClientID(clientID);
        chat.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().toString().equals("ENTER")){
                chatController.sendMessage();
                send();
            }
        });

        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(keyEvent -> {
            key = keyEvent.getCode().toString();
            switch (key) {
                case "A" -> left = true;
                case "D" -> right = true;
                case "SPACE" -> jump = true;
            }
            send();
        });
        canvas.setOnMouseClicked(keyEvent -> {
            gameState = 2;
        });
        canvas.setOnKeyReleased(keyEvent -> {
            key = keyEvent.getCode().toString();
            switch (key){
                case "A" -> left = false;
                case "D" -> right = false;
                case "SPACE" -> jump = false;
            }
            send();
        });
        canvas.setOnMouseClicked(e -> canvas.requestFocus());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        HBox container = new HBox(canvas);

        URL url = getClass().getResource("/app/beachvolleyball/background.jpg");
        BackgroundImage backgroundImage= new BackgroundImage(new Image(String.valueOf(url),
                SCREEN_WIDTH,SCREEN_HEIGHT,false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        container.setBackground(new Background(backgroundImage));

        ballImage = new Image(String.valueOf(getClass().getResource(ball.getImagePath())));
        redImage = new Image(String.valueOf(getClass().getResource(players[0].getImagePath())));
        greenImage = new Image(String.valueOf(getClass().getResource(players[1].getImagePath())));

        container.getChildren().add(chat);
        Scene scene = new Scene(container);

        stage.setScene(scene);
        stage.show();
        return gc;
    }

    public void prepare(GraphicsContext gc){
        gc.setFill(Color.RED);
        gc.drawImage(redImage, players[0].getCoordinates().x, players[0].getCoordinates().y);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void run(GraphicsContext gc){
        display(gc);
        //send();
    }

    public void display(GraphicsContext gc){
        gc.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gc.drawImage(redImage, players[0].getCoordinates().x, players[0].getCoordinates().y);
        gc.drawImage(greenImage, players[1].getCoordinates().x, players[1].getCoordinates().y);
        gc.drawImage(ballImage, ball.getCoordinates().x, ball.getCoordinates().y);
    }

    public void send(){
        gameState = 1;
        try {
            oos.writeObject(new ClientMessage(left, right, jump, chatController.getCurrentMessage()));
            chatController.setCurrentMessage("");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void receive() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    ServerMessage serverMessage = (ServerMessage)ois.readObject();
                    handleServerMessage(serverMessage);
                } catch (IOException | ClassNotFoundException e) {
                    //e.printStackTrace();
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
        if(!message.getVerifiedMessage().isEmpty())
            Platform.runLater(() -> chatController.receiveMessage(message.getVerifiedMessage()));
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