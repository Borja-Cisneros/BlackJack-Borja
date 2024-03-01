module di.blackjackborja {
    requires javafx.controls;
    requires javafx.fxml;


    opens di.blackjackborja to javafx.fxml;
    exports di.blackjackborja;
}