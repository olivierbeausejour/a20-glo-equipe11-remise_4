package ca.ulaval.glo2004.utils;

import java.io.Serializable;

public class Vector3 implements Serializable {

    public float x, y, z;

    public Vector3() {
    }

    public Vector3(float _x, float _y, float _z) {
        x = _x;
        y = _y;
        z = _z;
    }

    public static Vector3 add(Vector3 _lhs, Vector3 _rhs) {
        return new Vector3(_lhs.x + _rhs.x, _lhs.y + _rhs.y, _lhs.z + _rhs.z);
    }

    public static Vector3 subtract(Vector3 _lhs, Vector3 _rhs) {
        return new Vector3(_lhs.x - _rhs.x, _lhs.y - _rhs.y, _lhs.z - _rhs.z);
    }

    public static Vector3 zero() {
        return new Vector3(0, 0, 0);
    }

    public boolean equals(Vector3 _rhs) {
        return x == _rhs.x && y == _rhs.y && z == _rhs.z;
    }
}
