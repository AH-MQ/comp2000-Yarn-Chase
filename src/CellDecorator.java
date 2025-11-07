import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

// Decorator Pattern: Adds weather visualization to cells
public class CellDecorator extends Cell {
    private Cell decoratedCell;
    private WeatherEffect currentEffect;
    private double rainLevel = 0.0;
    private double windLevel = 0.0;
    private double tempLevel = 0.0;

    public CellDecorator(Cell cell) {
        super(cell.col, cell.row, cell.x, cell.y);
        this.decoratedCell = cell;
        this.currentEffect = new NoWeatherEffect();
    }

    public void updateWeather(String attribute, double value) {
        switch (attribute.toLowerCase()) {
            case "rain":
                rainLevel = Math.max(rainLevel, value); // Keep the highest value
                break;
            case "windx":
            case "windy":
                windLevel = Math.max(windLevel, value);
                break;
            case "temp":
                tempLevel = Math.max(tempLevel, value);
                break;
        }
        determineEffect();
    }

    // Using threshold logic to determine dominant weather effect
    private void determineEffect() {
        if (rainLevel > 0.4) {
            currentEffect = new RainEffect();
        } else if (tempLevel > 0.5) {
            currentEffect = new HeatEffect();
        } else if (windLevel > 0.4) {
            currentEffect = new WindEffect();
        } else {
            currentEffect = new NoWeatherEffect();
        }
    }

    public WeatherEffect getCurrentEffect() {
        return currentEffect;
    }

    @Override
    public void paint(Graphics g, Point mousePos) {
        decoratedCell.paint(g, mousePos);
        
        // Only show the DOMINANT weather effect with distinct colors
        if (rainLevel > 0.4 && currentEffect instanceof RainEffect) {
            // Rain is BLUE
            g.setColor(new Color(0, 100, 255, (int)(rainLevel * 120)));
            g.fillRect(x + 2, y + 2, width - 4, height - 4);
        } else if (tempLevel > 0.5 && currentEffect instanceof HeatEffect) {
            // Heat is ORANGE/YELLOW
            g.setColor(new Color(255, 150, 0, (int)(tempLevel * 100)));
            g.fillRect(x + 2, y + 2, width - 4, height - 4);
        } else if (windLevel > 0.4 && currentEffect instanceof WindEffect) {
            // Wind is LIGHT GRAY/WHITE
            g.setColor(new Color(220, 220, 220, (int)(windLevel * 110)));
            g.fillRect(x + 2, y + 2, width - 4, height - 4);
        }
    }

    public void decayWeather() {
        // Much slower decay - effects last longer (was 0.99, now 0.995)
        // At 50 FPS, 0.995 means effects last ~10 seconds instead of ~2 seconds
        rainLevel *= 0.995;
        windLevel *= 0.995;
        tempLevel *= 0.995;
        determineEffect();
    }
}