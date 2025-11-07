import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Cell extends Rectangle {
  static int size = 35;
  char col;
  int row;

  public Cell(char inCol, int inRow, int x, int y) {
    super(x, y, size, size);
    col = inCol;
    row = inRow;
  }

  public void paint(Graphics g, Point mousePos) {
    // Create a unique checkerboard pattern with gradient effect
    boolean isDark = (col + row) % 2 == 0;
    
    if(contains(mousePos)) {
      // Highlight on hover with a light green tint
      g.setColor(new Color(200, 255, 200));
    } else {
      // Alternating light cream and soft lavender colors
      if (isDark) {
        g.setColor(new Color(245, 245, 240)); // Light cream
      } else {
        g.setColor(new Color(230, 220, 245)); // Soft lavender
      }
    }
    g.fillRect(x, y, size, size);
    
    // Add subtle grid lines
    g.setColor(new Color(180, 180, 180));
    g.drawRect(x, y, size, size);
    
    // Add corner dots for visual interest
    g.setColor(new Color(200, 200, 200, 100));
    g.fillOval(x + 2, y + 2, 3, 3);
    g.fillOval(x + size - 5, y + size - 5, 3, 3);
  }

  @Override
  public boolean contains(Point p) {
    if(p != null) {
      return super.contains(p);
    } else {
      return false;
    }
  }

  public int leftOfComparison(Cell c) {
    return Integer.compare(col, c.col);
  }

  public int aboveComparison(Cell c) {
    return Integer.compare(row, c.row);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Cell cell = (Cell) obj;
    return col == cell.col && row == cell.row;
  }

  @Override
  public int hashCode() {
    return 31 * col + row;
  }
}