import java.awt.Graphics;
import java.util.List;

public class BotMoving implements GameState {
  @Override
  public void mouseClick(int x, int y, Stage s) {
    // no mouseClick activity for this GameState
  }

  @Override
  public void paint(Graphics g, Stage s) {
    for(Actor player: s.listOfPlayers) {
      if(player.isBot()) {
        List<Cell> possibleLocs = s.getClearRadius(player.loc, player.moves);
        if (!possibleLocs.isEmpty()) {
          // Use yarn-seeking strategy for bots
          MoveStrategy yarnSeeker = new MoveToYarn(s.yarnBalls);
          Cell chosenCell = yarnSeeker.chooseNextLoc(possibleLocs, player, s.listOfPlayers);
          player.setLocation(chosenCell);
          
          // Bots can also collect yarn
          s.checkYarnCollection(player);
          
          // Apply weather effects to bots too
          s.applyWeatherEffect(player);
        }
      }
    }
    s.currentState = new ChoosingActor();
    for(Actor player: s.listOfPlayers) {
      player.turns = 1;
    }
  }  

  public String toString() {
    return getClass().getSimpleName();
  }
}