package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        String directoryPath = "../all_tables";
        File folder = new File(directoryPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (jsonFiles == null || jsonFiles.length == 0) {
            System.out.println("No files found in the specified directory.");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        for (File jsonFile : jsonFiles) {
            System.out.println("Processing the file: " + jsonFile.getName());
            try {
                Map<String, Map<String, Object>> data = objectMapper.readValue(jsonFile, new TypeReference<>() {});

                for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                    String tableId = entry.getKey(); // example --> "S3.T1", "S5.T2"
                    Map<String, Object> tableData = entry.getValue();

                    // Procesar caption
                    processField("Caption", tableData.get("caption"));

                    processField("Footnotes", tableData.get("footnotes"));

                    // Procesar references
                    processField("References", tableData.get("references"));

                    System.out.println("-------------------------------");
                }

            } catch (IOException e) {
                System.err.println("Unexpected error when processing the file:  " + jsonFile.getName());
                e.printStackTrace();
            }
        }
    }

    // Procesar todo
    private static void processField(String fieldName, Object fieldValue) {
        if (fieldValue != null) {
            System.out.println(fieldName + ":");
            if (fieldValue instanceof String) {
                fieldValue = ((String) fieldValue).replaceAll("\\s+", " ").trim();
                System.out.println("- " + fieldValue);
            } else if (fieldValue instanceof List<?>) {
                List<?> list = (List<?>) fieldValue;
                for (Object item : list) {
                    if (item instanceof String) {
                        // Limpiar los strings en la lista de la misma manera
                        item = ((String) item).replaceAll("\\s+", " ").trim();
                        System.out.println("- " + item);
                    } else {
                        System.out.println("- (Item is not a String, so no process is being done)");
                    }
                }
            } else {
                System.out.println("- Not expected type fot " + fieldName);
            }
        } else {
            System.out.println("Doesn't have the field --> `" + fieldName + "`.");
        }
    }
}
