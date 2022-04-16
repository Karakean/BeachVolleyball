package app.beachvolleyball;

import app.beachvolleyball.entity.Net;
import app.beachvolleyball.entity.Player;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

@AllArgsConstructor
public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    Socket socket;
    Player player1, player2;
    Net net;

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())){
            oos.writeObject("server ready");
            if (!ois.readObject().equals("client ready")){
                System.out.println("Error: client not ready");
                return;
            }
            oos.writeObject(player1);
            oos.writeObject(player2);
            oos.writeObject(net);
            oos.writeObject("done");

            while(socket.isConnected()){
                oos.writeObject(player1.getCoordinates().x);
                oos.writeObject(player1.getCoordinates().y);
                player1.setCoordinateX(player1.getCoordinates().x + (Integer) ois.readObject());
                player1.setCoordinateY(player1.getCoordinates().y + (Integer) ois.readObject());
            }
        }
        catch (IOException | ClassNotFoundException ex){
            System.err.println(ex);
        }
        finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}