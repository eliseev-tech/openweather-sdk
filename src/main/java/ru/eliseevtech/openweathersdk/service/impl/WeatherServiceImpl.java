package ru.eliseevtech.openweathersdk.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eliseevtech.openweathersdk.cache.LruTimedCache;
import ru.eliseevtech.openweathersdk.config.WeatherProperties;
import ru.eliseevtech.openweathersdk.dto.WeatherDto;
import ru.eliseevtech.openweathersdk.exception.WeatherException;
import ru.eliseevtech.openweathersdk.mapper.WeatherMapper;
import ru.eliseevtech.openweathersdk.service.WeatherService;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

import static ru.eliseevtech.openweathersdk.utils.WeatherUtils.nvl;
import static ru.eliseevtech.openweathersdk.utils.WeatherUtils.required;

@Slf4j
@Service
@RequiredArgsConstructor
public final class WeatherServiceImpl implements WeatherService {

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final int MAX_CITIES = 10;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final WeatherProperties props;
    private final WeatherMapper weatherMapper;

    private final LruTimedCache<String, WeatherDto> cache = new LruTimedCache<>(MAX_CITIES, TTL);

    @Override
    public WeatherDto getCurrentWeather(String city) throws WeatherException {
        if (city == null || city.isBlank()) {
            throw new WeatherException("City name must not be empty");
        }

        Optional<WeatherDto> fresh = cache.getIfFresh(city);
        if (fresh.isPresent()) {
            return fresh.get();
        }

        WeatherDto dto = fetchFromApi(city);
        cache.put(city, dto);
        return dto;
    }

    private WeatherDto fetchFromApi(String city) throws WeatherException {
        String apiKey = required(props.getApiKey(), "OpenWeather API key is not set");
        String units = nvl(props.getUnits(), "standard");
        String lang = nvl(props.getLang(), "en");

        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?q="
                         + URLEncoder.encode(city, StandardCharsets.UTF_8)
                         + "&appid=" + apiKey
                         + "&units=" + units
                         + "&lang=" + lang;

            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            int sc = resp.statusCode();

            if (sc == 401) {
                throw new WeatherException("Unauthorized (401): invalid or inactive API key.");
            }
            if (sc == 404) {
                throw new WeatherException("City not found (404): check city name.");
            }
            if (sc >= 400) {
                throw new WeatherException("OpenWeather error: HTTP " + sc + " - " + resp.body());
            }

            JsonNode raw = objectMapper.readTree(resp.body());
            return weatherMapper.toDto(raw);

        } catch (IOException e) {
            throw new WeatherException("IO error when calling OpenWeather", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WeatherException("Interrupted when calling OpenWeather", e);
        }
    }

    @Override
    public void pollOnce() {
        try {
            for (String city : cache.snapshotAll().keySet()) {
                refreshCitySafely(city);
            }
        } catch (Exception e) {
            log.debug("Polling iteration failed: {}", e.getMessage());
        }
    }

    @Override
    public void delete() {
        cache.clear();
    }

    @Override
    public void close() {
        delete();
    }

    /**
     * Обновляет данные для одного города с перехватом ошибок.
     */
    private void refreshCitySafely(String city) {
        try {
            WeatherDto fresh = fetchFromApi(city);
            cache.put(city, fresh);
        } catch (Exception e) {
            log.trace("Failed to refresh city {}: {}", city, e.getMessage());
        }
    }

}
