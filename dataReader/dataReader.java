package dataReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import point.point;

public class dataReader {
	private List<point> points = null;

	public dataReader(String filename) {
		openFile(new File(filename));
	}

	public dataReader(File file) {
		openFile(file);
	}

	public List<point> getPoints() {
		return points;
	}

    private void openFile(File file)
    {
        points = new ArrayList<point>();;
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
                
                if (coordinates.length == 6) {
                    float normal_x = (float)Double.parseDouble(coordinates[3]);
                    float normal_y = (float)Double.parseDouble(coordinates[4]);
                    float normal_z = (float)Double.parseDouble(coordinates[5]);
                    points.add(new point(x, y, z, normal_x, normal_y, normal_z));
                } else if (coordinates.length == 4) {
                    float color = (float)Double.parseDouble(coordinates[3]);
                    points.add(new point(x, y, z, color));   
                } else {
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
