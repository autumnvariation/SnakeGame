package com.company;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

public class SnakeGameFrame extends JFrame implements ComponentListener {

    SnakeGamePanel gamePanel;
    long timeOfLastResize;
    public SnakeGameFrame() throws HeadlessException, UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        timeOfLastResize = System.currentTimeMillis();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel = new SnakeGamePanel(64,36);
        add(gamePanel);
        setResizable(true);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        addComponentListener(this);
        gamePanel.gameLoop();
    }

    public static void main(String[] args) throws InterruptedException, UnsupportedAudioFileException, LineUnavailableException, IOException {
        // write your code here
        new SnakeGameFrame();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        gamePanel.resetBoard(gamePanel.getWidth()/SnakeGamePanel.TILE_SIZE, gamePanel.getHeight()/SnakeGamePanel.TILE_SIZE);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
