import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Optional;

public class SelectingNewLocation implements GameState {
  @Override
  public void mouseClick(int x, int y, Stage s) {
    Optional<Cell> clicked = Optional.empty();
    for(Cell c: s.cellOverlay) {
      if(c.contains(x, y)) {
        clicked = Optional.of(c);
      }
    }
    s.cellOverlay = new ArrayList<Cell>();
    if(clicked.isPresent() && s.playerInAction.isPresent()) {
      Actor actor = s.playerInAction.get();
      actor.setLocation(clicked.get());
      
      // Check for yarn collection
      s.checkYarnCollection(actor);
      
      // Apply weather effects after movement
      s.applyWeatherEffect(actor);
      
      actor.turns--;
      int humansWithMovesLeft = 0;
      for(Actor player: s.listOfPlayers) {
        if(!player.isBot() && player.turns > 0) {
          humansWithMovesLeft++;
        }
      }
      if(humansWithMovesLeft > 0) {
        s.currentState = new ChoosingActor();
      } else {
        s.currentState = new BotMoving();
      }
    }
  }

  @Override
  public void paint(Graphics g, Stage s) {
    // no paint activity for this GameState
  }

  public String toString() {
    return getClass().getSimpleName();
  }
}