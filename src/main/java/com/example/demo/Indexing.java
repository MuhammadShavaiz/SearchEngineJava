package com.example.demo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexing extends Base implements IndexingInterface {
    public void forwardIndexAndLexicon() {
        JSONArray jsonArray = loadData();
        for (Object obj: jsonArray) {
            String url = (String) ((JSONObject) obj).get("url");
            String content = (String) ((JSONObject) obj).get("text");
            String title = (String) ((JSONObject) obj).get("title");
            String[] key = {url, title};
            String[] tokens = TextProcessor.processText(content).toArray(new String[0]);
            dataStructures.updateForwardIndex(key, tokens);
            dataStructures.updateLexicon(tokens);
        }
    }

    // verify this method
    public void invertedIndex() {
        for (String key: dataStructures.lexicon) {
            dataStructures.invertedIndex.put(key, new ArrayList<>());
        }
        for (String[] key: dataStructures.forwardIndex.keySet()) {
            String[] tokens = dataStructures.forwardIndex.get(key);
            for (String token: tokens) {
                if (dataStructures.invertedIndex.containsKey(token)) {
                    ArrayList<HashMap<String[], Double>> postings = dataStructures.invertedIndex.get(token);
                    HashMap<String[], Double> posting = new HashMap<>();
                    posting.put(key, 1.0);
                    postings.add(posting);
                    dataStructures.invertedIndex.put(token, postings);
                }
            }
        }

        double numDocs = dataStructures.forwardIndex.size();
        for (String term: dataStructures.invertedIndex.keySet()) {
            ArrayList<HashMap<String[], Double>> postings = dataStructures.invertedIndex.get(term);
            double numDocsWithTerm = postings.size();
            double IDF = calcIDF(numDocs, numDocsWithTerm);
            for (HashMap<String[], Double> posting: postings) {
                String[] key = posting.keySet().iterator().next();
                double docLength = dataStructures.forwardIndex.get(key).length;
                double termFreq = posting.get(key);
                double TF = calcTF(termFreq, docLength);
                double TFIDF = calcTFIDF(TF, IDF);
                posting.put(key, TFIDF);
            }
            postings.sort((posting1, posting2) -> {
                Double score1 = posting1.values().iterator().next();
                Double score2 = posting2.values().iterator().next();
                return score2.compareTo(score1);
            });
            dataStructures.invertedIndex.put(term, postings);
        }
    }


    public ArrayList<HashMap<String[], Double>> search(String query) {
        List<String> tokens = TextProcessor.processText(query);
        Map<String[], Double> aggregatedResults = new HashMap<>();

        if (tokens.isEmpty()) {
            return new ArrayList<>();
        }
        for (String token : tokens) {
            ArrayList<HashMap<String[], Double>> postings = dataStructures.invertedIndex.get(token);
            if (postings != null) {
                for (HashMap<String[], Double> posting : postings) {
                    for (Map.Entry<String[], Double> entry : posting.entrySet()) {
                        String[] docKey = entry.getKey();
                        double score = entry.getValue();
                        aggregatedResults.put(docKey, aggregatedResults.getOrDefault(docKey, 0.0) + score);
                    }
                }
            }
        }

        // Convert the aggregated results to a list of HashMaps
        ArrayList<HashMap<String[], Double>> results = new ArrayList<>();
        for (Map.Entry<String[], Double> entry : aggregatedResults.entrySet()) {
            HashMap<String[], Double> result = new HashMap<>();
            result.put(entry.getKey(), entry.getValue());
            results.add(result);
        }

        // Sort the results by score in descending order
        results.sort((a, b) -> {
            Double scoreA = a.values().iterator().next();
            Double scoreB = b.values().iterator().next();
            return scoreB.compareTo(scoreA);
        });

        return new ArrayList<>(results.subList(0, Math.min(30, results.size())));

}

    public double calcTF(double termFreq, double docLength) {
        return termFreq / docLength;
    }

    public double calcIDF(double numDocs, double numDocsWithTerm) {
        return Math.log(numDocs / numDocsWithTerm);
    }

    public double calcTFIDF(double TF, double IDF) {
        return TF * IDF;
    }
}
