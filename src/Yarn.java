import java.awt.Color;
import java.awt.Graphics;

public class Yarn {
    private Cell location;
    private static final int SIZE = 12;
    private Color color = new Color(128, 0, 128); // Purple

    public Yarn(Cell loc) {
        this.location = loc;
    }

    public void paint(Graphics g) {
        // Draw purple yarn ball
        g.setColor(color);
        int centerX = location.x + location.width / 2;
        int centerY = location.y + location.height / 2;
        g.fillOval(centerX - SIZE/2, centerY - SIZE/2, SIZE, SIZE);
        
        // Add some texture lines
        g.setColor(Color.MAGENTA);
        g.drawOval(centerX - SIZE/2, centerY - SIZE/2, SIZE, SIZE);
        g.drawLine(centerX - SIZE/4, centerY - SIZE/4, centerX + SIZE/4, centerY + SIZE/4);
        g.drawLine(centerX - SIZE/4, centerY + SIZE/4, centerX + SIZE/4, centerY - SIZE/4);
    }

    public Cell getLocation() {
        return location;
    }

    public boolean isAt(Cell c) {
        return location.col == c.col && location.row == c.row;
    }
}