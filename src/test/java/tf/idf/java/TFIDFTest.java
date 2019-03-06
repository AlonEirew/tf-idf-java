package tf.idf.java;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TFIDFTest {

    @Test
    public void convertDocStringToBowTest() {
        TFIDF corpus = new TFIDF();
        String testText = getString1();

        final Map<String, AtomicInteger> stringAtomicIntegerMap = corpus.convertDocStringToBow(testText);

        int count = 0;
        for (AtomicInteger atomicInteger : stringAtomicIntegerMap.values()) {
            count += atomicInteger.get();
        }

        String[] ret = corpus.cleanSplitTextAndPunc(testText);
        Assert.assertEquals("Not same count", ret.length, count);
    }

    @Test
    public void getTFIDFTest() {
        TFIDF corpus = new TFIDF();
        String testText1 = getString1();
        String testText2 = getString2();

        final Map<String, AtomicInteger> stringAtomicIntegerMap1 = corpus.convertDocStringToBow(testText1);
        final Map<String, AtomicInteger> stringAtomicIntegerMap2 = corpus.convertDocStringToBow(testText2);

        corpus.addDoc("1", stringAtomicIntegerMap1);
        corpus.addDoc("2", stringAtomicIntegerMap2);

        double tf = corpus.getTF("1", "tf–idf");
        Assert.assertEquals("Not Equal", 0.04545, tf, 0.0001);

        double idf = corpus.getIDF("tf–idf");
        Assert.assertEquals("Not Equal", 0.69314, idf, 0.0001);

        double tfidf = corpus.getTFIDF("1", "tf–idf");
        Assert.assertEquals("Not Equal", 0.0315, tfidf, 0.0001);
    }

    private String getString1() {
        return "In information retrieval, tf–idf or TFIDF, short for term frequency–inverse document frequency, " +
                    "is a numerical statistic that is intended to reflect how important a word is to a document in a collection or corpus." +
                    "[1] It is often used as a weighting factor in searches of information retrieval, text mining, and user modeling. " +
                    "The tf–idf value increases proportionally to the number of times a word appears in the document and is offset by " +
                    "the number of documents in the corpus that contain the word, which helps to adjust for the fact that some words " +
                    "appear more frequently in general. Tf–idf is one of the most popular term-weighting schemes today; 83% of text-based " +
                    "recommender systems in digital libraries use tf–idf.[2]\n\n" +
                    "Variations of the tf–idf weighting scheme are often used by search engines as a central tool in scoring and ranking " +
                    "a document's relevance given a user query. tf–idf can be successfully used for stop-words filtering in various subject " +
                    "fields, including text summarization and classification.\n\n" +
                    "One of the simplest ranking functions is computed by summing the tf–idf for each query term; many more sophisticated " +
                    "ranking functions are variants of this simple model.";
    }

    private String getString2() {
        return "Deep learning (also known as deep structured learning or hierarchical learning) is part of a broader family of machine " +
                "learning methods based on learning data representations, as opposed to task-specific algorithms. Learning can be " +
                "supervised, semi-supervised or unsupervised.[1][2][3]\n" +
                "\n" +
                "Deep learning architectures such as deep neural networks, deep belief networks and recurrent neural networks have been applied " +
                "to fields including computer vision, speech recognition, natural language processing, audio recognition, social network filtering," +
                " machine translation, bioinformatics, drug design, medical image analysis, material inspection and board game programs, where they" +
                " have produced results comparable to and in some cases superior to human experts.[4][5][6]\n\n" +
                "Deep learning models are vaguely inspired by information processing and communication patterns in biological nervous systems " +
                "yet have various differences from the structural and functional properties of biological brains (especially human brains), " +
                "which make them incompatible with neuroscience evidences.";
    }
}
