package tf.idf.java;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class MapDataSet implements Iterable<Pair<String, String>> {

    private File[] corpus;

    public MapDataSet(String folderLocation) throws IOException {
        if(folderLocation != null) {
            File corpusFolder = new File(folderLocation);
            if (!corpusFolder.exists()) {
                throw new FileNotFoundException("Corpus folder-" + folderLocation + ", does not exist!");
            } else if (!corpusFolder.isDirectory()) {
                throw new FileNotFoundException("Expected folder not file-" + folderLocation);
            }

            this.corpus = corpusFolder.listFiles();
        }
    }

    @Override
    public Iterator<Pair<String, String>> iterator() {
        return new Iterator<>() {

            private int currntElem = 0;

            @Override
            public boolean hasNext() {
                if (currntElem < corpus.length)
                    return true;

                return false;
            }

            @Override
            public Pair<String, String> next() {
                Pair<String, String> next = null;
                try {
                    File file = corpus[currntElem];
                    String fileAsString = FileUtils.readFileToString(file, "UTF-8");
                    next = new Pair(file.getName(), fileAsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currntElem ++;
                return next;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Pair<String, String>> action) {

    }

    @Override
    public Spliterator<Pair<String, String>> spliterator() {
        return null;
    }
}
