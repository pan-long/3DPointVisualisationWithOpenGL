package dataReader;

import point.point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * class dataReader
 * read data from pcd file and return the points data
 */
public class dataReader {
    private List<point> points = null;

    /**
     * constructor
     * @param filename  the filename of data file
     */
    public dataReader(String filename) {
        openFile(new File(filename));
    }

    /**
     * constructor
     * @param file  the file object of data file
     */
    public dataReader(File file) {
        openFile(file);
    }

    /**
     * @return the point list in the data file
     */
    public List<point> getPoints() {
        return points;
    }

    /**
     * open file and read the points 
     */
    private void openFile(File file)
    {
        points = new ArrayList<point>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int numOfPoints = 0;

            //read number of points and skip other unused header entries
            for (int i=0; i < 12; i++ ) {
                String[] temp = reader.readLine().split(" ");
                if (temp[0].equals("POINTS")) {
                    numOfPoints = Integer.parseInt(temp[1]);
                } else if (temp[0].equals("DATA")) {
                    break;
                }
            }

            for (int i = 0; i < numOfPoints; i++)
            {
                String[] coordinates = reader.readLine().split(" ");
                float x = (float)Double.parseDouble(coordinates[0]);
                float y = (float)Double.parseDouble(coordinates[1]);
                float z = (float)Double.parseDouble(coordinates[2]);

                if (coordinates.length == 7) {
                    float curvature = (float)Double.parseDouble(coordinates[3]);
                    float normal_x = (float)Double.parseDouble(coordinates[4]);
                    float normal_y = (float)Double.parseDouble(coordinates[5]);
                    float normal_z = (float)Double.parseDouble(coordinates[6]);
                    points.add(new point(x, y, z, curvature, normal_x, normal_y, normal_z));
                }
                else if (coordinates.length == 6) {
                    float normal_x = (float)Double.parseDouble(coordinates[3]);
                    float normal_y = (float)Double.parseDouble(coordinates[4]);
                    float normal_z = (float)Double.parseDouble(coordinates[5]);
                    points.add(new point(x, y, z, normal_x, normal_y, normal_z));
                }
                else if (coordinates.length == 4) {
                    float temp = (float)Double.parseDouble(coordinates[3]);
                    if (temp > 1) {
                        int color = (int)temp;
                        points.add(new point(x, y, z, color));
                    } else {
                        points.add(new point(x, y, z, temp));
                    }
                }
                else {
                    points.add(new point(x, y, z));
                }
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
