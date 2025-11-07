public class WeatherData {
    public final long timestamp;
    public final String attribute;
    public final int x;
    public final int y;
    public final double value;

    public WeatherData(long timestamp, String attribute, int x, int y, double value) {
        this.timestamp = timestamp;
        this.attribute = attribute;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    // Convert from server coordinates (origin at center) to grid coordinates
    public int getGridX() {
        return x + 10; // Grid is 20x20, center is at 10,10
    }

    public int getGridY() {
        return y + 10;
    }

    public boolean isInGrid() {
        int gridX = getGridX();
        int gridY = getGridY();
        return gridX >= 0 && gridX < 20 && gridY >= 0 && gridY < 20;
    }

    public char getGridCol() {
        return (char) ('A' + getGridX());
    }

    public int getGridRow() {
        return getGridY();
    }

    @Override
    public String toString() {
        return String.format("%d %s (%d,%d) = %.2f", timestamp, attribute, x, y, value);
    }
}