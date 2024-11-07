package catintheforest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class CatGame extends JPanel implements ActionListener, KeyListener {
    private int catX = 50, catY = 350, catWidth = 60, catHeight = 60;
    private int velocity = 0;
    private boolean jumping = false;
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private ImageIcon fishIcon, fishLifeIcon;
    private ArrayList<Rectangle> fishItems = new ArrayList<>();
    private ArrayList<ImageIcon> lifeIcons;
    private int lives = 3;
    private int score = 0;
    private Timer timer;
    private int hitboxWidth = 40, hitboxHeight = 40;
    private ImageIcon catIcon, spikeIcon, ghostIcon, backgroundIcon, startBackgroundIcon, gameOverBackgroundIcon;
    private Rectangle background;

    // CardLayout for switching panels
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel startScreen, gameScreen, gameOverScreen;

    public CatGame() {
        // Load images
        catIcon = loadImage("/img/catrun.gif");
        spikeIcon = loadImage("/img/thorn.png");
        ghostIcon = loadImage("/img/ghost.png");
        backgroundIcon = loadImage("/img/bg.png");
        fishIcon = loadImage("/img/fish_normal.png");
        fishLifeIcon = loadImage("/img/fish_heal.png");
        startBackgroundIcon = loadImage("/img/start.png");
        gameOverBackgroundIcon = loadImage("/img/Game_over.png");

        // Initialize life icons
        lifeIcons = new ArrayList<>();
        for (int i = 0; i < lives; i++) {
            lifeIcons.add(fishLifeIcon);
        }

        // Set up CardLayout and main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        setupStartScreen();
        setupGameScreen();
        setupGameOverScreen();

        // Set up JFrame
        JFrame frame = new JFrame("Cat in the Forest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

private void setupStartScreen() {
    startScreen = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (startBackgroundIcon != null) {
                g.drawImage(startBackgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        }
    };
    startScreen.setLayout(null);

    // Create a Start button and style it
    JButton startButton = new JButton("Start");
    startButton.setBounds(350, 400, 120, 50);
    startButton.setFont(new Font("Arial", Font.BOLD, 18));
    startButton.setForeground(Color.WHITE);
    startButton.setBackground(new Color(0, 153, 51)); // Green background
    startButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // White border
    startButton.setFocusPainted(false);
    startButton.setContentAreaFilled(false);
    startButton.setOpaque(true);
    startButton.addActionListener(e -> startGame());

    // Add mouse hover effect
    startButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            startButton.setBackground(new Color(0, 204, 102)); // Lighter green when hover
        }

        @Override
        public void mouseExited(MouseEvent e) {
            startButton.setBackground(new Color(0, 153, 51)); // Reset to original green
        }
    });

    startScreen.add(startButton);
    mainPanel.add(startScreen, "StartScreen");
}

    private void setupGameScreen() {
        gameScreen = this;
        setFocusable(true);
        addKeyListener(this);
        mainPanel.add(gameScreen, "GameScreen");

        // Timer for game refresh
        timer = new Timer(10, this);
    }

