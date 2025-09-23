import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Main extends GBSGame {

    public static final int SCREEN_WIDTH = 2048;
    public static final int SCREEN_HEIGHT = 1024;

    ArrayList<Ball> balls;

    private double[] reportedFrameTimes = new double[100];
    private int rollingIndex = 0;

    public Main() {
        balls = new ArrayList<>();
        for (int i = 0; i < 7500; i++) {
            balls.add( new Ball() );
        }
        // balls.add(new Ball(128, 256, 50, 100, 0));
        // balls.add(new Ball(128+256, 256, 30, -100, 0));
    }

    @Override
    public void update(double dt) {
        double start = System.nanoTime();
        for (int i = 0; i < balls.size(); i++) {
            Ball b = balls.get(i);
            for (int j = i+1; j < balls.size(); j++) {
                Ball o = balls.get(j);
                b.collidesWith(o);
            }
        }
        reportedFrameTimes[rollingIndex] = (double) (System.nanoTime() - start)/1000000;
        rollingIndex++;
        if (rollingIndex > reportedFrameTimes.length-1) {
            rollingIndex=0;
            double sum = 0;
            for (double n: reportedFrameTimes) {
                sum += n;
            }
            System.out.println(sum / reportedFrameTimes.length);
        }
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
        m.setFrameRate(1200);
        m.createWindow();
    }
}
