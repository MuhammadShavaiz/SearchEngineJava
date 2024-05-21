package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;

public interface IndexingInterface {
    void forwardIndexAndLexicon();
    void invertedIndex();
    ArrayList<HashMap<String[], Double>> search(String query);
    double calcTF(double termFreq, double docLength);
    double calcIDF(double numDocs, double numDocsWithTerm);
    double calcTFIDF(double TF, double IDF);
}
