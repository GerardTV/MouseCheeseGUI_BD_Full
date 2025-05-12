package com.example.mousecheeseguigame.model;

public class GameCell {
    private int points;
    private boolean isCat;
    private boolean isCheese;
    private boolean isDiscovered;

    public GameCell() {
        this.points = (int) ((Math.random()*3)+1) * 10;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public boolean isCat() {
        return isCat;
    }

    public void setCat() {
        isCat = true;
    }

    public void setCheese() {
        isCheese = true;
    }

    public boolean isDiscovered() {
        return isDiscovered;
    }

    public void setDiscovered() {
        isDiscovered = true;
    }

    @Override
    public String toString() {
        if(isCheese)
            return "CH";
        if(isCat)
            return "CC";
        else
            return String.format("%d",points);
    }
}
