package ru.eliseevtech.openweathersdk.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.eliseevtech.openweathersdk.dto.WeatherDto;

@Component
@RequiredArgsConstructor
public class WeatherMapper {

    private static final String WEATHER = "weather";
    private static final String VISIBILITY = "visibility";
    private static final String SUNRISE = "sunrise";
    private static final String SUNSET = "sunset";
    private static final String TIMEZONE = "timezone";

    private final ObjectMapper objectMapper;

    public WeatherDto toDto(JsonNode raw) {
        ObjectNode out = objectMapper.createObjectNode();

        // weather[0]
        ObjectNode weather = objectMapper.createObjectNode();
        JsonNode w0 = raw.path(WEATHER).isArray() && !raw.path(WEATHER).isEmpty()
                ? raw.path(WEATHER).get(0)
                : null;
        if (w0 != null) {
            weather.put("main", w0.path("main").asText(null));
            weather.put("description", w0.path("description").asText(null));
        }
        out.set(WEATHER, weather);

        // temperature
        ObjectNode temperature = objectMapper.createObjectNode();
        temperature.put("temp", raw.path("main").path("temp").asDouble());
        temperature.put("feels_like", raw.path("main").path("feels_like").asDouble());
        out.set("temperature", temperature);

        // visibility
        if (raw.has(VISIBILITY)) {
            out.put(VISIBILITY, raw.get(VISIBILITY).asInt());
        }

        // wind
        ObjectNode wind = objectMapper.createObjectNode();
        wind.put("speed", raw.path("wind").path("speed").asDouble());
        out.set("wind", wind);

        // datetime
        if (raw.has("dt")) {
            out.put("datetime", raw.get("dt").asLong());
        }

        // sys.sunrise/sunset
        ObjectNode sys = objectMapper.createObjectNode();
        if (raw.path("sys").has(SUNRISE)) {
            sys.put(SUNRISE, raw.path("sys").path(SUNRISE).asLong());
        }
        if (raw.path("sys").has(SUNSET)) {
            sys.put(SUNSET, raw.path("sys").path(SUNSET).asLong());
        }
        out.set("sys", sys);

        // timezone/name
        if (raw.has(TIMEZONE)) {
            out.put(TIMEZONE, raw.get(TIMEZONE).asInt());
        }
        if (raw.has("name")) {
            out.put("name", raw.get("name").asText());
        }

        return new WeatherDto(out);
    }

}
