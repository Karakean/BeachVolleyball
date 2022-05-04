module app.beachvolleyball {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.datatransfer;
    requires java.desktop;


    opens app.beachvolleyball to javafx.fxml;
    exports app.beachvolleyball.chat;
    opens app.beachvolleyball.chat to javafx.fxml;
    exports app.beachvolleyball;
}