module di.blackjackborja {
    requires javafx.controls;
    requires javafx.fxml;
    requires di.carta;


    opens di.blackjackborja to javafx.fxml;
    exports di.blackjackborja;
}