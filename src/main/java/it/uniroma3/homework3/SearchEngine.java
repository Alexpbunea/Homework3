package it.uniroma3.homework3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SearchEngine {

    /**
     * Perform a search based on the topic query in multiple fields.
     * @param query the search query as a string
     * @return list of results based on the search query
     */
    public List<String> search(String query) {
        // Fields to search in
        String[] fields = {"title", "references", "footnotes", "table", "columnsAndRows"};
        
        // Analyzer to use for searching
        Analyzer[] analyzers = {new WhitespaceAnalyzer(), new StandardAnalyzer(), new StandardAnalyzer(), new StandardAnalyzer(), new StandardAnalyzer()};
        
        // List to store results
        List<String> results = new ArrayList<>();
        
        // Define the index path (update the path as needed)
        Path indexPath = Paths.get("path_to_your_lucene_index");
        
        try (Directory directory = FSDirectory.open(indexPath);
             IndexReader indexReader = DirectoryReader.open(directory)) {
             
            IndexSearcher searcher = new IndexSearcher(indexReader);
            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
            
            // Process the query for each field
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                QueryParser queryParser = new QueryParser(field, analyzers[i]);
                String escapedQuery = QueryParser.escape(query); // Escape the query to handle special characters
                Query fieldQuery = queryParser.parse(escapedQuery);
                booleanQueryBuilder.add(fieldQuery, BooleanClause.Occur.MUST);
            }
            
            // Build the final combined query
            Query finalQuery = booleanQueryBuilder.build();
            TopDocs topDocs = searcher.search(finalQuery, 10); // Search top 10 results
            
            // Process search results
            for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
                String result = String.format(
                        "<b>Document ID:</b> %d, <b>Title:</b> %s, <b>References:</b> %s, <b>Footnotes:</b> %s, " +
                                "<b>Table:</b> %s, <b>Columns and Rows:</b> %s",
                        topDocs.scoreDocs[i].doc,
                        doc.get("title"),
                        doc.get("references"),
                        doc.get("footnotes"),
                        doc.get("table"),
                        doc.get("columnsAndRows")
                );
                results.add(result);
            }

        } catch (IOException | org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
        }

        // If no results, add a "No results" message
        if (results.isEmpty()) {
            results.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
        }

        return results;
    }
}
