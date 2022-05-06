package ca.ulaval.glo2004.utils;

import java.util.HashMap;

/**
 * This class contains the conversion method used in Pationator to get nominal value, inches value or metric value from
 * another value. It's also possible to get feet value from inches and the reverse.
 */
public class Conversion {
    /**
     * Coefficient used to convert inches to millimeter or the reverse.
     */
    public static final float MM_INCHES_CONVERSION_COEFFICIENT = 25.4f;
    private static final HashMap<Float, Float> actualInchesFromNominal = new HashMap<Float, Float>() {{
        put(1f, 3f / 4f);
        put(1.25f, 1f);
        put(2f, 1f + 1f / 2f);
        put(3f, 2f + 1f / 2f);
        put(4f, 3f + 1f / 2);
        put(5f, 4f + 1f / 2);
        put(6f, 5f + 1f / 2);
        put(7f, 6f + 1f / 4);
        put(8f, 7f + 1f / 4);
        put(10f, 9f + 1f / 4);
        put(12f, 11f + 1f / 4);
    }};
    private static final HashMap<Float, Float> nominalFromMillimeter = new HashMap<Float, Float>() {{
        put(19f, 1f);
        put(25f, 1.25f);
        put(38f, 2f);
        put(64f, 3f);
        put(89f, 4f);
        put(114f, 5f);
        put(140f, 6f);
        put(159f, 7f);
        put(184f, 8f);
        put(235f, 10f);
        put(286f, 12f);
    }};
    private static final HashMap<Float, Float> nominalFromActualInches = new HashMap<Float, Float>() {{
        put(3f / 4f, 1f);
        put(1f, 1.25f);
        put(1f + 1f / 2f, 2f);
        put(2f + 1f / 2f, 3f);
        put(3f + 1f / 2, 4f);
        put(4f + 1f / 2, 5f);
        put(5f + 1f / 2, 6f);
        put(6f + 1f / 4, 7f);
        put(7f + 1f / 4, 8f);
        put(9f + 1f / 4, 10f);
        put(11f + 1f / 4, 12f);
    }};
    private static final HashMap<Float, Float> millimeterFromNominal = new HashMap<Float, Float>() {{
        put(1f, 19f);
        put(1.25f, 25f);
        put(2f, 38f);
        put(3f, 64f);
        put(4f, 89f);
        put(5f, 114f);
        put(6f, 140f);
        put(7f, 159f);
        put(8f, 184f);
        put(10f, 235f);
        put(12f, 286f);
    }};

    /**
     * Get inches value from a feet value.
     *
     * @param _feet Feet value portion to convert.
     * @return Inches value corresponding the feet value specified.
     */
    public static float feetToInches(float _feet) {
        return feetToInches(_feet, 0);
    }

    /**
     * Get inches value from a feet value.
     *
     * @param _feet   Feet value portion to convert.
     * @param _inches Inches value portion to convert.
     * @return Inches value corresponding the feet and inches value specified.
     */
    public static float feetToInches(float _feet, float _inches) {
        return (12.0f * _feet) + _inches;
    }

    /**
     * Get feet value from an inches value.
     *
     * @param _inches Inches value to convert.
     * @return Feet value.
     */
    public static float inchesToFeet(float _inches) {
        return _inches / 12.0f;
    }

    /**
     * Get inches from a nominal value.
     *
     * @param _nominalValue Value in nominal inches.
     * @return Inches value.
     */
    public static float getActualInchesFromNominal(float _nominalValue) {
        return actualInchesFromNominal.containsKey(_nominalValue) ? actualInchesFromNominal.get(_nominalValue) : 0;
    }

    /**
     * Get nominal value from millimeter value.
     *
     * @param _millimeter Value in millimeter.
     * @return Nominal value
     */
    public static float getNominalFromMillimeter(float _millimeter) {
        return nominalFromMillimeter.containsKey(_millimeter) ? nominalFromMillimeter.get(_millimeter) : 0;
    }

    /**
     * Get nominal value from inches value.
     *
     * @param _inchesValue Value in inches.
     * @return Nominal value
     */
    public static float getNominalFromActualInches(float _inchesValue) {
        return nominalFromActualInches.containsKey(_inchesValue) ? nominalFromActualInches.get(_inchesValue) : 0;
    }

    /**
     * Get millimeter from inches value.
     *
     * @param _inchesValue Value in inches.
     * @return Millimeter value.
     */
    public static float getMillimeterFromActual(float _inchesValue) {
        return _inchesValue * MM_INCHES_CONVERSION_COEFFICIENT;
    }

    /**
     * Get millimeter from a nominal value.
     *
     * @param _nominalValue Value in nominal inches.
     * @return Millimeter value.
     */
    public static float getMillimeterFromNominal(float _nominalValue) {
        return millimeterFromNominal.containsKey(_nominalValue) ? millimeterFromNominal.get(_nominalValue) : 0;
    }
}
