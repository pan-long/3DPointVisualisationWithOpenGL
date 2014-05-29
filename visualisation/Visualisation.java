package visualisation;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.IntBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import point.point;
import util.Matrix;
import util.VirtualSphere;

import com.jogamp.opengl.util.FPSAnimator;

import configuration.ScaleConfiguration;
import dataReader.dataReader;

public class Visualisation extends GLCanvas implements GLEventListener,
		KeyListener, MouseListener, MouseMotionListener, ActionListener {

	private static final long serialVersionUID = 1L;
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
	private boolean mouseDragging= false;
	private float rot_matrix[] = Matrix.identity();

	private static double scaleFactor;
	private static double radius;

	private static String TITLE = "3D Visualisation Tool";
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;
	private static final int FPS = 60;

	static final int UPDATE = 1, SELECT = 2;
	int cmd = UPDATE;
	double mouseX, mouseY;

	public static void main(String[] args) {
		initDataReader();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GLCanvas canvas = new Visualisation();
				canvas.setPreferredSize(new Dimension(WINDOW_WIDTH - 100,
						WINDOW_HEIGHT));

				final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

				final Container container = new Container();
				container.add(canvas);
				final JPanel jPanel = new JPanel();
				jPanel.setLayout(new BorderLayout());
				jPanel.add(container, BorderLayout.CENTER);
				jPanel.add(new Button("west"), BorderLayout.WEST);

				final JFrame frame = new JFrame();
				frame.setContentPane(jPanel);
				frame.getContentPane().add(canvas);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						new Thread() {
							@Override
							public void run() {
								if (animator.isStarted())
									animator.stop();
								System.exit(0);
							}
						}.start();
					}
				});
				frame.setTitle(TITLE);
				frame.setPreferredSize(new Dimension(WINDOW_WIDTH,
						WINDOW_HEIGHT));
				frame.pack();
				frame.setVisible(true);
				animator.start();
			}
		});
	}

	public Visualisation() {
		this.addGLEventListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public static void initDataReader() {
		dr = new dataReader("output1.pcd");
		pointsList = dr.getPoints();
		sc = new ScaleConfiguration(pointsList, 10);
		scaleFactor = sc.getScaleFactor();
		radius = sc.getRadius();
	}

	// --------------- Methods of the GLEventListener interface -----------
	public void buildPoints(GL2 gl) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL2.GL_POINT_SPRITE);
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPointSize((float) radius / 2);

		gl.glBegin(GL.GL_POINTS);
		for (point p : pointsList) {
			gl.glPushMatrix();
			gl.glTranslatef(p.getX(), p.getY(), p.getZ());
			gl.glColor3f(0.95f, 0.207f, 0.031f);
			gl.glVertex3f((float) (p.getX() * scaleFactor),
					(float) (p.getY() * scaleFactor),
					(float) (p.getZ() * scaleFactor));
			gl.glPopMatrix();
		}
		gl.glEnd();
	}

	public void buildAxes(GL2 gl) {
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

		buildPoints(gl);
		buildAxes(gl);

		gl.glPopMatrix();
	}

	public void setupVS(int w, int h) {
		cueCenter.x = w / 2;
		cueCenter.y = h / 2;
		cueRadius = (int) (Math.sqrt(w * w + h * h) / 2);
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

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		doLighting(gl);

		buildPoints(gl);
		buildAxes(gl);
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

	public void processHits(int hits, IntBuffer buffer) {
		System.out.println("---------------------------------");
		System.out.println(" HITS: " + hits);
		int offset = 0;
		int names;
		float z1, z2;
		for (int i = 0; i < hits; i++) {
			System.out.println("- - - - - - - - - - - -");
			System.out.println(" hit: " + (i + 1));
			names = buffer.get(offset);
			offset++;
			z1 = (float) (buffer.get(offset) & 0xffffffffL) / 0x7fffffff;
			offset++;
			z2 = (float) (buffer.get(offset) & 0xffffffffL) / 0x7fffffff;
			offset++;
			System.out.println(" number of names: " + names);
			System.out.println(" z1: " + z1);
			System.out.println(" z2: " + z2);
			System.out.println(" names: ");

			for (int j = 0; j < names; j++) {
				System.out.print("       " + buffer.get(offset));
				if (j == (names - 1))
					System.out.println("<-");
				else
					System.out.println();
				offset++;
			}
			System.out.println("- - - - - - - - - - - -");
		}
		System.out.println("---------------------------------");
	}

	/**
	 *
	 * Called when the size of the GLJPanel changes. Note:
	 * glViewport(x,y,width,height) has already been called before this method
	 * is called!
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		float h = (float) width / (float) height;

		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective(35, h, 0.1, 10000);
		glu.gluLookAt(0, 0, 30, 0, 0, 0, 0, 1, 0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -30.0f);

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
	public void mouseClicked(MouseEvent e) {

	}

	private float[] mouseMtx = new float[16];

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!mouseDragging)
			return;
		
		Point newMouse = new Point(e.getX(), e.getY());

		if (newMouse.x != prevMouse.x || newMouse.y != prevMouse.y) {
			vs.makeRotationMtx(prevMouse, newMouse, cueCenter, cueRadius,
					mouseMtx);

			rot_matrix = Matrix.multiply(rot_matrix, mouseMtx);
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
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseDragging = true;
		prevMouse = new Point(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDragging = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
