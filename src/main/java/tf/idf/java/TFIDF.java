package tf.idf.java;

import javafx.util.Pair;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TFIDF {
    private Map<String, Map<String, AtomicInteger>> corpusBow;
    private ChunkerME chunker;
    private POSTaggerME tagger;
    private WhitespaceTokenizer whitespaceTokenizer = WhitespaceTokenizer.INSTANCE;
    private String determiners_en = "the, a, an, this, that, these, those," +
            " my, your, his, her, its, our, their, much, many, few, most, some, any, enough, all," +
            " both, half, either, neither, each, every, other, another, such, what, rather, quite";


    public TFIDF() throws IOException {
        this(new HashMap<>());
    }

    /**
     *
     * @param corpusBow - Initiate the class with already provided Bag of words (BOW)
     */
    public TFIDF(Map<String, Map<String, AtomicInteger>> corpusBow) throws IOException {
        this.corpusBow = corpusBow;
        initParseAndChunk();
    }

    /**
     *
     * @param dataset - Iterator to go over the dataset
     */
    public TFIDF(Iterator<Pair<String, String>> dataset) throws IOException {
        initParseAndChunk();
        this.corpusBow = fromCorpusToBow(dataset);
    }

    private void initParseAndChunk() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream("/en-chunker.bin");
        ChunkerModel chunkerModel = new ChunkerModel(inputStream);
        this.chunker = new ChunkerME(chunkerModel);

        File file = new File(this.getClass().getResource("/en-pos-maxent.bin").getFile());
        POSModel model = new POSModelLoader().load(file);
        this.tagger = new POSTaggerME(model);
    }

    public double getTF(String docName, String term) {
        term = term.toLowerCase();
        final Map<String, AtomicInteger> doc = this.getDoc(docName);
        if(doc != null) {
            double total = 0.0;
            if(isPhrase(term)) {
                for (String key : doc.keySet()) {
                    if (key.equals(term)) {
                        total += doc.get(key).get();
                    } else if (key.contains(term)) {
                        total += doc.get(key).get() / 2.0;
                    }
                }
            } else if(doc.containsKey(term)) {
                total = doc.get(term).get();
            }

            return total / Double.valueOf(doc.size());

        }

        return 0.0;
    }

    public double getIDF(String term) {
        term = term.toLowerCase();
        final int numberOfDocsWithTerm = this.numberOfDocsWithTerm(term);
        if(numberOfDocsWithTerm != 0) {
            return Math.log(Double.valueOf(this.corpusBow.size()) / Double.valueOf(numberOfDocsWithTerm));
        } else {
            return 0;
        }
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
            final boolean nounPhrase = isPhrase(term);
            if(nounPhrase) {
                for (String key : doc.keySet()) {
                    if(key.contains(term)) {
                        count++;
                        break;
                    }
                }
            } else if(doc.containsKey(term)) {
                count++;
            }
        }

        return count;
    }

    boolean isPhrase(String term) {
        if (term.split(" ").length > 1) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param corpusToMap - Map for FileName, File text(UTF-8)
     * @return Bag of words for each document (key=doc name) in the corpus, inner map key=word, value=count
     * @throws IOException
     */
    Map<String, Map<String, AtomicInteger>> fromCorpusToBow(Iterator<Pair<String, String>> corpusToMap) {
        Map<String, Map<String, AtomicInteger>> localBow = new HashMap<>();
        while(corpusToMap.hasNext()) {
            Pair<String, String> next = corpusToMap.next();
            Map<String, AtomicInteger> docBow = convertDocStringToBow(next.getValue());
            localBow.put(next.getKey(), docBow);
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

        String[] tags = this.tagger.tag(cleanSplitText);
        Span[] spanResults = this.chunker.chunkAsSpans(cleanSplitText, tags);
        for (Span span : spanResults) {
            if(span.getType().equals("NP")) {
                StringBuilder sb = new StringBuilder();
                for(int i = span.getStart(); i < span.getEnd() ; i++ ) {
                    if(i == span.getStart() && this.determiners_en.contains(cleanSplitText[i] + ","))
                        continue;

                    sb.append(cleanSplitText[i]).append(" ");
                }

                String phrase = sb.toString().trim();
                if (docBow.containsKey(phrase)) {
                    docBow.get(phrase).incrementAndGet();
                } else {
                    docBow.put(phrase, new AtomicInteger(1));
                }
            }
        }

        return docBow;
    }

    String[] cleanSplitTextAndPunc(String fileAsString) {
        fileAsString = fileAsString.toLowerCase();
        String cleanString = fileAsString.replaceAll("\\p{Punct}", "");
        String[] words = whitespaceTokenizer.tokenize(cleanString);
        return words;
    }
}
