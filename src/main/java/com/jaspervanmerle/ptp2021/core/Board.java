package com.jaspervanmerle.ptp2021.core;

import com.jaspervanmerle.ptp2021.model.Direction;
import com.jaspervanmerle.ptp2021.model.Move;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {
    public static final char EMPTY_CELL = '▢';
    public static final int INVALID_MOVE = -1;

    private final int size;
    private final WordList wordList;

    private final char[][] cells;

    private int score = 0;
    private final List<Move> appliedMoves = new ArrayList<>();
    private final Set<String> playedWords = new HashSet<>();

    public Board(int size, WordList wordList) {
        this.size = size;
        this.wordList = wordList;

        cells = new char[size][size];

        for (int y = 0; y < size; y++) {
            cells[y] = new char[size];

            for (int x = 0; x < size; x++) {
                cells[y][x] = EMPTY_CELL;
            }
        }
    }

    public char getCell(int x, int y) {
        return cells[y][x];
    }

    public void setCell(int x, int y, char ch) {
        cells[y][x] = ch;
    }

    public void applyMove(Move move) {
        int moveScore = getMoveScore(move, true);

        if (moveScore == INVALID_MOVE) {
            throw new RuntimeException("Move is invalid");
        }

        score += moveScore;
        appliedMoves.add(move);
    }

    public int getMoveScore(Move move) {
        return getMoveScore(move, false);
    }

    public int getScore() {
        return score;
    }

    public List<Move> getAppliedMoves() {
        return new ArrayList<>(appliedMoves);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(' ');
        if (size > 10) {
            sb.append(' ');
        }

        for (int i = 0; i < size; i++) {
            if (i < 10) {
                sb.append(' ');
            }

            sb.append(i);

            if (i != size - 1) {
                sb.append(' ');
            }
        }

        sb.append('\n');

        for (int y = 0; y < size; y++) {
            if (y < 10 && size > 10) {
                sb.append(' ');
            }

            sb.append(y);

            for (int x = 0; x < size; x++) {
                sb.append(' ');

                if (x % 5 == 0 && y % 5 == 0) {
                    if (getCell(x, y) == EMPTY_CELL) {
                        sb.append('▣');
                    } else {
                        sb.append(Character.toUpperCase(getCell(x, y)));
                    }
                } else {
                    sb.append(getCell(x, y));
                }

                if (x != size - 1) {
                    sb.append(' ');
                }
            }

            if (y != size - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    private int getMoveScore(Move move, boolean saveState) {
        String word = move.getWord();
        if (!wordList.contains(word)) {
            return INVALID_MOVE;
        }

        char[] characters = move.getWord().toCharArray();
        if (characters.length == 1) {
            return INVALID_MOVE;
        }

        if (move.getDirection() == Direction.Horizontal) {
            return getMoveScoreHorizontal(move.getStartX(), move.getStartY(), characters, saveState);
        } else {
            return getMoveScoreVertical(move.getStartX(), move.getStartY(), characters, saveState);
        }
    }

    private int getMoveScoreHorizontal(int startX, int startY, char[] word, boolean saveState) {
        int moveScore = 0;

        if (startX + word.length > size) {
            return INVALID_MOVE;
        }

        if (startX != 0 && getCell(startX - 1, startY) != EMPTY_CELL) {
            return INVALID_MOVE;
        }

        if (startX + word.length < size && getCell(startX + word.length, startY) != EMPTY_CELL) {
            return INVALID_MOVE;
        }

        Set<Integer> newCells = new HashSet<>();

        for (int i = 0; i < word.length; i++) {
            char cell = getCell(startX + i, startY);

            if (cell == EMPTY_CELL) {
                newCells.add(encodeCoordinate(startX + i, startY));
            }

            if (cell != EMPTY_CELL && cell != word[i]) {
                return INVALID_MOVE;
            }
        }

        Set<String> wordsThisMove = new HashSet<>();

        for (int i = 0; i < word.length; i++) {
            if (getCell(startX + i, startY) != EMPTY_CELL) {
                continue;
            }

            setCell(startX + i, startY, word[i]);

            int wordStartX = startX + i;
            int wordStartY = startY;

            while (wordStartY != 0 && getCell(wordStartX, wordStartY - 1) != EMPTY_CELL) {
                wordStartY--;
            }

            int indirectWordScore = getWordScore(wordStartX, wordStartY, 0, 1, newCells, wordsThisMove, saveState);

            if (!saveState) {
                setCell(startX + i, startY, EMPTY_CELL);
            }

            if (indirectWordScore == INVALID_MOVE) {
                return INVALID_MOVE;
            }

            moveScore += indirectWordScore;
        }

        char[] originalCells = new char[word.length];
        for (int i = 0; i < word.length; i++) {
            originalCells[i] = getCell(startX + i, startY);
            setCell(startX + i, startY, word[i]);
        }

        int mainWordScore = getWordScore(startX, startY, 1, 0, newCells, wordsThisMove, saveState);

        if (!saveState) {
            for (int i = 0; i < word.length; i++) {
                setCell(startX + i, startY, originalCells[i]);
            }
        }

        if (mainWordScore == INVALID_MOVE) {
            return INVALID_MOVE;
        }

        moveScore += mainWordScore;

        return moveScore;
    }

    private int getMoveScoreVertical(int startX, int startY, char[] word, boolean saveState) {
        int moveScore = 0;

        if (startY + word.length > size) {
            return INVALID_MOVE;
        }

        if (startY != 0 && getCell(startX, startY - 1) != EMPTY_CELL) {
            return INVALID_MOVE;
        }

        if (startY + word.length < size && getCell(startX, startY + word.length) != EMPTY_CELL) {
            return INVALID_MOVE;
        }

        Set<Integer> newCells = new HashSet<>();

        for (int i = 0; i < word.length; i++) {
            char cell = getCell(startX, startY + i);

            if (cell == EMPTY_CELL) {
                newCells.add(encodeCoordinate(startX, startY + i));
            }

            if (cell != EMPTY_CELL && cell != word[i]) {
                return INVALID_MOVE;
            }
        }

        Set<String> wordsThisMove = new HashSet<>();

        for (int i = 0; i < word.length; i++) {
            if (getCell(startX, startY + i) != EMPTY_CELL) {
                continue;
            }

            setCell(startX, startY + i, word[i]);

            int wordStartX = startX;
            int wordStartY = startY + i;

            while (wordStartX != 0 && getCell(wordStartX - 1, wordStartY) != EMPTY_CELL) {
                wordStartX--;
            }

            int indirectWordScore = getWordScore(wordStartX, wordStartY, 1, 0, newCells, wordsThisMove, saveState);

            if (!saveState) {
                setCell(startX, startY + i, EMPTY_CELL);
            }

            if (indirectWordScore == INVALID_MOVE) {
                return INVALID_MOVE;
            }

            moveScore += indirectWordScore;
        }

        char[] originalCells = new char[word.length];
        for (int i = 0; i < word.length; i++) {
            originalCells[i] = getCell(startX, startY + i);
            setCell(startX, startY + i, word[i]);
        }

        int mainWordScore = getWordScore(startX, startY, 0, 1, newCells, wordsThisMove, saveState);

        if (!saveState) {
            for (int i = 0; i < word.length; i++) {
                setCell(startX, startY + i, originalCells[i]);
            }
        }

        if (mainWordScore == INVALID_MOVE) {
            return INVALID_MOVE;
        }

        moveScore += mainWordScore;

        return moveScore;
    }

    private int getWordScore(int startX, int startY, int xDelta, int yDelta, Set<Integer> newCells, Set<String> wordsThisMove, boolean saveState) {
        StringBuilder word = new StringBuilder();
        int wordScore = 0;
        int scoreMultiplier = 1;

        int currentX = startX;
        int currentY = startY;

        while (currentX < size && currentY < size) {
            char cell = getCell(currentX, currentY);

            if (cell == EMPTY_CELL) {
                break;
            }

            word.append(cell);

            wordScore += (cell - 'a') % 10;
            if (currentX % 5 == 0 && currentY % 5 == 0 && newCells.contains(encodeCoordinate(currentX, currentY))) {
                scoreMultiplier *= 3;
            }

            currentX += xDelta;
            currentY += yDelta;
        }

        String fullWord = word.toString();

        if (fullWord.length() == 1) {
            return 0;
        }

        if (!wordList.contains(fullWord) || playedWords.contains(fullWord) || wordsThisMove.contains(fullWord)) {
            return INVALID_MOVE;
        }

        wordsThisMove.add(fullWord);

        if (saveState) {
            playedWords.add(fullWord);
        }

        return wordScore * scoreMultiplier;
    }

    private int encodeCoordinate(int x, int y) {
        return y * size + x;
    }
}
