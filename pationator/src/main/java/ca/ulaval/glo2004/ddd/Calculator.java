package ca.ulaval.glo2004.ddd;

/**
 * Calculator for 3d drawing.
 */
public class Calculator {
    static double drawX = 0, drawY = 0;

    /**
     * Calculate the new X position.
     *
     * @param _viewFrom User position.
     * @param _viewTo   Target position.
     * @param _x        Actual x position.
     * @param _y        Actual y position.
     * @param _z        Actual z position.
     * @return New x position.
     */
    static double calculatePositionX(double[] _viewFrom, double[] _viewTo, double _x, double _y, double _z) {
        drawUpdate(_viewFrom, _viewTo, _x, _y, _z);

        return drawX;
    }

    /**
     * Update drawX and drawY value.
     *
     * @param _viewFrom User position.
     * @param _viewTo   Target position.
     * @param _x        Actual x position.
     * @param _y        Actual y position.
     * @param _z        Actual z position.
     */
    private static void drawUpdate(double[] _viewFrom, double[] _viewTo, double _x, double _y, double _z) {
        Vector3D viewVector3D = new Vector3D(
                _viewTo[0] - _viewFrom[0], _viewTo[1] - _viewFrom[1], _viewTo[2] - _viewFrom[2]);

        Vector3D rotationVector3D = getRotationVector(_viewFrom, _viewTo);
        Vector3D crossProduct1 = viewVector3D.crossProduct(rotationVector3D);
        Vector3D crossProduct2 = viewVector3D.crossProduct(crossProduct1);

        Vector3D ViewToPoint = new Vector3D(_x - _viewFrom[0], _y - _viewFrom[1], _z - _viewFrom[2]);

        double vector1 = viewVector3D.x * _viewTo[0] + viewVector3D.y * _viewTo[1] + viewVector3D.z * _viewTo[2];
        double vector2 = viewVector3D.x * _viewFrom[0] + viewVector3D.y * _viewFrom[1] + viewVector3D.z * _viewFrom[2];
        double vector3 =
                viewVector3D.x * ViewToPoint.x + viewVector3D.y * ViewToPoint.y + viewVector3D.z * ViewToPoint.z;

        double t = vector1 - vector2 / vector3;

        _x = _viewFrom[0] + ViewToPoint.x * t;
        _y = _viewFrom[1] + ViewToPoint.y * t;
        _z = _viewFrom[2] + ViewToPoint.z * t;

        if (t > 0) {
            drawX = crossProduct2.x * _x + crossProduct2.y * _y + crossProduct2.z * _z;
            drawY = crossProduct1.x * _x + crossProduct1.y * _y + crossProduct1.z * _z;
        }
    }

    /**
     * Get a vector to rotate around target position.
     *
     * @param _viewFrom User position.
     * @param _viewTo   Target position.
     * @return Vector3D with a rotation
     */
    private static Vector3D getRotationVector(double[] _viewFrom, double[] _viewTo) {
        double dx = Math.abs(_viewFrom[0] - _viewTo[0]);
        double dy = Math.abs(_viewFrom[1] - _viewTo[1]);
        double xRot, yRot;

        xRot = dy / (dx + dy);
        yRot = dx / (dx + dy);

        if (_viewFrom[1] > _viewTo[1])
            xRot = -xRot;
        if (_viewFrom[0] < _viewTo[0])
            yRot = -yRot;

        return new Vector3D(xRot, yRot, 0);
    }

    /**
     * Calculate the new Y position.
     *
     * @param _viewFrom User position.
     * @param _viewTo   Target position.
     * @param _x        Actual x position.
     * @param _y        Actual y position.
     * @param _z        Actual z position.
     * @return New y position.
     */
    static double calculatePositionY(double[] _viewFrom, double[] _viewTo, double _x, double _y, double _z) {
        drawUpdate(_viewFrom, _viewTo, _x, _y, _z);

        return drawY;
    }
}
