package visualisation;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;  // for drawing the sample teapot

/**
 * A template for a basic JOGL application with support for animation and for
 * keyboard and mouse event handling.  To enable the support, uncomment the
 * appropriate lines in the init() method.  As an example, the program draws
 * the GLUT teapot.
 */
public class Visualisation extends JPanel implements 
                   GLEventListener, KeyListener, MouseListener, MouseMotionListener, ActionListener {

    static final long serialVersionUID = 1l;

    public static void main(String[] args) {
        JFrame window = new JFrame("JOGL");
        window.setContentPane(new Visualisation());
        window.pack();
        window.setLocation(50,50);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
    
    private GLJPanel display;
    private Timer animationTimer;
    private float rotateX, rotateY;   // rotation amounts about axes, controlled by keyboard

    public Visualisation() {
        GLCapabilities caps = new GLCapabilities(null);
        display = new GLJPanel(caps);
        display.setPreferredSize( new Dimension(600,600) );  // TODO: set display size here
        display.addGLEventListener(this);
        setLayout(new BorderLayout());
        add(display,BorderLayout.CENTER);
        // TODO:  Other components could be added to the main panel.
        
        rotateX = 15;  // initialize some variables used in the drawing.
        rotateY = 15;
        
        // TODO:  Uncomment the next two lines to enable keyboard event handling
        requestFocusInWindow();
        display.addKeyListener(this);

        // TODO:  Uncomment the next one or two lines to enable mouse event handling
        display.addMouseListener(this);
        display.addMouseMotionListener(this);
        
        // TODO: Uncomment the following line to start the animation
        /* startAnimation(); */
    }

    // ---------------  Methods of the GLEventListener interface -----------
    
    private GLUT glut = new GLUT();  // for drawing the teapot
    private GLU glu = new GLU();

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
        /* gl.glColor3f(1.0f, 1.0f, 1.0f); */
        glut.glutSolidTeapot(0.5);
        /* glut.glutSolidSphere(1.0, 10, 10); */

        /* gl.glEnable( GL2.GL_POINT_SPRITE ); // GL_POINT_SPRITE_ARB if you're */
        /*  */
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
    }

    private void doLighting( GL2 gl )
    {
        float[] lightPos = new float[4];
        lightPos[0] = 50005;
        lightPos[1] = 30000;
        lightPos[2] = 50000;
        lightPos[3] = 1;
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        float[] noAmbient ={ 0.1f, 0.1f, 0.1f, 1f }; // low ambient light
        float[] spec =    { 1f, 0.6f, 0f, 1f }; // low ambient light
        float[] diffuse ={ 1f, 1f, 1f, 1f };
        // properties of the light
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, noAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, spec, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
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
        repaint();
    }

    /**
     * Called when the user types a character.
     */
    public void keyTyped(KeyEvent e) { 
        char ch = e.getKeyChar();  // Which character was typed.
    }

    public void keyReleased(KeyEvent e) { 
    }

    // --------------------------- animation support ---------------------------

    private int frameNumber = 0;

    private boolean animating;

    private void updateFrame() {
        // TODO:  add any other updating required for the next frame.
        rotateX += 10;
        frameNumber++;
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



    // ---------------------- support for mouse events ----------------------

    private boolean dragging;  // is a drag operation in progress?

    private int startX, startY;  // starting location of mouse during drag
    private int prevX, prevY;    // previous location of mouse during drag

    /**
     * Called when the user presses a mouse button on the display.
     */
    public void mousePressed(MouseEvent evt) {
        if (dragging) {
            return;  // don't start a new drag while one is already in progress
        }
        int x = evt.getX();
        int y = evt.getY();
        // TODO: respond to mouse click at (x,y)
        dragging = true;  // might not always be correct!
        prevX = startX = x;
        prevY = startY = y;
        display.repaint();    //  only needed if display should change
    }

    /**
     * Called when the user releases a mouse button after pressing it on the display.
     */
    public void mouseReleased(MouseEvent evt) {
        if (! dragging) {
            return;
        }
        dragging = false;
        // TODO:  finish drag (generally nothing to do here)
    }

    /**
     * Called during a drag operation when the user drags the mouse on the display/
     */
    public void mouseDragged(MouseEvent evt) {
        if (! dragging) {
            return;
        }
        int x = evt.getX();
        int y = evt.getY();

        double mouseDeltaX = x - prevX;
        double mouseDeltaY = y - prevY;
        // TODO:  respond to mouse drag to new point (x,y)   

        if ((rotateX % 360 > 90 && rotateX % 360 < 270)
                || (rotateX % 360 < 0 && rotateX % 360 + 360 > 90 && rotateX % 360 + 360 < 270))
            rotateY += mouseDeltaX;
        else
            rotateY -= mouseDeltaX;

        rotateX += mouseDeltaY;

        prevX = x;
        prevY = y;

        display.repaint();
    }

    public void mouseMoved(MouseEvent evt) { 

    }
    public void mouseClicked(MouseEvent evt) { 

    }
    public void mouseEntered(MouseEvent evt) { 

    }
    public void mouseExited(MouseEvent evt) { 

    }
                   }
