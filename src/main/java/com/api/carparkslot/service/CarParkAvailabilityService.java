package com.api.carparkslot.service;

import com.api.carparkslot.entity.CarParkAvailability;
import com.api.carparkslot.repository.CarParkAvailabilityRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CarParkAvailabilityService {
    private static final Logger logger = LoggerFactory.getLogger(CarParkAvailabilityService.class);

    @Autowired
    private CarParkAvailabilityRepository repository;

    @Scheduled(fixedRate = 60000) // Execute every 60 seconds (adjust as needed)
    public void fetchAndSaveDataFromAPI() {
        logger.debug("Fetching data from API and saving to database...");
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://api.data.gov.sg/v1/transport/carpark-availability";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode items = root.get("items");

                List<CarParkAvailability> carParkAvailabilityList = new ArrayList<>();

                for (JsonNode item : items) {
                    String timestamp = item.get("timestamp").asText();
                    JsonNode carparkData = item.get("carpark_data");

                    for (JsonNode data : carparkData) {
                        JsonNode carparkInfo = data.get("carpark_info").get(0);
                        String carParkNo = data.get("carpark_number").asText();
                        String updatedDateTime = data.get("update_datetime").asText();
                        int totalLots = Integer.parseInt(carparkInfo.get("total_lots").asText());
                        int lotsAvailable = Integer.parseInt(carparkInfo.get("lots_available").asText());

                        CarParkAvailability carParkAvailability = new CarParkAvailability();
                        carParkAvailability.setDateTime(LocalDateTime.now());
                        carParkAvailability.setCarParkNo(carParkNo);
                        carParkAvailability.setTotalLots(totalLots);
                        carParkAvailability.setLotsAvailable(lotsAvailable);

                        carParkAvailabilityList.add(carParkAvailability);
                    }
                }
                repository.saveAll(carParkAvailabilityList);
            } catch (IOException e) {
                logger.error("Error while fetching data from API or saving to database", e);
                // Handle exception
            }
        } else {
            logger.error("Failed to fetch data from API. Status code: {}", response.getStatusCode());
        }
    }
}
