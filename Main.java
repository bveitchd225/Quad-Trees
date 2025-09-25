import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import java.awt.event.MouseEvent;

public class Main extends GBSGame {

    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 1024;

    ArrayList<Particle> particles;
    QuadTree quadTree;
    boolean viewQuadTree;

    public Main() {
        particles = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
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
        for (Particle b : particles) {
            b.update(dt);
        }

        // Build Quad Tree
        quadTree = new QuadTree(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT), 4);
        for (Particle b : particles) {
            quadTree.add(b);
        }

        // Check Collisions
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
    }

    @Override
    public void onMouseClick(MouseEvent e) {
        particles.add(new Particle(e.getX(), e.getY(), 5, 0, 0));
    }

    @Override
    public void draw(Graphics g) {

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Draw Particles
        for (int i = 0; i < particles.size(); i++) {
            Particle b = particles.get(i);
            b.draw(g);
        }

        // Draw Quad Tree
        if (viewQuadTree) {
            g.setColor(Color.black);
            quadTree.draw(g);
        }
        g.setColor(Color.BLACK);
        g.fillRect(10, 10, 100, 16 * 1 + 4);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + getActiveFrameRate(), 15, 25);
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.setResolution(SCREEN_WIDTH, SCREEN_HEIGHT);
        m.setFrameRate(480);
        m.createWindow();
    }
}
