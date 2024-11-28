package it.uniroma3.homework3.controller;

import it.uniroma3.homework3.service.SearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class DataController {

    private final SearchEngine searchEngine;
    private List<String> receivedData;

    @Autowired
    public DataController(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
        this.searchEngine.setAnalyzer();
    }

    @GetMapping("/")
    public String index() {
        return "web.html";  // This will resolve to resources/static/web.html
    }

    /**
     * Endpoint to receive data via POST request.
     *
     * @param data incoming data
     * @return a response indicating the data was received successfully
     */
    @PostMapping("/receive")
    @ResponseBody
    public Map<String, Object> receiveData(@RequestBody String data) {
        System.out.println("Data received: " + data);
        this.receivedData = this.searchEngine.search(data);

        return Map.of(
                "message", "Data received successfully",
                "receivedData", this.receivedData
        );
    }

    /**
     * Endpoint to retrieve the last received data via GET request.
     *
     * @return the last received data or a message indicating no data is available
     */
    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getReceivedData() {
        if (this.receivedData != null) {
            return Map.of(
                    "message", "Data retrieved successfully",
                    "receivedData", this.receivedData
            );
        } else {
            return Map.of("message", "No data available");
        }
    }
}