###3D Visualisation Tool (OpenGL)

####Overview
This visualisation tool is for Point Cloud Research Project, to help visualise 3d objects and debug. It's based on Java Swing and JOGL.

####Screen Shot
![visualisation tool img](https://raw.githubusercontent.com/pan-long/3DPointVisualisationWithOpenGL/master/img.png)

####Support Point Format
- x y z
- x y z rgb
- x y z curvature
- x y z normalX normalY normalZ
- x y z curvature normalX normalY normalZ

####System Requirement
- this tool is developed and tested under `jdk 1.6` && `jogl`
- known issue: UI layout got problems under `jdk 1.7`

####Notice
This tool is only used to visualise 3d points. It DOES NOT "calculate" the normal vector, curvature or any additional information not provided in the input file.

####Contact Us
Pan Long: aga.panlong@me.com
Tang Ning: nus-tn@hotmail.com
