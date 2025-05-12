package com.example.mousecheeseguigame.model;

import java.sql.*;

public class GameDatabaseUpdater {
    private final Connection connection;

    // cell_data table queries...
    // consultas de la tabla cell_data...
    private final String retrieveSpecificCellQuery;
    private final String retrieveAllCellQuery;
    private final String retrieveMouseCellQuery;
    private final String insertCellQuery;
    private final String updateCellQuery;
    private final String updateMouseCellQuery;
    private final String deleteCellQuery;
    private final String updateCurrentGamePointQuery;
    private final String getCurrentGamePointQuery;
    // cell_data table queries...
    // consultas de la tabla cell_data...

    // point_history table queries...
    // Consultas de la tabla point_history...
    private final String insertPointQuery;
    private final String highestPointQuery;
    // point_history table queries...
    // Consultas de la tabla point_history...

    private final String questionAnswerQuery;
    private final String totalQuestionQuery;

    private final String CELL_RESERVED_TEXT = "yes";
    private final String MOUSE_CELL_TEXT = "mouse";

    public GameDatabaseUpdater() {
        GameDatabase gameDatabase = new GameDatabase();
        connection = gameDatabase.getConnection();

        // consultas de la tabla cell_data...
        retrieveSpecificCellQuery = "select * from "+gameDatabase.getCELL_DATA_TABLE_NAME()+
                " where cell_row=? and cell_column=?";
        retrieveAllCellQuery = "select * from "+gameDatabase.getCELL_DATA_TABLE_NAME();
        retrieveMouseCellQuery = "select * from "+gameDatabase.getCELL_DATA_TABLE_NAME()+
                " where is_mouse=?";
        insertCellQuery =  "insert into "+ gameDatabase.getCELL_DATA_TABLE_NAME()+"(cell_row,cell_column,matrix) " +
                "VALUES(?,?,?)";
        updateCellQuery = "update "+gameDatabase.getCELL_DATA_TABLE_NAME()+" set is_cell_reserved=? " +
                "where cell_row=? AND cell_column=?";
        updateMouseCellQuery = "update "+gameDatabase.getCELL_DATA_TABLE_NAME()+" set is_mouse=? " +
                "where cell_row=? and cell_column=?";
        deleteCellQuery = "delete from "+gameDatabase.getCELL_DATA_TABLE_NAME()+" where cell_row=?";
        updateCurrentGamePointQuery = "update "+gameDatabase.getCELL_DATA_TABLE_NAME()+" set point=? " +
                "where cell_row=? and cell_column=?";
        getCurrentGamePointQuery = "select * from "+gameDatabase.getCELL_DATA_TABLE_NAME()+
                " where cell_row=? and cell_column=?";
        // consultas de la tabla cell_data...

        // Consultas de la tabla point_history...
        insertPointQuery = "insert into "+gameDatabase.getPOINT_HISTORY_TABLE_NAME()+"(points) values(?)";
        highestPointQuery = "select max(points) as points from "+gameDatabase.getPOINT_HISTORY_TABLE_NAME();
        // Consultas de la tabla point_history...


        totalQuestionQuery = "select count(*) as total from "+gameDatabase.getQUESTIONS_ANSWERS_TABLE_NAME();
        questionAnswerQuery = "select * from "+gameDatabase.getQUESTIONS_ANSWERS_TABLE_NAME()+" where id=?";
    }

    public boolean isGameCompletelyFinished() {
        boolean isGameFinished = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement(retrieveAllCellQuery);
             ResultSet resultSet = preparedStatement.executeQuery() ) {
            int cellLineCounter=0;
            while ( resultSet.next() ) {
                ++cellLineCounter;
                // if cheese cell or cat cell is stepped by mouse
                // si la celda de queso o la celda de gato es pisada por el mouse
                if ( resultSet.getString("matrix").equalsIgnoreCase("CH") ||
                    resultSet.getString("matrix").equalsIgnoreCase("CC") ) {
                    if ( resultSet.getString("is_cell_reserved") != null &&
                        resultSet.getString("is_cell_reserved").equalsIgnoreCase(CELL_RESERVED_TEXT) ) {
                        isGameFinished = true;
                        break;
                    }
                }
            }
            if ( cellLineCounter != 16 ) isGameFinished=true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isGameFinished;
    }


