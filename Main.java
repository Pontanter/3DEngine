import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class Main extends JFrame implements KeyListener {
    private JPanel panel;

    private double frame;
    private int frame_c;
    private int FPS;
    private double delta;

    private Vector2 res = new Vector2(1920, 1080);

    private int scalar = 5;

    private boolean debug = false;
    private boolean wireframe = false;
    private boolean ghost = false;
    private int s = 5;

    private int jumps;

    private boolean W,A,S,D,RIGHT,LEFT,/*UP,DOWN,*/SHIFT;
    private boolean Crouch = false;

    private Vector2 tileFloor = new Vector2(50, 50);
    private Face[] faces = new Face[(int)(tileFloor.X*tileFloor.Y)+8];

    private double tileSize = .1;
    
    private Vector3 velocity = new Vector3();
    private Viewport viewport = new Viewport(new Vector3(tileSize*tileFloor.X*.5, 1, tileSize*tileFloor.Y*.5), new Vector3(), res, scalar);

    private double leanZ;
    private double H = .25;
    private double height = H;

    Main() {
        for (int y = 0; y < (int) tileFloor.Y; y++)
            for (int x = 0; x < (int) tileFloor.X; x++)
                faces[x + y * (int) tileFloor.X] = new Face(
                    new Vector3[] {
                        new Vector3(x*tileSize, 0, y*tileSize),
                        new Vector3(x*tileSize, 0, y*tileSize-tileSize),
                        new Vector3(x*tileSize-tileSize, 0, y*tileSize-tileSize),
                        new Vector3(x*tileSize-tileSize, 0, y*tileSize)
                    }, 
                    new Color(255, 255, 255),
                    viewport
                );
        Vector3 cubeOrigin = new Vector3(tileSize*tileFloor.X*.5, .25, 0);
        faces[faces.length-8] = new Face(
            new Vector3[] {
                new Vector3(-.5, .5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, .5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, -.5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(-.5, -.5, .5).mul(tileSize).add(cubeOrigin)
            },
            Color.BLUE,
            viewport
        );
        faces[faces.length-7] = new Face(
            new Vector3[] {
                new Vector3(-.5, .5, -.5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, .5, -.5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, -.5, -.5).mul(tileSize).add(cubeOrigin),
                new Vector3(-.5, -.5, -.5).mul(tileSize).add(cubeOrigin)
            },
            Color.BLUE,
            viewport
        );
        faces[faces.length-6] = new Face(
            new Vector3[] {
                new Vector3(-.5, .5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(-.5, -.5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(-.5, -.5, -.5).mul(tileSize).add(cubeOrigin),
                new Vector3(-.5, .5, -.5).mul(tileSize).add(cubeOrigin)
            },
            Color.RED,
            viewport
        );
        faces[faces.length-5] = new Face(
            new Vector3[] {
                new Vector3(.5, .5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, -.5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, -.5, -.5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, .5, -.5).mul(tileSize).add(cubeOrigin)
            },
            Color.RED,
            viewport
        );
        faces[faces.length-4] = new Face(
            new Vector3[] {
                new Vector3(-.5, .5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, .5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, .5, -.5).mul(tileSize).add(cubeOrigin),
                new Vector3(-.5, .5, -.5).mul(tileSize).add(cubeOrigin)
            },
            Color.GREEN,
            viewport
        );
        faces[faces.length-3] = new Face(
            new Vector3[] {
                new Vector3(-.5, -.5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, -.5, .5).mul(tileSize).add(cubeOrigin),
                new Vector3(.5, -.5, -.5).mul(tileSize).add(cubeOrigin),
                new Vector3(-.5, -.5, -.5).mul(tileSize).add(cubeOrigin)
            },
            Color.GREEN,
            viewport
        );
        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                g2D.setColor(Color.BLACK);
                g2D.fillRect(0, 0, getWidth(), getHeight());
                // g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Face[] sorted = new Face[faces.length];
                for (Face face : faces) {
                    if (face == null) continue;
                    if (!face.displayable) continue;
                    double distanceToCamera = Math.sqrt(
                        Math.pow(face.origin.X - viewport.position.X, 2) + 
                        Math.pow(face.origin.Y - viewport.position.Y, 2) + 
                        Math.pow((face.origin.Z + Face.warpCorrection) - viewport.position.Z, 2)
                    );
                    int i = 0;
                    while (i < sorted.length && sorted[i] != null) {
                        double sortedDistanceToCamera = Math.sqrt(
                            Math.pow(sorted[i].origin.X - viewport.position.X, 2) +
                            Math.pow(sorted[i].origin.Y - viewport.position.Y, 2) +
                            Math.pow((sorted[i].origin.Z + Face.warpCorrection) - viewport.position.Z, 2)
                        );
                        if (distanceToCamera > sortedDistanceToCamera) {
                            break;
                        }
                        i++;
                    }
                    for (int j = sorted.length - 1; j > i; j--)
                        sorted[j] = sorted[j - 1];
                    sorted[i] = face;
                }
                for (Face face : sorted)
                    if (face != null) {
                        g2D.setColor(face.color);
                        if (ghost)
                            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
                        if (wireframe) {
                            g2D.setStroke(new BasicStroke(2));
                            g2D.drawPolygon(face.face);
                        } else
                            g2D.fillPolygon(face.face);
                        if (ghost)
                            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                    }
                if (debug) {
                    g2D.setColor(Color.WHITE);
                    for (Face face : sorted)
                        if (face != null) {
                            Vector2 origin = face.projectedOrigin;
                            g2D.fillOval((int)origin.X-s/2, (int)origin.Y-s/2, s, s);
                            for (Vector2 vertex : face.verticies)
                                g2D.fillOval((int)vertex.X-s/2, (int)vertex.Y-s/2, s, s);
                        }
                    g2D.setFont(new Font("Consolas", Font.PLAIN, 20));
                    g2D.drawString("FPS: "+FPS, 10, 30);
                    g2D.drawString("Delta: "+delta, 10, 60);
                    g2D.drawString("Wireframe mode "+(wireframe?"on":"off"), 10, 90);
                    g2D.drawString("Ghost mode "+(ghost?"on":"off"), 10, 120);
                    g2D.drawString("Viewport position: "+viewport.position, 10, 150);
                    g2D.drawString("Viewport rotation: "+viewport.rotation, 10, 180);
                    g2D.drawString("Viewport velocity: "+velocity, 10, 210);
                    g2D.drawString("Height: "+height, 10, 240);
                }
                g2D.dispose();
                g.dispose();
            }
        };

        add(panel);
        setUndecorated(true);
        pack();
        setTitle("3D Engine");
        setSize(res.toDimension());
        setResizable(false);
        addKeyListener(this);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);

        new Timer(1, e -> step()).start();
        new Timer(1000, e -> { FPS = frame_c; delta = 1.0/FPS; frame_c = 0; setTitle("3D Engine - "+FPS+" FPS"); }).start();
    }

    private void step() {
        double timeScale = delta/(1.0/60.0);
        frame += timeScale;
        viewport.rotate(new Vector3(
            0,
            (LEFT?.45:0)-(RIGHT?.45:0),
            0
        ).mul(.15).mul(timeScale));
        leanZ = leanZ + (velocity.X/2 - leanZ) *.1;
        double intensity = (height-H)*-.5;
        viewport.rotation = new Vector3(
            0,
            viewport.rotation.Y,
            leanZ + Math.sin(frame/20.0)*(intensity>.2?.2:intensity)
        );
        velocity = velocity == null? new Vector3() : velocity.add(new Vector3(
            (A?.1:0)-(D?.1:0),
            0,
            (S?.1:0)-(W?.1:0)
        ).mul(Crouch?.25:SHIFT?1:.5).mul(timeScale));
        velocity = new Vector3(
            velocity.X * .75,
            velocity.Y + .001,
            velocity.Z * .75
        );
        viewport.move(velocity.mul(.1).rotate(-viewport.rotation.Y, 1));
        H = H + ((Crouch?.15:.25) - H) *.1;
        height -= velocity.Y;
        if (height < H) {
            height = H;
            velocity.Y *= -.1;
            jumps = 2;
        }
        double off = Math.sin(frame/(5.0-(velocity.Z/100)))*clamp(velocity.Z/15, -.01, .01);
        viewport.position.Y = height+off+Math.sin(frame/60.0)*.005*(Crouch?.75:1);
        for (int i = 0; i < faces.length; i++)
            if (faces[i] != null)
                faces[i].update(viewport);
        panel.repaint();
        frame_c++;
    }

    private double clamp(double val, double min, double max) {
        return val > max? max : val < min? min : val;
    }

    public static void main(String[] args) {
        new Main();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        switch(k) {
            case KeyEvent.VK_ESCAPE: System.exit(0); break;
            case KeyEvent.VK_F1: debug = !debug; break;
            case KeyEvent.VK_Z: wireframe = !wireframe; break;
            case KeyEvent.VK_X: ghost = !ghost; break;
            case KeyEvent.VK_R: viewport = new Viewport(new Vector3(), new Vector3(), res, scalar); break;

            case KeyEvent.VK_W: W = true; break;
            case KeyEvent.VK_A: A = true; break;
            case KeyEvent.VK_S: S = true; break;
            case KeyEvent.VK_D: D = true; break;
            case KeyEvent.VK_RIGHT: RIGHT = true; break;
            case KeyEvent.VK_LEFT: LEFT = true; break;
            case KeyEvent.VK_SHIFT: SHIFT = true; break;
            // case KeyEvent.VK_UP: UP = true; break;
            // case KeyEvent.VK_DOWN: DOWN = true; break;
            
            case KeyEvent.VK_CONTROL: if (jumps < 2) break; Crouch = !Crouch; break;
            case KeyEvent.VK_Q: velocity.Z += W&&S?0:S?1:W?-1:0; break;
            case KeyEvent.VK_SPACE: if (jumps < 1) break; Crouch = false; velocity.Y = velocity.Y < 0? -.025 : velocity.Y - .025; jumps--; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        switch(k) {
            case KeyEvent.VK_W: W = false; break;
            case KeyEvent.VK_A: A = false; break;
            case KeyEvent.VK_S: S = false; break;
            case KeyEvent.VK_D: D = false; break;
            case KeyEvent.VK_RIGHT: RIGHT = false; break;
            case KeyEvent.VK_LEFT: LEFT = false; break;
            case KeyEvent.VK_SHIFT: SHIFT = false; break;
            // case KeyEvent.VK_UP: UP = false; break;
            // case KeyEvent.VK_DOWN: DOWN = false; break;
        }
    }
}