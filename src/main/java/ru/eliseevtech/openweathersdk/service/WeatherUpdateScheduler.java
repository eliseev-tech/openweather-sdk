package ru.eliseevtech.openweathersdk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.eliseevtech.openweathersdk.config.WeatherProperties;
import ru.eliseevtech.openweathersdk.constant.PollingMode;

/**
 * Планировщик фонового обновления данных погоды (режим POLLING).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherUpdateScheduler {

    private final WeatherService weatherService;
    private final WeatherProperties props;

    /**
     * Запускает одну итерацию фонового обновления кеша.
     * Интервал задаётся свойством: openweather.polling-ms (по умолчанию 60000 мс).
     */
    @Scheduled(fixedDelayString = "${openweather.polling-ms:60000}")
    public void run() {
        if (!PollingMode.POLLING.name().equalsIgnoreCase(props.getMode())) {
            return;
        }
        try {
            weatherService.pollOnce();
        } catch (Exception e) {
            log.debug("Weather polling iteration failed: {}", e.getMessage());
        }
    }

}
