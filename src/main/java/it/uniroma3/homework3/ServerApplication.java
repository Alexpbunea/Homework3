package it.uniroma3.homework3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @RestController
    @RequestMapping("/api")
    public class DataController {

        private List<String> receivedData;

        /**
         * POST endpoint to process a search query.
         * @param query the search query as a simple string
         * @return search results as a list of strings
         */
        @PostMapping("/search")
        public Map<String, Object> search(@RequestBody String query) {
            System.out.println("Search query received: " + query);

            // Create a SearchEngine instance and perform the search
            SearchEngine engine = new SearchEngine();
            this.receivedData = engine.search(query);

            return Map.of(
                    "message", "Search completed",
                    "results", this.receivedData
            );
        }

        /**
         * GET endpoint to retrieve the last search results.
         * @return last search results or a message if no results are available
         */
        @GetMapping("/data")
        public Map<String, Object> getReceivedData() {
            if (this.receivedData != null && !this.receivedData.isEmpty()) {
                return Map.of(
                        "message", "Data retrieved successfully",
                        "receivedData", this.receivedData
                );
            } else {
                return Map.of("message", "No data available");
            }
        }
    }
}
