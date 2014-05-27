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
import javax.swing.Timer;

import point.point;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;

import dataReader.dataReader;
import configuration.ScaleConfiguration;

public class Visualisation implements 
                   GLEventListener, KeyListener, MouseListener, ActionListener {

    static final long serialVersionUID = 1l;
    private static List<point> pointsList = null;
    private static dataReader dr = null;
    private GLJPanel display;
    private Timer animationTimer;
    private float rotateX, rotateY;   // rotation amounts about axes, controlled by keyboard
    /* private GLUT glut = new GLUT();  // for drawing the teapot */
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
//        GLCapabilities caps = new GLCapabilities(null);
//        display = new GLJPanel(caps);
//        display.setPreferredSize( new Dimension(600,600) );  // TODO: set display size here
//        display.addGLEventListener(this);
//        setLayout(new BorderLayout());
//        add(display,BorderLayout.CENTER);
        // TODO:  Other components could be added to the main panel.
        
//        rotateX = 15;  // initialize some variables used in the drawing.
//        rotateY = 15;
        
        // TODO:  Uncomment the next two lines to enable keyboard event handling
//        requestFocusInWindow();
//        display.addKeyListener(this);

        // TODO:  Uncomment the next one or two lines to enable mouse event handling
//        display.addMouseListener(this);
//        display.addMouseMotionListener(this);
        
        // TODO: Uncomment the following line to start the animation
        /* startAnimation();  */
    }
    
    public static void initDataReader(){
    	dr = new dataReader("output1.pcd");
    	pointsList = dr.getPoints();
        sc = new ScaleConfiguration(pointsList, 10);
        scaleFactor = sc.getScaleFactor();
        radius = sc.getRadius();
    }

    // ---------------  Methods of the GLEventListener interface -----------
    public void buildPoints(GLAutoDrawable drawable){
    	GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_POINT_SPRITE); // GL_POINT_SPRITE_ARB if you're
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glPointSize((float)radius);

    	gl.glBegin(GL.GL_POINTS);
    	for (point p : pointsList) {
			gl.glPushMatrix();
			gl.glTranslatef(p.getX(), p.getY(), p.getZ());
			/* gl.glColor3f(0.95f, 0.207f, 0.031f); */
			gl.glColor3f(0.95f, 0.207f, 0.031f);
			gl.glVertex3f((float)(p.getX() * scaleFactor), (float)(p.getY() * scaleFactor), (float)(p.getZ() * scaleFactor));
			gl.glPopMatrix();
		}
    	gl.glEnd();
    }

    /**
     * This method is called when the OpenGL display needs to be redrawn.
     */
    public void display(GLAutoDrawable drawable) {  
            // called when the panel needs to be drawn

        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.8f,0.8f,0.8f,0);
        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

        gl.glMatrixMode(GL2.GL_PROJECTION);  // TODO: Set up a better projection?
        gl.glLoadIdentity();
        /* gl.glOrtho(-1,1,-1,1,-2,2); */
        glu.gluPerspective(35, 1, 0.1, 10000);
        glu.gluLookAt(0, 0, 40, 0, 0, 0, 0, 1, 0);
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glLoadIdentity();             // Set up modelview transform. 
        gl.glRotatef(rotateY,0,1,0);
        gl.glRotatef(rotateX,1,0,0);

        // TODO: add drawing code!!  As an example, draw a GLUT teapot
        buildPoints(drawable);
        /* gl.glColor3f(1.0f, 1.0f, 1.0f); */
        /* glut.glutSolidTeapot(5); */
