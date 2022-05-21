package app.beachvolleyball;

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
import java.util.List;

@AllArgsConstructor
public class ClientHandler implements Runnable{
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int GROUND_Y = SCREEN_HEIGHT - 25;
    private static final int JUMP_POWER = 20;
    private static final float GRAVITY = 1.0f;
    public static final float HIT_FORCE_X = 5.0f;
    public static final float HIT_FORCE_Y = 4.0f;
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static byte connectedClients = 0;
    private Socket socket;
    private Player[] players;
    private Net net;
    private Ball ball;
    private int clientID;
    @Setter
    private String verifiedMessage;
    private List<String> swearWords;

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
            oos.writeObject(ball);
            oos.writeObject("done");
            connectedClients+=1;
            new Thread(() -> {
                while (connectedClients < 2) {
                    Thread.onSpinWait();
                }
                while(socket.isConnected()){
                    ClientMessage received = null;
                    try {
                        received = (ClientMessage) ois.readObject();
                    } catch (IOException | ClassNotFoundException ignored) {
                    }
                    if (received != null)
                        handleClientMessage(received);
                }
            }).start();

            new Thread(() -> {
                while (connectedClients < 2) {
                    Thread.onSpinWait();
                }
                while(socket.isConnected()){
                    try {
                        oos.writeObject(new ServerMessage(new Point(players[0].getCoordinates().x, players[0].getCoordinates().y),
                                new Point(players[1].getCoordinates().x, players[1].getCoordinates().y),
                                new Point(ball.getCoordinates().x, ball.getCoordinates().y),
                                verifiedMessage, players[0].getScore(), players[1].getScore(), connectedClients));
                    } catch (IOException ignored) {
                    }
                    verifiedMessage = "";
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while (connectedClients < 2) {
                Thread.onSpinWait();
            }
            while(socket.isConnected()){
                updatePlayerPosition();
                if (clientID==0)
                    updateBallPosition();
                Thread.sleep(10);
            }
        }
        catch (IOException | ClassNotFoundException | InterruptedException ex){// | InterruptedException ex){//| InterruptedException ex){
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
                connectedClients-=1;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    //            synchronized (lock) {
//                while (!responded) {
//                    lock.wait();
//                }
//                responded = false;
//            }

    //                    synchronized (lock) {
//                        responded = true;
//                        lock.notifyAll();
//                    }

    public void handleClientMessage(ClientMessage message){
        if(message.isLeft()){
            players[clientID].setVelocityX(-3.0f);
        }
        else if (!message.isRight()){
            players[clientID].setVelocityX(0);
        }
        if(message.isRight()){
            players[clientID].setVelocityX(3.0f);
        }
        else if (!message.isLeft()){
            players[clientID].setVelocityX(0);
        }

        if(message.isJump()){
            if(!players[clientID].isJump()){
                players[clientID].setJump(true);
                players[clientID].setVelocityY(-JUMP_POWER);
            }
        }

        if(!message.getTextMessage().isEmpty())
            verifyMessage(message);
    }

    public void verifyMessage(ClientMessage message){
        new Thread(() -> {
            String content = message.getTextMessage();
            if(!isMessageValid(content)){
                content = "Player" + (clientID+1) + ": ***** ***";
            }
            for(ClientHandler ch : clientHandlers)
                ch.setVerifiedMessage(content);
        }).start();
    }

    private boolean isMessageValid(String message){
        for(String swear : swearWords){
            if(message.contains(swear))
                return false;
        }
        return true;
    }

    private void update(){
        updatePlayerPosition();
        if (clientID==0)
            updateBallPosition();
    }

    private void updatePlayerPosition(){
        players[clientID].setCoordinateX(players[clientID].getCoordinates().x + players[clientID].getVelocityX());
        float left_border = clientID * (SCREEN_WIDTH + ball.getWidth())/2;
        float right_border = (clientID + 1) * (float)SCREEN_WIDTH/2  - players[clientID].getWidth()
                + (clientID - 1) * ball.getWidth()/2;
        if (players[clientID].getCoordinates().x <= left_border){
            players[clientID].setCoordinateX(left_border);
        }
        if (players[clientID]. getCoordinates().x >= right_border){
            players[clientID].setCoordinateX(right_border);
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
    }

    private void updateBallPosition(){
        //player collision
        int result = ballPlayerCollision();
        if(result!=0){
            bounceBall(result-1);
        }
        //ground collision
        if(ball.getCoordinates().y >= GROUND_Y - ball.getHeight()){
            ball.setCoordinateY(GROUND_Y - ball.getHeight());
            ball.setVelocityY(0);
            if(ball.getCoordinates().x < SCREEN_WIDTH / 2) {
                players[1].setScore(players[1].getScore()+1);
            }
            else{
                players[0].setScore(players[0].getScore()+1);
            }
            ball.setCoordinateX((float)(0.25 * SCREEN_WIDTH - 50));
            ball.setCoordinateY(0);
            ball.setVelocityY(0);
        }
        else{
            ball.setVelocityY(ball.getVelocityY() + 0.03f * GRAVITY);
        }

        //wall and net collision
        int left_wall = 0;
        int right_wall = SCREEN_WIDTH - (int)ball.getWidth();
        int net_left = (int)(net.getCoordinates().x - ball.getWidth());
        int net_right = (int)(net.getCoordinates().x + net.getWidth());
        if(ball.getCoordinates().x <= left_wall){
            ball.setCoordinateX(left_wall);
            ball.setVelocityX(-ball.getVelocityX());
        }
        else if(ball.getCoordinates().x >= right_wall){
            ball.setCoordinateX(right_wall);
            ball.setVelocityX(-ball.getVelocityX());
        }
        else if(ball.getCoordinates().y + ball.getHeight() >= net.getHeight()){
            if(ball.getCoordinates().x >= net_left && ball.getCoordinates().x <= net_right){
                if(ball.getCoordinates().x <= (SCREEN_WIDTH - ball.getWidth())/2)
                    ball.setCoordinateX(net_left);
                else
                    ball.setCoordinateX(net_right);
                ball.setVelocityX(-ball.getVelocityX());
            }
        }
        //position update
        ball.setCoordinateY(ball.getCoordinates().y + ball.getVelocityY());
        ball.setCoordinateX(ball.getCoordinates().x + ball.getVelocityX());
    }

    private int ballPlayerCollision(){
        for(int i=0; i<2; i++){
            if (ball.getCoordinates().y <= players[i].getCoordinates().y + players[i].getHeight()
                    && ball.getCoordinates().y + ball.getHeight() >= players[i].getCoordinates().y
                    && ball.getCoordinates().x <= players[i].getCoordinates().x + players[i].getWidth()
                    && ball.getCoordinates().x + ball.getWidth() >= players[i].getCoordinates().x){
                return i+1;
            }
        }
        return 0;
    }

    private void bounceBall(int colliding_player){
        ball.setVelocityX(direction(colliding_player) * HIT_FORCE_X + 0.1f * players[colliding_player].getVelocityX());
        ball.setVelocityY(-HIT_FORCE_Y + 0.1f * players[colliding_player].getVelocityY());
    }

    private int direction(int colliding_player){
        if(colliding_player == 0)
            return 1;
        return -1;
    }
}