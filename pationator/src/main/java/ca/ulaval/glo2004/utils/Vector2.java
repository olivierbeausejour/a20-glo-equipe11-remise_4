package ca.ulaval.glo2004.utils;

public class Vector2 {

    public float x,y;

    public Vector2(){}

    public Vector2(float _x, float _y) {
        x = _x;
        y = _y;
    }

    public static Vector2 add(Vector2 _lhs, Vector2 _rhs) {
        return new Vector2(_lhs.x + _rhs.x, _lhs.y + _rhs.y);
    }

    public static Vector2 subtract(Vector2 _lhs, Vector2 _rhs) {
        return new Vector2(_lhs.x - _rhs.x, _lhs.y - _rhs.y);
    }

    public static Vector2 zero() {
        return new Vector2(0, 0);
    }
}
