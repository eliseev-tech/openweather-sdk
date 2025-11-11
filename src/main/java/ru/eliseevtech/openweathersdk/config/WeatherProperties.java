package ru.eliseevtech.openweathersdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openweather")
public class WeatherProperties {

    private String apiKey;
    private String mode = "ON_DEMAND";
    private String units = "standard";
    private String lang  = "en";
    private long pollingMs = 60_000L;

}