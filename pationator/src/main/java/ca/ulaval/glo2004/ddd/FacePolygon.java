package ca.ulaval.glo2004.ddd;

import java.awt.*;

/**
 * Polygon object but with distance from user and WoodPiece color.
 */
public class FacePolygon {
    Polygon polygon;
    Color color;
    double distance;

    /**
     * Create a polygon
     *
     * @param _x     X position.
     * @param _y     Y position.
     * @param _color WoodPiece color
     */
    public FacePolygon(double[] _x, double[] _y, Color _color) {
        Entry.polygonsNumber++;
        polygon = new Polygon();

        for (int i = 0; i < _x.length; i++) {
            polygon.addPoint((int) _x[i], (int) _y[i]);
        }

        color = _color;
        distance = 0;
    }

    /**
     * Draw polygon on screen.
     *
     * @param _graphics Graphics object.
     */
    void drawPolygon(Graphics _graphics) {
        _graphics.setColor(color);
        _graphics.fillPolygon(polygon);
        _graphics.setColor(Color.black);
        _graphics.drawPolygon(polygon);
    }
}
