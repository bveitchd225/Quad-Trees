import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GBSGame extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Thread gameThread;
    private JFrame f;

    private int FPS;
    private double maxFrameTime;
    private int WIDTH;
    private int HEIGHT;

    private BufferedImage buffer;
    private Graphics screen;

    private int lastReportedFrameRate = -1;
    private int[] reportedFrameRates = new int[100];
    private int rollingIndex = 0;

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

    public int getActiveFrameRate() {
        return (int) Math.min(lastReportedFrameRate, 1/(maxFrameTime/1000000000));
    }

    public double getAverageFrameTime() {
        return 1000.0/lastReportedFrameRate;
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
        double delta = 0;
        double lastTime = System.nanoTime();
        long currentTime = 0;

        // double lastFrame = System.nanoTime();
        double start = System.nanoTime();
        double after = System.nanoTime();
        double frameTime = 1000000000/FPS;

        while (gameThread != null) {
            // currentTime = System.nanoTime();

            // delta += (currentTime - lastTime) / drawInterval;
            // lastTime = currentTime;

            // if (delta >= 1) {
                
            //     // reportedFrameRates[rollingIndex] = (int) (1.0 / ((System.nanoTime() - lastFrame)/1000000000));
            //     // lastFrame = System.nanoTime();
            //     // rollingIndex++;
            //     // if (rollingIndex > reportedFrameRates.length-1) {
            //     //     rollingIndex = 0;
            //     //     int sum = 0;
            //     //     for (int n: reportedFrameRates) {
            //     //         sum += n;
            //     //     }
            //     //     lastReportedFrameRate = sum / reportedFrameRates.length;
            //     // }
            //     double start = System.nanoTime();
            //     update(delta/FPS);
            //     repaint();
            //     System.out.println(System.nanoTime() - start);
            //     delta=0; 
            // }

            double lastStart = start;
            start = System.nanoTime();
            update((start - lastStart)/1000000000);
            draw(screen);
            after = System.nanoTime();
            repaint();
            frameTime = after - start;
            // System.out.println(frameTime/1000000);

            reportedFrameRates[rollingIndex] = (int) (1.0 / (frameTime/1000000000));
            
        

            if (frameTime < maxFrameTime) {
                reportedFrameRates[rollingIndex] = (int) (1.0 / (frameTime/1000000000));
                // wait until we're ready
                try {
                    // System.out.println("Waiting: " + (int) (maxFrameTime - frameTime));
                    Thread.sleep((long) (maxFrameTime - frameTime)/1000000);
                } catch (InterruptedException e) {
                    
                }
            }
            else {
                reportedFrameRates[rollingIndex] = (int) (1.0 / (frameTime/1000000000));
            }

            rollingIndex++;
            if (rollingIndex > reportedFrameRates.length-1) {
                rollingIndex = 0;
                int sum = 0;
                for (int n: reportedFrameRates) {
                    sum += n;
                }
                lastReportedFrameRate = sum / reportedFrameRates.length;
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
        if (!keys.contains(e.getKeyChar() + "")) {
            keyEvents.add(e.getKeyChar() + "");
        }
        keys.add(e.getKeyChar() + "");
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String key = e.getKeyChar() + "";
        while (keys.contains(key)) {
            keys.remove(key);
        }
        while (keyEvents.contains(key)) {
            keyEvents.remove(key);
        }
    }

    public void onMouseClick(MouseEvent e) {
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        onMouseClick(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

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