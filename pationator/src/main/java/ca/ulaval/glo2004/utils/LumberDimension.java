package ca.ulaval.glo2004.utils;

import ca.ulaval.glo2004.patio.ComponentType;
import ca.ulaval.glo2004.patio.MeasureType;
import ca.ulaval.glo2004.patio.MeasureUnit;

import static ca.ulaval.glo2004.patio.MeasureType.REAL;
import static ca.ulaval.glo2004.patio.MeasureUnit.METRIC;

/**
 * This class contains the lumber dimension possibility used in Pationator, and method need to convert string values
 * related to lumber dimensions.
 */
public class LumberDimension {
    private LumberDimension() {
    }

    /**
     * Convert an height and a width value to a imperial lumber dimensions.
     *
     * @param _height Height value.
     * @param _width  Width value.
     * @return String of a lumber dimension in format 'height" x width"'.
     */
    public static String formatToImperialLumberDimensions(String _height, String _width) {
        return _height + "\" x " + _width + "\"";
    }

    /**
     * Separate the width and height dimension value.
     *
     * @param _lumberDimensions Imperial dimensions to split.
     * @return String array with width and height value.
     */
    public static String[] splitLumberDimensions(String _lumberDimensions) {
        _lumberDimensions = _lumberDimensions.replace("\"", "");
        _lumberDimensions = _lumberDimensions.replace(" ", "");

        return _lumberDimensions.split("x");
    }

    /**
     * Get an array of possible metric or inches dimensions according to a specified component type.
     *
     * @param _measureUnit   Specify is the dimensions are in metric or imperial.
     * @param _componentType Specify the component type.
     * @return String array of possible lumber dimensions.
     */
    public static String[] getPossibility(MeasureUnit _measureUnit, ComponentType _componentType) {
        if (_measureUnit == METRIC) {
            switch (_componentType) {
                case JOIST:
                    return new String[]{
                            "38 x 89 mm",
                            "38 x 140 mm",
                            "38 x 184 mm",
                            "38 x 235 mm",
                            "38 x 286 mm",
                    };
                case BEAM:
                    return new String[]{
                            "38 x 140 mm",
                            "38 x 184 mm",
                            "38 x 235 mm",
                            "38 x 286 mm",
                    };
                case POST:
                    return new String[]{
                            "89 x 89 mm",
                            "140 x 140 mm"
                    };
                case COVERING_PLANK:
                    return new String[]{
                            "38 x 140 mm",
                            "25 x 140 mm"
                    };
                default:
                    return null;
            }
        }

        return getPossibility(REAL, _componentType);
    }

    /**
     * Get an array of possible nominal or inches dimensions according to a specified component type.
     *
     * @param _measureType   Specify is the dimensions are nominal or real.
     * @param _componentType Specify the component type.
     * @return String array of possible lumber dimensions.
     */
    public static String[] getPossibility(MeasureType _measureType, ComponentType _componentType) {
        if (_measureType == REAL) {
            switch (_componentType) {
                case JOIST:
                    return new String[]{
                            "1-1/2\" x 3-1/2\"",
                            "1-1/2\" x 5-1/2\"",
                            "1-1/2\" x 7-1/4\"",
                            "1-1/2\" x 9-1/4\"",
                            "1-1/2\" x 11-1/4\""
                    };
                case BEAM:
                    return new String[]{
                            "1-1/2\" x 5-1/2\"",
                            "1-1/2\" x 7-1/4\"",
                            "1-1/2\" x 9-1/4\"",
                            "1-1/2\" x 11-1/4\""
                    };
                case POST:
                    return new String[]{
                            "3-1/2\" x 3-1/2\"",
                            "5-1/2\" x 5-1/2\""
                    };
                case COVERING_PLANK:
                    return new String[]{
                            "1-1/2\" x 5-1/2\"",
                            "1\" x 5-1/2\""
                    };
                default:
                    return getPossibility();
            }
        } else {
            switch (_componentType) {
                case JOIST:
                    return new String[]{
                            "2\" x 4\"",
                            "2\" x 6\"",
                            "2\" x 8\"",
                            "2\" x 10\"",
                            "2\" x 12\""
                    };
                case BEAM:
                    return new String[]{
                            "2\" x 6\"",
                            "2\" x 8\"",
                            "2\" x 10\"",
                            "2\" x 12\""
                    };
                case POST:
                    return new String[]{
                            "4\" x 4\"",
                            "6\" x 6\""
                    };
                case COVERING_PLANK:
                    return new String[]{
                            "2\" x 6\"",
                            "5/4\" x 6\""
                    };
                default:
                    return getPossibility();
            }
        }
    }

    /**
     * Get an array of possible nominal dimensions.
     *
     * @return String array of nominal dimensions.
     */
    public static String[] getPossibility() {
        return new String[]{
                "2\" x 4\"",
                "2\" x 6\"",
                "2\" x 8\"",
                "2\" x 10\"",
                "2\" x 12\"",
                "4\" x 4\"",
                "5/4\" x 6\"",
                "6\" x 6\""
        };
    }
}