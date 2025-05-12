package com.example.mousecheeseguigame;

import com.example.mousecheeseguigame.model.GameDatabaseUpdater;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HallOfFameController {

    @FXML
    private Label highestPointLabel;

    public void initialize() {
        highestPointLabel.setText(String.valueOf(new GameDatabaseUpdater().getHighestPoint()));
    }
}
