package ca.ulaval.glo2004.utils;

public class ImperialMeasure {
    private float feet;
    private float inches;

    public ImperialMeasure(float _feet, float _inches) {
        this.feet = _feet;
        this.inches = _inches;
    }

    public ImperialMeasure() {
        this(0, 0);
    }

    public float getFeet() {
        return feet;
    }

    public void setFeet(float _feet) {
        this.feet = _feet;
    }

    public float getInches() {
        return inches;
    }

    public void setInches(float _inches) {
        this.inches = _inches;
    }
}
