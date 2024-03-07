module di.blackjackborja {
    requires javafx.controls;
    requires javafx.fxml;
    requires di.carta;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens di.blackjackborja to javafx.fxml;
    exports di.blackjackborja;
}