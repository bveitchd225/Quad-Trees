import java.awt.*;

public class Ball {

    public static final double max_random_velocity = 60;
    // private double x;
    // private double y;
    private Vector2 position;
    private int r;
    private Color color;

    // private double x_vel;
    // private double y_vel;
    private Vector2 velocity;
    

    public Ball(int x, int y, int r, int x_vel, int y_vel) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(x_vel, y_vel);
        this.r = r;
        this.color = new Color((int) (Math.random() * 256) ,(int) (Math.random() * 256) ,(int) (Math.random() * 256) );
    }

    public Ball(int x, int y, int r) {
        this(x, y, r, (int) (-max_random_velocity + Math.random()*(max_random_velocity - -max_random_velocity + 1)), (int) (-max_random_velocity + Math.random()*(max_random_velocity - -max_random_velocity + 1)));
    }

    public Ball() {
        // Really ugly as call to another constructor must be first line :(
        this((int) (20 + Math.random()*(Main.SCREEN_WIDTH - 20 - 20 + 1)), (int) (20 + Math.random()*(Main.SCREEN_HEIGHT - 20 - 20 + 1)), (int) (5 + Math.random()*(10 - 10 + 1)));
    }

    public double getSpeed() {
        return this.velocity.getMagnitude();
    }

    public double getMass() {
        return this.r * this.r * 3.14;
    }

    public void collidesWith(Ball other) {
        double d = Math.sqrt(Math.pow(this.position.getX() - other.position.getX(),2) + Math.pow(this.position.getY() - other.position.getY(),2));
        if (d == 0) {
            this.velocity = new Vector2((int) (-max_random_velocity + Math.random()*(max_random_velocity - -max_random_velocity + 1)), (int) (-max_random_velocity + Math.random()*(max_random_velocity - -max_random_velocity + 1)));
        }
        if (d < this.r + other.r) {
            // System.out.print(this.velocity);
            double thism = this.r*this.r*3.14;
            double otherm = other.r*other.r*3.14;            
            
            Vector2 impact = Vector2.subtract(other.position, this.position);
            Vector2 vDiff  = Vector2.subtract(other.velocity, this.velocity);

            double overlap = d - (this.r + other.r);
            Vector2 dir = impact.copy();
            dir.setMagnitude(overlap * 0.5);
            this.position.add(dir);
            other.position.subtract(dir);
            
            d = this.r + other.r;
            impact.setMagnitude(d);

            // Particle A
            double numA = 2 * otherm * vDiff.dot(impact);
            double den = (thism + otherm) * d * d;
            Vector2 deltaV = impact.copy();
            deltaV.multiply(numA / den);
            this.velocity.add(deltaV);

            

            // Particle B
            vDiff.multiply(-1);
            impact.multiply(-1);
            double numB = 2 * thism * vDiff.dot(impact);
            Vector2 deltaVB = impact.copy();
            // deltaVB.multiply(-1.0);
            deltaVB.multiply(numB / den);
            other.velocity.add(deltaVB);
            // System.out.println(" -> " + this.velocity);
            // other.position.add(Vector2.multiply(impact,-0.25));
            
        }
    }

    public void update(double dt) {
        // System.out.println(dt);
        // this.y_vel += 60*9.81*dt;
        // this.velocity.setY(this.velocity.getY() + 60*9.81*dt);

        this.position.setX(this.position.getX() + this.velocity.getX() * dt);
        this.position.setY(this.position.getY() + this.velocity.getY() * dt);
        // this.x += this.x_vel*dt;
        // this.y += this.y_vel*dt;

        // Collide with bottom of screen
        if (this.position.getY() + this.r > Main.SCREEN_HEIGHT) {
            this.position.setY(Main.SCREEN_HEIGHT - this.r);
            this.velocity.setY(this.velocity.getY() * -1);
            this.velocity.setX(this.velocity.getX() * 0.9);
        }

        // Collide with top of screen
        if (this.position.getY() - this.r < 0) {
            this.position.setY(this.r);
            this.velocity.setY(this.velocity.getY() * -1);
        }

        // Collide with left of screen
        if (this.position.getX() - this.r < 0) {
            this.position.setX(this.r);
            this.velocity.setX(this.velocity.getX() * -1);
        }

        // Collide with right of screen
        if (this.position.getX() + this.r > Main.SCREEN_WIDTH) {
            this.position.setX(Main.SCREEN_WIDTH - this.r);
            this.velocity.setX(this.velocity.getX() * -1);
        }

        // To avoid really small meaningless calculations
        if (-0.1 < this.velocity.getY() && this.velocity.getY() < 0.1) {
            this.velocity.setY(0);
        }
        if (-0.1 < this.velocity.getX() && this.velocity.getX() < 0.1) {
            this.velocity.setX(0);
        }
    }

    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillOval((int) (this.position.getX() - this.r), (int) (this.position.getY() - this.r), this.r*2, this.r*2);
    }
}
