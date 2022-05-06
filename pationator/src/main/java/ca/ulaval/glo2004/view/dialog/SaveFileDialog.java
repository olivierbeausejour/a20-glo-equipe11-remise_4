package ca.ulaval.glo2004.view.dialog;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.patio.PatioInfo;
import ca.ulaval.glo2004.utils.LocaleText;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Save file dialog class.
 */
public class SaveFileDialog implements Dialog {
    private static final String PATIONATOR_EXTENSION = "ptor";
    private static final String DEFAULT_FILENAME = "patio";

    private final JFrame parentFrame;
    private final PatioController patioController;
    private final PatioInfo patioInfo;
    private String newPationatorFile;

    /**
     * Create a save file dialog.
     *
     * @param _parentFrame     Dialog parent.
     * @param _patioController Controller to communicate with domain.
     */
    public SaveFileDialog(JFrame _parentFrame, PatioController _patioController) {
        this.parentFrame = _parentFrame;
        this.patioController = _patioController;
        this.patioInfo = _patioController.getPatio().getPatioInfo();
        this.newPationatorFile = "";
    }

    /**
     * Get new pationator file.
     *
     * @return File path.
     */
    public String getNewPationatorFile() {
        return newPationatorFile;
    }

    /**
     * Get patio info.
     *
     * @return Patio info.
     */
    public PatioInfo getInfoToSave() {
        return patioInfo;
    }

    /**
     * Open save dialog
     *
     * @return True if user approve choice.
     */
    @Override
    public boolean open() {
        JFileChooser saveFileChooser = new JFileChooser(patioController.getCurrentFileName()) {
            @Override
            public void approveSelection() {
                File newFile = getSelectedFile();
                if (!newFile.toString().toLowerCase().endsWith(PATIONATOR_EXTENSION))
                    newFile = new File(newFile.toString() + "." + PATIONATOR_EXTENSION);

                if (newFile.exists() && getDialogType() == SAVE_DIALOG) {
                    OverwriteFileWarning overwriteFileWarning = new OverwriteFileWarning(parentFrame);
                    if (!overwriteFileWarning.open())
                        return;
                }
                super.approveSelection();
            }
        };
        saveFileChooser.setDialogTitle(LocaleText.getString("SAVE_DIALOG_TITLE"));
        saveFileChooser.setSelectedFile(new File(DEFAULT_FILENAME));
        saveFileChooser.setFileFilter(new FileNameExtensionFilter(LocaleText.getString("PATIONATOR_FILE_DESCRIPTION"),
                PATIONATOR_EXTENSION));

        int userChoice = saveFileChooser.showSaveDialog(parentFrame);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            newPationatorFile = saveFileChooser.getSelectedFile().toString();
            if (!newPationatorFile.toLowerCase().endsWith(PATIONATOR_EXTENSION))
                newPationatorFile += "." + PATIONATOR_EXTENSION;

            return true;
        }
        return false;
    }
}
