package ca.ulaval.glo2004.utils;

import ca.ulaval.glo2004.patio.MeasureUnit;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringValidator {
    /**
     * Check if the text field text is a valid price format.
     *
     * @param _text      Text to validate.
     * @param _lastValue Last valid numeric value.
     * @return Price value in numeric format.
     */
    public static Number priceTextFieldValidator(String _text, float _lastValue) {
        float dollar;
        float cents;

        if (_text.isEmpty()) {
            return _lastValue;
        }
        Pattern pricePattern = Pattern.compile(
                "^(?!$)(?:([0-9]+))[,.]?(?:(\\d\\d)?)?(?:(\\$)?)?$", Pattern.CASE_INSENSITIVE);
        Matcher patternMatcher = pricePattern.matcher(_text);
        boolean isValid = patternMatcher.find();

        if (isValid) {
            String dollarString = patternMatcher.group(1);
            String centsString = patternMatcher.group(2);
            String symbol = patternMatcher.group(3);

            if (dollarString != null) {
                dollar = Float.parseFloat(dollarString);
            } else {
                dollar = 0;
            }
            if (centsString != null) {
                cents = Float.parseFloat("0." + centsString);
            } else {
                cents = 0;
            }
            if (symbol == null) {
                symbol = "$";
            }
        } else {
            return null;
        }

        return (dollar + cents);
    }

    /**
     * Validate text field input depending on measure unit.
     *
     * @param _component          JTextField validate.
     * @param _componentLastValue Current value of the JTextField.
     * @param _measureUnit        Used measure unit.
     * @return Double value representing the input in inch.
     */
    public static double textFieldValidator(JTextField _component, double _componentLastValue, MeasureUnit _measureUnit,
                                            boolean _pationatorInitialisation) {
        return textFieldValidator(_component, _componentLastValue, _measureUnit,
                _pationatorInitialisation, Double.MIN_VALUE);
    }

    /**
     * Validate text field input depending on measure unit and minimum value.
     *
     * @param _component          JTextField validate.
     * @param _componentLastValue Current value of the JTextField.
     * @param _measureUnit        Used measure unit.
     * @param _minimumValue       Minimum value needed for the field
     * @return Double value representing the input in inch.
     */
    public static double textFieldValidator(
            JTextField _component, double _componentLastValue, MeasureUnit _measureUnit,
            boolean _pationatorInitialisation, double _minimumValue) {
        double componentLastValue = _componentLastValue;

        switch (_measureUnit) {
            case METRIC:
                _componentLastValue = metricValidator(_component, _componentLastValue);
                break;
            case IMPERIAL:
            default:
                _componentLastValue = imperialValidator(_component, _componentLastValue);
                break;
        }

        if (_componentLastValue >= _minimumValue || _pationatorInitialisation) {
            return _componentLastValue;
        } else {
            _component.setBorder(new LineBorder(Color.RED, 1));
            return componentLastValue;
        }
    }

    /**
     * Validate metric input (format [0-9] [mm || cm || dm || m])
     *
     * @param _component          JTextField validate.
     * @param _componentLastValue Current value of the JTextField.
     * @return Double value representing the input in inch.
     */
    private static double metricValidator(JTextField _component, double _componentLastValue) {
        _component.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

        double integer;
        double decimal;

        Pattern pattern = Pattern.compile("^(?!$)(\\d+)[,.]?(\\d*)\\s?(m|dm|cm|mm)?$", Pattern.CASE_INSENSITIVE);
        Matcher paternMatcher = pattern.matcher(_component.getText());
        boolean isValid = paternMatcher.find();

        if (isValid) {
            String integerString = paternMatcher.group(1);
            String decimalString = paternMatcher.group(2);
            String mesureUnit = paternMatcher.group(3);

            if (integerString != null) {
                integer = Double.parseDouble(integerString);
            } else {
                integer = 0;
            }
            if (decimalString != null) {
                decimal = Double.parseDouble("0." + decimalString);
            } else {
                decimal = 0;
            }
            if (mesureUnit == null) {
                mesureUnit = "mm";
            }

            _componentLastValue = (integer + decimal);
            switch (mesureUnit.toLowerCase()) {
                case "m":
                    _componentLastValue *= 100;
                    break;
                case "dm":
                    _componentLastValue *= 10;
                    break;
                case "cm":
                    _componentLastValue *= 1;
                    break;
                case "mm":
                default:
                    _componentLastValue *= 0.1;
                    break;
            }

            _componentLastValue *= 0.3937008;
        } else {
            _component.setBorder(new LineBorder(Color.RED, 1));
        }

        return _componentLastValue;
    }

    /**
     * Validate imperial input (format feet:[0-9]' inches:[0-9]" or [0-9]-[0-9]/[0-9]" or [0-9])
     *
     * @param _component          JTextField validate.
     * @param _componentLastValue Current value of the JTextField.
     * @return Double value representing the input in inch.
     */
    private static double imperialValidator(JTextField _component, double _componentLastValue) {
        _component.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

        double feet;
        double inch;
        double fraction;

        // w'x" y/z OR w'x" OR x" y/z OR x y/z OR w/x' OR w/x"
        Pattern pattern = Pattern.compile(
                "^(?!$|.*\\'[^\\x22]+$)(?:([0-9]+([,.][0-9]+)?+|[1-9][0-9]*[\\/][1-9][0-9]*)\\')?[ ]?(?:([0-9]+([,.][0-9]+)?+|[1-9][0-9]*[\\/][1-9][0-9]*))?(?:\\x22|[ -]([1-9][0-9]*[\\/][1-9][0-9]*)?)?(?:\\x22?)$",
                Pattern.CASE_INSENSITIVE);
        Matcher paternMatcher = pattern.matcher(_component.getText());
        boolean isValid = paternMatcher.find();

        if (isValid) {
            String feetInString = paternMatcher.group(1);
            String inchInString = paternMatcher.group(3);
            String fractionInstring = paternMatcher.group(5);

            if (feetInString != null) {
                feet = Fraction.fractionValidator(feetInString);
            } else {
                feet = 0;
            }
            if (inchInString != null) {
                inch = Fraction.fractionValidator(inchInString);
            } else {
                inch = 0;
            }
            if (fractionInstring != null) {
                fraction = Fraction.fractionValidator(fractionInstring);
            } else {
                fraction = 0;
            }

            _componentLastValue = (feet * 12 + inch + fraction);
        } else {
            _component.setBorder(new LineBorder(Color.RED, 1));
        }

        return _componentLastValue;
    }
}
