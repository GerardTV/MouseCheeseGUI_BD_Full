package com.example.mousecheeseguigame.model;

import java.util.Random;

public class MouseCheeseGame {
    private GameCell[][] gameBoard;
    private int totalEarnedPoints = 0;
    private int colMousePosition;
    private int rowMousePosition;
    private boolean won;
    private boolean lost;
    private boolean testMode;
    private boolean firstTimeRun;
    private boolean newGame;
    private int randomQuestionId;
    private static GameDatabaseUpdater gameDatabaseUpdater;

    /**
     * Crear un tablero de rows x cols i coloca un gato una celda propicia i una celda no propicia.
     * @param rows nombre de files
     * @param cols nombre de columnes
     */
    public MouseCheeseGame(int rows, int cols,boolean testMode){
        firstTimeRun = true;
        gameDatabaseUpdater = new GameDatabaseUpdater();
        randomQuestionId = new Random().nextInt(gameDatabaseUpdater.getTotalQuestion())+1;
        this.testMode = testMode;
        this.gameBoard = new GameCell[rows][cols];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                gameBoard[i][j] = new GameCell();
            }
        }

        //añadimos el gato
        while(true) {
            int x = (int) (Math.random() * rows);
            int y = (int) (Math.random() * cols);
            if ((x > 0 || y > 0) && (x < rows || y > cols) && !(x == rows-1 && y == cols-1)  && !(gameBoard[x][y] instanceof QuestionableCell )){
                gameBoard[x][y].setCat();
                break;
            }
        }

        //añadimos celda de pregunta propicia
        while(true) {
            int x = (int) (Math.random() * rows);
            int y = (int) (Math.random() * cols);
            if ((x > 0 || y > 0) && (x < rows || y > cols) && !(x == rows-1 && y == cols-1) && !(gameBoard[x][y] instanceof QuestionableCell ) && !gameBoard[x][y].isCat()){
                gameBoard[x][y] = new PropitiousCell(gameDatabaseUpdater.getQuestionFromDatabase(randomQuestionId),
                        gameDatabaseUpdater.getAnswerFromDatabase(randomQuestionId));
                break;
            }
        }

        //añadimos celda de pregunta no propicia
        while(true) {
            int x = (int) (Math.random() * rows);
            int y = (int) (Math.random() * cols);
            if ((x > 0 || y > 0) && (x < rows || y > cols) && !(x == rows-1 && y == cols-1) && !(gameBoard[x][y] instanceof QuestionableCell ) && !gameBoard[x][y].isCat()){
                gameBoard[x][y] = new UnpropitiousCell();
                break;
            }
        }
        //añadimos el queso
        gameBoard[rows-1][cols-1].setCheese();

        if ( !gameDatabaseUpdater.isGameCompletelyFinished() ) {
            totalEarnedPoints = gameDatabaseUpdater.getCurrentGamePoint();
//            totalEarnedPoints -= getCurrentCell().getPoints();
        }

        newGame = gameDatabaseUpdater.isGameCompletelyFinished();
        insertCellToDatabase(gameBoard);

        if(testMode) {
            printSolutionBoard(gameBoard);
        }



    }

    public void start() {
        if ( newGame ) {
            colMousePosition = 0;
            rowMousePosition = 0;
        }

        getCurrentCell().setDiscovered();
        completeMouseMovement();

    }

    public boolean hasWon() {
        return won;
    }

    public boolean hasLost() {
        return lost;
    }
    public int getTotalEarnedPoints() {
        return totalEarnedPoints;
    }

    public int getColMousePosition() {
        return colMousePosition;
    }

    public int getRowMousePosition() {
        return rowMousePosition;
    }

    public void makeCellAsDiscovered(int row,int col) {
        gameBoard[row][col].setDiscovered();
    }

    public String startMouseMovement(String movement) {
        switch (movement){
            case "l":
                if (colMousePosition == 0) {
                    return null;
                }
                if(gameBoard[rowMousePosition][colMousePosition-1].isDiscovered()) {
                    return null;
                }
                colMousePosition--;
                break;
            case "r":
                if (colMousePosition == gameBoard[rowMousePosition].length-1) {
                    return null;
                }
                if(gameBoard[rowMousePosition][colMousePosition+1].isDiscovered()) {
                    return null;
                }
                colMousePosition++;
                break;
            case "u":
                if (rowMousePosition == 0) {
                    return null;
                }
                if(gameBoard[rowMousePosition-1][colMousePosition].isDiscovered())
                    return null;
                rowMousePosition--;
                break;
            case "d":
                if (rowMousePosition == gameBoard.length-1) {
                    return null;
                }
                if(gameBoard[rowMousePosition+1][colMousePosition].isDiscovered()) {
                    return null;
                }
                rowMousePosition++;
                break;
        }
        getCurrentCell().setDiscovered();

        if (rowMousePosition == gameBoard.length-1 && colMousePosition == gameBoard[rowMousePosition].length-1 ){
            this.won = true;
            return null;
        }else if(getCurrentCell().isCat()){
            this.lost = true;
            return null;
        }else if(getCurrentCell() instanceof QuestionableCell){
            return ((QuestionableCell)getCurrentCell()).getQuestion();
        }
        completeMouseMovement();
        return null;
    }

    private GameCell getCurrentCell() {
        return gameBoard[rowMousePosition][colMousePosition];
    }

    public void updateMousePosition(int row,int column) {
        rowMousePosition = row;
        colMousePosition = column;
    }


    public int completeMouseMovement(String userAnswer) {
        if(getCurrentCell() instanceof PropitiousCell) {
            if (((PropitiousCell) getCurrentCell()).submitAnswer(userAnswer)) {
                this.totalEarnedPoints += 50;
                return 50;
            }
        }
        if(getCurrentCell() instanceof UnpropitiousCell) {
            if (!(((UnpropitiousCell) getCurrentCell()).submitAnswer(userAnswer))) {
                this.totalEarnedPoints -= 50;
                return -50;
            }
        }
        return 0;
    }

    public int completeMouseMovement() {
        if ( !newGame && firstTimeRun ) {
            firstTimeRun = false;
        } else {
            this.totalEarnedPoints += getCurrentCell().getPoints();
        }
        return getCurrentCell().getPoints();
    }

    public String getCurrentFigure() {
        return this.getCurrentCell().toString();
    }

    public static void insertCellToDatabase(GameCell[][] gameBoard) {
        // x = row , y = column
        if ( gameDatabaseUpdater.isGameCompletelyFinished() ) {
            gameDatabaseUpdater.deleteAllCell();
            for(int x = 0; x < gameBoard.length; x++){
                for(int y = 0; y < gameBoard[0].length ; y++){
                    gameDatabaseUpdater.insertCellData(x,y,gameBoard[x][y].toString());
                }
            }
        } else {
            for(int x = 0; x < gameBoard.length; x++){
                for(int y = 0; y < gameBoard[0].length ; y++){
                    gameBoard[x][y] = new GameCell();
                    String retrievedData = gameDatabaseUpdater.retrieveCellData(x,y);
                    if (retrievedData.equalsIgnoreCase("CH") ) {
                        gameBoard[x][y].setCheese();
                    } else if (retrievedData.equalsIgnoreCase("CC") ) {
                        gameBoard[x][y].setCat();
                    } else if (retrievedData.equalsIgnoreCase("++") ) {
                        gameBoard[x][y] = new PropitiousCell("Di el nombre de un IDE para programar en JAVA:","Intellij Idea,Intellij,NetBeans,Eclipse");
                    } else if ( retrievedData.equalsIgnoreCase("--") ) {
                        gameBoard[x][y] = new UnpropitiousCell();
                    } else {
                        gameBoard[x][y].setPoints(Integer.parseInt(retrievedData));
                    }
                }
            }
        }

    }


    public static void printSolutionBoard(GameCell[][] gameBoard){
        // x = row , y = column
        for(int x = 0; x < gameBoard.length; x++){
            for(int y = 0; y < gameBoard[0].length ; y++){
                System.out.print(gameBoard[x][y] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }


}
