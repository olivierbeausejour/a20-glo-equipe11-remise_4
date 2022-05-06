package ca.ulaval.glo2004.view.dialog;

import ca.ulaval.glo2004.utils.LocaleText;

import javax.swing.*;

/**
 * Overwrite changes warning dialog.
 */
public class OverwriteFileWarning implements Dialog {
    private final JFrame parentFrame;

    private final String popUpMessage;
    private final String popUpTitle;
    private final String[] popUpOptions;

    /**
     * Overwrite changes warning dialog.
     *
     * @param _parentFrame Dialog parent.
     */
    public OverwriteFileWarning(JFrame _parentFrame) {
        popUpMessage = LocaleText.getString("OVERWRITE_FILE_POP_UP_MESSAGE");
        popUpTitle = LocaleText.getString("OVERWRITE_FILE_POP_UP_TITLE");
        popUpOptions = new String[]{
                LocaleText.getString("YES_OPTION"),
                LocaleText.getString("NO_OPTION")};

        parentFrame = _parentFrame;
    }

    /**
     * Open a warning dialog.
     *
     * @return User choice.
     */
    @Override
    public boolean open() {
        int userChoice = JOptionPane.showOptionDialog(parentFrame, popUpMessage, popUpTitle, JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null, popUpOptions, popUpOptions[0]);

        return userChoice == JOptionPane.YES_OPTION;
    }
}
