package it.uniroma3.homework3.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer {
    private final Analyzer analyzer;

    public Indexer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void index(String filePath, Map<String, List<List<Table>>> mapTables) {
        try {
            Directory directory = FSDirectory.open(Paths.get(filePath)); // Define where to save Lucene index
            IndexWriter writer = createIndexWriter(directory, analyzer);
            for (String JsonFileName : mapTables.keySet()) {
                List<List<Table>> tableLists = mapTables.get(JsonFileName);
                for (List<Table> tableList : tableLists) {
                    for (Table table : tableList) {
                        Document doc = new Document();
                        doc.add(new StringField("JsonFileName", JsonFileName, Field.Store.YES));
                        doc.add(new StringField("ID", table.getId(), Field.Store.YES));
                        doc.add(new TextField("Caption", table.getCaption(), Field.Store.YES));
                        doc.add(new TextField("Table", table.getTable(), Field.Store.YES));
                        doc.add(new TextField("TableInfo", table.getTableInfo(), Field.Store.YES));
                        doc.add(new TextField("Footnotes", table.getFootnotes(), Field.Store.YES));
                        doc.add(new TextField("References", table.getReferences(), Field.Store.YES));
                        writer.addDocument(doc);
                    }
                }
            }
            writer.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Creates an IndexWriter with the specified directory and analyzer.
     *
     * @param directory the directory to store the index
     * @param analyzer  the analyzer for the index
     * @return the configured IndexWriter
     * @throws IOException if an I/O error occurs
     */
    private IndexWriter createIndexWriter(Directory directory, Analyzer analyzer) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setCodec(new SimpleTextCodec()); // We prefer to create files in binary rather than on .scf or plain text, because it is more efficient
        return new IndexWriter(directory, config);
    }
}
