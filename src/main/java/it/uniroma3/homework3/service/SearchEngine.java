package it.uniroma3.homework3.service;

import it.uniroma3.homework3.util.AnalyzerFactory;
import it.uniroma3.homework3.util.StructureResults;
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
import java.util.*;

@Service
public class SearchEngine {

    private Analyzer analyzer;

    public void setAnalyzer() {
        this.analyzer = AnalyzerFactory.getAnalyzer();
    }

    public Map<String, List<StructureResults>> search(String queryString) {
        Map<String , List<StructureResults>> mapResults = new LinkedHashMap<>();
        String[] fields = {"Caption", "TableInfo", "Footnotes", "References"};
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
                Map<String, Float> boosts = new HashMap<>();
                boosts.put("TableInfo", 5.0f);
                MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, this.analyzer/*, boosts*/);
                Query query = queryParser.parse(escapedQuery);
                mapResults = executeQuery(searcher, query);
            }
        } catch (IOException e) {
            System.err.println("Error accessing the index: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Error parsing the query: " + e.getMessage());
        }
        System.out.println(mapResults);
        return mapResults;
    }


    private static Map<String , List<StructureResults>> executeQuery(IndexSearcher searcher, Query query) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        Map<String , List<StructureResults>> mapResults = new LinkedHashMap<>();

//        if (hits.scoreDocs.length == 0) {
//            results.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
//            return results;
//        }

        StoredFields storedFields = searcher.storedFields();
        int indexResult = 1;
        List<StructureResults> results = new ArrayList<>();

        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = storedFields.document(scoreDoc.doc);


            String jsonFileName = doc.get("JsonFileName") != null ? doc.get("JsonFileName") : "Unknown";
            String id = doc.get("ID") != null ? doc.get("ID") : "Unknown";
            String caption = doc.get("Caption") != null ? doc.get("Caption") : "No Caption";
            String tableContent = doc.get("Table") != null ? doc.get("Table") : "No Table Data";

            StructureResults HTMLResult = new StructureResults(
                    jsonFileName ,
                    id,
                    caption,
                    String.valueOf(scoreDoc.score),
                    tableContent
            );
            results.add(HTMLResult);


        }
        //Sorts automatically the results
        results.sort((r1, r2) -> {
            float score1 = Float.parseFloat(r1.getMark());
            float score2 = Float.parseFloat(r2.getMark());
            return Float.compare(score2, score1);
        });


        for (StructureResults r : results){
            List<StructureResults> list = new ArrayList<>();
            list.add(r);
            String key = "Result " + indexResult;
            mapResults.put(key, list);
            indexResult++;
        }

        return mapResults;
    }
}