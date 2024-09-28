import java.awt.Dimension;
import java.awt.Point;

public class Vector2 {
    public double X, Y;

    public Vector2() { this(0, 0); }
    public Vector2(double X, double Y) { this.X = X; this.Y = Y; }

    public Vector2 add(Vector2 other) { return new Vector2(X + other.X, Y + other.Y); }
    public Vector2 sub(Vector2 other) { return new Vector2(X - other.X, Y - other.Y); }
    public Vector2 mul(double scalar) { return new Vector2(X * scalar, Y * scalar); }
    public Vector2 div(double scalar) { return new Vector2(X / scalar, Y / scalar); }

    public double magnitude() { return Math.sqrt(X * X + Y * Y); }
    public double lookAt(Vector2 point) { return Math.atan2(point.sub(this).Y, point.sub(this).X ); }
    public double dot(Vector2 other) { return X * other.X + Y * other.Y; }
    public Vector3 extend() { return new Vector3(X, 0, Y); }
    public Vector2 unit() { return div(magnitude()); }
    public Vector2 clone() { return new Vector2(X, Y); }
    public Vector2 move(Vector2 amt, double angle) { return add(amt.rotate(angle)); }
    public Vector2 rotate(double theta) {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Vector2(X * cos - Y * sin, X * sin + Y * cos);
    }
    
    public Dimension toDimension() { return new Dimension((int)Math.round(X), (int)Math.round(Y)); }
    public Point toPoint() { return new Point((int)Math.round(X), (int)Math.round(Y)); }

    @Override
    public String toString() { return String.format("Vector2(%.2f, %.2f)", X, Y); }

    public static double distance(Vector2 p1, Vector2 p2) { return p1.sub(p2).magnitude(); }
    public static double lookAt(Vector2 p1, Vector2 p2) { return p1.lookAt(p2); }

    public static Vector2 getOrigin(Vector2[] verticies) {
        Vector2 origin = new Vector2();
        for (Vector2 v : verticies) origin = origin.add(v);
        return origin.div(verticies.length);
    }
}