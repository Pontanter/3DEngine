/*
    so APPARENTLY it's spelt "Vertices" and not "Verticies".
    Yeah... womp womp, I'm not even gonna attempt to correct all the instances of "Verticies" in my code.
*/

import java.awt.Color;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

public class Face {
    public Polygon face;
    public Color color, matColor;
    public Vector3 origin;
    public Vector2 projectedOrigin;

    public Vector2[] verticies;
    public Vector3[] verticies3D;

    public boolean isImage = false;
    public Image image;
    public Vector2[] imageVerticies;
    public AffineTransform transform;

    public boolean displayable = true; /* rather than destroying the face, just hide it temporarily if obstructed. */

    public static final int warpCorrection = 2; /* I have no clue what this does, or why I added it, but if I remove it everything breaks */

    public Face(Vector3[] verticies, Color color, Viewport viewport) {
        this.color = color;
        this.verticies = new Vector2[verticies.length];
        matColor = color;
        verticies3D = verticies;
        update(viewport);
    }

    public void update(Viewport viewport) {
        if (isImage && image == null) {
            displayable = false;
            return;
        }
        displayable = true;
        face = new Polygon();
        for (Vector3 vertex : verticies3D) {
            Vector2 translated = viewport.project(vertex.add(new Vector3(0, 0, warpCorrection))).mul(2*viewport.scalar).add(viewport.resolution.mul(.5));
            Vector2 lineDir = new Vector2(Math.sin(viewport.rotation.Y), Math.cos(viewport.rotation.Y));
            Vector2 toLine = viewport.position.sub(new Vector3(0, 0, warpCorrection)).sub(vertex).truncate();
            /* vertex.add(new Vector3(0, 0, warpCorrection)).sub(viewport.position).magnitude() < 1.3 - comment of shame for the single worst implementation of literally anything that I have ever done in my entire life */
            if ((lineDir.X * toLine.X) - (lineDir.Y * toLine.Y) > 0)
                displayable = false;
            face.addPoint((int)translated.X, (int)translated.Y);
            this.verticies[face.npoints - 1] = translated;
        }
        origin = Vector3.getOrigin(verticies3D);
        double dist = origin.sub(viewport.position.sub(new Vector3(0, 0, warpCorrection))).magnitude()/2.5;
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
        projectedOrigin = viewport.project(origin.add(new Vector3(0, 0, warpCorrection))).mul(2*viewport.scalar).add(viewport.resolution.mul(.5));
    }
}