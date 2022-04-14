package app.beachvolleyball.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{
    Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())){
            oos.writeObject("ready");
            Integer n = (Integer) ois.readObject();
            for (int i=0; i<n; i++){
                //Message message = (Message) ois.readObject();
                //System.out.println(message.getContent());
            }
            oos.writeObject("done");
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