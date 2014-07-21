package configuration;

import point.point;

import java.util.Collections;
import java.util.List;

/**
 *  class ScaleConfiguration
 *  Calculate configurations like scale factor, radius, canter of mass, etc, based on the 
 *  points given
 */
public class ScaleConfiguration {
    private final double cameraDistance = -40;
    private final double fieldOfView = 35;

    private List<point> pointsList = null;

    // masAbsCoor is the actual coordinates shown on the screen
    // by default, x, y, z are all from -10 to 10
    private double maxAbsCoor = 0;

    // how much the points coordinates should be scaled to be displayed properly
    private double scaleFactor = 0;


    private double radius = 0;
    private double[] centerOfMass = new double[3];
    private double[] movedCenterOfMass = new double[3];

    /**
     * constructor 
     * @param pointsList    the points to be visualised 
     * @param maxAbsCoor    the max coordinates of the visualisation tool (default -10 to 10)
     */
    public ScaleConfiguration(List<point> pointsList, double maxAbsCoor) {
        this.pointsList = pointsList;
        Collections.sort(this.pointsList);

        this.maxAbsCoor = maxAbsCoor;
        this.scaleFactor = calculateScaleFactor();
        this.radius = (calculateMinDis(0, pointsList.size() - 1) / 2)
                * scaleFactor;

        this.centerOfMass = calculateCenterOfMass();
        this.movedCenterOfMass = centerOfMass;
    }

    /**
     * @return the scale factor of the points on visualisation tool
     */
    public double getScaleFactor() {
        return this.scaleFactor;
    }

    /**
     * @return the center of mass of given points
     */
    public double[] getCenterOfMass() {
        return this.movedCenterOfMass;
    }

    /**
     * calculate the center of mass of given points
     * @return center of mass
     */
    private double[] calculateCenterOfMass() {
        double sumX, sumY, sumZ;
        double[] center = new double[3];
        sumX = sumY = sumZ = 0;

        for (point p : pointsList) {
            sumX += p.getX();
            sumY += p.getY();
            sumZ += p.getZ();
        }

        double size = (double) pointsList.size();
        center[0] = scaleFactor * sumX / size;
        center[1] = scaleFactor * sumY / size;
        center[2] = scaleFactor * sumZ / size;

        return center;
    }

    /**
     * calculate the scale factor based on given points
     * @return the scale factor
     */
    private double calculateScaleFactor() {
        double max = 0.0;
        for (int i = 0; i < pointsList.size(); i++) {
            max = Math.max(max, Math.abs(pointsList.get(i).getX()));
            max = Math.max(max, Math.abs(pointsList.get(i).getY()));
            max = Math.max(max, Math.abs(pointsList.get(i).getZ()));
        }

        return maxAbsCoor / max;
    }

    /**
     * @return radius of points to be visualised
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * This method is approximated in 3 dimensions.
     * @param start start index of point
     * @param end end index of point
     * @return minimum distance of any two points from index start to end
     */
    private double calculateMinDis(int start, int end) {
        if (start >= end)
            return 0;
        else {
            int middle = (start + end) / 2;
            double d1 = calculateMinDis(start, middle);
            double d2 = calculateMinDis(middle + 1, end);
            double d3 = Double.MAX_VALUE;

            double end_op = (end - middle - 1 > 6) ? middle + 7 : end;

            for (int i = (middle - start > 6) ? middle - 6 : start; i <= middle; i++)
                for (int j = middle + 1; j <= end_op; j++)
                    if (pointsList.get(i).getX() - pointsList.get(j).getX() <= d3) {
                        double dis = pointsList.get(i).disTo(pointsList.get(j));
                        if (dis > 0)
                            d3 = Math.min(d3, dis);
                    }

            double minD = Double.MAX_VALUE;
            if (d1 > 0)
                minD = Math.min(minD, d1);
            if (d2 > 0)
                minD = Math.min(minD, d2);
            if (d3 > 0)
                minD = Math.min(minD, d3);

            return minD;
        }
    }

    /**
     * @return the camera distance 
     */
    public double getCameraDistance() {
        return this.cameraDistance;
    }

    /** 
     * @return the field of view of camera
     */
    public double getFieldOfView() {
        return this.fieldOfView;
    }
}
