import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Stage implements WeatherObserver {
  Grid grid;
  List<Actor> listOfPlayers;
  List<Cell> cellOverlay;
  Optional<Actor> playerInAction;
  List<Yarn> yarnBalls;
  boolean gameWon = false;
  Actor winner = null; // Track which actor won
  Rectangle exitButton;

  GameState currentState;
  Beat beat;
  
  WeatherStreamClient weatherClient;
  Map<String, CellDecorator> cellDecorators = new ConcurrentHashMap<>();
  private Random random = new Random();

  public Stage() {
    grid = new Grid();
    listOfPlayers = new ArrayList<Actor>();
    cellOverlay = new ArrayList<Cell>();
    playerInAction = Optional.empty();
    currentState = new ChoosingActor();
    beat = new AnimationBeat();
    yarnBalls = new ArrayList<>();
    
    // Create exit button for win screen
    exitButton = new Rectangle(412, 450, 200, 50);
    
    // Initialize weather system
    weatherClient = new WeatherStreamClient();
    weatherClient.addObserver(this);
    weatherClient.startStreaming();
    
    // Spawn initial yarn balls
    spawnYarn(15); // Start with 15 yarn balls on the grid
  }

  private void spawnYarn(int count) {
    for (int i = 0; i < count; i++) {
      int col = random.nextInt(20);
      int row = random.nextInt(20);
      grid.cellAtColRow(col, row).ifPresent(cell -> {
        // Make sure no yarn already at this location
        boolean occupied = yarnBalls.stream().anyMatch(y -> y.isAt(cell));
        if (!occupied) {
          yarnBalls.add(new Yarn(cell));
        }
      });
    }
  }

  public void addPlayer(Actor player) {
    listOfPlayers.add(player);
    if(player.isBot()) {
      beat.punchIn(player);
    }
  }

  @Override
  public void onWeatherUpdate(WeatherData data) {
    // Observer pattern: receive weather updates
    if (data.isInGrid()) {
      String key = data.getGridCol() + "" + data.getGridRow();
      CellDecorator decorator = cellDecorators.computeIfAbsent(key, k -> {
        Optional<Cell> cell = grid.cellAtColRow(data.getGridCol(), data.getGridRow());
        return cell.map(CellDecorator::new).orElse(null);
      });
      
      if (decorator != null) {
        decorator.updateWeather(data.attribute, data.value);
      }
    }
  }

  public void paint(Graphics g, Point mouseLoc) {
    if (gameWon) {
      paintWinScreen(g);
      return;
    }

    // do we have bot moves to make?
    currentState.paint(g, this);
    grid.paint(g, mouseLoc);
    
    // Paint weather effects on cells using streams
    cellDecorators.values().stream()
        .forEach(decorator -> decorator.paint(g, mouseLoc));
    
    // Decay weather effects over time
    cellDecorators.values().forEach(CellDecorator::decayWeather);
    
    // Blue cell selection overlay with 50% transparency
    grid.paintOverlay(g, cellOverlay, new Color(0f, 0f, 1f, 0.5f));

    // Paint yarn balls
    yarnBalls.forEach(yarn -> yarn.paint(g));

    beat.ticktock();
    for(Actor player: listOfPlayers) {
      player.paint(g);
    }
    draw_sidepanel(g, mouseLoc);
  }

  private void paintWinScreen(Graphics g) {
    // Background
    g.setColor(new Color(128, 0, 128));
    g.fillRect(0, 0, 1024, 720);
    
    // Determine winner name and type
    String winnerName = winner.getClass().getSimpleName().toUpperCase();
    String winnerType = winner.isBot() ? " (AI)" : " (YOU)";
    boolean playerWon = !winner.isBot();
    
    // Main title
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 60));
    if (playerWon) {
      g.drawString("YOU WIN!", 320, 200);
    } else {
      g.drawString("GAME OVER", 280, 200);
    }
    
    // Winner announcement
    g.setFont(new Font("Arial", Font.BOLD, 36));
    if (playerWon) {
      g.setColor(Color.YELLOW);
    } else {
      g.setColor(Color.RED);
    }
    g.drawString(winnerName + " WINS!" + winnerType, 280, 270);
    
    // Stats
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.PLAIN, 24));
    g.drawString("Yarn Collected: " + winner.getYarnCollected(), 350, 330);
    
    // Fun message
    g.setFont(new Font("Arial", Font.ITALIC, 18));
    if (playerWon) {
      g.drawString("Congratulations! You collected 20 yarn balls!", 280, 370);
    } else {
      g.drawString("The AI got to 20 yarn balls first. Try again!", 280, 370);
    }
    
    // Exit button
    g.setColor(Color.RED);
    g.fillRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
    g.setColor(Color.WHITE);
    g.drawRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
    g.setFont(new Font("Arial", Font.BOLD, 24));
    g.drawString("EXIT TO MENU", exitButton.x + 25, exitButton.y + 33);
  }

  private void draw_sidepanel(Graphics g, Point mouseLoc) {
    final int hTab = 10;
    final int blockVT = 35;
    final int margin = 21*blockVT;
    int yLoc = 20;

    // Get player's yarn count (first non-bot actor)
    int playerYarnCount = 0;
    for (Actor a : listOfPlayers) {
      if (!a.isBot()) {
        playerYarnCount = a.getYarnCollected();
        break;
      }
    }

    // Player Yarn counter with percentage bar
    g.setColor(new Color(128, 0, 128));
    g.setFont(new Font("Arial", Font.BOLD, 16));
    g.drawString("Your Yarn: " + playerYarnCount + " / 20", margin, yLoc);
    yLoc += 5;
    
    // Draw percentage bar
    int barWidth = 150;
    int barHeight = 20;
    double percentage = playerYarnCount / 20.0;
    
    // Bar background
    g.setColor(new Color(200, 200, 200));
    g.fillRect(margin, yLoc, barWidth, barHeight);
    
    // Bar fill
    g.setColor(new Color(128, 0, 128));
    g.fillRect(margin, yLoc, (int)(barWidth * percentage), barHeight);
    
    // Bar outline
    g.setColor(Color.BLACK);
    g.drawRect(margin, yLoc, barWidth, barHeight);
    
    // Percentage text
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 12));
    String percentText = (int)(percentage * 100) + "%";
    g.drawString(percentText, margin + barWidth/2 - 15, yLoc + 14);
    
    yLoc += blockVT;

    // state display
    g.setColor(Color.DARK_GRAY);
    g.setFont(new Font("Arial", Font.PLAIN, 12));
    g.drawString(currentState.toString(), margin, yLoc);
    yLoc = yLoc + blockVT;
    
    Optional<Cell> underMouse = grid.cellAtPoint(mouseLoc);
    if(underMouse.isPresent()) {
      Cell hoverCell = underMouse.get();
      g.setColor(Color.DARK_GRAY);
      String coord = String.valueOf(hoverCell.col) + String.valueOf(hoverCell.row);
      g.drawString(coord, margin, yLoc);
      yLoc += 15;
      
      // Show weather effect at this cell
      String key = hoverCell.col + "" + hoverCell.row;
      CellDecorator decorator = cellDecorators.get(key);
      if (decorator != null) {
        g.drawString("Weather: " + decorator.getCurrentEffect().getDescription(), margin, yLoc);
      }
    }

    // agent display
    final int vTab = 15;
    final int labelIndent = margin + hTab;
    final int valueIndent = margin + 3*blockVT;
    yLoc = yLoc + 2*blockVT;
    for(int i = 0; i < listOfPlayers.size(); i++){
      Actor a = listOfPlayers.get(i);
      yLoc = yLoc + 2*blockVT;
      g.drawString(a.getClass().getName(), margin, yLoc);
      g.drawString("location:", labelIndent, yLoc+vTab);
      g.drawString(Character.toString(a.loc.col) + Integer.toString(a.loc.row), valueIndent, yLoc+vTab);
      g.drawString("player type:", labelIndent, yLoc+2*vTab);
      g.drawString(a.isBot() ? "Bot" : "Human", valueIndent, yLoc+2*vTab);
      
      // Show yarn collected by this actor
      g.setColor(new Color(128, 0, 128));
      g.drawString("yarn collected:", labelIndent, yLoc+3*vTab);
      g.drawString(String.valueOf(a.getYarnCollected()), valueIndent, yLoc+3*vTab);
      g.setColor(Color.DARK_GRAY);
      
      if(a.isBot() && a.mover != null) {
        g.drawString("mover:", labelIndent, yLoc+4*vTab);
        g.drawString(a.mover.getClass().getName(), valueIndent, yLoc+4*vTab);
      }
    }    
  }

  public List<Cell> getClearRadius(Cell from, int size) {
    List<Cell> init = grid.getRadius(from, size);
    for(Actor player: listOfPlayers) {
      init.remove(player.loc);
    }
    return init;
  }

  public String mouseClicked(int x, int y) {
    if (gameWon) {
      if (exitButton.contains(x, y)) {
        return "exit";
      }
      return "playing";
    }
    currentState.mouseClick(x, y, this);
    return "playing";
  }

  public void checkYarnCollection(Actor actor) {
    // Using streams to find and remove collected yarn
    List<Yarn> collected = yarnBalls.stream()
        .filter(yarn -> yarn.isAt(actor.loc))
        .collect(Collectors.toList());
    
    yarnBalls.removeAll(collected);
    
    // Update actor's personal count only
    int collectedCount = collected.size();
    for (int i = 0; i < collectedCount; i++) {
      actor.collectYarn();
    }
    
    // Check if this actor won
    if (actor.getYarnCollected() >= 20) {
      gameWon = true;
      winner = actor;
    }
  }

  public void applyWeatherEffect(Actor actor) {
    String key = actor.loc.col + "" + actor.loc.row;
    CellDecorator decorator = cellDecorators.get(key);
    
    if (decorator != null && decorator.getCurrentEffect() != null) {
      WeatherEffect effect = decorator.getCurrentEffect();
      Cell newLocation = effect.applyEffect(actor.loc, grid, listOfPlayers);
      
      // Make sure new location is not occupied by another actor
      boolean occupied = listOfPlayers.stream()
          .anyMatch(a -> a != actor && a.loc.equals(newLocation));
      
      if (!occupied) {
        actor.setLocation(newLocation);
      }
    }
  }
}