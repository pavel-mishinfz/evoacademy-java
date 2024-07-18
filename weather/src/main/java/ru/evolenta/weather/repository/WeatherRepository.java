package ru.evolenta.weather.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.evolenta.weather.model.Weather;

import java.util.Optional;

@Repository
public interface WeatherRepository extends CrudRepository<Weather, Integer> {
    Optional<Weather> findWeatherByLatitudeAndLongitude(double latitude, double longitude);
}
