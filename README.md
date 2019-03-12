# tf-idf-java
Java API for extracting TF (term Frequency), IDF (inverse document frequency) and TFIDF from a large corpus


### Code Example:

        MapDataSet mapDataSet = new MapDataSet("/Users/aeirew/workspace/tf-idf-java/src/test/resources/corpus");
        TFIDF tfidf = new TFIDF(mapDataSet.iterator());
        final double ml1 = tfidf.getTFIDF("Machine_Learning.txt", "Machine Learning");
        Assert.assertEquals("TFIDF value for Machine Learning", 0.0266, ml1, 0.0001);
        final double ml2 = tfidf.getTFIDF("Machine_Learning.txt", "Learning");
        Assert.assertEquals("TFIDF value for Learning", 0.0373, ml2, 0.0001);

