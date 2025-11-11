package ru.eliseevtech.openweathersdk.service;

import ru.eliseevtech.openweathersdk.dto.WeatherDto;
import ru.eliseevtech.openweathersdk.exception.WeatherException;

public interface WeatherService extends AutoCloseable {

    /**
     * Возвращает текущую погоду для города.
     * Если в кеше актуально (<10 минут) — возвращает кеш.
     * Иначе — вызывает OpenWeather API и обновляет кеш.
     *
     * @throws WeatherException при сетевых/парсинг/бизнес ошибках
     */
    WeatherDto getCurrentWeather(String city) throws WeatherException;

    /**
     * Запуск одной итерации фонового обновления (для режима POLLING).
     */
    void pollOnce();

    /**
     * Остановка/освобождение ресурсов.
     */
    void delete();

    @Override
    default void close() {
        delete();
    }

}
