package visualisation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import point.DataType;
import point.point;
import util.Matrix;
import util.VirtualSphere;

import com.jogamp.opengl.util.FPSAnimator;
import configuration.ScaleConfiguration;
import dataReader.dataReader;

public class Visualisation extends GLCanvas implements GLEventListener,
		MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	private static List<point> pointsList = null;
	private static dataReader dr = null;
	private GLU glu = new GLU();
	private static ScaleConfiguration sc = null;
	private double screenRatio = 10 / 6.0;

	private Point prevMouse;
	private VirtualSphere vs = new VirtualSphere();
	private Point cueCenter = new Point();
	private int cueRadius;
	private boolean isMouseDragging = false;
	private float rot_matrix[] = Matrix.identity();

	private static boolean isSetToOrigin = false;
	private static boolean isAxesVisible = true;
	private static double[] centerOfMass;
	private static double scaleFactor;
	private static double radius;
	private static double defaultRadius;
	private static double selectedCurMax = 1;
	private static double selectedCurMin = 0;
	private static double cameraDistance = 25;
	private static double fieldOfView = 30;
	private static double lookAtX = 0;
	private static double lookAtY = 0;

	private static JSlider cameraDistanceSlider = null;
	private static JSlider fieldOfViewSlider = null;
	private static JSlider curvatureJSlider = null;
	private static JCheckBox setToOriginCheckBox = null;
	private static JCheckBox setAxeVisibleCheckBox = null;

	private static String TITLE = "3D Visualisation Tool";
	private static final int WINDOW_WIDTH = 1000;
	private static final int WINDOW_HEIGHT = 600;
	private static final double MAX_ABS_COORDINATE = 10;
	private static final int FPS = 60;

	public static void main(String[] args) {
		initDataReader();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GLCanvas canvas = new Visualisation();
				final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

				final GridLayout defaultLayout = new GridLayout(2, 1, 0, -8);
				final JPanel leftJPanel = new JPanel();
				leftJPanel.setPreferredSize(new Dimension(250, 600));
				leftJPanel.setLayout(new GridLayout(7, 1, 0, 0));

				final JLabel cameraDistanceJLabel = new JLabel(
						"  Camera Distance");
				final JLabel cameraDistanceValueJLabel = new JLabel("25.00");
				cameraDistanceSlider = initSlider();
				cameraDistanceSlider.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						JSlider source = (JSlider) e.getSource();
						int v = source.getValue();
						if (v < 30) {
							cameraDistance = 25.0 * 10.0 / (40.0 - v);
						} else {
							cameraDistance = 25.0 * (v - 20.0) / 10.0;
						}
						cameraDistanceValueJLabel.setText(String.format("%.2f",
								cameraDistance));
					}
				});
				JPanel cameraDistanceValueJPanel = new JPanel(
						new BorderLayout());
				cameraDistanceValueJPanel.add(cameraDistanceSlider,
						BorderLayout.CENTER);
				cameraDistanceValueJPanel.add(cameraDistanceValueJLabel,
						BorderLayout.EAST);
				JPanel cameraDistanceJPanel = new JPanel(defaultLayout);
				cameraDistanceJPanel.add(cameraDistanceJLabel);
				cameraDistanceJPanel.add(cameraDistanceValueJPanel);

				final JLabel fieldOfViewJLabel = new JLabel("  Field Of View");
				final JLabel fieldOfViewValueJLabel = new JLabel("30.00");
				fieldOfViewSlider = initSlider();
				fieldOfViewSlider.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						JSlider source = (JSlider) e.getSource();
						int v = source.getValue();

						if (v < 30) {
							fieldOfView = 30.0 * 10.0 / (40.0 - v);
						} else {
							fieldOfView = 30.0 * (v - 20.0) / 10.0;
						}
						fieldOfViewValueJLabel.setText(String.format("%.2f",
								fieldOfView));
					}
				});
				JPanel fieldOfViewValueJPanel = new JPanel(new BorderLayout());
				fieldOfViewValueJPanel.add(fieldOfViewSlider,
						BorderLayout.CENTER);
				fieldOfViewValueJPanel.add(fieldOfViewValueJLabel,
						BorderLayout.EAST);
				JPanel fieldOfViewJPanel = new JPanel(defaultLayout);
				fieldOfViewJPanel.add(fieldOfViewJLabel);
				fieldOfViewJPanel.add(fieldOfViewValueJPanel);

				final JLabel radiusJLabel = new JLabel("  Point Radius");
				final JLabel radiusValueJLabel = new JLabel(String.format(
						"%.2f", radius));
				JSlider radiusJSlider = initSlider();
				radiusJSlider.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						JSlider source = (JSlider) e.getSource();
						int v = source.getValue();

						if (v < 30) {
							radius = defaultRadius * 10.0 / (40.0 - v);
						} else {
							radius = defaultRadius * (v - 20.0) / 10.0;
						}
						radiusValueJLabel.setText(String.format("%.2f", radius));
					}
				});
				JPanel radiusValueJPanel = new JPanel(new BorderLayout());
				radiusValueJPanel.add(radiusJSlider, BorderLayout.CENTER);
				radiusValueJPanel.add(radiusValueJLabel, BorderLayout.EAST);
				JPanel radiusJPanel = new JPanel(defaultLayout);
				radiusJPanel.add(radiusJLabel);
				radiusJPanel.add(radiusValueJPanel);

				JLabel curvatureJLabel = new JLabel("  Range Of Curvature");
				curvatureJSlider = initCurvatureSlider();
				JPanel curvatureJPanel = new JPanel(defaultLayout);
				curvatureJPanel.add(curvatureJLabel);
				curvatureJPanel.add(curvatureJSlider);

				setToOriginCheckBox = new JCheckBox("Set Center To Origin");
				setToOriginCheckBox.setSelected(false);
				setToOriginCheckBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AbstractButton abstractButton = (AbstractButton) e
								.getSource();
						isSetToOrigin = abstractButton.isSelected();
					}
				});

				setAxeVisibleCheckBox = new JCheckBox("Show Axes");
				setAxeVisibleCheckBox.setSelected(true);
				setAxeVisibleCheckBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AbstractButton abstractButton = (AbstractButton) e
								.getSource();
						isAxesVisible = abstractButton.isSelected();
					}
				});

				JPanel checkboxJPanel = new JPanel(defaultLayout);
				checkboxJPanel.add(setToOriginCheckBox);
				checkboxJPanel.add(setAxeVisibleCheckBox);

				final JPanel fileChooserRowJPanel = new JPanel(new GridLayout(2, 1, 1, 1));
				final JPanel fileChooserJPanel = new JPanel(new BorderLayout());
				JLabel fileJLabel = new JLabel("No File Chosen");
				JButton openButton = new JButton("Choose File...");
				openButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter(
						        "Point Cloud Data Format", "pcd");
						fileChooser.setFileFilter(filter);
						int rVal = fileChooser.showOpenDialog(fileChooserJPanel);
						if (rVal == JFileChooser.APPROVE_OPTION) {
							File file = fileChooser.getSelectedFile();
							dr = new dataReader(file);
						}
					}
				});
				JButton buildButton = new JButton("Build");
				buildButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
					}
				});
				fileChooserJPanel.add(openButton, BorderLayout.WEST);
				fileChooserJPanel.add(fileJLabel, BorderLayout.CENTER);
				fileChooserRowJPanel.add(fileChooserJPanel);
				fileChooserRowJPanel.add(buildButton);
				
				leftJPanel.add(cameraDistanceJPanel);
				leftJPanel.add(fieldOfViewJPanel);
				leftJPanel.add(radiusJPanel);
				leftJPanel.add(curvatureJPanel);
				leftJPanel.add(checkboxJPanel);
				leftJPanel.add(fileChooserRowJPanel);

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
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 60, 30);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("1/4"));
		labelTable.put(new Integer(10), new JLabel("1/3"));
		labelTable.put(new Integer(20), new JLabel("1/2"));
		labelTable.put(new Integer(30), new JLabel("1"));
		labelTable.put(new Integer(40), new JLabel("2"));
		labelTable.put(new Integer(50), new JLabel("3"));
		labelTable.put(new Integer(60), new JLabel("4"));
		slider.setLabelTable(labelTable);

		return slider;
	}

	public static JSlider initCurvatureSlider() {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("0"));
		labelTable.put(new Integer(20), new JLabel("0.2"));
		labelTable.put(new Integer(40), new JLabel("0.4"));
		labelTable.put(new Integer(60), new JLabel("0.6"));
		labelTable.put(new Integer(80), new JLabel("0.8"));
		labelTable.put(new Integer(100), new JLabel("1"));
		slider.setLabelTable(labelTable);

		return slider;
	}

	public Visualisation() {
		addGLEventListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.requestFocusInWindow();
	}

	public void reset() {
		setAxeVisibleCheckBox.setSelected(true);
		isAxesVisible = true;
		setToOriginCheckBox.setSelected(false);
		isSetToOrigin = false;
		fieldOfViewSlider.setValue(30);
		fieldOfView = 30;
		curvatureJSlider.setValue(30);
		// TODO: reset curvature back to 30
		cameraDistanceSlider.setValue(30);
		cameraDistance = 25;

		// reset look at point
		lookAtX = lookAtY = 0;
	}

	public static void initDataReader() {
		dr = new dataReader("output1.pcd");
		pointsList = dr.getPoints();
		sc = new ScaleConfiguration(pointsList, 10);
		scaleFactor = sc.getScaleFactor();
		defaultRadius = radius = sc.getRadius()
				* (WINDOW_HEIGHT / MAX_ABS_COORDINATE);
		centerOfMass = sc.getCenterOfMass();
	}

	// --------------- Methods of the GLEventListener interface -----------
	public void buildPoints(GL2 gl) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL2.GL_POINT_SPRITE);
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPointSize((float) radius);

		gl.glBegin(GL.GL_POINTS);
		for (point p : pointsList) {
			gl.glPushMatrix();
			gl.glTranslatef(p.getX(), p.getY(), p.getZ());

			if (p.getType() != DataType.XYZC
					|| (p.getCurvature() > selectedCurMin && p.getCurvature() < selectedCurMax))
				gl.glColor3f(0.95f, 0.207f, 0.031f);
			else {
				gl.glColor3d(0, 154, 199);
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

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective(fieldOfView, screenRatio, 0.1, 10000);
		glu.gluLookAt(0, 0, cameraDistance, lookAtX, lookAtY, 0, 0, 1, 0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		gl.glPushMatrix();
		gl.glMultMatrixf(rot_matrix, 0);

		buildPoints(gl);

		if (isAxesVisible)
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
		screenRatio = (float) width / (float) height;

		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective(fieldOfView, screenRatio, 0.1, 10000);
		glu.gluLookAt(0, 0, cameraDistance, lookAtX, lookAtY, 0, 0, 1, 0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -25.0f);

		setupVS(width, height);
	}

	/**
	 * This is called before the GLJPanel is destroyed. It can be used to
	 * release OpenGL resources.
	 */
	public void dispose(GLAutoDrawable drawable) {
	}

	// MouseEvent support
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	private float[] mouseMtx = new float[16];

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!isMouseDragging)
			return;

		Point newMouse = new Point(e.getX(), e.getY());

		if (newMouse.x != prevMouse.x || newMouse.y != prevMouse.y) {
			if (!e.isControlDown()) {
				vs.makeRotationMtx(prevMouse, newMouse, cueCenter, cueRadius,
						mouseMtx);

				rot_matrix = Matrix.multiply(rot_matrix, mouseMtx);
				rot_matrix = Matrix.multiply(rot_matrix, mouseMtx);
				fixRotationMatrix();

			} else {
				lookAtX -= (newMouse.x - prevMouse.x)
						/ (WINDOW_HEIGHT / (2 * MAX_ABS_COORDINATE))
						* (cameraDistance / 25) * (fieldOfView / 30);
				lookAtY += (newMouse.y - prevMouse.y)
						/ (WINDOW_HEIGHT / (2 * MAX_ABS_COORDINATE))
						* (cameraDistance / 25) * (fieldOfView / 30);
			}

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
		isMouseDragging = true;
		prevMouse = new Point(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isMouseDragging = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
