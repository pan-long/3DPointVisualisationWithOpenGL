package visualisation;

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
    private float rotateX, rotateY; // rotation amounts about axes, controlled
                                    // by keyboard
    /* private GLUT glut = new GLUT(); // for drawing the teapot */
    private GLU glu = new GLU();
    private static ScaleConfiguration sc = null;

    private static double scaleFactor;
    private static double radius;

    private static String TITLE = "JOGL 2 with NEWT";
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
        // GLCapabilities caps = new GLCapabilities(null);
        // display = new GLJPanel(caps);
        // display.setPreferredSize( new Dimension(600,600) ); // TODO: set
        // display size here
        // display.addGLEventListener(this);
        // setLayout(new BorderLayout());
        // add(display,BorderLayout.CENTER);
        // TODO: Other components could be added to the main panel.

        // rotateX = 15; // initialize some variables used in the drawing.
        // rotateY = 15;

        // TODO: Uncomment the next two lines to enable keyboard event handling
        // requestFocusInWindow();
        // display.addKeyListener(this);

        // TODO: Uncomment the next one or two lines to enable mouse event
        // handling
        // display.addMouseListener(this);
        // display.addMouseMotionListener(this);

        // TODO: Uncomment the following line to start the animation
        /* startAnimation(); */
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
    
    public void buildAxes(GLAutoDrawable drawable){
    	GL2 gl = drawable.getGL().getGL2();
    	float cylinderRadius = 0.1f;
    	float cylinderHeight = 30;
    	int slices = 16;
    	int stacks = 16;
    	GLUquadric body = glu.gluNewQuadric();
    	
    	gl.glPushMatrix();
        /* gl.glLoadIdentity(); */
    	gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
    	/* gl.glRotatef(0f, 1.0f, 0.0f, 0.0f); */
        gl.glTranslatef(0.0f,0.0f, -cylinderHeight / 2);
        gl.glColor3f(0.1f,0.4f, 0.4f);
        glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight, slices, stacks);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
        /* gl.glLoadIdentity(); */
        gl.glTranslatef(0.0f,0.0f,-cylinderHeight / 2);
		/* gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f); */
    	/* gl.glRotatef(0f, 1.0f, 0.0f, 0.0f); */
		gl.glColor3f(0.0f,0.906f,0.909f);
		glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight, slices, stacks);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
        /* gl.glLoadIdentity(); */
        gl.glTranslatef(-cylinderHeight / 2,0.0f,0.0f);
		gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
    	/* gl.glRotatef(0f, 1.0f, 0.0f, 0.0f); */
		gl.glColor3f(1f, 1f, 0.0f);
		glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight, slices, stacks);
		gl.glPopMatrix();
    }

    /**
     * This method is called when the OpenGL display needs to be redrawn.
     */
    public void display(GLAutoDrawable drawable) {  
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.8f, 0.8f, 0.8f, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL2.GL_PROJECTION); // TODO: Set up a better projection?
        gl.glLoadIdentity();
        /* gl.glOrtho(-1,1,-1,1,-2,2); */
        glu.gluPerspective(35, 1, 0.1, 10000);
        glu.gluLookAt(0, 0, 40, 0, 0, 0, 0, 1, 0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glLoadIdentity(); // Set up modelview transform.
        gl.glRotatef(rotateY, 0, 1, 0);
        gl.glRotatef(rotateX, 1, 0, 0);

        // TODO: add drawing code!! As an example, draw a GLUT teapot
        buildPoints(drawable);
        buildAxes(drawable);
    }

    /**
     * This is called when the GLJPanel is first created. It can be used to
     * initialize the OpenGL drawing context.
     */
    public void init(GLAutoDrawable drawable) {
        // called when the panel is created
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.8F, 0.8F, 0.8F, 1.0F);
        gl.glEnable(GL.GL_DEPTH_TEST);
        /* gl.glEnable(GL2.GL_LIGHTING); */
        /* gl.glEnable(GL2.GL_LIGHT0); */
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        doLighting(gl);

        buildPoints(drawable);
        buildAxes(drawable);
    }

    private void doLighting(GL2 gl) {
        float[] light_ambient=new float[] {0.3f,0.3f,0.3f,1.0f};
        float[] light_diffuse=new float[] {1.0f,1.0f,1.0f,1.0f};
        float[] light_specular=new float[] {1.0f,1.0f,1.0f,1.0f};
        float[] light_position=new float[] {1.0f,1.0f,1.0f,0.0f};
        gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_AMBIENT,light_ambient,0);
        gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_DIFFUSE,light_diffuse,0);
        gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_SPECULAR,light_specular,0);
        gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_POSITION,light_position,0);
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
        int key = e.getKeyCode(); // Tells which key was pressed.
        if (key == KeyEvent.VK_LEFT)
            rotateY -= 15;
        else if (key == KeyEvent.VK_RIGHT)
            rotateY += 15;
        else if (key == KeyEvent.VK_DOWN)
            rotateX += 15;
        else if (key == KeyEvent.VK_UP)
            rotateX -= 15;
        else if (key == KeyEvent.VK_HOME)
            rotateX = rotateY = 0;
        // repaint();
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
        // TODO: add any other updating required for the next frame.
        rotateY += 10;
        /* frameNumber++; */
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
    private boolean dragging = false;
    private double prevX, prevY;

    @Override
    public void mouseClicked(com.jogamp.newt.event.MouseEvent arg0) {
    }

    @Override
    public void mouseDragged(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub
        if (!dragging) {
            return;
        }

        int x = arg0.getX();
        int y = arg0.getY();

        double mouseDeltaX = x - prevX;
        double mouseDeltaY = y - prevY;
        // TODO: respond to mouse drag to new point (x,y)

        if ((rotateY % 360 > 90 && rotateY % 360 < 270)
                || (rotateY % 360 < 0 && rotateY % 360 + 360 > 90 && rotateY % 360 + 360 < 270))
            rotateX -= mouseDeltaY;
        else
            rotateX += mouseDeltaY;

        rotateY += mouseDeltaX;

        /* System.out.println("x: " + rotateX + "    y: " + rotateY); */

        prevX = x;
        prevY = y;

        /* display.repaint(); */
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
        // TODO Auto-generated method stub
        if (dragging) {
            return; // don't start a new drag while one is already in progress
        }
        int x = arg0.getX();
        int y = arg0.getY();

        // TODO: respond to mouse click at (x,y)
        dragging = true; // might not always be correct!
        /* prevX = startX = x; */
        /* prevY = startY = y; */
        prevX = x;
        prevY = y;
        /* display.repaint(); */

    }

    @Override
    public void mouseReleased(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub
        if (!dragging) {
            return;
        }
        dragging = false;

    }

    @Override
    public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub

    }
}
