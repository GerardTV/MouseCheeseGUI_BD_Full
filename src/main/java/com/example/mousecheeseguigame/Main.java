package com.example.mousecheeseguigame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("cover.fxml"));
        primaryStage.setTitle("M03B- Gerard Torrents");
        Scene scene = new Scene(root, 350, 350);
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Parent exam = FXMLLoader.load(getClass().getResource("board.fxml"));
                    Scene scene = new Scene(exam, 350, 350);
                    primaryStage.setScene(scene);
                    primaryStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
