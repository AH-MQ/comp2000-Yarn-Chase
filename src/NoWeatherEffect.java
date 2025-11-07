import java.util.List;

public class NoWeatherEffect implements WeatherEffect {
    @Override
    public Cell applyEffect(Cell currentLocation, Grid grid, List<Actor> otherActors) {
        // No weather effect, normal movement
        return currentLocation;
    }

    @Override
    public String getDescription() {
        return "Clear";
    }
}