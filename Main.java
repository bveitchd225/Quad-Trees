import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Main extends GBSGame {

    public static final int SCREEN_WIDTH = 2048;
    public static final int SCREEN_HEIGHT = 1024;

    ArrayList<Ball> balls;

    public Main() {
        balls = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            balls.add( new Ball() );
        }
        // balls.add(new Ball(128, 256, 50, 100, 0));
        // balls.add(new Ball(128+256, 256, 30, -100, 0));
    }

    @Override
    public void update(double dt) {
        double start = System.nanoTime();
        for (Ball b: balls) {
            for (Ball o: balls) {
                if (b == o) {
                    continue;
                }
                b.collidesWith(o);
            }
        }
        System.out.println((System.nanoTime() - start)/1000000);
        for (Ball b: balls) {
            b.update(dt);
        }
        // double speedA = balls.get(0).getSpeed();
        // double speedB = balls.get(1).getSpeed();
        // double massA = balls.get(0).getMass();
        // double massB = balls.get(1).getMass();
        // double kinA = 0.5 * massA * speedA * speedA/1000;
        // double kinB = 0.5 * massB * speedB * speedB/1000;
        // System.out.println(kinA + kinB);
    }

    @Override
    public void draw(Graphics g) {
        for (Ball b: balls) {
            b.draw(g);
        }
        g.setColor(Color.BLACK);
        g.fillRect(10,10,60,20);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + getActiveFrameRate(), 15, 25);
    }







    public static void main(String[] args) {
        Main m = new Main();
        m.setResolution(SCREEN_WIDTH, SCREEN_HEIGHT);
        m.setFrameRate(10);
        m.createWindow();
    }
}
