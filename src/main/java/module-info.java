module app.beachvolleyball {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.datatransfer;
    requires java.desktop;


    opens app.beachvolleyball to javafx.fxml;
    exports app.beachvolleyball.messenger;
    opens app.beachvolleyball.messenger to javafx.fxml;
    exports app.beachvolleyball.server;
    opens app.beachvolleyball.server to javafx.fxml;
    exports app.beachvolleyball.client;
    opens app.beachvolleyball.client to javafx.fxml;
}