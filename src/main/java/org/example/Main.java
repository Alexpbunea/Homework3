package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

//This is only for the table processing, because it is written in html
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {
        String directoryPath = "../jsons";
        String pathIndex = "../lucene-index";

        File folder = new File(directoryPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        Map<String, List<List<Tabla>>> mapTables = new HashMap<>();

        if (jsonFiles == null || jsonFiles.length == 0) {
            System.out.println("No files found in the specified directory.");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        int count = 0;
        int countFiles = 0;
        for (File jsonFile : jsonFiles) {
            System.out.println("Processing the file: " + jsonFile.getName());

            try {
                Map<String, Map<String, Object>> data = objectMapper.readValue(jsonFile, new TypeReference<>() {});

                // Crear una nueva lista para cada archivo JSON
                List<List<Tabla>> listsJsonNamesForFile = new ArrayList<>();

                for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                    String tableId = entry.getKey();
                    Map<String, Object> tableData = entry.getValue();

                    // Crear una lista para las tablas de un archivo específico
                    List<Tabla> list = new ArrayList<>();

                    list.add(new Tabla(
                            tableId,
                            processField("Caption", tableData.get("caption")),
                            processField("Table", tableData.get("table")),
                            processTables(tableData.get("table")),
                            processField("Footnotes", tableData.get("footnotes")),
                            processField("References", tableData.get("references"))));

                    listsJsonNamesForFile.add(list);  // Agregar la tabla a la lista de este archivo
                }

                // Ahora agregamos la lista de este archivo al mapa, usando el nombre del archivo como clave
                mapTables.put(jsonFile.getName(), listsJsonNamesForFile);

            } catch (IOException e) {
                System.err.println("Unexpected error when processing the file:  " + jsonFile.getName());
                e.printStackTrace();
            }
            countFiles += 1;
        }


        Indexer index = new Indexer();
        index.index(pathIndex, mapTables);
        //System.out.println(mapTables);
//        for (String clave : mapTables.keySet()) {
//            System.out.println("Clave: " + clave);
//
//            // Obtener la lista de listas asociada a esta clave
//            List<List<Tabla>> listasTablas = mapTables.get(clave);
//
//            // Iterar por cada lista dentro de la lista principal
//            for (List<Tabla> listaTablas : listasTablas) {
//                System.out.println("\tNueva lista de tablas:");
//
//                // Iterar por cada tabla en esta lista
//                for (Tabla tabla : listaTablas) {
//                    System.out.println("\t\tTabla ID: " + tabla.getId());
//                    System.out.println("\t\tCaption: " + tabla.getCaption());
//                    //System.out.println("\t\tTabla HTML: " + tabla.getTabla());
//                    //System.out.println("\t\tInformación de la tabla: " + tabla.getInformacionTabla());
//                    //System.out.println("\t\tNotas al pie: " + tabla.getFootnotes());
//                    //System.out.println("\t\tReferencias: " + tabla.getReferencias());
//                }
//            }
//        }
//        System.out.println("================= " + count);
//        System.out.println("================= " + countFiles);
    }


    private static String processField(String fieldName, Object fieldValue) {
        String result = "Null";

        if (fieldValue != null) {
            if (fieldValue instanceof String) {
                String processedValue = ((String) fieldValue).replaceAll("\\s+", " ").trim();
                if (!processedValue.isEmpty()) {
                    result = processedValue;
                } else {
                    System.out.println("- Field `" + fieldName + "` is empty or only whitespace.");
                }
            } else {
                System.out.println("- Not expected type for `" + fieldName + "`.");
            }
        } else {
            System.out.println("Doesn't have the field --> `" + fieldName + "`.");
        }

        return result;
    }


    private static String processTables(Object table) {
        String columnsAndRows = "Null,";

        if (table != null) {
            if (table instanceof String) {
                String tableString = ((String) table).replaceAll("\\s+", " ").trim();
                if (!tableString.isEmpty()) {
                    // Procesar la tabla solo si no está vacía
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
