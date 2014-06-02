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

import configuration.Constants;
import configuration.ScaleConfiguration;
import dataReader.dataReader;

public class Visualisation extends GLCanvas implements Constants,
		GLEventListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	private static GLU glu = new GLU();

	private static int cueRadius;
	private static Point prevMouse;
	private static Point cueCenter = new Point();
	private static double screenRatio = DEFAULT_SCREEN_RATIO;
	private static boolean isMouseDragging = false;
	private static float rot_matrix[] = Matrix.identity();
	private static VirtualSphere vs = new VirtualSphere();

	private static Visualisation visualisation = null;
	private static List<point> pointsList = null;
	private static ScaleConfiguration sc = null;
	private static dataReader dr = null;
	private static File file = null;
	private static boolean isSetToOrigin = DEFAULT_IS_SET_TO_ORIGIN;
	private static boolean isAxesVisible = DEFAULT_IS_AXES_VISIBLE;
	private static double[] centerOfMass;
	private static double scaleFactor;
	private static double radius;
	private static double curvature;
	private static double defaultRadius;
	private static double selectedCurMax = DEFAULT_MAX_SELECTED_CURVATURE;
	private static double selectedCurMin = DEFAULT_MIN_SELECTED_CURVATURE;
	private static double cameraDistance = DEFAULT_CAMERA_DISTANCE;
	private static double fieldOfView = DEFAULT_FIELD_OF_VIEW;
	private static double lookAtX = DEFAULT_LOOK_AT_POINT_X;
	private static double lookAtY = DEFAULT_LOOK_AT_POINT_Y;
	private static int window_height = DEFAULT_WINDOW_HEIGHT;
	private static int window_width = DEFAULT_WINDOW_WIDTH;

	private static JSlider cameraDistanceSlider = null;
	private static JSlider fieldOfViewSlider = null;
	private static JSlider radiusJSlider = null;
	private static JCheckBox setChooseCurvatureCheckBox = null;
	private static JSlider curvatureJSlider = null;
	private static JCheckBox setToOriginCheckBox = null;
	private static JCheckBox setAxeVisibleCheckBox = null;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final GLCanvas canvas = getVisualisationInstance();
				final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

				initJFrame(canvas, animator);
			}
		});
	}

	public static JPanel initLeftPanel(final GLCanvas canvas) {
		final JPanel leftJPanel = new JPanel();
		leftJPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH,
				LEFT_PANEL_HEIGHT));
		leftJPanel.setLayout(new GridLayout(LEFT_PANEL_LAYOUT_ROW,
				LEFT_PANEL_LAYOUT_COLUMN));

		leftJPanel.add(configCameraDistanceSlider());
		leftJPanel.add(configFieldOfViewSlider());
		leftJPanel.add(configRadiusSlider());
		leftJPanel.add(configCurvatureSlider());
		leftJPanel.add(configCheckbox());
		leftJPanel.add(configFileChooser(canvas));

		return leftJPanel;
	}

	public static JPanel configCameraDistanceSlider() {
		final JLabel cameraDistanceJLabel = new JLabel("  Camera Distance");
		final JLabel cameraDistanceValueJLabel = new JLabel(String.format(
				"%.2f", DEFAULT_CAMERA_DISTANCE));
		cameraDistanceSlider = initSlider();
		cameraDistanceSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int v = source.getValue();
				if (v < DEFAULT_SLIDER_VALUE) {
					cameraDistance = DEFAULT_CAMERA_DISTANCE * 10.0
							/ (DEFAULT_SLIDER_VALUE + 10.0 - v);
				} else {
					cameraDistance = DEFAULT_CAMERA_DISTANCE
							* (v - DEFAULT_SLIDER_VALUE + 10.0) / 10.0;
				}
				cameraDistanceValueJLabel.setText(String.format("%.2f",
						cameraDistance));
			}
		});
		JPanel cameraDistanceValueJPanel = new JPanel(new BorderLayout());
		cameraDistanceValueJPanel
				.add(cameraDistanceSlider, BorderLayout.CENTER);
		cameraDistanceValueJPanel.add(cameraDistanceValueJLabel,
				BorderLayout.EAST);
		cameraDistanceValueJLabel.setPreferredSize(DEFAULT_JLABEL_DIMENSION);
		JPanel cameraDistanceJPanel = new JPanel(defaultLayout);
		cameraDistanceJPanel.add(cameraDistanceJLabel);
		cameraDistanceJPanel.add(cameraDistanceValueJPanel);

		return cameraDistanceJPanel;
	}

	public static JPanel configFieldOfViewSlider() {
		final JLabel fieldOfViewJLabel = new JLabel("  Field Of View");
		final JLabel fieldOfViewValueJLabel = new JLabel(String.format("%.2f",
				DEFAULT_FIELD_OF_VIEW));
		fieldOfViewSlider = initSlider();
		fieldOfViewSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int v = source.getValue();

				if (v < DEFAULT_SLIDER_VALUE) {
					fieldOfView = DEFAULT_FIELD_OF_VIEW * 10.0
							/ (DEFAULT_SLIDER_VALUE + 10.0 - v);
				} else {
					fieldOfView = DEFAULT_FIELD_OF_VIEW
							* (v - DEFAULT_SLIDER_VALUE + 10.0) / 10.0;
				}
				fieldOfViewValueJLabel.setText(String.format("%.2f",
						fieldOfView));
			}
		});
		JPanel fieldOfViewValueJPanel = new JPanel(new BorderLayout());
		fieldOfViewValueJPanel.add(fieldOfViewSlider, BorderLayout.CENTER);
		fieldOfViewValueJPanel.add(fieldOfViewValueJLabel, BorderLayout.EAST);
		fieldOfViewValueJLabel.setPreferredSize(DEFAULT_JLABEL_DIMENSION);
		JPanel fieldOfViewJPanel = new JPanel(defaultLayout);
		fieldOfViewJPanel.add(fieldOfViewJLabel);
		fieldOfViewJPanel.add(fieldOfViewValueJPanel);

		return fieldOfViewJPanel;
	}

	public static JPanel configRadiusSlider() {
		final JLabel radiusJLabel = new JLabel("  Point Radius");
		final JLabel radiusValueJLabel = new JLabel(String.format("%.2f",
				radius));
		radiusJSlider = initSlider();
		radiusJSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int v = source.getValue();

				if (v < DEFAULT_SLIDER_VALUE) {
					radius = defaultRadius * 10.0
							/ (DEFAULT_SLIDER_VALUE + 10.0 - v);
				} else {
					radius = defaultRadius * (v - DEFAULT_SLIDER_VALUE + 10.0)
							/ 10.0;
				}
				radiusValueJLabel.setText(String.format("%.2f", radius));
			}
		});
		JPanel radiusValueJPanel = new JPanel(new BorderLayout());
		radiusValueJLabel.setPreferredSize(DEFAULT_JLABEL_DIMENSION);
		radiusValueJPanel.add(radiusJSlider, BorderLayout.CENTER);
		radiusValueJPanel.add(radiusValueJLabel, BorderLayout.EAST);
		JPanel radiusJPanel = new JPanel(defaultLayout);
		radiusJPanel.add(radiusJLabel);
		radiusJPanel.add(radiusValueJPanel);

		return radiusJPanel;
	}

	public static JPanel configCurvatureSlider() {
		setChooseCurvatureCheckBox = new JCheckBox(
				"Enable Selection Of Curvature");
		final JLabel curvatureValueJLabel = new JLabel(String.format("%.2f",
				DEFAULT_SELECTED_CURVATURE));
		curvatureJSlider = initCurvatureSlider();
		curvatureJSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int v = source.getValue();

				curvature = (double) v / DEFAULT_SLIDER_MAX;
				selectedCurMin = curvature - DEFAULT_PRECISION;
				selectedCurMax = curvature + DEFAULT_PRECISION;

				curvatureValueJLabel.setText(String.format("%.2f", curvature));
			}
		});
		JPanel curvatureValueJPanel = new JPanel(new BorderLayout());
		curvatureValueJLabel.setPreferredSize(DEFAULT_JLABEL_DIMENSION);
		curvatureValueJPanel.add(curvatureJSlider, BorderLayout.CENTER);
		curvatureValueJPanel.add(curvatureValueJLabel, BorderLayout.EAST);
		JPanel curvatureJPanel = new JPanel(defaultLayout);
		curvatureJPanel.add(setChooseCurvatureCheckBox);
		curvatureJPanel.add(curvatureValueJPanel);

		setChooseCurvatureCheckBox.setSelected(DEFAULT_IS_SELECTING_CURVATURE);
		setChooseCurvatureCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				curvatureJSlider.setEnabled(abstractButton.isSelected());
				if (!abstractButton.isSelected()) {
					selectedCurMin = DEFAULT_MIN_SELECTED_CURVATURE;
					selectedCurMax = DEFAULT_MAX_SELECTED_CURVATURE;
				}
			}
		});

		return curvatureJPanel;
	}

	public static JPanel configCheckbox() {
		setToOriginCheckBox = new JCheckBox("Set Center To Origin");
		setToOriginCheckBox.setSelected(DEFAULT_IS_SET_TO_ORIGIN);
		setToOriginCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				isSetToOrigin = abstractButton.isSelected();
			}
		});
		setAxeVisibleCheckBox = new JCheckBox("Show Axes");
		setAxeVisibleCheckBox.setSelected(DEFAULT_IS_AXES_VISIBLE);
		setAxeVisibleCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				isAxesVisible = abstractButton.isSelected();
			}
		});

		JPanel checkboxJPanel = new JPanel(defaultLayout);
		checkboxJPanel.add(setToOriginCheckBox);
		checkboxJPanel.add(setAxeVisibleCheckBox);

		return checkboxJPanel;
	}

	public static JPanel configFileChooser(final GLCanvas canvas) {
		final JPanel fileChooserRowJPanel = new JPanel(new GridLayout(
				DEFAULT_LAYOUT_ROW, DEFAULT_LAYOUT_COLUMN,
				DEFAULT_LAYOUT_H_GAP, FILECHOOSER_LAYOUT_V_GAP));
		final JPanel fileChooserJPanel = new JPanel(new BorderLayout());
		final JLabel fileJLabel = new JLabel("No File Chosen");
		JButton openButton = new JButton("Choose File...");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Point Cloud Data Format", "pcd");
				fileChooser.setFileFilter(filter);
				fileChooser.setCurrentDirectory(new File("./"));
				int rVal = fileChooser.showOpenDialog(canvas);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();
					fileJLabel.setText(file.getName());
				}
			}
		});
		JButton buildButton = new JButton("Build");
		buildButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				initDataReader(file);
			}
		});
		fileChooserJPanel.add(openButton, BorderLayout.WEST);
		fileChooserJPanel.add(fileJLabel, BorderLayout.CENTER);
		fileChooserRowJPanel.add(fileChooserJPanel);
		fileChooserRowJPanel.add(buildButton);

		return fileChooserRowJPanel;
	}

	public static JPanel configMainJPanel(GLCanvas canvas) {
		final JPanel mainJPanel = new JPanel();
		mainJPanel.setLayout(new BorderLayout());
		mainJPanel.add(initLeftPanel(canvas), BorderLayout.WEST);
		mainJPanel.add(canvas, BorderLayout.CENTER);

		return mainJPanel;
	}

	public static void initJFrame(final GLCanvas canvas,
			final FPSAnimator animator) {
		final JFrame frame = new JFrame();
		frame.setContentPane(configMainJPanel(canvas));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		frame.setPreferredSize(new Dimension(DEFAULT_WINDOW_WIDTH,
				DEFAULT_WINDOW_HEIGHT));
		frame.pack();
		frame.setVisible(true);
		animator.start();
	}

	public static JSlider initSlider() {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, DEFAULT_SLIDER_MIN,
				DEFAULT_SLIDER_MAX, DEFAULT_SLIDER_VALUE);
		slider.setMajorTickSpacing(DEFAULT_MAJOR_TICK_SPACING);
		slider.setMinorTickSpacing(DEFAULT_MINOR_TICK_SPACING);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		int numberOfTick = DEFAULT_NUMBER_OF_TICK;

		int spacing = (DEFAULT_SLIDER_MAX - DEFAULT_SLIDER_MIN)
				/ (numberOfTick - 1);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		for (int i = 0; i < numberOfTick / 2; i++) {
			labelTable.put(
					new Integer(i * spacing),
					new JLabel(String
							.format("1/%d", (numberOfTick / 2) - i + 1)));
		}
		labelTable
				.put(new Integer(numberOfTick / 2 * spacing), new JLabel("1"));
		for (int i = numberOfTick / 2 + 1; i <= numberOfTick; i++) {
			labelTable.put(new Integer(i * spacing),
					new JLabel(String.format("%d", i - numberOfTick / 2 + 1)));
		}
		slider.setLabelTable(labelTable);

		return slider;
	}

	public static JSlider initCurvatureSlider() {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, DEFAULT_SLIDER_MIN,
				DEFAULT_SLIDER_MAX, DEFAULT_SLIDER_VALUE);
		slider.setMajorTickSpacing(DEFAULT_MAJOR_TICK_SPACING);
		slider.setMinorTickSpacing(DEFAULT_MINOR_TICK_SPACING);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setEnabled(DEFAULT_IS_SELECTING_CURVATURE);

		int numberOfTick = DEFAULT_NUMBER_OF_TICK_CURVATURE;

		int spacing = (DEFAULT_SLIDER_MAX - DEFAULT_SLIDER_MIN)
				/ (numberOfTick - 1);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		for (int i = 0; i < numberOfTick; i++) {
			labelTable.put(
					new Integer(i * spacing),
					new JLabel(String.format("%.1f", 1.0 * i * spacing
							/ DEFAULT_SLIDER_MAX)));
		}
		slider.setLabelTable(labelTable);

		return slider;
	}

	private Visualisation() {
		addGLEventListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.requestFocusInWindow();
	}

	public static Visualisation getVisualisationInstance() {
		if (visualisation == null)
			visualisation = new Visualisation();
		return visualisation;
	}

	public static void reset() {
		setAxeVisibleCheckBox.setSelected(DEFAULT_IS_AXES_VISIBLE);
		isAxesVisible = DEFAULT_IS_AXES_VISIBLE;
		setToOriginCheckBox.setSelected(DEFAULT_IS_SET_TO_ORIGIN);
		isSetToOrigin = DEFAULT_IS_SET_TO_ORIGIN;
		fieldOfViewSlider.setValue(DEFAULT_SLIDER_VALUE);
		fieldOfView = DEFAULT_FIELD_OF_VIEW;
		setChooseCurvatureCheckBox.setSelected(DEFAULT_IS_SELECTING_CURVATURE);
		curvatureJSlider.setValue(DEFAULT_SLIDER_VALUE);
		curvatureJSlider.setEnabled(DEFAULT_IS_SELECTING_CURVATURE);
		selectedCurMin = DEFAULT_MIN_SELECTED_CURVATURE;
		selectedCurMax = DEFAULT_MAX_SELECTED_CURVATURE;
		cameraDistanceSlider.setValue(DEFAULT_SLIDER_VALUE);
		cameraDistance = DEFAULT_CAMERA_DISTANCE;

		// reset look at point for camera
		lookAtX = DEFAULT_LOOK_AT_POINT_X;
		lookAtY = DEFAULT_LOOK_AT_POINT_Y;
	}

	public static void initDataReader(File file) {
		if (file == null)
			return;
		dr = new dataReader(file);
		pointsList = dr.getPoints();
		sc = new ScaleConfiguration(pointsList, 10);
		scaleFactor = sc.getScaleFactor();
		defaultRadius = radius = sc.getRadius()
				* (window_height / DEFAULT_MAX_ABS_COORIDINATE);
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

			if (p.getType() == DataType.XYZRGB) {
				int[] color = p.parseRGB();
				gl.glColor3d(color[0], color[1], color[2]);
			} else if (p.getType() != DataType.XYZC
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
		float cylinderRadius = (float) (0.1 * (Math.pow(cameraDistance
				/ DEFAULT_CAMERA_DISTANCE, 1.0 / 2) * (fieldOfView / DEFAULT_FIELD_OF_VIEW)));
		float cylinderHeight = (float) (3 * DEFAULT_MAX_ABS_COORIDINATE);
		int slices = DEFAULT_CYLINDER_SLICE;
		int stacks = DEFAULT_CYLINDER_STACK;
		GLUquadric body = glu.gluNewQuadric();

		gl.glPushMatrix();
		gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
		gl.glTranslatef(0.0f, 0.0f, -cylinderHeight / 2);
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight,
				slices, stacks);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -cylinderHeight / 2);
		gl.glColor3f(0.0f, 0.0f, 1.0f);
		glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight,
				slices, stacks);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(-cylinderHeight / 2, 0.0f, 0.0f);
		gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight,
				slices, stacks);
		gl.glPopMatrix();
	}

	/**
	 * This method is called when the OpenGL display needs to be redrawn.
	 */
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.8F, 0.8F, 0.8F, 1.0F);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective(fieldOfView, screenRatio, DEFAULT_CAMERA_NEAR_CLIP,
				DEFAULT_CAMERA_FAR_CLIP);
		glu.gluLookAt(0, 0, cameraDistance, lookAtX, lookAtY, 0, 0, 1, 0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		gl.glPushMatrix();
		gl.glMultMatrixf(rot_matrix, 0);

		if (pointsList != null)
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

		Matrix.rotateY(Matrix.deg2Rad(DEFAULT_CAMERA_ANGLE_X), rot_matrix);
		Matrix.rotateX(Matrix.deg2Rad(DEFAULT_CAMERA_ANGLE_Y), rot_matrix);

		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.8F, 0.8F, 0.8F, 1.0F);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
	}

	/**
	 *
	 * Called when the size of the GLJPanel changes. Note:
	 * glViewport(x,y,width,height) has already been called before this method
	 * is called!
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		window_height = height;
		window_width = width;

		screenRatio = (float) window_width / (float) window_height;

		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective(fieldOfView, screenRatio, DEFAULT_CAMERA_NEAR_CLIP,
				DEFAULT_CAMERA_FAR_CLIP);
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
			if (SwingUtilities.isLeftMouseButton(e)) {
				vs.makeRotationMtx(prevMouse, newMouse, cueCenter, cueRadius,
						mouseMtx);

				rot_matrix = Matrix.multiply(rot_matrix, mouseMtx);
				rot_matrix = Matrix.multiply(rot_matrix, mouseMtx);
				fixRotationMatrix();

			} else if (SwingUtilities.isRightMouseButton(e)) {
				lookAtX -= (newMouse.x - prevMouse.x)
						/ (window_height / (2 * DEFAULT_MAX_ABS_COORIDINATE))
						* (cameraDistance / DEFAULT_CAMERA_DISTANCE)
						* (fieldOfView / DEFAULT_FIELD_OF_VIEW);
				lookAtY += (newMouse.y - prevMouse.y)
						/ (window_height / (2 * DEFAULT_MAX_ABS_COORIDINATE))
						* (cameraDistance / DEFAULT_CAMERA_DISTANCE)
						* (fieldOfView / DEFAULT_FIELD_OF_VIEW);
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
		if (isMouseDragging)
			return;
		isMouseDragging = true;
		prevMouse = new Point(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!isMouseDragging)
			return;
		isMouseDragging = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
