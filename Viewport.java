public class Viewport {
    public Vector3 position = new Vector3();
    public Vector3 rotation = new Vector3();
    public Vector2 resolution = new Vector2(640, 480);
    public int scalar = 1;

    public Viewport() {}
    public Viewport(Vector3 position) { this.position = position; }
    public Viewport(Vector3 position, Vector3 rotation) { this.position = position; this.rotation = rotation; }
    public Viewport(Vector3 position, Vector3 rotation, Vector2 resolution) { this.position = position; this.rotation = rotation; this.resolution = resolution; }
    public Viewport(Vector3 position, Vector3 rotation, Vector2 resolution, int scalar) {
        this.position = position;
        this.rotation = rotation;
        this.resolution = resolution;
        this.scalar = scalar;
    }

    public void move(Vector3 amount) { position = position.add(amount); }
    public void rotate(Vector3 amount) { rotation = rotation.add(amount); }

    public Viewport clone() { return new Viewport(position, rotation, resolution, scalar); }

    public Vector2 project(Vector3 point) { return point.sub(position).rotate(rotation).project(); }
}