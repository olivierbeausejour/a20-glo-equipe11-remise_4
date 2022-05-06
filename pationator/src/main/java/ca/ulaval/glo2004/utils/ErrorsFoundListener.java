package ca.ulaval.glo2004.utils;

import ca.ulaval.glo2004.patio.ValidationErrorType;

import java.util.HashSet;

public interface ErrorsFoundListener {
    void onErrorsFound(HashSet<ValidationErrorType> errors);
}
