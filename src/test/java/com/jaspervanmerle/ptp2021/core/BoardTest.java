package com.jaspervanmerle.ptp2021.core;

import com.jaspervanmerle.ptp2021.model.Direction;
import com.jaspervanmerle.ptp2021.model.Move;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    @Test
    void getCellReturnsCellSetBySetCell() {
        Board board = new Board(5, new WordList());

        board.setCell(0, 0, 'A');

        assertEquals('A', board.getCell(0, 0));
    }

    @Test
    void getCellReturnsEmptyCellWhenCellNotSet() {
        Board board = new Board(5, new WordList());

        assertEquals(Board.EMPTY_CELL, board.getCell(0, 0));
    }

    @ParameterizedTest
    @CsvSource({"-1,0", "0,-1", "5,0", "0,5", "-1,-1", "5,5", "-1,5", "5,-1"})
    void setCellThrowsRuntimeExceptionWhenIndexOutOfBounds(int x, int y) {
        Board board = new Board(5, new WordList());

        assertThrows(RuntimeException.class, () -> board.setCell(x, y, 'A'));
    }

    @Test
    void applyMoveAppliesMovesInOrder() {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        board.applyMove(new Move(2, 4, Direction.Vertical, "pro"));
        board.applyMove(new Move(2, 4, Direction.Vertical, "prodrive"));
        board.applyMove(new Move(0, 11, Direction.Horizontal, "awesome"));
        board.applyMove(new Move(6, 6, Direction.Vertical, "online"));
        board.applyMove(new Move(4, 7, Direction.Horizontal, "contest"));
        board.applyMove(new Move(5, 10, Direction.Vertical, "amazing"));

        List<Move> appliedMoves = board.getAppliedMoves();

        assertEquals(6, appliedMoves.size());
        assertEquals("4,2,V,pro", appliedMoves.get(0).toString());
        assertEquals("4,2,V,prodrive", appliedMoves.get(1).toString());
        assertEquals("11,0,H,awesome", appliedMoves.get(2).toString());
        assertEquals("6,6,V,online", appliedMoves.get(3).toString());
        assertEquals("7,4,H,contest", appliedMoves.get(4).toString());
        assertEquals("10,5,V,amazing", appliedMoves.get(5).toString());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void applyMoveUpdatesScoreAfterEachMove(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);
        assertEquals(16, board.getScore());

        applyMove(board, new Move(2, 4, Direction.Vertical, "prodrive"), transpose);
        assertEquals(55, board.getScore());

        applyMove(board, new Move(0, 11, Direction.Horizontal, "awesome"), transpose);
        assertEquals(79, board.getScore());

        applyMove(board, new Move(6, 6, Direction.Vertical, "online"), transpose);
        assertEquals(102, board.getScore());

        applyMove(board, new Move(4, 7, Direction.Horizontal, "contest"), transpose);
        assertEquals(141, board.getScore());

        applyMove(board, new Move(5, 10, Direction.Vertical, "amazing"), transpose);
        assertEquals(366, board.getScore());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void applyMoveThrowsRuntimeExceptionWhenMoveCreatesInvalidWord(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);
        applyMove(board, new Move(2, 4, Direction.Vertical, "prodrive"), transpose);
        applyMove(board, new Move(0, 11, Direction.Horizontal, "awesome"), transpose);
        applyMove(board, new Move(6, 6, Direction.Vertical, "online"), transpose);
        applyMove(board, new Move(4, 7, Direction.Horizontal, "contest"), transpose);

        assertThrows(RuntimeException.class, () -> applyMove(board, new Move(3, 4, Direction.Vertical, "amazing"), transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void applyMoveThrowsRuntimeExceptionWhenWordWasPlayedBefore(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);
        applyMove(board, new Move(2, 4, Direction.Vertical, "prodrive"), transpose);
        applyMove(board, new Move(0, 11, Direction.Horizontal, "awesome"), transpose);
        applyMove(board, new Move(6, 6, Direction.Vertical, "online"), transpose);
        applyMove(board, new Move(4, 7, Direction.Horizontal, "contest"), transpose);
        applyMove(board, new Move(5, 10, Direction.Vertical, "amazing"), transpose);

        assertThrows(RuntimeException.class, () -> applyMove(board, new Move(0, 11, Direction.Vertical, "an"), transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void applyMoveThrowsRuntimeExceptionWhenWordIsPartOfLongerWord(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);
        applyMove(board, new Move(2, 4, Direction.Vertical, "prodrive"), transpose);

        assertThrows(RuntimeException.class, () -> applyMove(board, new Move(2, 4, Direction.Vertical, "prod"), transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void applyMoveThrowsRuntimeExceptionWhenWordOverlapsDifferentCharacters(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);

        assertThrows(RuntimeException.class, () -> applyMove(board, new Move(2, 4, Direction.Vertical, "kat"), transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void applyMoveThrowsRuntimeExceptionWhenMoveGeneratesSameWordTwice(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(0, 0, Direction.Vertical, "ijs"), transpose);

        assertThrows(RuntimeException.class, () -> applyMove(board, new Move(1, 0, Direction.Vertical, "ja"), transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void applyMoveThrowsRuntimeExceptionWhenMoveDoesNotFit(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        assertThrows(RuntimeException.class, () -> applyMove(board, new Move(15, 15, Direction.Vertical, "ijs"), transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void applyMoveThrowsRuntimeExceptionWhenWordIsOneCharacter(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        assertThrows(RuntimeException.class, () -> applyMove(board, new Move(0, 0, Direction.Horizontal, "a"), transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsScoreOfNewWordsOnly(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        Move currentMove = new Move(2, 4, Direction.Vertical, "pro");
        assertEquals(16, getMoveScore(board, currentMove, transpose));
        applyMove(board, currentMove, transpose);

        currentMove = new Move(2, 4, Direction.Vertical, "prodrive");
        assertEquals(39, getMoveScore(board, currentMove, transpose));
        applyMove(board, currentMove, transpose);

        currentMove = new Move(0, 11, Direction.Horizontal, "awesome");
        assertEquals(24, getMoveScore(board, currentMove, transpose));
        applyMove(board, currentMove, transpose);

        currentMove = new Move(6, 6, Direction.Vertical, "online");
        assertEquals(23, getMoveScore(board, currentMove, transpose));
        applyMove(board, currentMove, transpose);

        currentMove = new Move(4, 7, Direction.Horizontal, "contest");
        assertEquals(39, getMoveScore(board, currentMove, transpose));
        applyMove(board, currentMove, transpose);

        currentMove = new Move(5, 10, Direction.Vertical, "amazing");
        assertEquals(225, getMoveScore(board, currentMove, transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsScoreOfMoveWithoutApplyingIt(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);
        applyMove(board, new Move(2, 4, Direction.Vertical, "prodrive"), transpose);
        applyMove(board, new Move(0, 11, Direction.Horizontal, "awesome"), transpose);
        applyMove(board, new Move(6, 6, Direction.Vertical, "online"), transpose);
        applyMove(board, new Move(4, 7, Direction.Horizontal, "contest"), transpose);

        int moveScore = getMoveScore(board, new Move(5, 10, Direction.Vertical, "amazing"), transpose);

        assertEquals(225, moveScore);
        assertEquals(141, board.getScore());
        assertEquals(5, board.getAppliedMoves().size());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreOnlyAppliesMultipliersToNewTiles(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        Move currentMove = new Move(0, 0, Direction.Horizontal, "tr");
        assertEquals(48, getMoveScore(board, currentMove, transpose));
        applyMove(board, currentMove, transpose);

        currentMove = new Move(0, 0, Direction.Horizontal, "tri");
        assertEquals(24, getMoveScore(board, currentMove, transpose));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsInvalidMoveWhenMoveCreatesInvalidWord(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);
        applyMove(board, new Move(2, 4, Direction.Vertical, "prodrive"), transpose);
        applyMove(board, new Move(0, 11, Direction.Horizontal, "awesome"), transpose);
        applyMove(board, new Move(6, 6, Direction.Vertical, "online"), transpose);
        applyMove(board, new Move(4, 7, Direction.Horizontal, "contest"), transpose);

        int moveScore = getMoveScore(board, new Move(3, 4, Direction.Vertical, "amazing"), transpose);

        assertEquals(Board.INVALID_MOVE, moveScore);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsInvalidMoveWhenWordWasPlayedBefore(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);
        applyMove(board, new Move(2, 4, Direction.Vertical, "prodrive"), transpose);
        applyMove(board, new Move(0, 11, Direction.Horizontal, "awesome"), transpose);
        applyMove(board, new Move(6, 6, Direction.Vertical, "online"), transpose);
        applyMove(board, new Move(4, 7, Direction.Horizontal, "contest"), transpose);
        applyMove(board, new Move(5, 10, Direction.Vertical, "amazing"), transpose);

        int moveScore = getMoveScore(board, new Move(0, 11, Direction.Vertical, "an"), transpose);

        assertEquals(Board.INVALID_MOVE, moveScore);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsInvalidMoveWhenWordIsPartOfLongerWord(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);
        applyMove(board, new Move(2, 4, Direction.Vertical, "prodrive"), transpose);

        int moveScore = getMoveScore(board, new Move(2, 4, Direction.Vertical, "prod"), transpose);

        assertEquals(Board.INVALID_MOVE, moveScore);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsInvalidMoveWhenWordOverlapsDifferentCharacters(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(2, 4, Direction.Vertical, "pro"), transpose);

        int moveScore = getMoveScore(board, new Move(2, 4, Direction.Vertical, "kat"), transpose);

        assertEquals(Board.INVALID_MOVE, moveScore);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsInvalidMoveWhenMoveGeneratesSameWordTwice(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        applyMove(board, new Move(0, 0, Direction.Vertical, "ijs"), transpose);

        int moveScore = getMoveScore(board, new Move(1, 0, Direction.Vertical, "ja"), transpose);

        assertEquals(Board.INVALID_MOVE, moveScore);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsInvalidMoveWhenMoveDoesNotFit(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        int moveScore = getMoveScore(board, new Move(15, 15, Direction.Vertical, "ijs"), transpose);

        assertEquals(Board.INVALID_MOVE, moveScore);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getMoveScoreReturnsInvalidMoveWhenWordIsOneCharacter(boolean transpose) {
        Board board = new Board(17, WordList.fromStream(getClass().getResourceAsStream("/wordlist-full.txt")));

        int moveScore = getMoveScore(board, new Move(0, 0, Direction.Horizontal, "a"), transpose);

        assertEquals(Board.INVALID_MOVE, moveScore);
    }

    @Test
    void toStringReturnsReadableBoard() {
        Board board = new Board(6, new WordList());

        board.setCell(0, 0, 'a');
        board.setCell(1, 1, 'b');
        board.setCell(2, 2, 'c');
        board.setCell(3, 3, 'd');
        board.setCell(4, 4, 'e');
        board.setCell(5, 5, 'f');

        List<String> lines = new ArrayList<>();
        lines.add("  0  1  2  3  4  5");
        lines.add("0 A  ▢  ▢  ▢  ▢  ▣");
        lines.add("1 ▢  b  ▢  ▢  ▢  ▢");
        lines.add("2 ▢  ▢  c  ▢  ▢  ▢");
        lines.add("3 ▢  ▢  ▢  d  ▢  ▢");
        lines.add("4 ▢  ▢  ▢  ▢  e  ▢");
        lines.add("5 ▣  ▢  ▢  ▢  ▢  F");

        assertEquals(String.join("\n", lines), board.toString());
    }

    private void applyMove(Board board, Move move, boolean transpose) {
        if (transpose) {
            move = move.transpose();
        }

        board.applyMove(move);
    }

    private int getMoveScore(Board board, Move move, boolean transpose) {
        if (transpose) {
            move = move.transpose();
        }

        return board.getMoveScore(move);
    }
}
