package it.uniroma3.homework3;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.uniroma3.homework3.util.AnalyzerFactory;
import it.uniroma3.homework3.util.Table;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main(String[] args) {
        String directoryPath = "../all_tables";
        String pathIndex = "../lucene-index";

        File folder = new File(directoryPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        Map<String, List<List<Table>>> mapTables = new HashMap<>();

        if (jsonFiles == null || jsonFiles.length == 0) {
            System.out.println("No files found in the specified directory.");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        int countFiles = 0;
        for (File jsonFile : jsonFiles) {
            System.out.println("Processing the file: " + jsonFile.getName());

            try {
                Map<String, Map<String, Object>> data = objectMapper.readValue(jsonFile, new TypeReference<>() {});

                List<List<Table>> listsJsonNamesForFile = new ArrayList<>();

                for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                    String tableId = entry.getKey();
                    Map<String, Object> tableData = entry.getValue();

                    // List of tables for the current file
                    List<Table> list = new ArrayList<>();

                    list.add(new Table(
                            tableId,
                            processField("Caption", tableData.get("caption")),
                            processField("Table", tableData.get("table")),
                            processTables(tableData.get("table")),
                            processField("Footnotes", tableData.get("footnotes")),
                            processField("References", tableData.get("references"))));

                    listsJsonNamesForFile.add(list);  // Add the table
                }

                // Insert the list to the map with key the name of the Json File
                mapTables.put(jsonFile.getName(), listsJsonNamesForFile);

            } catch (IOException e) {
                System.err.println("Unexpected error when processing the file:  " + jsonFile.getName());
                e.printStackTrace();
            }
            countFiles++;
        }

        it.uniroma3.homework3.util.Indexer index = new it.uniroma3.homework3.util.Indexer(AnalyzerFactory.getAnalyzer());
        index.index(pathIndex, mapTables);
    }

    private static String processField(String fieldName, Object fieldValue) {
        String result = "Null";

        if (fieldValue != null) {
            if (fieldValue instanceof String) {
                String processedValue = ((String) fieldValue).replaceAll("\\s+", " ").trim();
                if (!processedValue.isEmpty()) {
                    result = processedValue;
                } else {
                    System.out.println("- Field ' " + fieldName + " ' is empty or only whitespace.");
                }
            } else {
                System.out.println("- Not expected type for ' " + fieldName + " '.");
            }
        } else {
            System.out.println("Doesn't have the field --> ' " + fieldName + " '.");
        }
        return result;
    }

    private static String processTables(Object table) {
        String columnsAndRows = "Null,";

        if (table != null) {
            if (table instanceof String) {
                String tableString = ((String) table).replaceAll("\\s+", " ").trim();
                if (!tableString.isEmpty()) {
                    // Process table only if it has content
                    Document doc = Jsoup.parse(tableString);
                    Elements rows = doc.select("tr");
                    StringBuilder resultBuilder = new StringBuilder();

                    for (Element row : rows) {
                        Elements cells = row.select("th");
                        for (Element cell : cells) {
                            String text = cell.text().trim();
                            resultBuilder.append(text).append(", ");
                        }
                    }
                    if (resultBuilder.length() > 0) {
                        columnsAndRows = resultBuilder.toString().trim();
                    } else {
                        System.out.println("- No header cells (`th`) found in the table.");
                    }
                } else {
                    System.out.println("- Table content is empty or only whitespace.");
                }
            } else {
                System.out.println("- Not expected type for table, expected a String.");
            }
        } else {
            System.out.println("- Table is null.");
        }

        return columnsAndRows;
    }
}
