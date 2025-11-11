package ru.eliseevtech.openweathersdk.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Текущая погода.
 *
 * <p>Пример JSON:</p>
 *
 * <pre><code>{
 *   "weather": { "main": "Clouds", "description": "scattered clouds" },
 *   "temperature": { "temp": 269.6, "feels_like": 267.57 },
 *   "visibility": 10000,
 *   "wind": { "speed": 1.38 },
 *   "datetime": 1675744800,
 *   "sys": { "sunrise": 1675751262, "sunset": 1675787560 },
 *   "timezone": 3600,
 *   "name": "Zocca"
 * }</code></pre>
 */
public final class WeatherDto {

    private final ObjectNode root;

    public WeatherDto(ObjectNode root) {
        this.root = root;
    }

    public ObjectNode asJson() {
        return root;
    }

    public String toJsonString() {
        return root.toString();
    }

    @Override
    public String toString() {
        return toJsonString();
    }

}
