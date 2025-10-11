import java.awt.*;

public class Particle {

    public static final double max_random_velocity = 120;

    public static final double energyReserved = 0.99;

    public static final int min_radius = 3;
    public static final int max_radius = 40;

    public static final int min_wakeup_vel = 10;

    private Vector2 position;
    private Vector2 velocity;
    private int radius;
    private Color color;
    private boolean asleep;

    public Particle(int x, int y, int r, int x_vel, int y_vel) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(x_vel, y_vel);
        this.radius = r;
        this.color = new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }

    public Particle(int x, int y, int r) {
        this(x, y, r, (int) (-max_random_velocity + Math.random() * (max_random_velocity - -max_random_velocity + 1)),
                (int) (-max_random_velocity + Math.random() * (max_random_velocity - -max_random_velocity + 1)));
    }

    public Particle(int x, int y) {
        // Really ugly as call to another constructor must be first line :(
        this(x, y, (int) (min_radius + Math.random() * (max_radius - min_radius + 1)));
    }

    public Particle() {
        // Really ugly as call to another constructor must be first line :(
        this((int) (20 + Math.random() * (MainTiming.SCREEN_WIDTH - 20 - 20 + 1)),
                (int) (20 + Math.random() * (MainTiming.SCREEN_HEIGHT - 20 - 20 + 1)),
                (int) (min_radius + Math.random() * (max_radius - min_radius + 1)));
    }

    public Particle(int r) {
        // Really ugly as call to another constructor must be first line :(
        this((int) (20 + Math.random() * (MainTiming.SCREEN_WIDTH - 20 - 20 + 1)),
                (int) (20 + Math.random() * (MainTiming.SCREEN_HEIGHT - 20 - 20 + 1)),
                r);
    }

    private void sleep() {
        asleep = true;
        this.velocity.setX(0);
        this.velocity.setY(0);
    }

    private void wakeup() {
        asleep = false;
    }

    public boolean isAsleep() {
        return asleep;
    }

    public double getSpeed() {
        return velocity.getMagnitude();
    }

    public double getMass() {
        return radius * radius * 3.14;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getRadius() {
        return radius;
    }

    public void collidesWith(Particle other) {
        if (other.isAsleep() && this.getSpeed() > 10) {
            other.wakeup();
        }
        if (this.isAsleep() && other.getSpeed() > 10) {
            this.wakeup();
        }

        double distance = Math.sqrt(Math.pow(position.getX() - other.position.getX(), 2)
                + Math.pow(position.getY() - other.position.getY(), 2));
        if (distance == 0) {
            this.position.setX(this.position.getX() + 0.01);

        }
        if (distance < radius + other.radius) {
            double thism = radius * radius * 3.14;
            double otherm = other.radius * other.radius * 3.14;

            Vector2 impact = Vector2.subtract(other.position, position);
            Vector2 vDiff = Vector2.subtract(other.velocity, velocity);

            double overlap = (radius + other.radius) - distance;
            Vector2 dir = impact.copy();
            dir.setMagnitude(overlap * 0.52);
            position.subtract(dir);
            other.position.add(dir);

            distance = radius + other.radius;
            impact.setMagnitude(distance);

            // Particle A
            double numA = 2 * otherm * vDiff.dot(impact);
            double den = (thism + otherm) * distance * distance;
            Vector2 deltaV = impact.copy();
            deltaV.multiply(energyReserved * numA / den);
            velocity.add(deltaV);

            // Particle B
            vDiff.multiply(-1);
            impact.multiply(-1);
            double numB = 2 * thism * vDiff.dot(impact);
            Vector2 deltaVB = impact.copy();
            deltaVB.multiply(energyReserved * numB / den);
            other.velocity.add(deltaVB);
        }
    }

    public void update(double dt) {

        // Collide with bottom of screen
        if (position.getY() + radius > MainTiming.SCREEN_HEIGHT) {
            position.setY(MainTiming.SCREEN_HEIGHT - radius);
            velocity.setY(velocity.getY() * -0.9);
            // velocity.setX(velocity.getX() * 0.9);
        }

        // Collide with top of screen
        if (position.getY() - radius < 0) {
            position.setY(radius);
            velocity.setY(velocity.getY() * -1);
        }

        // Collide with left of screen
        if (position.getX() - radius < 0) {
            position.setX(radius);
            velocity.setX(velocity.getX() * -1);
        }

        // Collide with right of screen
        if (position.getX() + radius > MainTiming.SCREEN_WIDTH) {
            position.setX(MainTiming.SCREEN_WIDTH - radius);
            velocity.setX(velocity.getX() * -1);
        }

        if (asleep) {
            return;
        }

        velocity.setY(velocity.getY() + 60 * 9.81 * dt);

        position.setX(position.getX() + velocity.getX() * dt);
        position.setY(position.getY() + velocity.getY() * dt);

        // Collide with bottom of screen
        if (position.getY() + radius > MainTiming.SCREEN_HEIGHT) {
            position.setY(MainTiming.SCREEN_HEIGHT - radius);
            velocity.setY(velocity.getY() * -0.9);
            velocity.setX(velocity.getX() * 0.9);
        }

        // Collide with top of screen
        if (position.getY() - radius < 0) {
            position.setY(radius);
            velocity.setY(velocity.getY() * -0.9);
        }

        // Collide with left of screen
        if (position.getX() - radius < 0) {
            position.setX(radius);
            velocity.setX(velocity.getX() * -0.9);
        }

        // Collide with right of screen
        if (position.getX() + radius > MainTiming.SCREEN_WIDTH) {
            position.setX(MainTiming.SCREEN_WIDTH - radius);
            velocity.setX(velocity.getX() * -0.9);
        }

        // To avoid really small meaningless calculations
        if (-0.1 < velocity.getY() && velocity.getY() < 0.1) {
            velocity.setY(0);
        }
        if (-0.1 < velocity.getX() && velocity.getX() < 0.1) {
            velocity.setX(0);
        }

        if (getSpeed() < 1) {
            sleep();
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);
        if (isAsleep()) {
            g.setColor(Color.BLACK);
        }
        // g.setColor(new Color((int) Math.min(velocity.getMagnitude() * 2, 255), 0, 0));
        // g.fillRect((int) (position.getX() - radius), (int) (position.getY() - radius), radius*2,radius*2);
        g.fillOval((int) (position.getX() - radius), (int) (position.getY() - radius), radius * 2, radius * 2);
        g.setColor(Color.BLACK);
        g.drawString((int) velocity.getX()+"," + (int) velocity.getY(), (int)position.getX(),(int) position.getY());
    }

    public String toString() {
        return "P(" + (int)this.position.getX() + "," + (int)this.position.getY() + ")";
    }
}
