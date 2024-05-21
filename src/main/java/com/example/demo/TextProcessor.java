package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class TextProcessor extends Base implements TextProcessorInterface {
    public static void main(String[] args) {
        String text = "This is a sample sentence, with some stop words like 'the' and 'is'. my name is taha, he is abdullah, I'm not a nigga";
        List<String> stemmedWords = processText(text);
        System.out.println("Stemmed Words: " + stemmedWords);
    }
    public static List<String> processText(String text) {
        text = text.toLowerCase();
        String[] tokens = tokenizer.tokenize(text);
        List<String> tokensCleaned = new ArrayList<>();
        for (String token: tokens) {
            token = token.replaceAll("[^a-zA-Z0-9\\s]", "");
            if (token.isEmpty()) {
                continue;
            }
            tokensCleaned.add(token);
        }
        List<String> stemmedWords = new ArrayList<>();
        for (String token : tokensCleaned) {
            if (!STOP_WORDS.contains(token.toLowerCase()) && (token.length() > 2 && token.length() < 32)) {
                String stem = stemmer.stem(token);
                stemmedWords.add(stem);
            }
        }
        return stemmedWords;
    }
}
