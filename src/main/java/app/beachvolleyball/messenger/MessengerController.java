package app.beachvolleyball.messenger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ResourceBundle;

public class MessengerController implements Serializable {

    @Setter
    private int clientID;

    @Getter
    @Setter
    private String currentMessage="";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox messageBox;

    @FXML
    private TextField textField;

//    @FXML
//    private Button sendButton;

    public void sendMessage() throws IOException {
        String tf = textField.getText();
        String msg = "Player" + (clientID+1) + ": " + tf;
        if (!tf.isEmpty()){
            currentMessage = msg;
            textField.clear();
        }
    }

    public void receiveMessage(String message){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        hBox.getChildren().add(textFlow);
        messageBox.getChildren().add(hBox);
    }

}