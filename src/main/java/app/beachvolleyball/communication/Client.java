package app.beachvolleyball.communication;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try (Socket client = new Socket("localhost", 9797)) {
            try(ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){

                String response = (String) ois.readObject();
                if (!response.equals("ready")){
                    System.out.println("Error: server not ready");
                    return;
                }
                int n = Integer.parseInt(args[0]);
                oos.writeObject(n);

                for (int i=0; i<n; i++) {
                    //oos.writeObject(new Message(i,"message"+i));
                }

                response = (String) ois.readObject();
                if (!response.equals("done")) {
                    System.out.println("Error: server didn't respond properly");
                }

            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }
}