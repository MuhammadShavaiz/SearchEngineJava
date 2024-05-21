package com.example.demo;

import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Base {
    protected static final List<String> STOP_WORDS = loadStopWords();
    protected static final PorterStemmer stemmer = new PorterStemmer();
    protected static final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    protected DataStructures dataStructures = new DataStructures();

    protected JSONArray loadData() {
        JSONArray jsonArray = new JSONArray();
        try {
            InputStream inputStream = Base.class.getResourceAsStream("data.json");

            if (inputStream != null) {
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                jsonArray = (JSONArray) JSONValue.parseWithException(reader);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return jsonArray;
    }

    protected static List<String> loadStopWords() {
        ArrayList<String> StopWords = new ArrayList<>();
        try {
            InputStream inputStream = Base.class.getResourceAsStream("stopwords.txt");
            if (inputStream != null) {
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                int data = reader.read();
                StringBuilder sb = new StringBuilder();
                while (data != -1) {
                    sb.append((char) data);
                    data = reader.read();
                }
                String[] stopWords = sb.toString().split("\n");
                StopWords.addAll(Arrays.asList(stopWords));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return StopWords;
    }
}

