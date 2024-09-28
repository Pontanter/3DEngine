/*
    so APPARENTLY it's spelt "Vertices" and not "Verticies".
    Yeah... womp womp, I'm not even gonna attempt to correct all the instances of "Verticies" in my code.
*/

import java.awt.Color;
import java.awt.Polygon;

public class Face {
    public Polygon face;
    public Color color, matColor;
    public Vector3 origin;
    public Vector2 projectedOrigin;
    public Mesh parentObject;

    public Vector2[] verticies;
    public Vector3[] verticies3D;

    public int ID;

    public boolean displayable = true; /* rather than destroying the face, just hide it temporarily if obstructed. */
    public boolean noDisplayableOverride = false;

    public Face(Vector3[] verticies, Color color, Viewport viewport, int ID) {
        this.color = color;
        this.verticies = new Vector2[verticies.length];
        this.ID = ID;
        matColor = color;
        verticies3D = verticies;
        update(viewport);
    }

    public Face(Vector3[] verticies, Color color, Viewport viewport, int ID, Mesh parentObject) {
        this(verticies, color, viewport, ID);
        this.parentObject = parentObject;
    }

    public void update(Viewport viewport) {
        if (noDisplayableOverride) {
            displayable = false;
            return;
        }
        displayable = true;
        face = new Polygon();
        for (Vector3 vertex : verticies3D) {
            Vector2 translated = viewport.project(vertex).mul(2*viewport.scalar).add(viewport.resolution.mul(.5));
            Vector2 lineDir = new Vector2(Math.sin(viewport.rotation.Y), Math.cos(viewport.rotation.Y));
            Vector2 toLine = viewport.position.sub(vertex).truncate();
            /* vertex.add(new Vector3(0, 0, warpCorrection)).sub(viewport.position).magnitude() < 1.3 - comment of shame for the single worst implementation of literally anything that I have ever done in my entire life */
            if ((lineDir.X * toLine.X) - (lineDir.Y * toLine.Y) > 0)
                displayable = false;
            face.addPoint((int)translated.X, (int)translated.Y);
            this.verticies[face.npoints - 1] = translated;
        }
        origin = Vector3.getOrigin(verticies3D);
        double dist = origin.sub(viewport.position).magnitude()/2.5;
        if (dist >= 1) {
            dist = 1;
            displayable = false;
        } else if (dist <= 0) dist = 0;
        int R,G,B;
        R = (int) (matColor.getRed() - 255 * dist);
        G = (int) (matColor.getGreen() - 255 * dist);
        B = (int) (matColor.getBlue() - 255 * dist);
        R = Math.max(R, 0);
        G = Math.max(G, 0);
        B = Math.max(B, 0);
        color = new Color(R,G,B);
        projectedOrigin = viewport.project(origin).mul(2*viewport.scalar).add(viewport.resolution.mul(.5));
    }
}