import java.util.List;

// Strategy Pattern: Different weather effects
public interface WeatherEffect {
    Cell applyEffect(Cell currentLocation, Grid grid, List<Actor> otherActors);
    String getDescription();
}