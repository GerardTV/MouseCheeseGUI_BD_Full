package com.example.mousecheeseguigame;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import  com.example.mousecheeseguigame.model.*;


import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class CheeseGameController {

    @FXML
    GridPane gpBoard;
    @FXML
    ImageView iv00,iv01,iv02,iv03,iv10,iv11,iv12,iv13,iv20,iv21,iv22,iv23,iv30,iv31,iv32,iv33;
    @FXML
    Label lblCounter = new Label();

    GameDatabaseUpdater gameDatabaseUpdater;
    final int totalGameRow = 4;
    final int totalGameCol = 4;
    int currentRowMousePosition = 0;
    int currentColMousePosition = 0;
    String currentCellFigure = "start";
    int newRowMousePosition = 0;
    int newColMousePosition = 0;
    int movement = 0;
    int earnedPoints = 0;
    int currentCellEarnedPoints = 0;
    MouseCheeseGame game;
    Stack<String> playerMovements = new Stack<>();
    Image mouse = new Image(Objects.requireNonNull(Main.class.getResource("/com/example/mousecheeseguigame/images/mouse.png")).toString());
    Image footPrints = new Image(Objects.requireNonNull(Main.class.getResource("/com/example/mousecheeseguigame/images/footPrints.png")).toString());
    Image thief = new Image(Objects.requireNonNull(Main.class.getResource("/com/example/mousecheeseguigame/images/thief.png")).toString());
    Image luckyMouse = new Image(Objects.requireNonNull(Main.class.getResource("/com/example/mousecheeseguigame/images/luckyMouse.png")).toString());
    Image cheese = new Image(Objects.requireNonNull(Main.class.getResource("/com/example/mousecheeseguigame/images/cheese.png")).toString());

    @FXML
    protected void initialize() throws Exception {
        gameDatabaseUpdater = new GameDatabaseUpdater();

//        Use this method when the game is stack...
//        Usa este método cuando el juego se atasque...
//        gameDatabaseUpdater.makeNewGame();


        game = new MouseCheeseGame(totalGameRow, totalGameCol, true);
        iv00.setImage(mouse);
        deployCorrespondImages();
        iv33.setImage(cheese);
        currentRowMousePosition = gameDatabaseUpdater.getMouseRowPos();
        currentColMousePosition = gameDatabaseUpdater.getMouseColPos();
        game.updateMousePosition(currentRowMousePosition,currentColMousePosition);
        game.makeCellAsDiscovered(currentRowMousePosition,currentColMousePosition);
        game.start();
        currentCellEarnedPoints = earnedPoints = game.getTotalEarnedPoints();
        updatePointsCounter(earnedPoints);
    }

    public void moveLeft() {
        performMovement(game.startMouseMovement("l"));
    }

    public void moveUp() {
        performMovement(game.startMouseMovement("u"));
    }

    public void moveDown() {
        performMovement(game.startMouseMovement("d"));
    }

    public void moveRight() {
        performMovement(game.startMouseMovement("r"));
    }

    private void performMovement(String question) {
        if (question == null) {
            newRowMousePosition = game.getRowMousePosition();
            newColMousePosition = game.getColMousePosition();
            gameDatabaseUpdater.updateCellData(newRowMousePosition,newColMousePosition);
            if (newRowMousePosition != currentRowMousePosition || newColMousePosition != currentColMousePosition) {
                try {
                    moveMouseToNewCell();
                } catch (GameOverException e) {
                    try {
                        Stage primaryStage = (Stage) gpBoard.getScene().getWindow();
                        Parent root = null;

                        root = FXMLLoader.load(getClass().getResource("loose.fxml"));

                        Scene scene = new Scene(root, 350, 350);
                        primaryStage.setScene(scene);
                        primaryStage.show();

                        Platform.runLater(() -> {
                            try {
                                Thread.sleep(2000);
                                Platform.exit();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return;
        }

        TextInputDialog td = new TextInputDialog("");
        final String[] userAnswer = {""};
        td.setTitle("Question cell!!");
        td.setHeaderText(question);
        // show the text input dialog
        td.showAndWait();

        // set the text of the label
        userAnswer[0] = td.getEditor().getText();

        game.completeMouseMovement(userAnswer[0]);
        performMovement(null);
    }

    private void moveMouseToNewCell() throws GameOverException {
        ((ImageView) getNodeByRowColumnIndex(newRowMousePosition, newColMousePosition, gpBoard)).setImage(mouse);
        switch (currentCellFigure) {
            case "--":
                ((ImageView) getNodeByRowColumnIndex(currentRowMousePosition, currentColMousePosition, gpBoard)).setImage(thief);
                playerMovements.push(String.format("%d %d%d %s %d", movement, currentRowMousePosition, currentColMousePosition, "thief", currentCellEarnedPoints));
                break;
            case "++":
                ((ImageView) getNodeByRowColumnIndex(currentRowMousePosition, currentColMousePosition, gpBoard)).setImage(luckyMouse);
                playerMovements.push(String.format("%d %d%d %s %d", movement, currentRowMousePosition, currentColMousePosition, "lucky", currentCellEarnedPoints));
                break;
            case "start":
                ((ImageView) getNodeByRowColumnIndex(currentRowMousePosition, currentColMousePosition, gpBoard)).setImage(footPrints);
                playerMovements.push(String.format("%d %d%d %s %d", movement, currentRowMousePosition, currentColMousePosition, "start", currentCellEarnedPoints));
                break;
            default:
                ((ImageView) getNodeByRowColumnIndex(currentRowMousePosition, currentColMousePosition, gpBoard)).setImage(footPrints);
                playerMovements.push(String.format("%d %d%d %s %d", movement, currentRowMousePosition, currentColMousePosition, "empty", currentCellEarnedPoints));
                break;
        }
        movement++;
        if (!game.hasLost()) {
            currentCellFigure = game.getCurrentFigure();
            currentCellEarnedPoints = game.getTotalEarnedPoints() - earnedPoints;
            earnedPoints = game.getTotalEarnedPoints();
            currentRowMousePosition = newRowMousePosition;
            currentColMousePosition = newColMousePosition;
            updatePointsCounter(game.getTotalEarnedPoints());
            checkWin();
        } else {
            playerMovements.push(String.format("%d %d%d %s %s", movement, newRowMousePosition, newColMousePosition, "cat", "00"));
            writeBackTrack(playerMovements);
            throw new GameOverException();
        }
    }

    private void updatePointsCounter(int totalEarnedPoints) {
        gameDatabaseUpdater.updateCurrentGamePoint(totalEarnedPoints);
        lblCounter.setText(String.format("%3d", totalEarnedPoints));
    }


    public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    public void checkWin() {
        if (game.hasWon()) {
            gameDatabaseUpdater.recordGamePointInDatabase(game.getTotalEarnedPoints());
            playerMovements.push(String.format("%d %d%d %s %d", movement, newRowMousePosition, newColMousePosition, "cheese", game.getTotalEarnedPoints()));
            writeBackTrack(playerMovements);
            try {
                Stage primaryStage = (Stage) gpBoard.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("win.fxml"));
                Parent root = loader.load();
                ((winController) loader.getController()).setLblPoints(String.format("You get %d points!!", game.getTotalEarnedPoints()));
                Scene scene = new Scene(root, 350, 350);
                primaryStage.setScene(scene);
                primaryStage.show();
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(2500);

                        Platform.exit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            } catch (IOException e) {
                System.out.println("ERROR: " + e.getClass().getName() + " - " + e.getMessage());
                ;
            }
        }
    }

    public void deployCorrespondImages() {
        if ( !gameDatabaseUpdater.isGameCompletelyFinished() ) {
            String retrievedData = "";
            ImageView[][] imageViewRolCol = new ImageView[totalGameRow][totalGameCol];
            imageViewRolCol[0][0] = iv00; imageViewRolCol[0][1] = iv01; imageViewRolCol[0][2] = iv02; imageViewRolCol[0][3] = iv03;
            imageViewRolCol[1][0] = iv10; imageViewRolCol[1][1] = iv11; imageViewRolCol[1][2] = iv12; imageViewRolCol[1][3] = iv13;
            imageViewRolCol[2][0] = iv20; imageViewRolCol[2][1] = iv21; imageViewRolCol[2][2] = iv22; imageViewRolCol[2][3] = iv23;
            imageViewRolCol[3][0] = iv30; imageViewRolCol[3][1] = iv31; imageViewRolCol[3][2] = iv32; imageViewRolCol[3][3] = iv33;

            for ( int row = 0 ; row < totalGameRow ; row++ ) {
                for ( int col = 0 ; col < totalGameCol ; col++ ) {
                    retrievedData = gameDatabaseUpdater.retrieveCellData(row,col);
                    if ( retrievedData.equalsIgnoreCase("++") && gameDatabaseUpdater.isCellReserved(row,col)) {
                        imageViewRolCol[row][col].setImage(luckyMouse);
//                        currentCellFigure = "++";
                    } else if ( retrievedData.equalsIgnoreCase("--") && gameDatabaseUpdater.isCellReserved(row,col) ) {
                        imageViewRolCol[row][col].setImage(thief);
//                        currentCellFigure = "--";
                    } else if ( gameDatabaseUpdater.isCellReserved(row,col) ) {
                        imageViewRolCol[row][col].setImage(footPrints);
                    }

                    if ( gameDatabaseUpdater.isCellReserved(row,col) ) {
                        game.makeCellAsDiscovered(row,col);
                    }
                }
            }

            imageViewRolCol[gameDatabaseUpdater.getMouseRowPos()][gameDatabaseUpdater.getMouseColPos()].setImage(mouse);
        }

    }

    private void writeBackTrack(Stack<String> playerMovements) {
        String filename = "MovementsBackTrack.txt";
        try {
            FileWriter fileWriter = new FileWriter(filename);
            while (!playerMovements.isEmpty())
                fileWriter.write(playerMovements.pop() + "\n");
            fileWriter.close();
        } catch (IOException ex) {
            System.err.println("I/O Error: " + ex);
        }
    }
}
