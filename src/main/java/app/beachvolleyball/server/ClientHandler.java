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
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
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
                        new Point(players[1].getCoordinates().x, players[1].getCoordinates().y), verifiedMessage));
                verifiedMessage = "";
                ClientMessage received = (ClientMessage) ois.readObject();
                handleClientMessage(received);
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
        if (message.isPressed()) {
            switch (message.getKey()) {
                case "W" -> players[clientID].setVelocityY(-2);
                case "A" -> players[clientID].setVelocityX(-2);
                case "S" -> players[clientID].setVelocityY(2);
                case "D" -> players[clientID].setVelocityX(2);
            }
        }
        else{
            switch (message.getKey()) {
                case "W", "S" -> players[clientID].setVelocityY(0);
                case "A", "D" -> players[clientID].setVelocityX(0);
            }
        }
        players[clientID].setCoordinateX(players[clientID].getCoordinates().x + players[clientID].getVelocityX());
        players[clientID].setCoordinateY(players[clientID].getCoordinates().y + players[clientID].getVelocityY());

        String msg = message.getTextMessage();
        if(!msg.isEmpty() && isMessageValid(msg)){
            for(ClientHandler ch : clientHandlers)
                ch.setVerifiedMessage(msg);
        }
    }

    private boolean isMessageValid(String message){
        return true;
    }
}