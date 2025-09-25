import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class QuadTree {

    private Rectangle boundary;
    private int maxSize;
    private ArrayList<Particle> particles;
    private boolean isDivided;

    private QuadTree topleft;
    private QuadTree topright;
    private QuadTree bottomleft;
    private QuadTree bottomright;

    public QuadTree(Rectangle boundary, int n) {
        this.boundary = boundary;
        this.maxSize = n;
        this.particles = new ArrayList<>();
        this.isDivided = false;
    }

    public void add(Particle particle) {
        if (!this.boundary.contains(particle.getX(), particle.getY())) {
            return;
        }

        if (!this.isDivided && this.particles.size() < this.maxSize) {
            this.particles.add(particle);
        }
        else {
            if (!this.isDivided) {
                this.subdivide();
                for (int i = this.particles.size()-1; i >= 0; i--) {
                    Particle b = this.particles.get(i);
                    topleft.add(b);
                    topright.add(b);
                    bottomleft.add(b);
                    bottomright.add(b);
                    particles.remove(i);
                }
                
            }
            topleft.add(particle);
            topright.add(particle);
            bottomleft.add(particle);
            bottomright.add(particle);

        }
    }

    public ArrayList<Particle> query(Rectangle range) {
        ArrayList<Particle> found = new ArrayList<>();
        if (!this.boundary.intersects(range)) {
            return found;
        }
        
        if (!this.isDivided) {
            found.addAll(this.particles);
        }
        else {
            found.addAll(topleft.query(range));
            found.addAll(topright.query(range));
            found.addAll(bottomleft.query(range));
            found.addAll(bottomright.query(range));
        }
        return found;
    }

    public void subdivide() {
        
        Rectangle topLeftRect     = new Rectangle(this.boundary.x, this.boundary.y, (int) this.boundary.getWidth()/2, (int) this.boundary.getHeight()/2);
        topleft  = new QuadTree(topLeftRect, maxSize);
        Rectangle topRightRect    = new Rectangle(this.boundary.x + (int)this.boundary.getWidth()/2, this.boundary.y, (int) this.boundary.getWidth()/2, (int) this.boundary.getHeight()/2);
        topright = new QuadTree(topRightRect, maxSize);
        Rectangle bottomLeftRect  = new Rectangle(this.boundary.x, this.boundary.y + (int) this.boundary.getHeight()/2, (int) this.boundary.getWidth()/2, (int) this.boundary.getHeight()/2);
        bottomleft  = new QuadTree(bottomLeftRect, maxSize);
        Rectangle bottomRightRect = new Rectangle(this.boundary.x + (int)this.boundary.getWidth()/2, this.boundary.y + (int) this.boundary.getHeight()/2, (int) this.boundary.getWidth()/2, (int) this.boundary.getHeight()/2);
        bottomright = new QuadTree(bottomRightRect, maxSize);
        this.isDivided = true;
    }

    public void update(double dt) {
        for (Particle b: this.particles) {
            b.update(dt);
        }
        if (this.isDivided) {
            topleft.update(dt);
            topright.update(dt);
            bottomleft.update(dt);
            bottomright.update(dt);
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(this.boundary.x, this.boundary.y, (int) this.boundary.getWidth(), (int) this.boundary.getHeight());
        
        g.setColor(Color.BLACK);
        if (this.isDivided) {
            topleft.draw(g);
            topright.draw(g);
            bottomleft.draw(g);
            bottomright.draw(g);
        }
        else {
            g.drawString(""+particles.size(), this.boundary.x+3, this.boundary.y+13);
        }
    }
}
