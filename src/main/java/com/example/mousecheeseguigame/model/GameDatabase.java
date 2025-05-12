package com.example.mousecheeseguigame.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GameDatabase {
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private final String URL = "jdbc:mysql://localhost:3306/mouse_cheese_game";
    private final String CELL_DATA_TABLE_NAME = "cell_data";
    private final String POINT_HISTORY_TABLE_NAME = "point_history";
    private final String QUESTIONS_ANSWERS_TABLE_NAME = "questions_answers";
    private Connection connection;
    public GameDatabase() {
        try {
            connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public String getCELL_DATA_TABLE_NAME() {
        return CELL_DATA_TABLE_NAME;
    }

    public String getPOINT_HISTORY_TABLE_NAME() {
        return POINT_HISTORY_TABLE_NAME;
    }

    public String getQUESTIONS_ANSWERS_TABLE_NAME() {
        return QUESTIONS_ANSWERS_TABLE_NAME;
    }
}
