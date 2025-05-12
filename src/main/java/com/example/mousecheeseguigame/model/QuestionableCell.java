package com.example.mousecheeseguigame.model;

public interface QuestionableCell {
    /**
     * Method that returns the question for this cell
     * @return The question to ask to the player.
     */
    public String getQuestion();

    /**
     * Method that decides if the submitted answer is right
     * @param answer
     * @return false if the answer is wrong, true if the answer is right.
     */
    public boolean submitAnswer(String answer);
}
