public class Vector2 {

    private double x;
    private double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }

    public static Vector2 subtract(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    public static Vector2 multiply(Vector2 a, double factor) {
        return new Vector2(a.x * factor, a.y * factor);
    }

    public void add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
    }

    public void subtract(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
    }

    public void multiply(double factor) {
        this.x *= factor;
        this.y *= factor;
    }

    public Vector2 copy() {
        return new Vector2(x, y);
    }

    public double dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getMagnitude() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public void setMagnitude(double newMagnitude) {
        double currentMagnitude = this.getMagnitude();
        if (currentMagnitude != 0) {
            this.x *= newMagnitude / currentMagnitude;
            this.y *= newMagnitude / currentMagnitude;
        }
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
