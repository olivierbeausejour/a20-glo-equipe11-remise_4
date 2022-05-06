package ca.ulaval.glo2004.view.dialog;

import ca.ulaval.glo2004.utils.LocaleText;

import javax.swing.*;

/**
 * Dialog display when a PrinterError is detected.
 */
public class PrinterError implements Dialog {
    private final JFrame parentFrame;

    private final String popUpMessage;
    private final String popUpTitle;

    /**
     * Create the dialog.
     *
     * @param _parentFrame  Parent where the dialog will be display.
     * @param _errorMessage Message explaining the current error.
     */
    public PrinterError(JFrame _parentFrame, String _errorMessage) {
        popUpTitle = LocaleText.getString("PRINTER_ERROR_DIALOG_TITLE");
        popUpMessage = _errorMessage;

        parentFrame = _parentFrame;
    }

    /**
     * Behavior when the dialog is open.
     *
     * @return True if the user click on 'OK' button.
     */
    @Override
    public boolean open() {
        int userChoice = JOptionPane.showOptionDialog(parentFrame, popUpMessage, popUpTitle, JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE, null, null, null);

        return userChoice == JOptionPane.OK_OPTION;
    }
}