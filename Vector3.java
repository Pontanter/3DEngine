public class Vector3 {
    public double X, Y, Z;

    public Vector3() { this(0, 0, 0); }
    public Vector3(double X, double Y, double Z) { this.X = X; this.Y = Y; this.Z = Z; }

    public Vector3 add(Vector3 other) { return new Vector3(X + other.X, Y + other.Y, Z + other.Z); }
    public Vector3 sub(Vector3 other) { return new Vector3(X - other.X, Y - other.Y, Z - other.Z); }
    public Vector3 mul(double scalar) { return new Vector3(X * scalar, Y * scalar, Z * scalar); }
    public Vector3 div(double scalar) { return scalar == 0? mul(0) : new Vector3(X / scalar, Y / scalar, Z / scalar); }

    public double product() { return X*Y*Z; }
    public double magnitude() { return Math.sqrt(X * X + Y * Y + Z * Z); }
    public Vector2 project() { return Z == 0? new Vector2(X, Y) : new Vector2(X, Y).div(Z/50); }
    public Vector2 truncate() { return new Vector2(X, Z); }
    public Vector3 unit() { return div(magnitude()); }
    public Vector3 move(Vector3 amount, Vector3 theta) { return this.rotate(theta.X, 0).rotate(theta.Y, 1).rotate(theta.Z, 2).add(amount); }
    public Vector3 rotate(Vector3 amount) { return this.rotate(amount.X, 0).rotate(amount.Y, 1).rotate(amount.Z, 2); }
    public Vector3 rotate(double angle, int axis) { /* 0 - X, 1 - Y, 2 - Z */
        double sin = Math.sin(angle), cos = Math.cos(angle);
        switch (axis) {
            case 0: return new Vector3(X, Y * cos - Z * sin, Y * sin + Z * cos);
            case 1: return new Vector3(X * cos + Z * sin, Y, -X * sin + Z * cos);
            case 2: return new Vector3(X * cos - Y * sin, X * sin + Y * cos, Z);
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    @Override
    public String toString() { return String.format("Vector3(%.2f, %.2f, %.2f)", X, Y, Z); }

    public static Vector3 getOrigin(Vector3[] verticies) {
        Vector3 origin = new Vector3();
        for (Vector3 v : verticies) origin = origin.add(v);
        return origin.div(verticies.length);
    }
}