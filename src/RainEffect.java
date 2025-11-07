import java.util.List;
import java.util.Random;

public class RainEffect implements WeatherEffect {
    private Random random = new Random();

    @Override
    public Cell applyEffect(Cell currentLocation, Grid grid, List<Actor> otherActors) {
        // Rain slides player left or right by 2 cells
        int direction = random.nextBoolean() ? 2 : -2;
        
        char currentCol = currentLocation.col;
        int newColIndex = (currentCol - 'A') + direction;
        
        // Ensure we stay within bounds
        if (newColIndex < 0) newColIndex = 0;
        if (newColIndex > 19) newColIndex = 19;
        
        char newCol = (char) ('A' + newColIndex);
        
        return grid.cellAtColRow(newCol, currentLocation.row)
                .orElse(currentLocation);
    }

    @Override
    public String getDescription() {
        return "Rain (slides 2 cells)";
    }
}