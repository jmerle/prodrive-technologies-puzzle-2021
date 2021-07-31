package com.jaspervanmerle.ptp2021.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {
    @Test
    void transposeReturnsTransposedMove() {
        Move move = new Move(2, 4, Direction.Vertical, "prodrive");

        Move newMove = move.transpose();

        assertEquals(4, newMove.getStartX());
        assertEquals(2, newMove.getStartY());
        assertEquals(Direction.Horizontal, newMove.getDirection());
        assertEquals("prodrive", newMove.getWord());
    }

    @Test
    void transposeDoesNotModifyCurrentMove() {
        Move move = new Move(2, 4, Direction.Vertical, "prodrive");

        move.transpose();

        assertEquals(2, move.getStartX());
        assertEquals(4, move.getStartY());
        assertEquals(Direction.Vertical, move.getDirection());
        assertEquals("prodrive", move.getWord());
    }

    @Test
    void toStringReturnsMoveInSubmissionFormat() {
        Move move = new Move(2, 4, Direction.Vertical, "prodrive");

        assertEquals("4,2,V,prodrive", move.toString());
    }
}
