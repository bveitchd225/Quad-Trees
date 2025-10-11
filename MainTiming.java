
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class MainTiming extends GBSGame {

    public static final int SCREEN_WIDTH = 512;
    public static final int SCREEN_HEIGHT = 512;

    ArrayList<Particle> particles;
    QuadTree quadTree;

    private RollingAverage particlesUpdate = new RollingAverage(100);
    private RollingAverage quadTreeBuild = new RollingAverage(100);
    private RollingAverage quadTreeCollisions = new RollingAverage(100);
    private RollingAverage particlesDraw = new RollingAverage(100);

    boolean viewQuadTree;

    ArrayList<Particle> particlesToAdd;
    int currentRadius;

    // Max for 480 is 700
    public MainTiming() {
        particles = new ArrayList<>();
        particlesToAdd = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            particles.add(new Particle(75));
        }
        viewQuadTree = false;
        currentRadius = 75;
    }

    @Override
    public void update(double dt) {
        if (particlesToAdd.size() > 0) {
            for (int i = 0; i < particlesToAdd.size(); i++) {
                particles.add(particlesToAdd.remove(i));
            }
        }

        if (GBSGame.keyPressed("q")) {
            viewQuadTree = !viewQuadTree;
        }

        if (GBSGame.keyPressed("up")) {
            currentRadius++;
        }
        if (GBSGame.keyPressed("down")) {
            currentRadius++;
        }

        updateParticles(dt);

    }

    public void updateParticles(double dt) {
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
            double searchRadius = b.getRadius()+b.getRadius();
            for (Particle o : quadTree.query(
                    new Rectangle((int) (b.getX() - searchRadius), (int) (b.getY() - searchRadius), (int) (searchRadius * 2), (int) (searchRadius * 2)))) {
                if (b == o) {
                    continue;
                }
                b.collidesWith(o);
            }
        }

        quadTreeCollisions.addValue(System.nanoTime() - start);
    }

    public void updateParticlesBad(double dt) {
        // Update Particles
        double start = System.nanoTime();
        for (Particle b : particles) {
            b.update(dt);
        }
        particlesUpdate.addValue(System.nanoTime() - start);

        // Build Quad Tree
        start = System.nanoTime();
        // quadTree = new QuadTree(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT), 4);
        // for (Particle b : particles) {
        //     quadTree.add(b);
        // }
        quadTreeBuild.addValue(System.nanoTime() - start);

        // Check Collisions
        start = System.nanoTime();
        for (int i = 0; i < particles.size(); i++) {
            Particle b = particles.get(i);
            // double searchRadius = b.getRadius() + Particle.max_radius;
            for (int j = i+1; j < particles.size(); j++) {
                Particle o = particles.get(j);
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
        // System.out.println(e.getButton());
        if (e.getButton() == 1) {
            particlesToAdd.add(new Particle(e.getX(), e.getY(), currentRadius, 0, 0));
        }
        if (e.getButton() == 0) {
            MouseWheelEvent we = (MouseWheelEvent) e;
            if (we.getPreciseWheelRotation() == -1) {
                currentRadius++;
            }
            else {
                currentRadius--;
            }

            // System.out.println(e);
        }

        
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
        g.fillRect(10, 10, 100, 16 * 7 + 4);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + getActiveFrameRate(), 15, 25);
        g.drawString("update:   " + Math.round(particlesUpdate.currentAverage() / 1000) / 1000.0, 15, 25 + 16 * 1);
        g.drawString("QT Build: " + Math.round(quadTreeBuild.currentAverage() / 1000) / 1000.0, 15, 25 + 16 * 2);
        g.drawString("QT Query: " + Math.round(quadTreeCollisions.currentAverage() / 1000) / 1000.0, 15, 25 + 16 * 3);
        g.drawString("update (G): " + Math.round(getAveragePhysicsTime() * 1000) / 1000.0, 15, 25 + 16 * 4);
        g.drawString("draw:     " + Math.round(particlesDraw.currentAverage() / 1000) / 1000.0, 15, 25 + 16 * 5);
        g.drawString("draw (G): " + Math.round(getAverageDrawTime() * 1000) / 1000.0, 15, 25 + 16 * 6);

        g.setColor(Color.BLACK);
        g.drawString("Current radius: " + currentRadius, SCREEN_WIDTH - 110, 30);

        double sum = 0;
        for (Particle p: particles) {
            sum += 0.5 * p.getMass() * p.getSpeed() * p.getSpeed();
        }

        g.drawString("Current Energy: " + (int) sum, SCREEN_WIDTH - 110, 60);
    }

    public static void main(String[] args) {
        MainTiming m = new MainTiming();
        m.setResolution(SCREEN_WIDTH, SCREEN_HEIGHT);
        m.setPhysicsRate(960);
        m.setFrameRate(60);
        m.createWindow();
    }
}
