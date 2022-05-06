package ca.ulaval.glo2004.view.dialog;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Dialog box for opening file.
 */
public class OpenFileDialog implements Dialog {
    private static final String PATIONATOR_EXTENSION = "ptor";

    private final JFrame parentFrame;
    private final PatioController patioController;
    private String pationatorFile;

    /**
     * Create a open file dialog.
     *
     * @param _parentFrame     Dialog parent.
     * @param _patioController Controller to communicate with domain.
     */
    public OpenFileDialog(JFrame _parentFrame, PatioController _patioController) {
        this.parentFrame = _parentFrame;
        this.patioController = _patioController;
        this.pationatorFile = "";
    }

    /**
     * Get pationator file.
     *
     * @return File path.
     */
    public String getPationatorFile() {
        return pationatorFile;
    }

    /**
     * Open open file dialog
     *
     * @return True if user approve choice.
     */
    @Override
    public boolean open() {
        JFileChooser openFileChooser = new JFileChooser();
        openFileChooser.setDialogTitle(LocaleText.getString("OPEN_DIALOG_TITLE"));
        openFileChooser.setFileFilter(new FileNameExtensionFilter(LocaleText.getString("PATIONATOR_FILE_DESCRIPTION"),
                PATIONATOR_EXTENSION));

        int userChoice = openFileChooser.showOpenDialog(parentFrame);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            pationatorFile = openFileChooser.getSelectedFile().toString();

            if (!pationatorFile.endsWith(PATIONATOR_EXTENSION))
                // File Error
                return false;

            if (patioController.shouldShowSavePopup()) {
                SaveChangesWarning saveChangesWarning = new SaveChangesWarning(parentFrame);
                if (saveChangesWarning.open()) {
                    if (patioController.getCurrentFileName() == null) {
                        SaveFileDialog saveFileDialog = new SaveFileDialog(saveChangesWarning.getParentFrame(),
                                patioController);

                        if (saveFileDialog.open())
                            patioController.saveFile(saveFileDialog.getNewPationatorFile(),
                                    saveFileDialog.getInfoToSave());
                    } else
                        patioController.saveFile();
                }
            }
            return true;
        }

        return false;
    }
}
