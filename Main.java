import com.jogamp.opengl.util.FPSAnimator;
import configuration.Constants;
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

/**
 * Created by a0105529 on 7/4/14.
 */
public class Main implements Constants{
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final GLCanvas canvas = Visualisation.getVisualisationInstance();
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
                selectedCurMin = curvature - curvaturePrecision;
                selectedCurMax = curvature + curvaturePrecision;

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

        JPanel curvaturePrecistionJPanel = new JPanel(new BorderLayout());
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
        curvaturePrecistionJPanel.add(curvatureTextField, BorderLayout.CENTER);
        curvaturePrecistionJPanel.add(setPrecisionJButton, BorderLayout.EAST);

        JPanel jPanel = new JPanel(new GridLayout(3, 1));
        jPanel.add(new JLabel("-------------------------------"));
        jPanel.add(label, 1);
        jPanel.add(curvaturePrecistionJPanel, 2);

        return jPanel;
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

        setNormalVisibleCheckBox = new JCheckBox("Show Normal Vectors");
        setNormalVisibleCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                isNormalVectorVisible = abstractButton.isSelected();
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
}
