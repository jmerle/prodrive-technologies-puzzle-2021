package com.jaspervanmerle.ptp2021;

import com.jaspervanmerle.ptp2021.core.Board;
import com.jaspervanmerle.ptp2021.core.WordList;
import com.jaspervanmerle.ptp2021.model.Direction;
import com.jaspervanmerle.ptp2021.model.Move;

public class Solver {
    private final int size;
    private final Board board;
    private final WordList wordList;

    public Solver(int size, Board board, WordList wordList) {
        this.size = size;
        this.board = board;
        this.wordList = wordList;
    }

    public void solve() {
        playBestWord(0, 0, Direction.Horizontal, false);
        playBestWord(0, 0, Direction.Vertical, false);

        for (int i = 5; i < size; i += 5) {
            if (board.getCell(0, i) != Board.EMPTY_CELL) {
                playBestWord(0, i, Direction.Horizontal, false);
            }
        }

        for (int i = 5; i < size; i += 5) {
            if (board.getCell(i, 0) != Board.EMPTY_CELL) {
                playBestWord(i, 0, Direction.Vertical, true);
            }
        }

        for (int i = 2; i < size; i += 5) {
            if (board.getCell(0, i) != Board.EMPTY_CELL) {
                playBestWord(0, i, Direction.Horizontal, true);
            }
        }

        for (int i = 2; i < size; i += 5) {
            if (board.getCell(i, 0) != Board.EMPTY_CELL) {
                playBestWord(i, 0, Direction.Vertical, true);
            }
        }

        for (int i = 0; i < size; i++) {
            if (board.getCell(0, i) != Board.EMPTY_CELL) {
                playBestWord(0, i, Direction.Horizontal, true);
            }
        }

        for (int i = 0; i < size; i++) {
            if (board.getCell(i, 0) != Board.EMPTY_CELL) {
                playBestWord(i, 0, Direction.Vertical, true);
            }
        }
    }

    private void playBestWord(int startX, int startY, Direction direction, boolean useSubMoves) {
        String bestWord = "";
        int bestScore = 0;

        for (String word : wordList) {
            Move move = new Move(startX, startY, direction, word);
            int score = board.getMoveScore(move);

            if (score != Board.INVALID_MOVE && score > bestScore) {
                bestWord = word;
                bestScore = score;
            }
        }

        if (useSubMoves) {
            for (int i = 0; i <= bestWord.length(); i++) {
                Move subMove = new Move(startX, startY, direction, bestWord.substring(0, i));
                if (board.getMoveScore(subMove) != Board.INVALID_MOVE) {
                    board.applyMove(subMove);
                }
            }
        } else {
            board.applyMove(new Move(startX, startY, direction, bestWord));
        }
    }
}
