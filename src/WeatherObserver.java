// Observer Pattern: Objects that need to be notified of weather changes
public interface WeatherObserver {
    void onWeatherUpdate(WeatherData data);
}