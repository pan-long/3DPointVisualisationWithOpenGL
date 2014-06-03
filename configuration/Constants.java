package configuration;

import java.awt.Dimension;
import java.awt.GridLayout;

public interface Constants {
	static final String TITLE = "3D Visualisation Tool";
	static final int DEFAULT_WINDOW_WIDTH = 1000;
	static final int DEFAULT_WINDOW_HEIGHT = 685;
	static final Dimension DEFAULT_JLABEL_DIMENSION = new Dimension(50, 20);
	static final double DEFAULT_CAMERA_DISTANCE = 25;
	static final double DEFAULT_FIELD_OF_VIEW = 30;
	static final double DEFAULT_SCREEN_RATIO = (double) DEFAULT_WINDOW_WIDTH
			/ (double) DEFAULT_WINDOW_HEIGHT;
	static final double DEFAULT_MAX_ABS_COORIDINATE = 10;
	static final double DEFAULT_PRECISION = 0.05;
	static final double DEFAULT_MAX_SELECTED_CURVATURE = 1;
	static final double DEFAULT_MIN_SELECTED_CURVATURE = 0;
	static final double DEFAULT_SELECTED_CURVATURE = 0.5;
	static final double DEFAULT_LOOK_AT_POINT_X = 0;
	static final double DEFAULT_LOOK_AT_POINT_Y = 0;
	static final double DEFAULT_CAMERA_NEAR_CLIP = 0.1;
	static final double DEFAULT_CAMERA_FAR_CLIP = 10000;
	static final double DEFAULT_NORMAL_VECTOR_LENGTH = 20;
	static final int DEFAULT_SLIDER_MIN = 0;
	static final int DEFAULT_SLIDER_MAX = 60;
	static final int DEFAULT_SLIDER_VALUE = 30;
	static final int DEFAULT_MAJOR_TICK_SPACING = 10;
	static final int DEFAULT_MINOR_TICK_SPACING = 5;
	static final int DEFAULT_NUMBER_OF_TICK = 7;
	static final int DEFAULT_NUMBER_OF_TICK_CURVATURE = 6;
	static final int DEFAULT_CYLINDER_SLICE = 16;
	static final int DEFAULT_CYLINDER_STACK = 16;
	static final int DEFAULT_CAMERA_ANGLE_X = 30;
	static final int DEFAULT_CAMERA_ANGLE_Y = 20;
	static final int DEFAULT_LAYOUT_ROW = 2;
	static final int DEFAULT_LAYOUT_COLUMN = 1;
	static final int DEFAULT_LAYOUT_H_GAP = 0;
	static final int DEFAULT_LAYOUT_V_GAP = -8;
	static final int FILECHOOSER_LAYOUT_V_GAP = 1;
	static final int LEFT_PANEL_LAYOUT_ROW = 8;
	static final int LEFT_PANEL_LAYOUT_COLUMN = 1;
	static final int LEFT_PANEL_WIDTH = 250;
	static final int LEFT_PANEL_HEIGHT = 685;
	static final boolean DEFAULT_IS_AXES_VISIBLE = true;
	static final boolean DEFAULT_IS_SET_TO_ORIGIN = false;
	static final boolean DEFAULT_IS_SELECTING_CURVATURE = false;
	static final boolean DEFAULT_IS_NORMAL_VECTOR_VISIBLE = false;
	static final int FPS = 60;
	static final GridLayout defaultLayout = new GridLayout(DEFAULT_LAYOUT_ROW,
			DEFAULT_LAYOUT_COLUMN, DEFAULT_LAYOUT_H_GAP, DEFAULT_LAYOUT_V_GAP);
}
