import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GBSGame extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Thread gameThread;
    private JFrame f;

    private int FPS;
    private int PPS = 60;
    private double maxFrameTime;
    private double maxPhysicsTime = 1.0 / PPS;
    private int WIDTH;
    private int HEIGHT;

    private BufferedImage buffer;
    private Graphics screen;

    private int lastReportedFrameRate = -1;
    private RollingAverage drawAverage = new RollingAverage(100);
    private RollingAverage physicsAverage = new RollingAverage(1000);

    private static ArrayList<String> keys = new ArrayList<>();
    private static ArrayList<String> keyEvents = new ArrayList<>();

    private static int lastReportedMouseX = -1;
    private static int lastReportedMouseY = -1;
    
    public void setResolution(int w, int h) {
        WIDTH = w;
        HEIGHT = h;
        buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        screen = buffer.getGraphics();
    }

    public void setFrameRate(int f) {
        FPS = f;
        maxFrameTime = 1000000000.0 / f;
    }

    public void setPhysicsRate(int f) {
        PPS = f;
        maxPhysicsTime = 1000000000.0 / f;
    }

    public int getActiveFrameRate() {
        return (int) Math.min(lastReportedFrameRate, 1/(maxFrameTime/1000000000));
    }

    public double getAverageDrawTime() {
        return drawAverage.currentAverage();
    }

    public double getAveragePhysicsTime() {
        return physicsAverage.currentAverage();
    }

    public void createWindow() {
        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
        }
        f = new JFrame();
        f.add(this);
        f.setTitle("Game");
        this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        f.setFocusable(true);
        startGameThread();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        // This will call the "run" method of the object passed into the thread.
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000/FPS;
        double drawDelta = 0;

        double physicsInterval = 1000000000/PPS;
        double physicsDelta = 0;

        double lastTime = System.nanoTime();
        long currentTime = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            drawDelta += (currentTime - lastTime) / drawInterval;
            physicsDelta += (currentTime - lastTime) / physicsInterval;
            lastTime = currentTime;

            if (physicsDelta >= 1) {
                double start = System.nanoTime();
                update(physicsDelta/PPS);
                physicsAverage.addValue((System.nanoTime() - start)/1000000);
                physicsDelta = 0;
            }

            if (drawDelta >= 1) {
                double start = System.nanoTime();
                draw(screen);
                drawAverage.addValue((System.nanoTime() - start)/1000000);
                repaint();
                drawDelta=0;
            }
        }
    }

    // Meant to override
    public void update(double dt) {

    }

    // Meant to override
    public void draw(Graphics g) {

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,WIDTH, HEIGHT);
        g.drawImage(buffer,0,0,null);
    }

    public static boolean keyDown(String key) {
        if (key.equals("space")) {
            key = " ";
        }
        return keys.contains(key);
    }

    public Image getImage(String filePath) {
        return javax.swing.ImageIcon.class.getResource(filePath) != null
            ? new javax.swing.ImageIcon(getClass().getResource(filePath)).getImage()
            : new javax.swing.ImageIcon(filePath).getImage();
    }

    public static boolean keyPressed(String key) {
        if (key.equals("space")) {
            key = " ";
        }
        boolean keyPressedBool = keyEvents.contains(key);
        while (keyEvents.contains(key)) {
            keyEvents.remove(key);
        }
        return keyPressedBool;
    }

    public static int getMouseX() {
        return lastReportedMouseX;
    }

    public static int getMouseY() {
        return lastReportedMouseY;
    }

    @Override
    public void keyTyped(KeyEvent e) {
       
    }

    @Override
    public void keyPressed(KeyEvent e) {

        String keyChar = e.getKeyChar() + "";
        
        if (e.getKeyCode() == 38) {
            keyChar = "up";
        }
        if (e.getKeyCode() == 40) {
            keyChar = "down";
        }
        if (e.getKeyCode() == 37) {
            keyChar = "left";
        }
        if (e.getKeyCode() == 39) {
            keyChar = "right";
        }

        if (!keys.contains(keyChar + "")) {
            keyEvents.add(keyChar + "");
        }
        keys.add(keyChar + "");
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String key = e.getKeyChar() + "";
        
        if (e.getKeyCode() == 38) {
            key = "up";
        }
        if (e.getKeyCode() == 40) {
            key = "down";
        }
        if (e.getKeyCode() == 37) {
            key = "left";
        }
        if (e.getKeyCode() == 39) {
            key = "right";
        }

        while (keys.contains(key)) {
            keys.remove(key);
        }
        while (keyEvents.contains(key)) {
            keyEvents.remove(key);
        }
    }

    public void onMouseClick(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        onMouseClick(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        lastReportedMouseX = e.getX();
        lastReportedMouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // TODO Auto-generated method stub
        onMouseClick(e);
    }
}