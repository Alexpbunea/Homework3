package it.uniroma3.homework3.service;

import it.uniroma3.homework3.util.AnalyzerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchEngine {

    private Analyzer analyzer;

    public void setAnalyzer() {
        this.analyzer = AnalyzerFactory.getAnalyzer();
    }

    public List<String> search(String queryString) {
        String[] fields = {"Caption", "TableInfo", "Footnotes", "References"};
        List<String> searchResults = new ArrayList<>();
        Path indexPath = Paths.get("../lucene-index");

        try (Directory directory = FSDirectory.open(indexPath);
             IndexReader indexReader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(indexReader);

            if (queryString == null || queryString.isBlank()) {
                // TODO Handle the case where the inserted string is empty or null
                return null;
            } else {
                String escapedQuery = QueryParser.escape(queryString);
                setAnalyzer();
                MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, this.analyzer);
                Query query = queryParser.parse(escapedQuery);
                searchResults = executeQuery(searcher, query);
            }
        } catch (IOException e) {
            System.err.println("Error accessing the index: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Error parsing the query: " + e.getMessage());
        }
        System.out.println(searchResults);
        return searchResults;
    }


    private static List<String> executeQuery(IndexSearcher searcher, Query query) throws IOException {
        TopDocs hits = searcher.search(query, 2);
        List<String> results = new ArrayList<>();
        if (hits.scoreDocs.length == 0) {
            results.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
            return results;
        }

        StoredFields storedFields = searcher.storedFields();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = storedFields.document(scoreDoc.doc);

            /* Needs to be edited to show the table and other necessary content
             * HTML Code is very raw, it needs to be tested to find the best way to show each result
             * Missing fields in HTML String are: Table, TableInfo, Caption, Footnotes and References */
            String HTMLResult = String.format(
                    "<br><span style='color:green;'><b>DocName:</b></span> %s, " +
                            "<span style='color:green;'><b>Table ID:</b></span> %s, " +
                            "<span style='color:green;'><b>Caption</b></span> %s, " +
                            "<span style='color:green;'><b>Mark:</b></span> <b>%f</b><br>" +
                            "%s",
                    doc.get("JsonFileName") != null ? doc.get("JsonFileName") : "N/A",
                    doc.get("ID") != null ? doc.get("ID") : "N/A",
                    doc.get("Caption") != null ? doc.get("Caption") : "N/A",
                    scoreDoc.score,
                    doc.get("Table") != null ? doc.get("Table") : "N/A"
            );

            results.add(HTMLResult);
            //String table = doc.get("Table");
            //results.add(table);
        }
        return results;
    }
}