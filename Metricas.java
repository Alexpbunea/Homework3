import java.util.*;
import java.util.stream.Collectors;



//PARA ADAPTARLO A A LOS PUNTUAJES DE LUCENE
// List<ScoredTable> results = new ArrayList<>();
// TopDocs topDocs = searcher.search(query, 10); // Los mejores 10 resultados
// for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
//     Document doc = searcher.doc(scoreDoc.doc);
//     results.add(new ScoredTable(doc.get("tableName"), scoreDoc.score));
// }





public class Metricas {

    public static void main(String[] args) {
        // Ejemplo: Similitud devuelta por Lucene para una consulta
        Map<String, List<ScoredTable>> results = new HashMap<>();
        
        // Simula resultados de Lucene: Tabla y su puntaje
        results.put("query1", Arrays.asList(
            new ScoredTable("table1", 0.9),  // Relevancia proxy alta
            new ScoredTable("table2", 0.7),  // Moderada
            new ScoredTable("table3", 0.2)   // Baja
        ));

        results.put("query2", Arrays.asList(
            new ScoredTable("table4", 0.8),
            new ScoredTable("table5", 0.4),
            new ScoredTable("table6", 0.1)
        ));

        // Calcular métricas
        System.out.println("MRR: " + calculateMRR(results));
        System.out.println("NDCG: " + calculateNDCG(results));
    }

    // MRR: Mean Reciprocal Rank
    public static double calculateMRR(Map<String, List<ScoredTable>> results) {
        double sumReciprocalRank = 0.0;
        int queryCount = results.size();

        for (Map.Entry<String, List<ScoredTable>> entry : results.entrySet()) {
            List<ScoredTable> rankedTables = entry.getValue();

            // Encuentra el primer resultado relevante (puntaje mayor a un umbral, por ejemplo, 0.5)
            for (int i = 0; i < rankedTables.size(); i++) {
                if (rankedTables.get(i).score > 0.5) { // Umbral de relevancia
                    sumReciprocalRank += 1.0 / (i + 1); // Reciprocal Rank
                    break; // Solo cuenta el primer relevante
                }
            }
        }

        return sumReciprocalRank / queryCount; // Media de las Reciprocal Ranks
    }

    // NDCG: Normalized Discounted Cumulative Gain
    public static double calculateNDCG(Map<String, List<ScoredTable>> results) {
        double totalNDCG = 0.0;
        int queryCount = results.size();

        for (Map.Entry<String, List<ScoredTable>> entry : results.entrySet()) {
            List<ScoredTable> rankedTables = entry.getValue();

            // Calcula DCG
            double dcg = 0.0;
            for (int i = 0; i < rankedTables.size(); i++) {
                double relevance = rankedTables.get(i).score; // Usamos el puntaje como proxy
                dcg += (Math.pow(2, relevance) - 1) / (Math.log(i + 2) / Math.log(2)); // DCG formula
            }

            // Calcula IDCG (DCG ideal)
            List<ScoredTable> idealRanking = rankedTables.stream()
                .sorted((t1, t2) -> Double.compare(t2.score, t1.score)) // Ordenar por puntaje descendente
                .collect(Collectors.toList());

            double idcg = 0.0;
            for (int i = 0; i < idealRanking.size(); i++) {
                double relevance = idealRanking.get(i).score;
                idcg += (Math.pow(2, relevance) - 1) / (Math.log(i + 2) / Math.log(2));
            }

            // Calcula NDCG para esta consulta
            totalNDCG += idcg > 0 ? (dcg / idcg) : 0.0; // Normaliza
        }

        return totalNDCG / queryCount; // Media de las NDCG por consulta
    }

    // Clase para representar una tabla con su puntaje
    static class ScoredTable {
        String tableName;
        double score;

        ScoredTable(String tableName, double score) {
            this.tableName = tableName;
            this.score = score;
        }
    }
}