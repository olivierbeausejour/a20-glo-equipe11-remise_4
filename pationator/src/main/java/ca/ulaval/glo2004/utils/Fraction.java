package ca.ulaval.glo2004.utils;

import static com.google.common.math.IntMath.gcd;

/**
 * Represent a numeric Fraction.
 */
public class Fraction {
    private int numerator = 1;
    private int denominator = 1;

    /**
     * Create a fraction from a decimal number.
     *
     * @param _decimalNumber Decimal number.
     */
    public Fraction(float _decimalNumber) {
        String numberInString = String.valueOf(_decimalNumber);
        int digits = numberInString.length() - 1 - numberInString.indexOf('.');

        for (int i = 0; i < digits; ++i) {
            _decimalNumber *= 10;
            denominator *= 10;
        }

        int commonDenominator = gcd((int) Math.round(_decimalNumber), denominator);

        numerator = (int) _decimalNumber / commonDenominator;
        denominator /= commonDenominator;
    }

    /**
     * Create a fraction from a string.
     *
     * @param _fractionString String fraction.
     */
    public Fraction(String _fractionString) {
        this(fractionValidator(_fractionString));
    }

    /**
     * Create a fraction from an integer numerator and integer denominator.
     *
     * @param _numerator   Numerator value.
     * @param _denominator Denominator value.
     */
    public Fraction(int _numerator, int _denominator) {
        numerator = _numerator;
        denominator = _denominator;
    }

    /**
     * Get numeric value from a fraction string.
     *
     * @param _text String representing the fraction.
     * @return String representing the numeric value of the fraction.
     */
    private static String getValueFromComplexFraction(String _text) {
        String[] values = _text.split("-");

        if (values.length == 2) {
            return String.valueOf((float) fractionValidator(values[0])
                    + (float) fractionValidator(values[1]));
        }

        return _text;
    }

    /**
     * Convert text representing a fraction to a double.
     *
     * @param _componentText String to convert.
     * @return Double equivalent to the fraction.
     */
    public static float fractionValidator(String _componentText) {
        if (_componentText.contains("-")) {
            return Float.parseFloat(getValueFromComplexFraction(_componentText));
        }

        if (_componentText.contains(" mm")) {
            return Float.parseFloat(_componentText.replace(" mm", ""));
        }

        String[] strings = _componentText.split("/");
        float _componentLastValue = 0.0f;
        if (strings.length == 2) {
            _componentLastValue = Float.parseFloat(strings[0]) / Float.parseFloat(strings[1]);
        } else if (strings.length < 2) {
            _componentLastValue = Float.parseFloat(strings[0]);
        }

        return _componentLastValue;
    }

    /**
     * Get the fraction in string format.
     *
     * @return String of the fraction with '/' as separator. If the fraction contains integer, the integer and fraction
     * will be separate with a '-'.
     */
    public String toString() {
        if(denominator != 1) {
            int integer = numerator / denominator;

            if (integer > 0) {
                int numeratorWithoutInteger = numerator - (denominator * integer);
                return integer + "-" + numeratorWithoutInteger + "/" + denominator;
            } else {
                return numerator + "/" + denominator;
            }
        } else {
            return String.valueOf(numerator);
        }
    }

    /**
     * Get the fraction in double.
     *
     * @return Double value of the fraction.
     */
    public Double toDouble() {
        return (double)numerator / (double)denominator;
    }

    /**
     * Get the fraction in float
     *
     * @return Float value of the fraction.
     */
    public Float toFloat() {
        return (float)numerator / (float)denominator;
    }
}
