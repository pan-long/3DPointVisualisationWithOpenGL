package visualisation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import point.point;
import util.Matrix;
import util.VirtualSphere;

import com.jogamp.opengl.util.FPSAnimator;

import configuration.ScaleConfiguration;
import dataReader.dataReader;

public class Visualisation extends GLCanvas implements GLEventListener,
		KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	private static List<point> pointsList = null;
	private static dataReader dr = null;
	private GLU glu = new GLU();
	private static ScaleConfiguration sc = null;

	private Point prevMouse;

	private VirtualSphere vs = new VirtualSphere();
	private Point cueCenter = new Point();
	private int cueRadius;
	private boolean mouseDragging = false;
	private float rot_matrix[] = Matrix.identity();

	private static boolean isSetToOrigin = false;
	private static double[] centerOfMass;
	private static double scaleFactor;
	private static double radius;
	private static double selectedCurMax = 1;
	private static double selectedCurMin = 0;

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

				final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

				final JPanel leftJPanel = new JPanel();
				leftJPanel.setLayout(new GridLayout(8, 1));
				JLabel cameraDistanceJLabel = new JLabel("  Camera Distance");
				JSlider cameraDistanceSlider = new JSlider(JSlider.HORIZONTAL,
						1, 10, 5);
				JPanel cameraDistanceJPanel = new JPanel(new GridLayout(2, 1));
				cameraDistanceJPanel.add(cameraDistanceJLabel);
				cameraDistanceJPanel.add(cameraDistanceSlider);

				JLabel fieldOfViewJLabel = new JLabel("  Field Of View");
				JPanel fieldOfViewJPanel = new JPanel(new GridLayout(2, 1));
				JSlider fieldOfViewSlider = new JSlider(JSlider.HORIZONTAL, 1,
						10, 5);
				fieldOfViewJPanel.add(fieldOfViewJLabel);
				fieldOfViewJPanel.add(fieldOfViewSlider);

				JLabel curvatureJLabel = new JLabel("  Range Of Curvature");
				JSlider curvatureJSlider = new JSlider(JSlider.HORIZONTAL, 0,
						100, 50);
				JPanel curvatureJPanel = new JPanel(new GridLayout(2, 1));
				curvatureJPanel.add(curvatureJLabel);
				curvatureJPanel.add(curvatureJSlider);

				JCheckBox setToOriginCheckBox = new JCheckBox(
						"Set Center To Origin");
				setToOriginCheckBox.setSelected(false);
				setToOriginCheckBox.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						AbstractButton abstractButton = (AbstractButton) e
								.getSource();
						isSetToOrigin = abstractButton.isSelected();
					}
				});

				leftJPanel.add(cameraDistanceJPanel);
				leftJPanel.add(fieldOfViewJPanel);
				leftJPanel.add(curvatureJPanel);
				leftJPanel.add(setToOriginCheckBox);

				final JPanel mainJPanel = new JPanel();
				mainJPanel.setLayout(new BorderLayout());
				mainJPanel.add(leftJPanel, BorderLayout.WEST);
				mainJPanel.add(canvas, BorderLayout.CENTER);

				final JFrame frame = new JFrame();
				frame.setContentPane(mainJPanel);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	
	public static JSlider initSlider() {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 6, 3);
		slider.setMajorTickSpacing(1);
		slider.setPaintLabels(true);
		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 0 ), new JLabel("1/4") );
		labelTable.put( new Integer( 1 ), new JLabel("1/3") );
		labelTable.put( new Integer( 2 ), new JLabel("1/2") );
		labelTable.put( new Integer( 3 ), new JLabel("1") );
		labelTable.put( new Integer( 4 ), new JLabel("2") );
		labelTable.put( new Integer( 5 ), new JLabel("3") );
		labelTable.put( new Integer( 6 ), new JLabel("4") );
		slider.setLabelTable( labelTable );
		
		return slider;
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
		centerOfMass = sc.getCenterOfMass();
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

			if (p.getCurvature() > selectedCurMin
					&& p.getCurvature() < selectedCurMax)
				gl.glColor3f(0.95f, 0.207f, 0.031f);
			else {
				gl.glColor3f(0.5f, 0.5f, 0.5f);
			}

			if (!isSetToOrigin) {
				gl.glVertex3f((float) (p.getX() * scaleFactor),
						(float) (p.getY() * scaleFactor),
						(float) (p.getZ() * scaleFactor));
			} else {
				gl.glVertex3f(
						(float) (p.getX() * scaleFactor - centerOfMass[0]),
						(float) (p.getY() * scaleFactor - centerOfMass[1]),
						(float) (p.getZ() * scaleFactor - centerOfMass[2]));
			}
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

		buildPoints(gl);
		buildAxes(gl);
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
