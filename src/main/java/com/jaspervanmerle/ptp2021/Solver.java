package com.jaspervanmerle.ptp2021;

import com.jaspervanmerle.ptp2021.core.Board;
import com.jaspervanmerle.ptp2021.core.WordList;
import com.jaspervanmerle.ptp2021.model.Direction;
import com.jaspervanmerle.ptp2021.model.Move;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

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
        playBestMultiplierMoves();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!isValidStart(x, y)) {
                    continue;
                }

                playBestWord(x, y, Direction.Horizontal);
                playBestWord(x, y, Direction.Vertical);
            }
        }
    }

    private void playBestMultiplierMoves() {
        Queue<String> multiplierWords = new ArrayDeque<>(getBestMultiplierWords());

        List<String> connectingWords = new ArrayList<>(1_000_000);
        for (String word : wordList) {
            if (word.length() == 5) {
                connectingWords.add(word);
            }
        }

        int multiplierTileCount = (size - 1) / 5 + 1;
        for (int i = 0; i < multiplierTileCount; i++) {
            int previousY = (i - 1) * 5;
            int currentY = i * 5;

            while (true) {
                String currentWord = multiplierWords.poll();
                if (currentWord == null) {
                    break;
                }

                if (i == 0) {
                    board.applyMove(new Move(0, currentY, Direction.Horizontal, currentWord));
                    break;
                }

                Move bestConnectingMove = null;
                int bestConnectingScore = 0;

                int currentWordLength = currentWord.length();
                for (int j = 0; j < size && j < currentWordLength; j++) {
                    char startCell = board.getCell(j, previousY);
                    if (startCell == Board.EMPTY_CELL) {
                        continue;
                    }

                    char endCell = currentWord.charAt(j);

                    for (String connectingWord : connectingWords) {
                        if (connectingWord.charAt(0) != startCell) {
                            continue;
                        }

                        String fullConnectingWord = connectingWord + endCell;
                        if (currentWord.equals(fullConnectingWord) || !wordList.contains(fullConnectingWord)) {
                            continue;
                        }

                        Move move = new Move(j, previousY, Direction.Vertical, connectingWord);
                        int score = board.getMoveScore(move);

                        if (score != Board.INVALID_MOVE && score > bestConnectingScore) {
                            Move fullConnectingMove = new Move(j, previousY, Direction.Vertical, fullConnectingWord);
                            if (board.getMoveScore(fullConnectingMove) == Board.INVALID_MOVE) {
                                continue;
                            }

                            bestConnectingMove = move;
                            bestConnectingScore = score;
                        }
                    }
                }

                if (bestConnectingMove == null) {
                    continue;
                }

                board.applyMove(bestConnectingMove);
                board.applyMove(new Move(0, currentY, Direction.Horizontal, currentWord));
                break;
            }
        }
    }

    private List<String> getBestMultiplierWords() {
        Map<String, Integer> multiplierWords = new HashMap<>();

        for (String word : wordList) {
            Move move = new Move(0, 0, Direction.Horizontal, word);
            int score = board.getMoveScore(move);

            if (score != Board.INVALID_MOVE) {
                multiplierWords.put(word, score);
            }
        }

        return multiplierWords
                .keySet()
                .stream()
                .sorted(Comparator.comparing(multiplierWords::get).reversed())
                .collect(Collectors.toList());
    }

    private void playBestWord(int startX, int startY, Direction direction) {
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

        for (int i = 0; i <= bestWord.length(); i++) {
            Move subMove = new Move(startX, startY, direction, bestWord.substring(0, i));
            if (board.getMoveScore(subMove) != Board.INVALID_MOVE) {
                board.applyMove(subMove);
            }
        }
    }

    private boolean isValidStart(int x, int y) {
        if (board.getCell(x, y) != Board.EMPTY_CELL) return true;
        if (x != 0 && board.getCell(x - 1, y) != Board.EMPTY_CELL) return true;
        if (x != size - 1 && board.getCell(x + 1, y) != Board.EMPTY_CELL) return true;
        if (y != 0 && board.getCell(x, y - 1) != Board.EMPTY_CELL) return true;
        if (y != size - 1 && board.getCell(x, y + 1) != Board.EMPTY_CELL) return true;

        return false;
    }
}
