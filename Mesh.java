import java.util.ArrayList;
import java.io.FileInputStream;
import java.awt.Color;

public class Mesh {
    public ArrayList<Face> faces = new ArrayList<Face>();
    public ArrayList<Vector3> verticies = new ArrayList<Vector3>();

    public Mesh(String mesh, Vector3 origin, Vector3 rotation, Color color, Viewport viewport, int ID, double scalar) {
        try (FileInputStream stream = new FileInputStream(mesh)) {
            StringBuilder buffer = new StringBuilder();
            int byteRead;
            while ((byteRead = stream.read()) != -1) {
                char c = (char) byteRead;
                if (c != '\n')
                    buffer.append(c);
                else {
                    String line = buffer.toString().trim();
                    buffer.setLength(0);
                    if (line.startsWith("v ")) {
                        verticies.add(Vector3.pack(line.substring(2)).mul(scalar).rotate(rotation).add(origin));
                    } else if (line.startsWith("f ")) {
                        String[] tokens = line.substring(2).split(" ");
                        Vector3[] faceVerticies = new Vector3[tokens.length];
                        for (int i = 0; i < tokens.length; i++) {
                            String[] vertexInfo = tokens[i].split("/");
                            int index = Integer.parseInt(vertexInfo[0]) - 1;
                            faceVerticies[i] = verticies.get(index);
                        }
                        faces.add(new Face(faceVerticies, color, viewport, ID));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
    
    public Mesh(String mesh, Vector3 origin, Vector3 rotation, Color color, Viewport viewport, int ID) {
        this(mesh, origin, rotation, color, viewport, ID, 1);
    }
}