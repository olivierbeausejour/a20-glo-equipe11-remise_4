package ca.ulaval.glo2004.view.dialog;

import ca.ulaval.glo2004.utils.LocaleText;

import javax.swing.*;

/**
 * AboutUs dialog.
 */
public class AboutUs implements Dialog {
    private final JFrame parentFrame;

    private final String popUpTitle;
    private final String popUpMessage;

    /**
     * Create about us dialog.
     *
     * @param _parentFrame Dialog parent.
     */
    public AboutUs(JFrame _parentFrame) {
        parentFrame = _parentFrame;

        popUpTitle = "Pationator";
        String members = "Olivier Beauséjour\nScott Chalmers\nJonathan Mathieu\nDerek Pouliot\n";
        String vision = LocaleText.getString("PATIONATOR_VISION");

        popUpMessage = vision + "\n\n" + members;
    }

    /**
     * Open about us dialog.
     *
     * @return User choice.
     */
    @Override
    public boolean open() {
        int userChoice = JOptionPane.showOptionDialog(
                parentFrame, popUpMessage, popUpTitle, JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        return userChoice == JOptionPane.YES_OPTION;
    }
}