private void setupGameOverScreen() {
    gameOverScreen = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (gameOverBackgroundIcon != null) {
                g.drawImage(gameOverBackgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        }
    };
    gameOverScreen.setLayout(null);

    // Play Again button
    JButton playAgainButton = new JButton("Play Again");
    playAgainButton.setBounds(300, 350, 120, 50);
    playAgainButton.setFont(new Font("Arial", Font.BOLD, 18));
    playAgainButton.setForeground(Color.WHITE);
    playAgainButton.setBackground(new Color(255, 102, 0)); // Orange background
    playAgainButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    playAgainButton.setFocusPainted(false);
    playAgainButton.setContentAreaFilled(false);
    playAgainButton.setOpaque(true);
    playAgainButton.addActionListener(e -> restartGame());

    // Exit button
    JButton exitButton = new JButton("Exit");
    exitButton.setBounds(450, 350, 100, 50);
    exitButton.setFont(new Font("Arial", Font.BOLD, 18));
    exitButton.setForeground(Color.WHITE);
    exitButton.setBackground(new Color(255, 0, 0)); // Red background
    exitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    exitButton.setFocusPainted(false);
    exitButton.setContentAreaFilled(false);
    exitButton.setOpaque(true);
    exitButton.addActionListener(e -> System.exit(0));

    // Add mouse hover effect for exit button
    exitButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            exitButton.setBackground(new Color(255, 51, 51)); // Lighter red when hover
        }

        @Override
        public void mouseExited(MouseEvent e) {
            exitButton.setBackground(new Color(255, 0, 0)); // Reset to original red
        }
    });

    gameOverScreen.add(playAgainButton);
    gameOverScreen.add(exitButton);
    mainPanel.add(gameOverScreen, "GameOverScreen");
}

    private void startGame() {
        cardLayout.show(mainPanel, "GameScreen");
        resetGame();
        gameScreen.requestFocusInWindow();
        timer.start();
    }

    private void resetGame() {
        lives = 3;
        score = 0;
        lifeIcons.clear();
        for (int i = 0; i < lives; i++) {
            lifeIcons.add(fishLifeIcon);
        }
        catX = 50;
        catY = 350;
        jumping = false;
        velocity = 0;
        obstacles.clear();
        fishItems.clear();
        generateObstacles();
        repaint();
    }

    private void restartGame() {
        cardLayout.show(mainPanel, "StartScreen");
        resetGame();
    }

    private ImageIcon loadImage(String path) {
        return new ImageIcon(getClass().getResource(path));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawCat(g);
        drawObstacles(g);
        drawFish(g);
        drawScoreAndLives(g);
    }

    private void drawBackground(Graphics g) {
        if (backgroundIcon != null) {
            g.drawImage(backgroundIcon.getImage(), 0, 0, this);
        }
    }

    private void drawCat(Graphics g) {
        g.drawImage(catIcon.getImage(), catX, catY, catWidth, catHeight, this);
    }

    private void drawObstacles(Graphics g) {
        for (Rectangle obstacle : obstacles) {
            g.drawImage(obstacle.y == 350 ? spikeIcon.getImage() : ghostIcon.getImage(),
                    obstacle.x, obstacle.y, 50, 50, this);
        }
    }

    private void drawFish(Graphics g) {
        for (Rectangle fish : fishItems) {
            g.drawImage(fishIcon.getImage(), fish.x, fish.y, 30, 30, this);
        }
    }

    private void drawScoreAndLives(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 650, 40);

        for (int i = 0; i < lifeIcons.size(); i++) {
            g.drawImage(lifeIcons.get(i).getImage(), 10 + i * 40, 20, 30, 30, this);
        }
    }

    private void generateObstacles() {
        Random rand = new Random();
        int lastObstacleX = 0;

        for (int i = 0; i < 4; i++) {
            int yPosition = rand.nextBoolean() ? 350 : 250;
            int xPosition = lastObstacleX + 300 + rand.nextInt(500);
            obstacles.add(new Rectangle(xPosition, yPosition, 50, 50));
            fishItems.add(new Rectangle(xPosition + 50, 340, 30, 30));
            lastObstacleX = xPosition;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (jumping) {
            velocity += 2;
            catY += velocity;
            if (catY >= 350) {
                jumping = false;
                catY = 350;
                velocity = 0;
            }
        }

        ArrayList<Rectangle> toRemove = new ArrayList<>();
        ArrayList<Rectangle> fishToRemove = new ArrayList<>();
        Rectangle catHitbox = new Rectangle(catX + 10, catY + 10, hitboxWidth, hitboxHeight);

        for (Rectangle obstacle : obstacles) {
            obstacle.x -= 5;
            if (obstacle.x < 0) obstacle.x = 800 + new Random().nextInt(500);
            if (obstacle.intersects(catHitbox) && !lifeIcons.isEmpty()) {
                lifeIcons.remove(lifeIcons.size() - 1);
                toRemove.add(obstacle);
                if (lifeIcons.isEmpty()) gameOver();
            }
        }

        for (Rectangle fish : fishItems) {
            fish.x -= 5;
            if (fish.intersects(catHitbox)) {
                score += 100;
                fishToRemove.add(fish);
            }
            if (fish.x < 0) fish.x = 800 + new Random().nextInt(500);
        }

        fishItems.removeAll(fishToRemove);
        obstacles.removeAll(toRemove);
        score += (catX-catX)+1;
        repaint();
    }

    private void gameOver() {
        timer.stop();
        cardLayout.show(mainPanel, "GameOverScreen");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !jumping) {
            jumping = true;
            velocity = -30;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new CatGame();
    }
}
