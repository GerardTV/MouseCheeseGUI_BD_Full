package com.example.mousecheeseguigame.model;

public class PropitiousCell extends GameCell implements QuestionableCell{
    private String question;
    private String[] answers;

    public PropitiousCell(String question, String answers) {

        this.question = question;
        this.answers = answers.toLowerCase().split(",");
    }

    @Override
    public String getQuestion() {
        return "Para ganar 50 puntos. "+this.question;
    }

    @Override
    public boolean submitAnswer(String answer) {
        for (String a :this.answers) {
            if(a.equals(answer.toLowerCase()))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "++";
    }

}
