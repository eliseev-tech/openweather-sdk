package ru.eliseevtech.openweathersdk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.eliseevtech.openweathersdk.dto.WeatherDto;
import ru.eliseevtech.openweathersdk.service.WeatherService;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public String get(@RequestParam String city) throws Exception {
        WeatherDto dto = weatherService.getCurrentWeather(city);
        return dto.toJsonString();
    }

}
