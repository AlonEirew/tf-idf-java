package tf.idf.java;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TFIDF {
    private Map<String, Map<String, AtomicInteger>> corpusBow;
    private boolean caseSensitive = false;

    public TFIDF() {
        this.corpusBow = new HashMap<>();
    }

    /**
     *
     * @param corpusBow - Initiate the class with already provided Bag of words
     */
    public TFIDF(Map<String, Map<String, AtomicInteger>> corpusBow) {
        this.corpusBow = corpusBow;
    }

    public TFIDF(String folderLocation) throws IOException {
        this(folderLocation, false);
    }

    /**
     *
     * @param folderLocation - Folder location where corpus text(UTF-8) files are at
     * @param caseSensitive - weather corpusBow should be case sensitive or not
     */
    public TFIDF(String folderLocation, boolean caseSensitive) throws IOException {
        this.caseSensitive = caseSensitive;
        if(folderLocation != null) {
            File corpusFolder = new File(folderLocation);
            if (!corpusFolder.exists()) {
                throw new FileNotFoundException("Corpus folder-" + folderLocation + ", does not exist!");
            } else if(!corpusFolder.isDirectory()) {
                throw new FileNotFoundException("Expected folder not file-" + folderLocation);
            }

            this.corpusBow = fromCorpusToBow(corpusFolder);
        }
    }

    public double getTF(String docName, String term) {
        final Map<String, AtomicInteger> doc = this.getDoc(docName);
        if(doc.containsKey(term)) {
            return doc.get(term).get() / Double.valueOf(doc.size());
        }

        return 0.0;
    }

    public double getIDF(String term) {
        final int numberOfDocsWithTerm = this.numberOfDocsWithTerm(term);
        return Math.log(Double.valueOf(this.corpusBow.size()) / Double.valueOf(numberOfDocsWithTerm));
    }

    public double getTFIDF(String docName, String term) {
        return getTF(docName, term) * getIDF(term);
    }

    public Map<String, AtomicInteger> getDoc(String docId) {
        if(this.corpusBow.containsKey(docId)) {
            return this.corpusBow.get(docId);
        }

        return null;
    }

    public void addDoc(String docId, Map<String, AtomicInteger> doc) {
        if(doc != null) {
            this.corpusBow.put(docId, doc);
        }
    }

    public int numberOfDocsWithTerm(String term) {
        int count = 0;
        for(Map<String, AtomicInteger> doc : this.corpusBow.values()) {
            if(doc.containsKey(term)) {
                count++;
            }
        }

        return count;
    }

    /**
     *
     * @param folderLocation - Folder location where corpus text(UTF-8) files are at
     * @return Bag of words for each document (key=doc name) in the corpus, inner map key=word, value=count
     * @throws IOException
     */
    Map<String, Map<String, AtomicInteger>> fromCorpusToBow(File folderLocation) throws IOException {
        Map<String, Map<String, AtomicInteger>> localBow = new HashMap<>();
        for (File file : folderLocation.listFiles()) {
            String fileAsString = FileUtils.readFileToString(file, "UTF-8");
            Map<String, AtomicInteger> docBow = convertDocStringToBow(fileAsString);
            localBow.put(file.getName(), docBow);
        }

        return localBow;
    }

    Map<String, AtomicInteger> convertDocStringToBow(String fileAsString) {
        Map<String, AtomicInteger> docBow = new HashMap<>();
        String[] cleanSplitText = cleanSplitTextAndPunc(fileAsString);
        for(String word : cleanSplitText) {
            word = word.replaceAll("\n+", " ").trim();
            if (docBow.containsKey(word)) {
                docBow.get(word).incrementAndGet();
            } else {
                docBow.put(word, new AtomicInteger(1));
            }
        }
        return docBow;
    }

    String[] cleanSplitTextAndPunc(String fileAsString) {
        if(this.caseSensitive) {
            fileAsString = fileAsString.toLowerCase();
        }

        String[] words = fileAsString.replaceAll("\\p{Punct}", "").split(" ");
        return words;
    }
}
