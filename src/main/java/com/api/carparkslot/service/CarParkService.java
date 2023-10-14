package com.api.carparkslot.service;

import com.api.carparkslot.entity.CarPark;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CarParkService {

    private final JdbcTemplate jdbcTemplate;
    private final List<CarPark> carParks;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(CarParkService.class);

    @Value("${onemap.api.token}")
    private String oneMapApiToken;

    @Value("${onemap.api.url}")
    private String oneMapApiUrl;

    @Autowired
    public CarParkService(RestTemplate restTemplate, JdbcTemplate jdbcTemplate) throws IOException {
        this.restTemplate = restTemplate;

        ClassPathResource resource = new ClassPathResource("static/HDBCarparkInformation.csv");
        FileReader reader = new FileReader(resource.getFile());
        CsvToBean<CarPark> csvToBean = new CsvToBeanBuilder<CarPark>(reader)
                .withType(CarPark.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        this.carParks = csvToBean.parse();
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CarPark> findMatchingCarParks(double latitude, double longitude, int page, int perPage) {
        try {
            int startIndex = (page - 1) * perPage;
            int endIndex = Math.min(startIndex + perPage, carParks.size());

            List<CarPark> matchingCarParks = carParks.stream()
                    .filter(carPark -> carPark.getX_coord() == latitude && carPark.getY_coord() == longitude)
                    .skip(startIndex)
                    .limit(perPage)
                    .collect(Collectors.toList());

            for (CarPark carPark : matchingCarParks) {
                String carParkNo = carPark.getCarParkNo();

                // Get total_lots dan available_lots from database by car_park_no
                String sql = "SELECT total_lots, lots_available FROM car_park_availability WHERE car_park_no  = ? ORDER BY date_time DESC LIMIT 1";
                Object[] params = {carParkNo};

                List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);

                if (!rows.isEmpty()) {
                    Map<String, Object> result = rows.get(0);
                    int totalLots = (int) result.get("total_lots");
                    int lotsAvailable = (int) result.get("lots_available");

                    // Set total_lots dan available_lots di objek CarPark
                    carPark.setTotalLots(totalLots);
                    carPark.setLotsAvailable(lotsAvailable);
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(oneMapApiToken);

            // Use API OneMap for convert coordinate
            String url = oneMapApiUrl + "X={x}&Y={y}";
            String oneMapUrl = url.replace("{x}", String.valueOf(latitude)).replace("{y}", String.valueOf(longitude));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CarPark> response = restTemplate.exchange(oneMapUrl, HttpMethod.GET, entity, CarPark.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                CarPark result = response.getBody();
                if (result != null) {
                    logger.info("Response from OneMap API: Latitude = {}, Longitude = {}",
                            result.getLatitude(), result.getLongitude());

                    for (CarPark carPark : matchingCarParks) {
                        carPark.setLatitude(result.getLatitude());
                        carPark.setLongitude(result.getLongitude());
                        logger.info("Matching Car Park: CarParkNo = {}, Address = {}, Latitude = {}, Longitude = {}, TotalLots={}, AvailableLots={}",
                                carPark.getCarParkNo(), carPark.getAddress(), carPark.getLatitude(), carPark.getLongitude(), carPark.getTotalLots(), carPark.getLotsAvailable());
                    }
                    return matchingCarParks;
                } else {
                    logger.warn("Failed convert coordinate");
                    return Collections.emptyList();
                }
            } else {
                logger.error("Failed access OneMap API. Status code: {}", response.getStatusCode());
                return Collections.emptyList();
            }

        } catch (HttpServerErrorException e) {
            logger.error("Error from OneMap API: {}", e.getRawStatusCode());
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}

