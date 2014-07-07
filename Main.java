import com.jogamp.opengl.util.FPSAnimator;
import configuration.Constants;
import configuration.ScaleConfiguration;
import dataReader.dataReader;
import point.point;
import visualisation.Visualisation;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;

/**
 * Created by a0105529 on 7/4/14.
 */
public class Main implements Constants{
    private static JSlider cameraDistanceSlider = null;
    private static JSlider fieldOfViewSlider = null;
    private static JSlider radiusJSlider = null;
    private static JCheckBox setChooseCurvatureCheckBox = null;
    private static JSlider curvatureJSlider = null;
    private static JCheckBox setToOriginCheckBox = null;
    private static JCheckBox setAxeVisibleCheckBox = null;
    private static JCheckBox setNormalVisibleCheckBox = null;

    private static double selectedCurMax = DEFAULT_MAX_SELECTED_CURVATURE;
    private static double selectedCurMin = DEFAULT_MIN_SELECTED_CURVATURE;
    private static double cameraDistance = DEFAULT_CAMERA_DISTANCE;
    private static double fieldOfView = DEFAULT_FIELD_OF_VIEW;
    private static double curvaturePrecision = DEFAULT_PRECISION;
    private static int window_height = DEFAULT_WINDOW_HEIGHT;
    private static int window_width = DEFAULT_WINDOW_WIDTH;
    private static double radius;
    private static double curvature;

    private static File file = null;
    private static dataReader dr = null;
    private static ScaleConfiguration sc = null;

    private static final Visualisation canvas = Visualisation.getVisualisationInstance();

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //final GLCanvas canvas = Visualisation.getVisualisationInstance();
                final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

                initJFrame(canvas, animator);
            }
        });
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

    public static JPanel configMainJPanel(GLCanvas canvas) {
        final JPanel mainJPanel = new JPanel();
        mainJPanel.setLayout(new BorderLayout());
        mainJPanel.add(initLeftPanel(canvas), BorderLayout.WEST);
        mainJPanel.add(canvas, BorderLayout.CENTER);

        return mainJPanel;
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
        leftJPanel.add(configSetCurvaturePrecision());
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
                canvas.setCameraDistance(cameraDistance);
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
                    radius = sc.getRadius() * 10.0
                            / (DEFAULT_SLIDER_VALUE + 10.0 - v);
                } else {
                    radius = sc.getRadius() * (v - DEFAULT_SLIDER_VALUE + 10.0)
                            / 10.0;
                }
                canvas.setRadius(radius);
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
                selectedCurMin = curvature - curvaturePrecision;
                selectedCurMax = curvature + curvaturePrecision;

                canvas.setCurvature(curvature);
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

    public static JPanel configSetCurvaturePrecision(){
        JLabel label = new JLabel("  Set Curvature Precision(0~1)");

        JPanel curvaturePrecisionJPanel = new JPanel(new BorderLayout());
        final JTextField curvatureTextField = new JTextField(String.format("%.2f", curvaturePrecision), 5);
        final JButton setPrecisionJButton = new JButton("update");
        setPrecisionJButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                double newVal = Double.parseDouble(curvatureTextField.getText());
                if (newVal >= 0 && newVal < 1) {
                    curvaturePrecision = newVal;
                }
                curvatureTextField.setText(String.format("%.2f", curvaturePrecision));
            }
        });
        curvaturePrecisionJPanel.add(curvatureTextField, BorderLayout.CENTER);
        curvaturePrecisionJPanel.add(setPrecisionJButton, BorderLayout.EAST);

        JPanel jPanel = new JPanel(new GridLayout(3, 1));
        jPanel.add(new JLabel("-------------------------------"));
        jPanel.add(label, 1);
        jPanel.add(curvaturePrecisionJPanel, 2);

        return jPanel;
    }

    public static JPanel configCheckbox() {
        setToOriginCheckBox = new JCheckBox("Set Center To Origin");
        setToOriginCheckBox.setSelected(DEFAULT_IS_SET_TO_ORIGIN);
        setToOriginCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                canvas.setIsSetToOrigin(abstractButton.isSelected());
            }
        });
        setAxeVisibleCheckBox = new JCheckBox("Show Axes");
        setAxeVisibleCheckBox.setSelected(DEFAULT_IS_AXES_VISIBLE);
        setAxeVisibleCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                canvas.setIsAxesVisible(abstractButton.isSelected());
            }
        });

        setNormalVisibleCheckBox = new JCheckBox("Show Normal Vectors");
        setNormalVisibleCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                canvas.setIsNormalVectorVisible(abstractButton.isSelected());
            }
        });

        JPanel checkboxJPanel = new JPanel(new GridLayout(3, 1));
        checkboxJPanel.add(setToOriginCheckBox);
        checkboxJPanel.add(setNormalVisibleCheckBox);
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

    public static void initDataReader(File file) {
        if (file == null)
            return;
        dr = new dataReader(file);
        canvas.setPointsList(dr.getPoints());
        sc = new ScaleConfiguration(dr.getPoints(), 10);
        canvas.setScaleFactor(sc.getScaleFactor());
        canvas.setRadius(sc.getRadius() * (window_height / DEFAULT_MAX_ABS_COORIDINATE));
        canvas.setDefaultRadius(sc.getRadius() * (window_height / DEFAULT_MAX_ABS_COORIDINATE));
        canvas.setCenterOfMass(sc.getCenterOfMass());
    }

    public static void reset() {
        setAxeVisibleCheckBox.setSelected(DEFAULT_IS_AXES_VISIBLE);
        canvas.setIsSetToOrigin(DEFAULT_IS_SET_TO_ORIGIN);
        canvas.setIsAxesVisible(DEFAULT_IS_AXES_VISIBLE);
        canvas.setIsNormalVectorVisible(DEFAULT_IS_NORMAL_VECTOR_VISIBLE);
        setToOriginCheckBox.setSelected(DEFAULT_IS_SET_TO_ORIGIN);
        fieldOfViewSlider.setValue(DEFAULT_SLIDER_VALUE);
        fieldOfView = DEFAULT_FIELD_OF_VIEW;
        setChooseCurvatureCheckBox.setSelected(DEFAULT_IS_SELECTING_CURVATURE);
        setNormalVisibleCheckBox.setSelected(DEFAULT_IS_NORMAL_VECTOR_VISIBLE);
        curvatureJSlider.setValue(DEFAULT_SLIDER_VALUE);
        curvatureJSlider.setEnabled(DEFAULT_IS_SELECTING_CURVATURE);
        selectedCurMin = DEFAULT_MIN_SELECTED_CURVATURE;
        selectedCurMax = DEFAULT_MAX_SELECTED_CURVATURE;
        cameraDistanceSlider.setValue(DEFAULT_SLIDER_VALUE);
        cameraDistance = DEFAULT_CAMERA_DISTANCE;
        curvaturePrecision = DEFAULT_PRECISION;

        // reset look at point for camera
        canvas.setLookAt(DEFAULT_LOOK_AT_POINT_X, DEFAULT_LOOK_AT_POINT_Y);
    }
}
