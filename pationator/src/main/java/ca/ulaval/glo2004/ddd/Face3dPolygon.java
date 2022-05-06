package ca.ulaval.glo2004.ddd;

import java.awt.*;

import static ca.ulaval.glo2004.ddd.Entry.*;

/**
 * Multiple FacePolygon to create a 3d object.
 */
public class Face3dPolygon {
    private final Color faceColor;
    private final double[] xPosition;
    private final double[] yPosition;
    private final double[] zPosition;

    private int polygonCounter;

    /**
     * Create a 3D Polygons
     *
     * @param _x     X position.
     * @param _y     Y position.
     * @param _z     Z position.
     * @param _color Polygon color.
     */
    public Face3dPolygon(double[] _x, double[] _y, double[] _z, Color _color) {
        xPosition = _x;
        yPosition = _y;
        zPosition = _z;
        faceColor = _color;

        create3dPolygon();
    }

    /**
     * Create multiple face with the 3d polygon attribute.
     */
    void create3dPolygon() {
        polygonCounter = polygonsNumber;

        drawableFace.add(polygonCounter, new FacePolygon(new double[]{}, new double[]{}, faceColor));
        drawableFace.get(polygonCounter).distance = getDistance();

        updatePolygon();
    }

    /**
     * Get polygon distance.
     *
     * @return Mean distance.
     */
    public double getDistance() {
        double totalDistance = 0;

        for (int i = 0; i < xPosition.length; i++) {
            totalDistance += getDistanceBetweenUserTarget(i);
        }

        return totalDistance / xPosition.length;
    }

    /**
     * Update polygon position according to zoom and user position.
     */
    public void updatePolygon() {
        double zoomRatio = 50;
        double dx = -zoomRatio * Calculator.calculatePositionX(viewFrom, viewTo,
                viewTo[0], viewTo[1], viewTo[2]);
        double dy = -zoomRatio * Calculator.calculatePositionY(viewFrom, viewTo,
                viewTo[0], viewTo[1], viewTo[2]);

        double[] nouveauX = new double[xPosition.length];
        double[] nouveauY = new double[yPosition.length];

        for (int i = 0; i < xPosition.length; i++) {
            nouveauX[i] = dx + (double) dimension.width / 2 + zoomRatio * Calculator.calculatePositionX(
                    viewFrom, viewTo, xPosition[i], yPosition[i], zPosition[i]);
            nouveauY[i] = dy + (double) dimension.height / 2 + zoomRatio * Calculator.calculatePositionY(
                    viewFrom, viewTo, xPosition[i], yPosition[i], zPosition[i]);
        }

        drawableFace.set(polygonCounter, new FacePolygon(nouveauX, nouveauY, faceColor));
        drawableFace.get(polygonCounter).distance = getDistance();
        polygonsNumber--;
    }

    /**
     * Get the distance between the user position and the target position.
     *
     * @param _arrayIndex Index in position array.
     * @return Distance between two position.
     */
    public double getDistanceBetweenUserTarget(int _arrayIndex) {
        return Math.sqrt(
                (viewFrom[0] - xPosition[_arrayIndex]) * (viewFrom[0] - xPosition[_arrayIndex])
                        + (viewFrom[1] - yPosition[_arrayIndex]) * (viewFrom[1] - yPosition[_arrayIndex])
                        + (viewFrom[2] - zPosition[_arrayIndex]) * (viewFrom[2] - zPosition[_arrayIndex])
        );
    }
}
