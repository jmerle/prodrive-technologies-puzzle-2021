package com.jaspervanmerle.ptp2021;

import com.jaspervanmerle.ptp2021.core.Board;
import com.jaspervanmerle.ptp2021.core.WordList;
import com.jaspervanmerle.ptp2021.model.Move;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Runner {
    private final DecimalFormat integerFormat;
    private final DecimalFormat decimalFormat;

    private Runner() {
        integerFormat = new DecimalFormat();
        integerFormat.setMinimumFractionDigits(0);
        integerFormat.setMaximumFractionDigits(0);
        integerFormat.setGroupingUsed(true);
        integerFormat.setGroupingSize(3);

        decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
    }

    private void run(int size) {
        double startTime = System.nanoTime();

        System.out.println("Solving for size " + size);

        WordList wordList = WordList.fromStream(getClass().getResourceAsStream("/wordlist.txt"), size);
        Board board = new Board(size, wordList);

        Solver solver = new Solver(size, board, wordList);
        solver.solve();

        double endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1e6;

        List<Move> appliedMoves = board.getAppliedMoves();

        List<String> outputLines = new ArrayList<>();
        outputLines.add("");
        outputLines.add("Moves (" + integerFormat.format(appliedMoves.size()) + "):");
        outputLines.add(appliedMoves.stream().map(Move::toString).collect(Collectors.joining("\n")));
        outputLines.add("");
        outputLines.add("Board:");
        outputLines.add(board.toString());
        outputLines.add("");
        outputLines.add("Score: " + integerFormat.format(board.getScore()));
        outputLines.add("");
        outputLines.add("Solving for size " + size + " took " + decimalFormat.format(executionTimeMs) + "ms");

        for (String line : outputLines) {
            System.out.println(line);
        }

        try {
            saveBestScore(size, board, outputLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBestScore(int size, Board board, List<String> outputLines) throws IOException {
        Path projectDirectory = Paths.get("").toAbsolutePath();

        int bestScore = 0;

        String resultsFileName = (size < 10 ? "0" : "") + size + ".txt";
        Path resultsFile = projectDirectory.resolve("results").resolve(resultsFileName);
        if (Files.isRegularFile(resultsFile)) {
            for (String line : Files.readAllLines(resultsFile)) {
                if (!line.startsWith("Score: ")) {
                    continue;
                }

                String scoreStr = line.replace("Score: ", "").replaceAll(",", "");
                bestScore = Integer.parseInt(scoreStr);

                break;
            }
        }

        int newScore = board.getScore();
        if (newScore <= bestScore) {
            return;
        }

        System.out.println("Found a new best for size " + size);

        Files.writeString(resultsFile, String.join("\n", outputLines).trim() + "\n");

        Path readmeFile = projectDirectory.resolve("README.md");
        List<String> readmeLines = new ArrayList<>();

        for (String line : Files.readAllLines(readmeFile)) {
            if (!line.startsWith("| " + size + " |")) {
                readmeLines.add(line);
                continue;
            }

            List<String> columns = new ArrayList<>();
            columns.add(Integer.toString(size));
            columns.add("[" + integerFormat.format(newScore) + "](./results/" + resultsFileName + ")");

            readmeLines.add("| " + String.join(" | ", columns) + " |");
        }

        Files.writeString(readmeFile, String.join("\n", readmeLines).trim() + "\n");
    }

    public static void main(String[] args) {
        Runner runner = new Runner();

        if (args.length == 1) {
            runner.run(Integer.parseInt(args[0]));
        } else {
            for (int i = 6; i <= 30; i++) {
                runner.run(i);
            }
        }
    }
}
