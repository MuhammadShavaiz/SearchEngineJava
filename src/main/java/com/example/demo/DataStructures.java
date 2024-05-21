package com.example.demo;

import java.util.*;

public class DataStructures implements DataStructuresInterface {
    public Set<String> lexicon = new HashSet<>();
    public HashMap<String[], String[]> forwardIndex = new HashMap<>();
    public HashMap<String, ArrayList<HashMap<String[], Double>>> invertedIndex = new HashMap<>();

    public void updateForwardIndex(String[] key, String[] tokens) {
        Set<String> uniqueTokens = new HashSet<>(Arrays.asList(tokens));
        this.forwardIndex.put(key, uniqueTokens.toArray(new String[0]));
    }

    public void updateLexicon(String[] tokens) {
        this.lexicon.addAll(Arrays.asList(tokens));
    }
}