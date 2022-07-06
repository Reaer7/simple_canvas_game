package com.gmail.reater.last;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class Game extends Canvas implements Runnable {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BUBBLE_DIAMETER = 80;
    private static final String WINDOW_TITLE = "TUTORIAL 1";
    private static final Color[] COLORS = {Color.RED, Color.PINK, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA};
    private static final Random random = new Random();

    private boolean running;
    private Sprite hero;
    private int xHeroCoordination;
    private int yHeroCoordination;
    private boolean isPopped;
    private int xBubbleCoordination;
    private int yBubbleCoordination;
    private int count;

    public static void main(String[] args) {
        Game game = new Game();
        game.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        JFrame frame = new JFrame(Game.WINDOW_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(game, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        game.start();
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        long delta;

        KeyInputHandler keyInputHandler = init();

        while (running) {
            delta = System.currentTimeMillis() - lastTime;
            lastTime = System.currentTimeMillis();
            render();
            update(delta, keyInputHandler);
        }
    }

    public KeyInputHandler init() {
        KeyInputHandler keyInputHandler = new KeyInputHandler();
        addKeyListener(keyInputHandler);
        hero = getSprite("person.png");
        return keyInputHandler;
    }

    public void render() {
        BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            createBufferStrategy(2);
            requestFocus();
            isPopped = true;
            return;
        }

        Graphics heroGraphics = bufferStrategy.getDrawGraphics();
        heroGraphics.setColor(Color.black);
        heroGraphics.fillRect(0, 0, getWidth(), getHeight());
        hero.draw(heroGraphics, xHeroCoordination, yHeroCoordination);
        heroGraphics.dispose();

        Graphics bubbleGraphics = bufferStrategy.getDrawGraphics();
        Color color = COLORS[count % COLORS.length];
        if (isPopped) {
            xBubbleCoordination = random.nextInt(WINDOW_WIDTH - BUBBLE_DIAMETER);
            yBubbleCoordination = random.nextInt(WINDOW_HEIGHT - BUBBLE_DIAMETER);
            isPopped = false;
        }
        bubbleGraphics.setColor(color);
        bubbleGraphics.fillOval(xBubbleCoordination, yBubbleCoordination, BUBBLE_DIAMETER, BUBBLE_DIAMETER);
        bubbleGraphics.dispose();

        Graphics textGraphics = bufferStrategy.getDrawGraphics();
        textGraphics.setColor(Color.WHITE);
        String message = "Count = " + count;
        textGraphics.drawString(message,
                WINDOW_WIDTH - message.length() * 8,
                WINDOW_HEIGHT - message.length() * 2
        );
        textGraphics.dispose();

        bufferStrategy.show();
    }

    public void update(long delta, KeyInputHandler keyInputHandler) {
        if (keyInputHandler.isLeftPressed() && xHeroCoordination > 0) {
            xHeroCoordination -= delta;
        }
        if (keyInputHandler.isRightPressed() && xHeroCoordination < WINDOW_WIDTH - hero.getWidth()) {
            xHeroCoordination += delta;
        }
        if (keyInputHandler.isUpPressed() && yHeroCoordination > 0) {
            yHeroCoordination -= delta;
        }
        if (keyInputHandler.isDownPressed() && yHeroCoordination < WINDOW_HEIGHT - hero.getHeight()) {
            yHeroCoordination += delta;
        }

        if (xHeroCoordination + hero.getWidth() / 2 >= xBubbleCoordination &&
                xHeroCoordination + hero.getWidth() / 2 <= xBubbleCoordination + BUBBLE_DIAMETER &&
                yHeroCoordination + hero.getHeight() / 2 >= yBubbleCoordination &&
                yHeroCoordination + hero.getHeight() / 2 <= yBubbleCoordination + BUBBLE_DIAMETER
        ) {
            isPopped = true;
            count++;
        }
    }

    public Sprite getSprite(String path) {
        try {
            URL url = this.getClass()
                    .getClassLoader()
                    .getResource(path);
            if (url != null) {
                BufferedImage sourceImage = ImageIO.read(url);
                return new Sprite(Toolkit.getDefaultToolkit()
                        .createImage(sourceImage.getSource())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}