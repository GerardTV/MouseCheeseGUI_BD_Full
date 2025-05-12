package com.example.mousecheeseguigame.model;

public class UnpropitiousCell extends GameCell implements QuestionableCell{

    @Override
    public String getQuestion() {
        return "Insert a number between 1 and 3";
    }

    @Override
    public boolean submitAnswer(String answer) {
        int intValue = 0;
        try {
            intValue = Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            return false;
        }
        int badLuckNumber = (int) (Math.random()*3)+1;
        return intValue == badLuckNumber;
    }

    @Override
    public String toString() {
        return "--";
    }
}
