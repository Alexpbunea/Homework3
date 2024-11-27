package org.example;

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

    public void index(String filePath, Map<String, List<List<Tabla>>> mapTables) throws IOException {
        try {
            Directory directory = FSDirectory.open(Paths.get(filePath)); // Define where to save Lucene index

            Analyzer defaultAnalyzer = new StandardAnalyzer();

            Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
            perFieldAnalyzers.put("Caption", new WhitespaceAnalyzer());
            perFieldAnalyzers.put("TableInfo", new CommaSeparatedAnalyzer());
            perFieldAnalyzers.put("Footnotes", new EnglishAnalyzer());
            perFieldAnalyzers.put("References", new EnglishAnalyzer());


            Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setCodec(new SimpleTextCodec()); // We prefer to create files in binary rather than on .scf or plain text, because it is more efficient
            IndexWriter writer = new IndexWriter(directory, config);


            for (String nombreJson : mapTables.keySet()) {
                List<List<Tabla>> listasTablas = mapTables.get(nombreJson);

                for (List<Tabla> listaTablas : listasTablas) {
                    for (Tabla tabla : listaTablas) {
                        Document doc = new Document();

                        doc.add(new StringField("JsonFileName", nombreJson, Field.Store.YES));
                        doc.add(new StringField("ID", tabla.getId(), Field.Store.YES));
                        doc.add(new TextField("Caption", tabla.getCaption(), Field.Store.YES));
                        doc.add(new TextField("Table", tabla.getTabla(), Field.Store.YES));
                        doc.add(new TextField("TableInfo", tabla.getInformacionTabla(), Field.Store.YES));
                        doc.add(new TextField("Footnotes", tabla.getFootnotes(), Field.Store.YES));
                        doc.add(new TextField("References", tabla.getReferencias(), Field.Store.YES));

                        writer.addDocument(doc);
                    }
                }
            }

            writer.close();

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}

class CommaSeparatedAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // Tokenizer que divide el texto por comas
        PatternTokenizer tokenizer = new PatternTokenizer("[,]", 0);
        return new TokenStreamComponents(tokenizer);
    }

    // Clase interna para el Tokenizer basado en patrón
    class PatternTokenizer extends org.apache.lucene.analysis.util.CharTokenizer {
        private final String pattern;

        public PatternTokenizer(String pattern, int flags) {
            this.pattern = pattern;
        }

        @Override
        protected boolean isTokenChar(int c) {
            // Aquí deberías definir cómo separar los tokens
            // En este caso es por comas
            return !(Character.isWhitespace(c) || c == ',');
        }
    }
}
