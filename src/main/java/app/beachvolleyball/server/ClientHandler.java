package app.beachvolleyball.server;

import app.beachvolleyball.client.ClientMessage;
import app.beachvolleyball.entity.Ball;
import app.beachvolleyball.entity.Net;
import app.beachvolleyball.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

@AllArgsConstructor
public class ClientHandler implements Runnable{
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int GROUND_Y = SCREEN_HEIGHT - 25;
    private static final int GRAVITY = 1;
    private static final int JUMP_POWER = 20;
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private Player[] players;
    private Net net;
    private Ball ball;
    private int clientID;
    @Setter
    private String verifiedMessage;

    @Override
    public void run() {
        clientHandlers.add(this);
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())){
            oos.writeObject("server ready");
            if (!ois.readObject().equals("client ready")){
                System.out.println("Error: client not ready");
                return;
            }
            oos.writeObject(clientID);
            oos.writeObject(players[0]);
            oos.writeObject(players[1]);
            oos.writeObject(net);
            oos.writeObject(ball);
            oos.writeObject("done");
            while(socket.isConnected()){
                oos.writeObject(new ServerMessage(new Point(players[0].getCoordinates().x, players[0].getCoordinates().y),
                        new Point(players[1].getCoordinates().x, players[1].getCoordinates().y),
                        new Point(ball.getCoordinates().x, ball.getCoordinates().y),
                        verifiedMessage));
                verifiedMessage = "";
                ClientMessage received = (ClientMessage) ois.readObject();
                handleClientMessage(received);
                update();
            }
        }
        catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void handleClientMessage(ClientMessage message){
//        if (message.isPressed()) {
//            switch (message.getKey()) {
//                case "W" -> players[clientID].setVelocityY(-2);
//                case "A" -> players[clientID].setVelocityX(-2);
//                case "S" -> players[clientID].setVelocityY(2);
//                case "D" -> players[clientID].setVelocityX(2);
//            }
//        }
//        else{
//            switch (message.getKey()) {
//                case "W", "S" -> players[clientID].setVelocityY(0);
//                case "A", "D" -> players[clientID].setVelocityX(0);
//            }
//        }
//        players[clientID].setCoordinateX(players[clientID].getCoordinates().x + players[clientID].getVelocityX());
//        players[clientID].setCoordinateY(players[clientID].getCoordinates().y + players[clientID].getVelocityY());

        if (message.isPressed()) {
            switch (message.getKey()) {
                case "SPACE" -> {
                    if(!players[clientID].isJump()){
                        players[clientID].setJump(true);
                        players[clientID].setVelocityY(-JUMP_POWER);
                    }
                }
                case "A" -> players[clientID].setVelocityX(-2);
                case "D" -> players[clientID].setVelocityX(2);
            }
        }
        else{
            switch (message.getKey()) {
                case "A", "D" -> players[clientID].setVelocityX(0);
            }
        }

        String msg = message.getTextMessage();
        if(!msg.isEmpty() && isMessageValid(msg)){
            for(ClientHandler ch : clientHandlers)
                ch.setVerifiedMessage(msg);
        }
    }

    private void update(){
        players[clientID].setCoordinateX(players[clientID].getCoordinates().x + players[clientID].getVelocityX());
        if (players[clientID].getCoordinates().x <= clientID * SCREEN_WIDTH / 2){
            players[clientID].setCoordinateX(clientID * SCREEN_WIDTH / 2);
        }
        if (players[clientID]. getCoordinates().x >= (clientID + 1) * SCREEN_WIDTH / 2 - players[clientID].getWidth()){
            players[clientID].setCoordinateX((clientID + 1) * SCREEN_WIDTH / 2 - players[clientID].getWidth());
        }
        players[clientID].setCoordinateY(players[clientID].getCoordinates().y + players[clientID].getVelocityY());
        if (players[clientID].isJump()){
            players[clientID].setVelocityY(players[clientID].getVelocityY() + GRAVITY);
            if (players[clientID].getCoordinates().y >= GROUND_Y - players[clientID].getHeight()){
                players[clientID].setVelocityY(0);
                players[clientID].setCoordinateY(GROUND_Y - players[clientID].getHeight());
                players[clientID].setJump(false);
            }
        }

        ball.setCoordinateY(ball.getCoordinates().y + ball.getVelocityY());
        if(ball.getCoordinates().y >= GROUND_Y - ball.getHeight()){
            ball.setCoordinateY(GROUND_Y - ball.getHeight());
        }
        ball.setVelocityY(ball.getVelocityY() + GRAVITY);
    }

    private boolean isMessageValid(String message){
        return true;
    }
}