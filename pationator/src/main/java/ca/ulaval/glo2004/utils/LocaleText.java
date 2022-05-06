package ca.ulaval.glo2004.utils;

import java.util.ResourceBundle;

public class LocaleText {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("pationator");

    public static String getString(String _key) {
        if (resourceBundle.containsKey(_key))
            return resourceBundle.getString(_key);
        return null;
    }
}
