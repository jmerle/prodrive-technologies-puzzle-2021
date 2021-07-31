package com.jaspervanmerle.ptp2021.model;

public enum Direction {
    Horizontal('H'),
    Vertical('V');

    private final char identifier;

    Direction(char identifier) {
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return identifier;
    }
}
