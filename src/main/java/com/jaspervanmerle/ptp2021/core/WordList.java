package com.jaspervanmerle.ptp2021.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WordList implements Iterable<String> {
    private final Set<String> words = new HashSet<>(1_000_000);

    public void addWord(String word) {
        words.add(word);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public int getSize() {
        return words.size();
    }

    @Override
    public Iterator<String> iterator() {
        return words.iterator();
    }

    public static WordList fromStream(InputStream inputStream, int maxLength) {
        WordList list = new WordList();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        try {
            while ((line = br.readLine()) != null) {
                if (line.length() <= maxLength) {
                    list.addWord(line);
                }
            }
        } catch (IOException e) {
            // Do nothing
        }

        return list;
    }

    public static WordList fromStream(InputStream inputStream) {
        return fromStream(inputStream, Integer.MAX_VALUE);
    }
}
