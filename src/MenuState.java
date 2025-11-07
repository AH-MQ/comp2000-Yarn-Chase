import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

public class MenuState {
    private Rectangle startButton;
    private Rectangle howToPlayButton;
    private Rectangle exitButton;
    private Rectangle backButton; // For returning from How to Play
    private boolean showingHowToPlay = false;
    
    public MenuState() {
        // Create buttons centered on screen
        int buttonWidth = 200;
        int buttonHeight = 50;
        int centerX = 512 - buttonWidth / 2;
        int startY = 250;
        
        startButton = new Rectangle(centerX, startY, buttonWidth, buttonHeight);
        howToPlayButton = new Rectangle(centerX, startY + 70, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, startY + 140, buttonWidth, buttonHeight);
        backButton = new Rectangle(centerX, 600, buttonWidth, buttonHeight);
    }
    
    public void paint(Graphics g) {
        if (showingHowToPlay) {
            paintHowToPlay(g);
        } else {
            paintMainMenu(g);
        }
    }
    
    private void paintMainMenu(Graphics g) {
        // Background
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, 1024, 720);
        
        // Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("YARN CHASE", 280, 150);
        
        // Subtitle
        g.setFont(new Font("Arial", Font.ITALIC, 20));
        g.drawString("Collect 20 yarn balls before the bots do!", 300, 190);
        
        // Draw buttons
        drawButton(g, startButton, "START", Color.GREEN);
        drawButton(g, howToPlayButton, "HOW TO PLAY", Color.BLUE);
        drawButton(g, exitButton, "EXIT", Color.RED);
    }
    
    private void paintHowToPlay(Graphics g) {
        // Background
        g.setColor(new Color(128, 0, 128));
        g.fillRect(0, 0, 1024, 720);
        
        // Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("HOW TO PLAY", 320, 80);
        
        // Instructions
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        int y = 140;
        int lineSpacing = 35;
        
        g.drawString("GOAL: Collect 20 yarn balls before the AI bots!", 100, y);
        y += lineSpacing + 10;
        
        g.drawString("CONTROLS:", 100, y);
        y += lineSpacing;
        g.drawString("  • Click on your animal (Cat, Dog, or Bird) to select it", 120, y);
        y += lineSpacing;
        g.drawString("  • Click on a highlighted cell to move there", 120, y);
        y += lineSpacing;
        g.drawString("  • Collect purple yarn balls by moving onto them", 120, y);
        y += lineSpacing + 10;
        
        g.drawString("ANIMALS:", 100, y);
        y += lineSpacing;
        g.setColor(Color.CYAN);
        g.drawString("  • Cat (BLUE):", 120, y);
        g.setColor(Color.WHITE);
        g.drawString(" moves 2 spaces", 280, y);
        y += lineSpacing;
        g.setColor(Color.YELLOW);
        g.drawString("  • Dog (YELLOW):", 120, y);
        g.setColor(Color.WHITE);
        g.drawString(" moves 1 space", 300, y);
        y += lineSpacing;
        g.setColor(Color.GREEN);
        g.drawString("  • Bird (GREEN):", 120, y);
        g.setColor(Color.WHITE);
        g.drawString(" moves 3 spaces", 305, y);
        y += lineSpacing + 10;
        
        g.drawString("WEATHER EFFECTS:", 100, y);
        y += lineSpacing;
        g.setColor(new Color(0, 100, 255));
        g.drawString("  • Rain (BLUE):", 120, y);
        g.setColor(Color.WHITE);
        g.drawString(" slides you 2 cells left or right", 290, y);
        y += lineSpacing;
        g.setColor(new Color(255, 150, 0));
        g.drawString("  • Heat (ORANGE):", 120, y);
        g.setColor(Color.WHITE);
        g.drawString(" makes you overheat and stay in place", 320, y);
        y += lineSpacing;
        g.setColor(new Color(220, 220, 220));
        g.drawString("  • Wind (WHITE):", 120, y);
        g.setColor(Color.WHITE);
        g.drawString(" teleports you to a random location", 310, y);
        y += lineSpacing + 10;
        
        g.drawString("TIP: Watch out for AI bots - they chase yarn too!", 100, y);
        
        // Back button
        drawButton(g, backButton, "BACK", Color.black);
    }
    
    private void drawButton(Graphics g, Rectangle button, String text, Color color) {
        // Button background
        g.setColor(color);
        g.fillRect(button.x, button.y, button.width, button.height);
        
        // Button border
        g.setColor(Color.WHITE);
        g.drawRect(button.x, button.y, button.width, button.height);
        
        // Button text
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textX = button.x + (button.width - textWidth) / 2;
        int textY = button.y + (button.height + 15) / 2;
        g.drawString(text, textX, textY);
    }
    
    public String handleClick(int x, int y) {
        if (showingHowToPlay) {
            if (backButton.contains(x, y)) {
                showingHowToPlay = false;
            }
            return "menu";
        }
        
        if (startButton.contains(x, y)) {
            return "start";
        } else if (howToPlayButton.contains(x, y)) {
            showingHowToPlay = true;
            return "menu";
        } else if (exitButton.contains(x, y)) {
            return "exit";
        }
        return "menu";
    }
}