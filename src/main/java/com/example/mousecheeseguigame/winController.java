package com.example.mousecheeseguigame;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class winController {

    @FXML
    Label lblPoints = new Label();

    public void setLblPoints(String text) {
        this.lblPoints.setText(text);
    }
}
