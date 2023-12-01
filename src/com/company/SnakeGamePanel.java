package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SnakeGamePanel extends JPanel implements KeyListener {
    private static final double START_GAME_SPEED = 10; //FPS
    private static final double GAME_SPEED_INCREMENT = 0.5; //FPS
    private static final double MAX_GAME_SPEED = 20; //FPS
    private static final int SNAKE_START_SIZE = 3;
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;
    private static final int TILE_SIZE = 40;
    private static final Color SNAKE_RGB = new Color(11, 59, 3);
    private static final boolean SHADOW_MODE = true;
    private static final Color BACKGROUND_COLOR = Color.black;
    public int[][] board;
    public double currentGameSpeed;
    public int xSnakeHead;
    public int ySnakeHead;
    public int snakeSize;
    public int xApplePosition;
    public int yApplePosition;
    public int score;
    ArrayList<KeyEvent> queue;

    public static JFrame jFrame;
    public static boolean fullscreen;
    public SnakeGamePanel() {
        setBackground(new Color(BACKGROUND_COLOR.getRed(),BACKGROUND_COLOR.getGreen(),BACKGROUND_COLOR.getBlue()));
        setPreferredSize(new Dimension(BOARD_WIDTH * TILE_SIZE, BOARD_HEIGHT * TILE_SIZE));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
    }

    private void gameLoop() throws InterruptedException {
        while (true){
            board = new int[BOARD_WIDTH][BOARD_HEIGHT];
            xSnakeHead = BOARD_WIDTH/2;
            ySnakeHead = BOARD_HEIGHT/2;
            snakeSize = SNAKE_START_SIZE;
            currentGameSpeed = ((int)(1000/START_GAME_SPEED));
            queue = new ArrayList<>();

            board[xSnakeHead][ySnakeHead] = snakeSize;
            createNewApple();
            score = 0;
            while (tick()){
                if (appleIsGone()){
                    score++;
                    snakeSize++;
                    createNewApple();
                    if (currentGameSpeed < MAX_GAME_SPEED){
                        currentGameSpeed += GAME_SPEED_INCREMENT;
                    }
                }
                TimeUnit.MICROSECONDS.sleep((long)currentGameSpeed * 1000);
                repaint();
            }
        }
    }

    private boolean appleIsGone() {
        return !(board[xApplePosition][yApplePosition] < 0);
    }

    private void createNewApple() {
        Random random = new Random();
        ArrayList<Integer> availableXSquares = new ArrayList<>();
        ArrayList<Integer> availableYSquares = new ArrayList<>();
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
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

    private boolean tick() {
        if (queue.size() == 0){
            return true;
        }
        KeyEvent previous = queue.get(0);
        if (queue.size() >= 2){
            queue.remove(0);
        }

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                if (board[x][y] > 0){
                    board[x][y]--;
                }
            }
        }

        KeyEvent currentKeyEvent = queue.get(0);

        int keyCode = currentKeyEvent.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP -> {
                if (ySnakeHead - 1 < 0){
                    return false;
                }

                if (board[xSnakeHead][ySnakeHead - 1] == snakeSize - 2){
                    queue.add(previous);
                    queue.remove(0);
                    for (int x = 0; x < board.length; x++) {
                        for (int y = 0; y < board[0].length; y++) {
                            if (board[x][y] > 0){
                                board[x][y]++;
                            }
                        }
                    }
                    tick();
                    return true;
                }
                if (board[xSnakeHead][ySnakeHead - 1] > 0){
                    return false;
                }

                board[xSnakeHead][ySnakeHead - 1] = snakeSize;
                ySnakeHead--;
            }
            case KeyEvent.VK_DOWN -> {
                if (ySnakeHead + 1 > board[0].length - 1){
                    return false;
                }
                if (board[xSnakeHead][ySnakeHead + 1] == snakeSize - 2){
                    queue.add(previous);
                    queue.remove(0);
                    for (int x = 0; x < board.length; x++) {
                        for (int y = 0; y < board[0].length; y++) {
                            if (board[x][y] > 0){
                                board[x][y]++;
                            }
                        }
                    }
                    tick();
                    return true;
                }
                if (board[xSnakeHead][ySnakeHead + 1] > 0){
                    return false;
                }
                board[xSnakeHead][ySnakeHead + 1] = snakeSize;
                ySnakeHead++;
            }
            case KeyEvent.VK_LEFT -> {
                if (xSnakeHead - 1 < 0){
                    return false;
                }
                if (board[xSnakeHead - 1][ySnakeHead] == snakeSize - 2){
                    queue.add(previous);
                    queue.remove(0);
                    for (int x = 0; x < board.length; x++) {
                        for (int y = 0; y < board[0].length; y++) {
                            if (board[x][y] > 0){
                                board[x][y]++;
                            }
                        }
                    }
                    tick();
                    return true;
                }
                if (board[xSnakeHead - 1][ySnakeHead] > 0){
                    return false;
                }
                board[xSnakeHead - 1][ySnakeHead] = snakeSize;
                xSnakeHead--;
            }
            case KeyEvent.VK_RIGHT -> {
                if (xSnakeHead + 1 > board.length - 1){
                    return false;
                }
                if (board[xSnakeHead + 1][ySnakeHead] == snakeSize - 2){
                    queue.add(previous);
                    queue.remove(0);
                    for (int x = 0; x < board.length; x++) {
                        for (int y = 0; y < board[0].length; y++) {
                            if (board[x][y] > 0){
                                board[x][y]++;
                            }
                        }
                    }
                    tick();
                    return true;
                }
                if (board[xSnakeHead + 1][ySnakeHead] > 0){
                    return false;
                }
                board[xSnakeHead + 1][ySnakeHead] = snakeSize;
                xSnakeHead++;
            }
        }
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        // write your code here
        fullscreen = false;
        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SnakeGamePanel gamePanel = new SnakeGamePanel();
        jFrame.add(gamePanel);
        jFrame.setResizable(false);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        gamePanel.gameLoop();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                if (board[x][y] > 0){
                    double alphaColor = 205 * ((double)board[x][y] / (double)snakeSize) + 50;
                    g.setColor(new Color(SNAKE_RGB.getRed(),SNAKE_RGB.getGreen(),SNAKE_RGB.getBlue(), (int)alphaColor));
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
                if (board[x][y] == 0 && SHADOW_MODE){
                    double snakeDistance = Math.sqrt(Math.pow(Math.abs(x - xSnakeHead), 2) + Math.pow(Math.abs(y - ySnakeHead), 2));
                    int alphaColor = (int) (100 * 255/(4 * 3.14159265 * Math.pow(snakeDistance,2)));
                    if (alphaColor < 0){
                        alphaColor = 0;
                    }
                    double snakeDistance1 = Math.sqrt(Math.pow(Math.abs(x - xApplePosition), 2) + Math.pow(Math.abs(y - yApplePosition), 2));
                    int alphaColor1 = (int) (100 * 255/(4 * 3.14159265 * Math.pow(snakeDistance1,2)));
                    if (alphaColor1 < 0){
                        alphaColor1 = 0;
                    }
                    int alphaSum = Math.min(Math.max(0, alphaColor + alphaColor1), 255);
                    g.setColor(new Color(255-BACKGROUND_COLOR.getRed(),255-BACKGROUND_COLOR.getGreen(),255-BACKGROUND_COLOR.getBlue(), alphaSum));
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        g.setColor(new Color(255,0,0,255));
        g.fillRect(xApplePosition * TILE_SIZE, yApplePosition * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        Font customFont = null;
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("font\\MinecraftRegular-Bmg3.ttf")).deriveFont((float) TILE_SIZE);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        if (score > 9){
            customFont = customFont.deriveFont(Font.PLAIN, (float)TILE_SIZE/2);
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //register the font
        ge.registerFont(customFont);
        g.setFont(customFont);
        g.setColor(Color.white);
        g.drawString(String.valueOf(score), xApplePosition * TILE_SIZE + TILE_SIZE/4, yApplePosition * TILE_SIZE + TILE_SIZE - TILE_SIZE/8);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F11){
            fullscreen = !fullscreen;
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
            case KeyEvent.VK_UP -> {
                System.out.println("up");
                queue.add(e);
            }
            case KeyEvent.VK_DOWN -> {
                System.out.println("down");
                queue.add(e);
            }
            case KeyEvent.VK_LEFT -> {
                System.out.println("left");
                queue.add(e);
            }
            case KeyEvent.VK_RIGHT -> {
                System.out.println("right");
                queue.add(e);
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
