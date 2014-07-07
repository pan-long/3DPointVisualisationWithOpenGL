package visualisation;

import configuration.Constants;
import configuration.ScaleConfiguration;
import dataReader.dataReader;
import point.DataType;
import point.point;
import util.Matrix;
import util.VirtualSphere;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.List;

public class Visualisation extends GLCanvas implements Constants,
		GLEventListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	private static GLU glu = new GLU();

	private static int cueRadius;
	private static Point prevMouse;
	private static Point cueCenter = new Point();
	private static double screenRatio = DEFAULT_SCREEN_RATIO;
	private static double curvaturePrecision = DEFAULT_PRECISION;
	private static boolean isMouseDragging = false;
	private static float rot_matrix[] = Matrix.identity();
	private static VirtualSphere vs = new VirtualSphere();

	private static Visualisation visualisation = null;
	private static List<point> pointsList = null;
	private static ScaleConfiguration sc = null;
	private static dataReader dr = null;
	private static File file = null;
	private boolean isSetToOrigin = DEFAULT_IS_SET_TO_ORIGIN;
	private boolean isAxesVisible = DEFAULT_IS_AXES_VISIBLE;
	private boolean isNormalVectorVisible = DEFAULT_IS_NORMAL_VECTOR_VISIBLE;
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

    public void setIsSetToOrigin(boolean b) {
        isSetToOrigin = b;
    }

    public void setIsAxesVisible(boolean b) {
        isAxesVisible = b;
    }

    public void setIsNormalVectorVisible(boolean b) {
        isNormalVectorVisible = b;
    }

    public void setRadius(double r){
        radius = r;
    }

    public void setDefaultRadius(double dr) {
        defaultRadius = dr;
    }

    public void setCurvature(double c) {
        curvature = c;
    }

    public void setCameraDistance(double d) {
        cameraDistance = d;
    }

    public void setPointsList(List<point> pl) {
        pointsList = pl;
    }

    public void setScaleFactor(double s) {
        scaleFactor = s;
    }

    public void setCenterOfMass(double[] c){
        centerOfMass = c;
    }

    public void setLookAt(double x, double y){
        lookAtX = x;
        lookAtY = y;
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


    /*
	public static void reset() {
		setAxeVisibleCheckBox.setSelected(DEFAULT_IS_AXES_VISIBLE);
		isAxesVisible = DEFAULT_IS_AXES_VISIBLE;
		setToOriginCheckBox.setSelected(DEFAULT_IS_SET_TO_ORIGIN);
		isSetToOrigin = DEFAULT_IS_SET_TO_ORIGIN;
		fieldOfViewSlider.setValue(DEFAULT_SLIDER_VALUE);
		fieldOfView = DEFAULT_FIELD_OF_VIEW;
		setChooseCurvatureCheckBox.setSelected(DEFAULT_IS_SELECTING_CURVATURE);
		setNormalVisibleCheckBox.setSelected(DEFAULT_IS_NORMAL_VECTOR_VISIBLE);
		isNormalVectorVisible = DEFAULT_IS_NORMAL_VECTOR_VISIBLE;
		curvatureJSlider.setValue(DEFAULT_SLIDER_VALUE);
		curvatureJSlider.setEnabled(DEFAULT_IS_SELECTING_CURVATURE);
		selectedCurMin = DEFAULT_MIN_SELECTED_CURVATURE;
		selectedCurMax = DEFAULT_MAX_SELECTED_CURVATURE;
		cameraDistanceSlider.setValue(DEFAULT_SLIDER_VALUE);
		cameraDistance = DEFAULT_CAMERA_DISTANCE;
		curvaturePrecision = DEFAULT_PRECISION;

		// reset look at point for camera
		lookAtX = DEFAULT_LOOK_AT_POINT_X;
		lookAtY = DEFAULT_LOOK_AT_POINT_Y;
	}*/

//	public static void initDataReader(File file) {
//		if (file == null)
//			return;
//		dr = new dataReader(file);
//		pointsList = dr.getPoints();
//		sc = new ScaleConfiguration(pointsList, 10);
//		scaleFactor = sc.getScaleFactor();
//		defaultRadius = radius = sc.getRadius()
//				* (window_height / DEFAULT_MAX_ABS_COORIDINATE);
//		centerOfMass = sc.getCenterOfMass();
//	}

	public void buildPoints(GL2 gl) {
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

	public void buildNormalVector(GL2 gl) {
		gl.glLineWidth((float) radius / 2);
		gl.glBegin(GL.GL_LINES);
		if (pointsList != null)
			for (point p : pointsList) {
				if (p.getType() == DataType.XYZNORMAL
						|| p.getType() == DataType.XYZCNORMAL) {
				double[] shift = null;
				if (!isSetToOrigin)
					shift = new double[] { 0, 0, 0 };
				else {
					shift = centerOfMass;
				}

				gl.glVertex3f((float) (p.getX() * scaleFactor - shift[0]),
						(float) (p.getY() * scaleFactor - shift[1]),
						(float) (p.getZ() * scaleFactor - shift[2]));

					float[] n = p.getNormal();
					float length = (float) Math.sqrt(n[0] * n[0] + n[1] * n[1]
							+ n[2] * n[2]);
				gl.glVertex3f((float) (p.getX() * scaleFactor - shift[0] + n[0]
						/ length * DEFAULT_NORMAL_VECTOR_LENGTH * radius
						/ scaleFactor),
						(float) (p.getY() * scaleFactor - shift[1] + n[1]
 / length
						* DEFAULT_NORMAL_VECTOR_LENGTH * radius
						/ scaleFactor), (float) (p.getZ()
								* scaleFactor - shift[2] + n[2] / length
 * DEFAULT_NORMAL_VECTOR_LENGTH
								* radius / scaleFactor));
					// System.out.println(length);
					// System.out.println(radius);
				}
			}
		gl.glEnd();
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

		if (pointsList != null) {
            buildPoints(gl);
            //System.out.println(radius);
        }
		if (isAxesVisible)
			buildAxes(gl);
		if (isNormalVectorVisible)
			buildNormalVector(gl);

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

    /**
     * fix the possible errors in rotation matrix
     */
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
