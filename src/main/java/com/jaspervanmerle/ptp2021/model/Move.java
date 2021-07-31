package com.jaspervanmerle.ptp2021.model;

public class Move {
    private final int startX;
    private final int startY;
    private final Direction direction;
    private final String word;

    public Move(int startX, int startY, Direction direction, String word) {
        this.startX = startX;
        this.startY = startY;
        this.direction = direction;
        this.word = word;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getWord() {
        return word;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Move transpose() {
        Direction newDirection = direction == Direction.Horizontal ? Direction.Vertical : Direction.Horizontal;
        return new Move(startY, startX, newDirection, word);
    }

    @Override
    public String toString() {
        return startY + "," + startX + "," + direction.getIdentifier() + "," + word;
    }
}
