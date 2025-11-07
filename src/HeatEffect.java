import java.util.List;

public class HeatEffect implements WeatherEffect {
    @Override
    public Cell applyEffect(Cell currentLocation, Grid grid, List<Actor> otherActors) {
        // Heat makes player stay in place (overheated, can't move)
        return currentLocation;
    }

    @Override
    public String getDescription() {
        return "Heat (overheated!)";
    }
}