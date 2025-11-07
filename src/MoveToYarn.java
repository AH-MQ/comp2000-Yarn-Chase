import java.util.List;
import java.util.Optional;

// Strategy Pattern: Bots move toward nearest yarn
public class MoveToYarn implements MoveStrategy {
  private List<Yarn> yarnBalls;
  
  public MoveToYarn(List<Yarn> yarnBalls) {
    this.yarnBalls = yarnBalls;
  }
  
  @Override
  public Cell chooseNextLoc(List<Cell> possibleLocs, Actor currActor, List<Actor> otherActors) {
    // Find nearest yarn ball
    Optional<Yarn> nearestYarn = yarnBalls.stream()
        .min((y1, y2) -> {
          int dist1 = distance(currActor.loc, y1.getLocation());
          int dist2 = distance(currActor.loc, y2.getLocation());
          return Integer.compare(dist1, dist2);
        });
    
    if (nearestYarn.isPresent()) {
      Cell yarnLoc = nearestYarn.get().getLocation();
      
      // Find the cell in possibleLocs that gets closest to the yarn
      return possibleLocs.stream()
          .min((c1, c2) -> {
            int dist1 = distance(c1, yarnLoc);
            int dist2 = distance(c2, yarnLoc);
            return Integer.compare(dist1, dist2);
          })
          .orElse(possibleLocs.get(0));
    }
    
    // If no yarn, move randomly
    return new MoveRandomly().chooseNextLoc(possibleLocs, currActor, otherActors);
  }
  
  private int distance(Cell c1, Cell c2) {
    int colDiff = Math.abs(c1.col - c2.col);
    int rowDiff = Math.abs(c1.row - c2.row);
    return colDiff + rowDiff; // Manhattan distance
  }
  
  public String toString() {
    return "yarn-seeking movement";
  }
}