//        glut.glutSolidSphere(1.0, 10, 10);

        /* gl.glEnable( GL2.GL_POINT_SPRITE ); // GL_POINT_SPRITE_ARB if you're */
        /* gl.glEnable( GL2.GL_POINT_SMOOTH ); */
        /* gl.glEnable( GL2.GL_BLEND ); */
        /* gl.glBlendFunc( GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA ); */
        /* gl.glPointSize(10f); */
        /* gl.glBegin(GL.GL_POINTS); */
        /* gl.glColor3f( 0.95f, 0.207f, 0.031f  ); */
        /* gl.glVertex3f( 0.5f, 0.5f, 0.5f); */
        /* gl.glVertex3f( 0.5f, 0.6f, 0.6f); */
        /* gl.glVertex3f( 0.5f, 0.8f, 0.7f); */
        /* gl.glVertex3f( 0.5f, 0.7f, 0.8f); */
        /* gl.glEnd(); */
        /*  */
        /* gl.glFinish(); */
        /* glutSwapBuffers(); */
    }

    /**
     * This is called when the GLJPanel is first created.  It can be used to initialize
     * the OpenGL drawing context.
     */
    public void init(GLAutoDrawable drawable) {
        // called when the panel is created
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.8F, 0.8F, 0.8F, 1.0F);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        doLighting(gl);
        
        buildPoints(drawable);
    }

    private void doLighting( GL2 gl )
    {
        float[] lightPos = new float[4];
        lightPos[0] = 50005;
        lightPos[1] = 30000;
        lightPos[2] = 50000;
        lightPos[3] = 1;
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        float[] noAmbient ={ 0.1f, 0.1f, 0.1f, 1f }; // low ambient light
        float[] spec =    { 1f, 0f, 0.6f, 1f }; // low ambient light
        float[] diffuse ={ 1f, 1f, 1f, 1f };
        // properties of the light
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, noAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, spec, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        /*  */
        /* float[] lightPos1 = new float[4]; */
        /* lightPos1[0] = -50005; */
        /* lightPos1[1] = -30000; */
        /* lightPos1[2] = -50000; */
        /* lightPos1[3] = 1; */
        /*  */
        /* gl.glEnable(GL2.GL_LIGHT1); */
        /*  */
        /* gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, noAmbient, 0); */
        /* gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, spec, 0); */
        /* gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0); */
        /* gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos1, 0); */
        /* float[] colorBlack  = {0.0f,0.0f,0.0f,1.0f}; */
        /* float[] colorWhite  = {1.0f,1.0f,1.0f,1.0f}; */
        /* float[] colorGray   = {0.6f,0.6f,0.6f,1.0f}; */
        /* float[] colorRed    = {1.0f,0.0f,0.0f,1.0f}; */
        /* float[] colorBlue   = {0.0f,0.0f,0.1f,1.0f}; */
        /* float[] colorYellow = {1.0f,1.0f,0.0f,1.0f}; */
        /* float[] colorLightYellow = {.5f,.5f,0.0f,1.0f}; */
        /* // First Switch the lights on. */
        /* gl.glEnable( GL2.GL_LIGHTING ); */
        /* gl.glEnable( GL2.GL_LIGHT0 ); */
        /* gl.glEnable( GL2.GL_LIGHT1 ); */
        /* gl.glEnable( GL2.GL_LIGHT2 ); */
        /*  */
        /* // */
        /* // Light 0. */
        /* //     */
        /* // Default from the red book. */
        /* // */
        /* float[] noAmbient ={ 0.3f, 0.3f, 0.3f, 1f }; // low ambient light */
        /* gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT, noAmbient, 0); */
        /* gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);     */
        /*  */
        /* // */
        /* // Light 1. */
        /* // */
        /* // Position and direction (spotlight) */
        /* float posLight1[] = { 1.0f, 1.f, 1.f, 0.0f }; */
        /* float spotDirection[] = { -1.0f, -1.0f, 0.f }; */
        /* //gl.glLightfv( GL.GL_LIGHT1, GL.GL_POSITION, posLight1 ); */
        /* //gl.glLightf( GL.GL_LIGHT1, GL.GL_SPOT_CUTOFF, 60.0F ); */
        /* //gl.glLightfv( GL.GL_LIGHT1, GL.GL_SPOT_DIRECTION, spotDirection ); */
        /* // */
        /* gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_AMBIENT, colorGray, 0); */
        /* gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_DIFFUSE, colorGray, 0); */
        /* gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_SPECULAR, colorWhite, 0); */
        /* //gl.glLightfv( GL.GL_LIGHT1, GL.GL_SPECULAR, colorRed ); */
        /* // */
        /* gl.glLightf( GL2.GL_LIGHT1, GL2.GL_CONSTANT_ATTENUATION, 0.2f ); */
        /*  */
        /* // */
        /* // Light 2. */
        /* // */
        /* // Position and direction */
        /* float posLight2[] = { .5f, 1.f, 3.f, 0.0f }; */
        /* gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_POSITION, posLight2, 0); */
        /* // */
        /* gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_AMBIENT, colorGray, 0); */
        /* gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_DIFFUSE, colorGray, 0); */
        /* gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_SPECULAR, colorWhite, 0); */
        /* // */
        /* gl.glLightf( GL2.GL_LIGHT2, GL2.GL_CONSTANT_ATTENUATION, 0.8f ); */
    }

    /**
     * 
     * Called when the size of the GLJPanel changes.  Note:  glViewport(x,y,width,height)
     * has already been called before this method is called!
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    /**
     * This is called before the GLJPanel is destroyed.  It can be used to release OpenGL resources.
     */
    public void dispose(GLAutoDrawable drawable) {
    }


    // ------------ Support for keyboard handling  ------------

    /**
     * Called when the user presses any key.
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();  // Tells which key was pressed.
        if ( key == KeyEvent.VK_LEFT )
            rotateY -= 15;
        else if ( key == KeyEvent.VK_RIGHT )
            rotateY += 15;
        else if ( key == KeyEvent.VK_DOWN)
            rotateX += 15;
        else if ( key == KeyEvent.VK_UP )
            rotateX -= 15;
        else if ( key == KeyEvent.VK_HOME )
            rotateX = rotateY = 0;
        //repaint();
    }

    /**
     * Called when the user types a character.
     */
    public void keyTyped(KeyEvent e) { 
        /* char ch = e.getKeyChar();  // Which character was typed. */
    }

    public void keyReleased(KeyEvent e) { 
    }

    // --------------------------- animation support ---------------------------

    /* private int frameNumber = 0; */

    private boolean animating;

    private void updateFrame() {
        // TODO:  add any other updating required for the next frame.
        rotateY += 10;
        /* frameNumber++; */
    }

    public void startAnimation() {
        if ( ! animating ) {
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
        if (! dragging) {
            return;
        }

        int x = arg0.getX();
        int y = arg0.getY();

        double mouseDeltaX = x - prevX;
        double mouseDeltaY = y - prevY;
        // TODO:  respond to mouse drag to new point (x,y)   

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
            return;  // don't start a new drag while one is already in progress
        }
        int x = arg0.getX();
        int y = arg0.getY();

        // TODO: respond to mouse click at (x,y)
        dragging = true;  // might not always be correct!
        /* prevX = startX = x; */
        /* prevY = startY = y; */
        prevX = x;
        prevY = y;
        /* display.repaint();   */

    }

    @Override
    public void mouseReleased(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub
        if (! dragging) {
            return;
        }
        dragging = false;

    }

    @Override
    public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent arg0) {
        // TODO Auto-generated method stub

    }
                   }
