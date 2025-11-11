# ☀️ OpenWeather SDK (Spring Boot)

Лёгкий SDK для интеграции с [OpenWeather API](https://openweathermap.org/api) в проектах на **Spring Boot**.  
Позволяет получать текущую погоду, использовать кэш (до 10 городов, TTL 10 минут) и работать в режимах **ON_DEMAND** или **POLLING**.

---

## ⚙️ Установка и настройка

### Требования
- Java **17+**
- Spring Boot **3+**
- Аккаунт и API-ключ OpenWeather (см. ниже)


### Конфигурация `application.yml`

```yaml
openweather:
  api-key: YOUR_OPENWEATHER_KEY    # ключ, полученный с сайта
  mode: POLLING                    # ON_DEMAND | POLLING
  units: metric                    # standard | metric | imperial
  lang: ru                         # язык описаний
  polling-ms: 60000                # интервал фонового обновления (мс)
```

---

## 🔑 Получение API-ключа

1. Зарегистрируйтесь на [home.openweathermap.org](https://home.openweathermap.org/users/sign_up).  
2. Подтвердите e-mail — иначе ключ будет неактивен.  
3. Перейдите в [API Keys](https://home.openweathermap.org/api_keys).  
4. Скопируйте ключ (обычно называется `default`).  
5. Подождите 10–15 минут после активации и добавьте его в `application.yml`.

Проверка ключа:

```bash
curl "https://api.openweathermap.org/data/2.5/weather?q=Moscow&appid=YOUR_KEY&units=metric"
```

---

## 🚀 Запуск

```bash
mvn spring-boot:run
# или
mvn clean package -DskipTests
java -jar target/*.jar
```

---

## 💡 Примеры использования

### Через сервис

```java
@Component
@RequiredArgsConstructor
public class WeatherClient {
    private final WeatherService weatherService;

    public void printMoscowWeather() throws Exception {
        var dto = weatherService.getCurrentWeather("Moscow");
        System.out.println(dto.toJsonString());
    }
}
```

### REST-контроллер

```java
@RestController
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService service;

    @GetMapping("/weather")
    public String get(@RequestParam String city) throws Exception {
        return service.getCurrentWeather(city).toJsonString();
    }
}
```

Запрос:

```bash
curl "http://localhost:8080/weather?city=London"
```

---

## 📄 Пример JSON-ответа

```json
{
  "weather": {"main": "Clouds", "description": "scattered clouds"},
  "temperature": {"temp": 12.3, "feels_like": 10.8},
  "visibility": 10000,
  "wind": {"speed": 2.3},
  "datetime": 1699512000,
  "sys": {"sunrise": 1699492800, "sunset": 1699527600},
  "timezone": 3600,
  "name": "London"
}
```

---

## ⚠️ Возможные ошибки

| Код | Причина |
|-----|----------|
| **401** | неверный или неактивный API-ключ |
| **404** | город не найден |
| **5xx / IOException** | ошибка сети или API |

---

## 🧾 Лицензия

MIT © 2025 Eliseev Technologies
