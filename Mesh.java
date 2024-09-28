import java.util.ArrayList;
import java.io.FileInputStream;
import java.awt.Color;

public class Mesh {
    public ArrayList<Face> faces = new ArrayList<Face>();
    public ArrayList<Vector3> verticies = new ArrayList<Vector3>();
    public Vector3 origin = new Vector3();
    public Vector3 rotation = new Vector3();
    public String name;

    public static ArrayList<Mesh> importMesh(String mesh, Vector3 origin, Vector3 rotation, Color color, Viewport viewport, int ID, double scalar) {
        try (FileInputStream stream = new FileInputStream(mesh)) {
            StringBuilder buffer = new StringBuilder();
            ArrayList<Mesh> objects = new ArrayList<Mesh>();
            ArrayList<Vector3> currVerticies = new ArrayList<Vector3>();
            ArrayList<Face> currFaces = new ArrayList<Face>();
            int byteRead;
            while ((byteRead = stream.read()) != -1) {
                char c = (char) byteRead;
                if (c != '\n')
                    buffer.append(c);
                else {
                    String line = buffer.toString().trim();
                    buffer.setLength(0);
                    if (line.startsWith("v "))
                        currVerticies.add(Vector3.pack(line.substring(2)).mul(scalar).rotate(rotation).add(origin));
                    else if (line.startsWith("f ")) {
                        String[] tokens = line.substring(2).split(" ");
                        Vector3[] faceVerticies = new Vector3[tokens.length];
                        for (int i = 0; i < tokens.length; i++) {
                            String[] vertexInfo = tokens[i].split("/");
                            int index = Integer.parseInt(vertexInfo[0]) - 1;
                            faceVerticies[i] = currVerticies.get(index);
                        }
                        currFaces.add(new Face(faceVerticies, color, viewport, ID));
                    } else if (line.startsWith("o ")) {
                        Mesh object = new Mesh(new Face[0], line.substring(2));
                        object.faces = currFaces;
                        object.verticies = currVerticies;
                        objects.add(object);
                    }
                }
            }
            return objects;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    public Mesh(Face[] faces, String name) {
        this.name = name;
        for (Face face : faces) {
            this.faces.add(face);
            for (Vector3 vertex : face.verticies3D)
                this.verticies.add(vertex);
        }
        origin = Vector3.getOrigin(this.verticies.toArray(new Vector3[0]));
    }

    public Mesh(ArrayList<Face> faces, String name) { this(faces.toArray(new Face[0]), name); }

    public void moveTo(Vector3 position) {
        for (Vector3 vertex : verticies)
            vertex.set(vertex.add(position.sub(origin)));
        for (Face face : faces)
            for (Vector3 vertex : face.verticies3D)
                vertex.set(vertex.add(position.sub(origin)));
    }
}