package com.api.carparkslot.controller;

import com.api.carparkslot.entity.CarPark;
import com.api.carparkslot.service.CarParkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CarParkController {

    @Autowired
    private CarParkService carParkService;

    @GetMapping("/carparks/nearest")
    public List<CarPark> getMatchingCarParks(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage) {
        return carParkService.findMatchingCarParks(latitude, longitude, page, perPage);
    }
}

