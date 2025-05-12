module com.example.mousecheeseguigame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.graphicsEmpty;
    requires java.sql;


    opens com.example.mousecheeseguigame to javafx.fxml;
    exports com.example.mousecheeseguigame;
}