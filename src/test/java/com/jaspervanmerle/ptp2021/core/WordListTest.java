package com.jaspervanmerle.ptp2021.core;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordListTest {
    @Test
    void containsReturnsTrueWhenWordIsInList() {
        WordList wordList = new WordList();

        wordList.addWord("word1");
        wordList.addWord("word2");
        wordList.addWord("word3");

        assertTrue(wordList.contains("word2"));
    }

    @Test
    void containsReturnsFalseWhenWordIsNotInList() {
        WordList wordList = new WordList();

        wordList.addWord("word1");
        wordList.addWord("word2");
        wordList.addWord("word3");

        assertFalse(wordList.contains("word4"));
    }

    @Test
    void getSizeReturnsNumberOfWordsInList() {
        WordList wordList = new WordList();

        wordList.addWord("word1");
        wordList.addWord("word2");
        wordList.addWord("word3");

        assertEquals(3, wordList.getSize());
    }

    @Test
    void iteratorReturnsIteratorGoingOverAllWordsInList() {
        WordList wordList = new WordList();

        wordList.addWord("word1");
        wordList.addWord("word2");
        wordList.addWord("word3");

        List<String> words = new ArrayList<>();

        for (String word : wordList) {
            words.add(word);
        }

        assertEquals(3, words.size());
        assertTrue(words.contains("word1"));
        assertTrue(words.contains("word2"));
        assertTrue(words.contains("word3"));
    }

    @Test
    void fromStreamReadsWordsFromStream() {
        InputStream inputStream = getClass().getResourceAsStream("/wordlist-small.txt");

        WordList wordList = WordList.fromStream(inputStream);

        assertEquals(5, wordList.getSize());
        assertTrue(wordList.contains("w"));
        assertTrue(wordList.contains("wo"));
        assertTrue(wordList.contains("wor"));
        assertTrue(wordList.contains("word"));
        assertTrue(wordList.contains("words"));
    }

    @Test
    void fromStreamDiscardsWordsLongerThanGivenMaxLength() {
        InputStream inputStream = getClass().getResourceAsStream("/wordlist-small.txt");

        WordList wordList = WordList.fromStream(inputStream, 3);

        assertEquals(3, wordList.getSize());
        assertTrue(wordList.contains("w"));
        assertTrue(wordList.contains("wo"));
        assertTrue(wordList.contains("wor"));
    }
}
