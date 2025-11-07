import java.util.List;
import java.util.Random;

public class WindEffect implements WeatherEffect {
    private Random random = new Random();

    @Override
    public Cell applyEffect(Cell currentLocation, Grid grid, List<Actor> otherActors) {
        // Wind teleports player to random location
        int randomCol = random.nextInt(20);
        int randomRow = random.nextInt(20);
        
        char col = (char) ('A' + randomCol);
        
        return grid.cellAtColRow(col, randomRow)
                .orElse(currentLocation);
    }

    @Override
    public String getDescription() {
        return "Wind (random teleport)";
    }
}