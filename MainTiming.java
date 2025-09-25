import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import java.awt.event.MouseEvent;

public class MainTiming extends GBSGame {

    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 1024;

    ArrayList<Particle> particles;
    QuadTree quadTree;

    private RollingAverage particlesUpdate = new RollingAverage(100);
    private RollingAverage quadTreeBuild = new RollingAverage(100);
    private RollingAverage quadTreeCollisions = new RollingAverage(100);
    private RollingAverage particlesDraw = new RollingAverage(100);

    boolean viewQuadTree;

    // Max for 480 is 700

    public MainTiming() {
        particles = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            particles.add(new Particle());
        }
        viewQuadTree = false;
    }

    @Override
    public void update(double dt) {

        if (GBSGame.keyPressed("q")) {
            viewQuadTree = !viewQuadTree;
        }

        // Update Particles
        double start = System.nanoTime();
        for (Particle b : particles) {
            b.update(dt);
        }
        particlesUpdate.addValue(System.nanoTime() - start);

        // Build Quad Tree
        start = System.nanoTime();
        quadTree = new QuadTree(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT), 4);
        for (Particle b : particles) {
            quadTree.add(b);
        }
        quadTreeBuild.addValue(System.nanoTime() - start);

        // Check Collisions
        start = System.nanoTime();
        for (int i = 0; i < particles.size(); i++) {
            Particle b = particles.get(i);
            for (Particle o : quadTree.query(
                    new Rectangle((int) b.getX(), (int) b.getY(), (int) b.getRadius() * 2, (int) b.getRadius() * 2))) {
                if (b == o) {
                    continue;
                }
                b.collidesWith(o);
            }
        }

        quadTreeCollisions.addValue(System.nanoTime() - start);
    }

    @Override
    public void onMouseClick(MouseEvent e) {
        particles.add(new Particle(e.getX(), e.getY(), 5, 0, 0));
    }

    @Override
    public void draw(Graphics g) {
        double start = System.nanoTime();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        // double start = System.nanoTime();
        for (int i = 0; i < particles.size(); i++) {
            Particle b = particles.get(i);
            b.draw(g);
        }
        particlesDraw.addValue(System.nanoTime() - start);

        if (viewQuadTree) {
            g.setColor(Color.black);
            quadTree.draw(g);
        }
        g.setColor(Color.BLACK);
        g.fillRect(10, 10, 100, 16 * 6 + 4);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + getActiveFrameRate(), 15, 25);
        g.drawString("update:   " + Math.round(particlesUpdate.currentAverage() / 1000) / 1000.0, 15, 25 + 16 * 1);
        g.drawString("QT Build: " + Math.round(quadTreeBuild.currentAverage() / 1000) / 1000.0, 15, 25 + 16 * 2);
        g.drawString("QT Query: " + Math.round(quadTreeCollisions.currentAverage() / 1000) / 1000.0, 15, 25 + 16 * 3);
        g.drawString("draw:     " + Math.round(particlesDraw.currentAverage() / 1000) / 1000.0, 15, 25 + 16 * 4);
        g.drawString("total:     " + Math.round(getAverageFrameTime() * 1000) / 1000.0, 15, 25 + 16 * 5);
    }

    public static void main(String[] args) {
        MainTiming m = new MainTiming();
        m.setResolution(SCREEN_WIDTH, SCREEN_HEIGHT);
        m.setFrameRate(480);
        m.createWindow();
    }
}
