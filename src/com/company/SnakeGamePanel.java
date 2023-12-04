package com.company;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SnakeGamePanel extends JPanel implements KeyListener{
    private static final double START_GAME_SPEED = 10; //FPS
    private static final double GAME_SPEED_INCREMENT = 0.5; //FPS
    private static final double MAX_GAME_SPEED = 20; //FPS
    private static final double SPEED_BOOST_SPEED = 5; //FPS
    public static final int APPLE_LIGHT_SPEED = 1000; //ms
    public static final int FONT_INCREASE_SPEED = 250;
    private static final int SNAKE_START_SIZE = 3;
    private static int boardWidth;
    private static int boardHeight;
    public  static final int TILE_SIZE = 32;
    private static final Color SNAKE_RGB = new Color(2, 135, 49);
    private static final boolean SHADOW_MODE = true;
    private static final Color BACKGROUND_COLOR = Color.black;
    public static final int SNAKE_HEAD_LIGHT_LEVEL = 5000;
    public static final int APPLE_LIGHT_LEVEL = 10000;
    public static final int FONT_SIZE = TILE_SIZE * 10;
    public static Font customFont;
    public static Clip sfx;
    public Color currentAppleColor;
    public int[][] board;
    public double currentGameSpeed;
    public int xSnakeHead;
    public int ySnakeHead;
    public int snakeLength;
    public int xApplePosition;
    public int yApplePosition;
    public int score;
    public int speedBoost;
    public long timeSinceLastApple;
    public long timeSinceLastTurn;
    public long timeOfLastTick;
    public ArrayList<Integer> appleInSnakePosition;
    public boolean appleEaten;
    ArrayList<KeyEvent> queue;
    public int lastDirection;
    public boolean paused;
    public static boolean fullscreen;
    public SnakeGamePanel(int boardWidth, int boardHeight) {
        SnakeGamePanel.boardWidth = boardWidth;
        SnakeGamePanel.boardHeight = boardHeight;
        setBackground(new Color(BACKGROUND_COLOR.getRed(),BACKGROUND_COLOR.getGreen(),BACKGROUND_COLOR.getBlue()));
        setPreferredSize(new Dimension(SnakeGamePanel.boardWidth * TILE_SIZE, SnakeGamePanel.boardHeight * TILE_SIZE));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        fullscreen = false;
        paused = false;
        loadFont();
    }

    public void gameLoop() throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
        while (true){
            resetBoard(boardWidth,boardHeight);
            while (tick()){
                if (paused){
                    continue;
                }
                if (appleIsGone()){
                    score++;
                    appleInSnakePosition.add(snakeLength);
                    appleEaten = true;

                    int loop = 1;
                    while(score % (Math.pow(10, loop)) == 0){
                        loop++;
                    }
                    loadSound();
                    sfx.loop(loop - 1);

                    if (speedBoost > 0){
                        currentGameSpeed = 1000/(1000/currentGameSpeed - (speedBoost * SPEED_BOOST_SPEED/100));
                    }
                    speedBoost=100;

                    snakeLength++;
                    createNewApple();
                    if (currentGameSpeed > 1000/MAX_GAME_SPEED){
                        currentGameSpeed = 1000/(1000/currentGameSpeed + GAME_SPEED_INCREMENT);
                    }
                    currentGameSpeed = 1000/(1000/currentGameSpeed + SPEED_BOOST_SPEED);
                }
                if (speedBoost > 0){
                    speedBoost--;
                    currentGameSpeed = 1000/(1000/currentGameSpeed - (SPEED_BOOST_SPEED/100));
                }
                long timeSinceLastTick = System.currentTimeMillis() - timeOfLastTick;
                TimeUnit.MICROSECONDS.sleep((long)currentGameSpeed * 1000 - timeSinceLastTick * 1000);
                repaint();
            }
        }
    }

    private boolean appleIsGone() {
        return !(board[xApplePosition][yApplePosition] < 0);
    }

    private void createNewApple() {
        timeSinceLastApple = System.currentTimeMillis();
        Random random = new Random();
        ArrayList<Integer> availableXSquares = new ArrayList<>();
        ArrayList<Integer> availableYSquares = new ArrayList<>();
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                if (board[x][y] == 0){
                    availableXSquares.add(x);
                    availableYSquares.add(y);
                }
            }
        }
        int rand = random.nextInt(availableXSquares.size());
        xApplePosition = availableXSquares.get(rand);
        yApplePosition = availableYSquares.get(rand);
        board[xApplePosition][yApplePosition] = -1;
    }
    public void setPaused (boolean pause){
        paused = pause;
    }

    private boolean tick() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (paused){
            return true;
        }
        timeOfLastTick = System.currentTimeMillis();
        if (queue.size() == 0){
            return true;
        }
        KeyEvent previous = queue.get(0);
        if (queue.size() >= 2){
            queue.remove(0);
        }

        KeyEvent currentKeyEvent = queue.get(0);

        int keyCode = currentKeyEvent.getKeyCode();

        int newXSnakeHead = 0;
        int newYSnakeHead = 0;
        if (previous != currentKeyEvent){

            timeSinceLastTurn = System.currentTimeMillis();
        }
        switch (keyCode) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                lastDirection = 1;
                newXSnakeHead = xSnakeHead;
                newYSnakeHead = ySnakeHead - 1;
                if (newYSnakeHead < 0){
                    return false;
                }
            }
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                lastDirection = 2;
                newXSnakeHead = xSnakeHead;
                newYSnakeHead = ySnakeHead + 1;
                if (newYSnakeHead > board[0].length - 1){
                    return false;
                }
            }
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                lastDirection = 3;
                newYSnakeHead = ySnakeHead;
                newXSnakeHead = xSnakeHead - 1;
                if (newXSnakeHead < 0){
                    return false;
                }
            }
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                lastDirection = 4;
                newYSnakeHead = ySnakeHead;
                newXSnakeHead = xSnakeHead + 1;
                if (newXSnakeHead > board.length - 1){
                    return false;
                }
            }
        }
        if (board[newXSnakeHead][newYSnakeHead] == snakeLength - 1){
            queue.add(previous);
            queue.remove(0);

            tick();
            return true;
        }
        if (board[newXSnakeHead][newYSnakeHead] > 1){
            return false;
        }
        board[newXSnakeHead][newYSnakeHead] = snakeLength + 1;
        xSnakeHead = newXSnakeHead;
        ySnakeHead = newYSnakeHead;

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                if (board[x][y] > 0){
                    board[x][y]--;
                }
            }
        }

        return true;
    }

    private void loadSound() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        sfx = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(System.getProperty("user.dir") + "\\resources\\sfx\\boop1.wav"));
        sfx.open(inputStream);
    }

    private static void loadFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "\\resources\\font\\MinecraftRegular-Bmg3.ttf")).deriveFont((float) FONT_SIZE);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //register the font
        ge.registerFont(customFont);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        double fontPercentage = (double)(System.currentTimeMillis() - timeSinceLastApple)/((double)FONT_INCREASE_SPEED);
        double fontIncrease = 128 * Math.max(0, (1 - fontPercentage));
        int fontSize = (int) (FONT_SIZE + fontIncrease);
        g.setFont(customFont.deriveFont((float)fontSize));
        g.setColor(new Color(Math.min(Math.max(0,255 - (score * 5)),255),0 ,Math.min(score * 5, 255),50 + 205 * speedBoost/100));
        g.drawString(String.valueOf(score), (((boardWidth - 1) * TILE_SIZE) / 2) - 2 * TILE_SIZE * String.valueOf(score).length() - (String.valueOf(score).length() - 1) * TILE_SIZE - (int)fontIncrease/4, ((boardHeight - 1) * TILE_SIZE/4) + 128);

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                if (board[x][y] > 0){
                    double alphaColor = Math.min(Math.max(0,205 * ((double)board[x][y] / (double) snakeLength) + 50), 255);

                    if (appleInSnakePosition.contains(board[x][y])){
                        g.setColor(new Color(currentAppleColor.getRed(),currentAppleColor.getGreen(),currentAppleColor.getBlue(), (int)alphaColor));
                    }
                    else{
                        g.setColor(new Color(SNAKE_RGB.getRed(),SNAKE_RGB.getGreen(),SNAKE_RGB.getBlue(), (int)alphaColor));
                    }
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
                if (board[x][y] <= 0 && SHADOW_MODE){
                    //credit: MONKE
                    double appleGlowPercentage = (double)(System.currentTimeMillis() - timeSinceLastApple)/((double)750);
                    double appleGlow = Math.max(0, ((double) 1 - appleGlowPercentage));

                    double turnGlowPercentage = (double)(System.currentTimeMillis() - timeSinceLastTurn)/((double)500);
                    double turnGlow = Math.max(0, ((double) 1 - turnGlowPercentage));
                    double snakeDistance = Math.sqrt(Math.pow(Math.abs(x - xSnakeHead), 2) + Math.pow(Math.abs(y - ySnakeHead), 2));
                    int alphaColor = (int) ((SNAKE_HEAD_LIGHT_LEVEL + (appleGlow * 10000) + (turnGlow * 5000))/(4 * 3.14159265 * Math.pow(snakeDistance,2)));
                    alphaColor = Math.min(Math.max(0, alphaColor), 255) ;
                    double appleDistance = Math.sqrt(Math.pow(Math.abs(x - xApplePosition), 2) + Math.pow(Math.abs(y - yApplePosition), 2));
                    double percentage = (double)(System.currentTimeMillis() - timeSinceLastApple)/((double)APPLE_LIGHT_SPEED);
                    double appleLightLevel = (double)APPLE_LIGHT_LEVEL * (1 - percentage);
                    int alphaColor1 = (int) (appleLightLevel / ((double) 4 * 3.14159265 * Math.pow(appleDistance,2)));
                    alphaColor1 = Math.min(Math.max(0, alphaColor1), 255) ;

                    int alphaSum = Math.min(Math.max(0, alphaColor + alphaColor1), 255);

                    double redPercentage = (double)(System.currentTimeMillis() - timeSinceLastApple)/((double)500);

                    int gb =  (int)Math.max(0, (Math.min(255,redPercentage * (double)255)));
                    g.setColor(new Color(255-BACKGROUND_COLOR.getRed(),gb,gb, alphaSum));
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    if (board[x][y] == -1 && SHADOW_MODE){
                        currentAppleColor = new Color(Math.min(Math.max(0,255 - (score * 5)),255),0 ,Math.min(score * 5, 255),alphaSum);
                        g.setColor(currentAppleColor);
                        g.fillRect(xApplePosition * TILE_SIZE, yApplePosition * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
        for (int i = 0; i < appleInSnakePosition.size(); i++) {
            appleInSnakePosition.set(i, appleInSnakePosition.get(i) - 2);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            paused = !paused;
        }
        if (paused){
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_F11){
            fullscreen = !fullscreen;
            JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (fullscreen){
                GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                jFrame.dispose();
                jFrame.setUndecorated(true);

                if (gd.isFullScreenSupported()) {
                    gd.setFullScreenWindow(jFrame);
                } else {
                    System.err.println("Full screen not supported");
                    setSize(100, 100); // just something to let you see the window
                    setVisible(true);
                }
            }
            else {
                jFrame.dispose();
                jFrame.setUndecorated(false);
                jFrame.setVisible(true);
            }
        }

        if (queue.size()!=0){
            if (queue.get(0).getKeyCode() == e.getKeyCode() && queue.size() < 2){
                return;
            }
        }

        if (queue.size() == 3){
            return;
        }

        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_A, KeyEvent.VK_S -> queue.add(e);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void resetBoard(int boardWidth1, int boardHeight1) {
        boardWidth = boardWidth1;
        boardHeight = boardHeight1;
        board = new int[boardWidth][boardHeight];
        xSnakeHead = boardWidth / 2;
        ySnakeHead = boardHeight / 2;
        snakeLength = SNAKE_START_SIZE;
        speedBoost = 100;
        currentGameSpeed = ((int)(1000/START_GAME_SPEED));
        queue = new ArrayList<>();
        appleInSnakePosition = new ArrayList<>();
        board[xSnakeHead][ySnakeHead] = snakeLength;
        createNewApple();
        score = 0;
    }
}