    // consultas de la tabla cell_data...
    public String retrieveCellData(int row,int column) {
        String retrievedData = "";
        try (PreparedStatement preparedStatement = connection.prepareStatement(retrieveSpecificCellQuery) ) {
            preparedStatement.setInt(1,row);
            preparedStatement.setInt(2,column);
            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                retrievedData = resultSet.getString("matrix");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retrievedData;
    }
    public void insertCellData(int row,int column,String matrix)  {
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCellQuery)) {
            preparedStatement.setInt(1,row);
            preparedStatement.setInt(2,column);
            preparedStatement.setString(3,matrix);
            preparedStatement.execute();
            updateCellData(0,0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateCellData(int row,int column) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateCellQuery)) {
            preparedStatement.setString(1,CELL_RESERVED_TEXT);
            preparedStatement.setInt(2,row);
            preparedStatement.setInt(3,column);
            preparedStatement.executeUpdate();
            updateMouseCell(row,column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updateMouseCell(int row,int column) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateMouseCellQuery);
        PreparedStatement getMousePreparedStatement = connection.prepareStatement(retrieveMouseCellQuery) ) {

            if (!( row == 0 && column == 0 )) {
                int mouseRowPos = 0;
                int mouseColPos = 0;
                getMousePreparedStatement.setString(1,MOUSE_CELL_TEXT);
                ResultSet resultSet = getMousePreparedStatement.executeQuery();
                while ( resultSet.next() ) {
                    mouseRowPos = resultSet.getInt("cell_row");
                    mouseColPos = resultSet.getInt("cell_column");
                }
                resultSet.close();
                preparedStatement.setNull(1 , Types.NULL );
                preparedStatement.setInt(2,mouseRowPos);
                preparedStatement.setInt(3,mouseColPos);
                preparedStatement.executeUpdate();

            }
            preparedStatement.setString(1,MOUSE_CELL_TEXT);
            preparedStatement.setInt(2,row);
            preparedStatement.setInt(3,column);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getMouseRowPos() {
        int rowPos=0;
        try ( PreparedStatement preparedStatement = connection.prepareStatement(retrieveMouseCellQuery) ) {
            preparedStatement.setString(1,MOUSE_CELL_TEXT);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rowPos = resultSet.getInt("cell_row");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowPos;
    }
    public int getMouseColPos() {
        int colPos=0;
        try ( PreparedStatement preparedStatement = connection.prepareStatement(retrieveMouseCellQuery) ) {
            preparedStatement.setString(1,MOUSE_CELL_TEXT);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                colPos = resultSet.getInt("cell_column");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colPos;
    }
    public boolean isCellReserved(int row,int col) {
        String retrievedData = "";
        try (PreparedStatement preparedStatement = connection.prepareStatement(retrieveSpecificCellQuery) ) {
            preparedStatement.setInt(1,row);
            preparedStatement.setInt(2,col);
            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                retrievedData = resultSet.getString("is_cell_reserved");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retrievedData!=null;
    }
    public void updateCurrentGamePoint (int point) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateCurrentGamePointQuery)) {
            preparedStatement.setInt(1,point);
            preparedStatement.setInt(2,0);
            preparedStatement.setInt(3,0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getCurrentGamePoint() {
        int point = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(getCurrentGamePointQuery)) {
            preparedStatement.setInt(1,0);
            preparedStatement.setInt(2,0);
            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                point = resultSet.getInt("point");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return point;
    }
    public void deleteAllCell() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteCellQuery)) {
            for ( int id = 0 ; id < 4 ; id++ ) {
                preparedStatement.setInt(1,id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void makeNewGame() {deleteAllCell();}

    // Consultas de la tabla point_history...
    public void recordGamePointInDatabase(int point) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertPointQuery)) {
            preparedStatement.setInt(1,point);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getHighestPoint() {
        int highestPoint = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(highestPointQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                highestPoint = resultSet.getInt("points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highestPoint;
    }

    public String getQuestionFromDatabase(int id) {
        String question = "";
        try (PreparedStatement preparedStatement = connection.prepareStatement(questionAnswerQuery)) {
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                question = resultSet.getString("question");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return question;
    }
    public String getAnswerFromDatabase(int id) {
        String answer = "";
        try (PreparedStatement preparedStatement = connection.prepareStatement(questionAnswerQuery)) {
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                answer = resultSet.getString("answer");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answer;
    }
    public int getTotalQuestion() {
        int total = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(totalQuestionQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                total = resultSet.getInt("total");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e ) {
            e.printStackTrace();
        }
    }
}
