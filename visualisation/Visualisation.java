package visualisation;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.Timer;

import point.point;

import util.Matrix;
import util.VirtualSphere;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;

import configuration.ScaleConfiguration;

import dataReader.dataReader;

public class Visualisation implements GLEventListener, KeyListener,
        MouseListener, ActionListener {
    static final long serialVersionUID = 1l;
    private static List<point> pointsList = null;
    private static dataReader dr = null;
    private GLJPanel display;
    private Timer animationTimer;
    private GLU glu = new GLU();
    private static ScaleConfiguration sc = null;

    private Point prevMouse;

    private VirtualSphere vs = new VirtualSphere();
    private Point cueCenter = new Point();
    private int cueRadius;
    private boolean mouseDown = false;
    private float rot_matrix[] = Matrix.identity();

    private static double scaleFactor;
    private static double radius;

    private static String TITLE = "3D Visualisation With Jogl";
    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 480;
    private static final int FPS = 60;

    public static void main(String[] args) {
        initDataReader();
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLWindow window = GLWindow.create(caps);

        final FPSAnimator animator = new FPSAnimator(window, FPS, true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent arg0) {
                // Use a dedicate thread to run the stop() to ensure that the
                // animator stops before program exits.
                new Thread() {
                    @Override
                    public void run() {
                        animator.stop(); // stop the animator loop
                        System.exit(0);
                    }
                }.start();
            };
        });

        Visualisation v = new Visualisation();
        window.addGLEventListener(v);
        window.addMouseListener(v);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setTitle(TITLE);
        window.setVisible(true);
        animator.start();
    }

    public Visualisation() {
    }

    public static void initDataReader() {
        dr = new dataReader("output1.pcd");
        pointsList = dr.getPoints();
        sc = new ScaleConfiguration(pointsList, 10);
        scaleFactor = sc.getScaleFactor();
        radius = sc.getRadius();
    }

    // --------------- Methods of the GLEventListener interface -----------
    public void buildPoints(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL2.GL_POINT_SPRITE); // GL_POINT_SPRITE_ARB if you're
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glPointSize((float) radius / 2);

        gl.glBegin(GL.GL_POINTS);
        for (point p : pointsList) {
            gl.glPushMatrix();
            gl.glTranslatef(p.getX(), p.getY(), p.getZ());
            /* gl.glColor3f(0.95f, 0.207f, 0.031f); */
            gl.glColor3f(0.95f, 0.207f, 0.031f);
            gl.glVertex3f((float) (p.getX() * scaleFactor),
                    (float) (p.getY() * scaleFactor),
                    (float) (p.getZ() * scaleFactor));
            gl.glPopMatrix();
        }
        gl.glEnd();
    }

    public void buildAxes(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        float cylinderRadius = 0.1f;
        float cylinderHeight = 30;
        int slices = 16;
        int stacks = 16;
        GLUquadric body = glu.gluNewQuadric();

        gl.glPushMatrix();
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, -cylinderHeight / 2);
        gl.glColor3f(0.1f, 0.4f, 0.4f);
        glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight,
                slices, stacks);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -cylinderHeight / 2);
        gl.glColor3f(0.0f, 0.906f, 0.909f);
        glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight,
                slices, stacks);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(-cylinderHeight / 2, 0.0f, 0.0f);
        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        gl.glColor3f(1f, 1f, 0.0f);
        glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight,
                slices, stacks);
        gl.glPopMatrix();
    }

    /**
     * This method is called when the OpenGL display needs to be redrawn.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.8f, 0.8f, 0.8f, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glPushMatrix();
        gl.glMultMatrixf(rot_matrix, 0);

        buildPoints(drawable);
        buildAxes(drawable);

        gl.glPopMatrix();
    }

    public void setupVS(int w, int h) {
        cueCenter.x = w / 2;
        cueCenter.y = h / 2;
        cueRadius = Math.min(w - 20, h - 20) / 2;
    }

    /**
     * This is called when the GLJPanel is first created. It can be used to
     * initialize the OpenGL drawing context.
     */
    public void init(GLAutoDrawable drawable) {
        // called when the panel is created
        setupVS(drawable.getWidth(), drawable.getHeight());

        Matrix.rotateY(Matrix.deg2Rad(30), rot_matrix);
        Matrix.rotateX(Matrix.deg2Rad(20), rot_matrix);

        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.8F, 0.8F, 0.8F, 1.0F);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        doLighting(gl);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        buildPoints(drawable);
        buildAxes(drawable);
    }

    private void doLighting(GL2 gl) {
        float[] light_ambient = new float[] { 0.3f, 0.3f, 0.3f, 1.0f };
        float[] light_diffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float[] light_specular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float[] light_position = new float[] { 1.0f, 1.0f, 1.0f, 0.0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_specular, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
    }

    /**
     *
     * Called when the size of the GLJPanel changes. Note:
     * glViewport(x,y,width,height) has already been called before this method
     * is called!
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        float h = (float) height / (float) width;

        GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(35, h, 0.1, 10000);
        glu.gluLookAt(0, 0, 40, 0, 0, 0, 0, 1, 0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -40.0f);

        setupVS(width, height);
    }

    /**
     * This is called before the GLJPanel is destroyed. It can be used to
     * release OpenGL resources.
     */
    public void dispose(GLAutoDrawable drawable) {
    }

    // ------------ Support for keyboard handling ------------

    /**
     * Called when the user presses any key.
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Called when the user types a character.
     */
    public void keyTyped(KeyEvent e) {
        /* char ch = e.getKeyChar(); // Which character was typed. */
    }

    public void keyReleased(KeyEvent e) {
    }

    // --------------------------- animation support ---------------------------

    /* private int frameNumber = 0; */

    private boolean animating;

    private void updateFrame() {
    }

    public void startAnimation() {
        if (!animating) {
            if (animationTimer == null) {
                animationTimer = new Timer(30, this);
            }
            animationTimer.start();
            animating = true;
        }
    }

    public void pauseAnimation() {
        if (animating) {
            animationTimer.stop();
            animating = false;
        }
    }

    public void actionPerformed(ActionEvent evt) {
        updateFrame();
        display.repaint();
    }

    // MouseEvent support
    @Override
    public void mouseClicked(com.jogamp.newt.event.MouseEvent arg0) {
    }

    private float[] mouseMtx = new float[16];

    @Override
    public void mouseDragged(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub
        if (!mouseDown)
            return;

        Point newMouse = new Point(arg0.getX(), arg0.getY());

        if (newMouse.x != prevMouse.x || newMouse.y != prevMouse.y) {
            vs.makeRotationMtx(prevMouse, newMouse, cueCenter, cueRadius,
                    mouseMtx);

            rot_matrix = Matrix.multiply(rot_matrix, mouseMtx);
            fixRotationMatrix();

            prevMouse = newMouse;
        }
    }

    private void fixRotationMatrix() {
        rot_matrix[3] = rot_matrix[7] = rot_matrix[11] = rot_matrix[12] = rot_matrix[13] = rot_matrix[14] = 0.0f;
        rot_matrix[15] = 1.0f;
        float fac;
        if ((fac = (float) Math.sqrt((rot_matrix[0] * rot_matrix[0])
                + (rot_matrix[4] * rot_matrix[4])
                + (rot_matrix[8] * rot_matrix[8]))) != 1.0f) {
            if (fac != 0.0f) {
                fac = 1.0f / fac;
                rot_matrix[0] *= fac;
                rot_matrix[4] *= fac;
                rot_matrix[8] *= fac;
            }
        }
        if ((fac = (float) Math.sqrt((rot_matrix[1] * rot_matrix[1])
                + (rot_matrix[5] * rot_matrix[5])
                + (rot_matrix[9] * rot_matrix[9]))) != 1.0f) {
            if (fac != 0.0f) {
                fac = 1.0f / fac;
                rot_matrix[1] *= fac;
                rot_matrix[5] *= fac;
                rot_matrix[9] *= fac;
            }
        }
        if ((fac = (float) Math.sqrt((rot_matrix[2] * rot_matrix[2])
                + (rot_matrix[6] * rot_matrix[6])
                + (rot_matrix[10] * rot_matrix[10]))) != 1.0f) {
            if (fac != 0.0f) {
                fac = 1.0f / fac;
                rot_matrix[2] *= fac;
                rot_matrix[6] *= fac;
                rot_matrix[10] *= fac;
            }
        }
    }

    @Override
    public void mouseEntered(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseMoved(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(com.jogamp.newt.event.MouseEvent arg0) {
        if (mouseDown)
            return;

        mouseDown = true;
        prevMouse = new Point(arg0.getX(), arg0.getY());
    }

    @Override
    public void mouseReleased(com.jogamp.newt.event.MouseEvent arg0) {
        if (!mouseDown)
            return;

        mouseDown = false;
    }

    @Override
    public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub
    }
}
