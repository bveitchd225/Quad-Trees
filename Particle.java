import java.awt.*;

public class Particle {

    public static final double max_random_velocity = 60;

    private Vector2 position;
    private Vector2 velocity;
    private int radius;
    private Color color;

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
        this(x, y, (int) (5 + Math.random() * (10 - 10 + 1)));
    }

    public Particle() {
        // Really ugly as call to another constructor must be first line :(
        this((int) (20 + Math.random() * (Main.SCREEN_WIDTH - 20 - 20 + 1)),
                (int) (20 + Math.random() * (Main.SCREEN_HEIGHT - 20 - 20 + 1)),
                (int) (5 + Math.random() * (10 - 5 + 1)));
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
        double distance = Math.sqrt(Math.pow(position.getX() - other.position.getX(), 2)
                + Math.pow(position.getY() - other.position.getY(), 2));
        if (distance == 0) {
            velocity = new Vector2(
                    (int) (-max_random_velocity + Math.random() * (max_random_velocity - -max_random_velocity + 1)),
                    (int) (-max_random_velocity + Math.random() * (max_random_velocity - -max_random_velocity + 1)));
        }
        if (distance < radius + other.radius) {
            double thism = radius * radius * 3.14;
            double otherm = other.radius * other.radius * 3.14;

            Vector2 impact = Vector2.subtract(other.position, position);
            Vector2 vDiff = Vector2.subtract(other.velocity, velocity);

            double overlap = distance - (radius + other.radius);
            Vector2 dir = impact.copy();
            dir.setMagnitude(overlap * 0.5);
            position.add(dir);
            other.position.subtract(dir);

            distance = radius + other.radius;
            impact.setMagnitude(distance);

            // Particle A
            double numA = 2 * otherm * vDiff.dot(impact);
            double den = (thism + otherm) * distance * distance;
            Vector2 deltaV = impact.copy();
            deltaV.multiply(numA / den);
            velocity.add(deltaV);

            // Particle B
            vDiff.multiply(-1);
            impact.multiply(-1);
            double numB = 2 * thism * vDiff.dot(impact);
            Vector2 deltaVB = impact.copy();
            deltaVB.multiply(numB / den);
            other.velocity.add(deltaVB);
        }
    }

    public void update(double dt) {

        velocity.setY(velocity.getY() + 60 * 9.81 * dt);

        position.setX(position.getX() + velocity.getX() * dt);
        position.setY(position.getY() + velocity.getY() * dt);

        // Collide with bottom of screen
        if (position.getY() + radius > Main.SCREEN_HEIGHT) {
            position.setY(Main.SCREEN_HEIGHT - radius);
            velocity.setY(velocity.getY() * -0.8);
            velocity.setX(velocity.getX() * 0.9);
        }

        // Collide with top of screen
        if (position.getY() - radius < 0) {
            position.setY(radius);
            velocity.setY(velocity.getY() * -0.8);
        }

        // Collide with left of screen
        if (position.getX() - radius < 0) {
            position.setX(radius);
            velocity.setX(velocity.getX() * -0.8);
        }

        // Collide with right of screen
        if (position.getX() + radius > Main.SCREEN_WIDTH) {
            position.setX(Main.SCREEN_WIDTH - radius);
            velocity.setX(velocity.getX() * -0.8);
        }

        // To avoid really small meaningless calculations
        if (-0.1 < velocity.getY() && velocity.getY() < 0.1) {
            velocity.setY(0);
        }
        if (-0.1 < velocity.getX() && velocity.getX() < 0.1) {
            velocity.setX(0);
        }
    }

    public void draw(Graphics g) {
        // g.setColor(color);
        g.setColor(new Color((int) Math.min(velocity.getMagnitude() * 2, 255), 0, 0));
        // g.fillRect((int) (position.getX() - r), (int) (position.getY() - r), r*2,
        // r*2);
        g.fillOval((int) (position.getX() - radius), (int) (position.getY() - radius), radius * 2, radius * 2);
    }
}
