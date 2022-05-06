package ca.ulaval.glo2004.ddd;

/**
 * Represent a vector for 3d representation.
 */
public class Vector3D {
    double x = 0, y = 0, z = 0;

    /**
     * Create a vector for 3d representation.
     *
     * @param _x X position.
     * @param _y Y position.
     * @param _z Z position.
     */
    public Vector3D(double _x, double _y, double _z) {
        double length = Math.sqrt(_x * _x + _y * _y + _z * _z);
        if (length >= 0) {
            x = _x / length;
            y = _y / length;
            z = _z / length;
        }
    }

    /**
     * Get a vector perpendicular to this vector and another vector
     *
     * @param _vector3D Second vector
     * @return Perpendicular vector.
     */
    Vector3D crossProduct(Vector3D _vector3D) {
        return new Vector3D(
                y * _vector3D.z - z * _vector3D.y,
                z * _vector3D.x - x * _vector3D.z,
                x * _vector3D.y - y * _vector3D.x);
    }
}
