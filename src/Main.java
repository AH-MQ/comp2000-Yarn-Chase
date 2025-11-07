import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.time.Duration;
import java.time.Instant;


public class Main extends JFrame {
    public static void main(String[] args) throws Exception {
      Main window = new Main();
      window.run();
    }

    class Canvas extends JPanel implements MouseListener {
      Stage stage;
      MenuState menu;
      String gameState = "menu"; // "menu", "playing", or "exit"
      
      public Canvas() {
        setPreferredSize(new Dimension(1024, 720));
        this.addMouseListener(this);
        menu = new MenuState();
      }

      @Override
      public void paint(Graphics g) {
        if (gameState.equals("menu")) {
          menu.paint(g);
        } else if (gameState.equals("playing")) {
          stage.paint(g, getMousePosition());
        }
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        if (gameState.equals("menu")) {
          String result = menu.handleClick(e.getX(), e.getY());
          if (result.equals("start")) {
            gameState = "playing";
            stage = StageReader.readStage("data/stage1.rvb");
          } else if (result.equals("exit")) {
            System.exit(0);
          }
        } else if (gameState.equals("playing")) {
          String result = stage.mouseClicked(e.getX(), e.getY());
          if (result.equals("exit")) {
            gameState = "menu";
          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {}

      @Override
      public void mouseReleased(MouseEvent e) {}

      @Override
      public void mouseEntered(MouseEvent e) {}

      @Override
      public void mouseExited(MouseEvent e) {}
    }

    private Main() {
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      Canvas canvas = new Canvas();
      this.setContentPane(canvas);
      this.pack();
      this.setVisible(true);
    }

    public void run() {
      while(true) {
        // Re-draw the screen 50 times per second
        Instant startTime = Instant.now();
        repaint();
        Instant endTime = Instant.now();
        long howLong = Duration.between(startTime, endTime).toMillis();
        try {
          Thread.sleep(20l - howLong);
        } catch(InterruptedException e) {
          System.out.println("thread was interrupted, nothing to worry about!");
        } catch(IllegalArgumentException e) {
          System.out.println("application can't keep up with framerate");
        }
      }
    }
}