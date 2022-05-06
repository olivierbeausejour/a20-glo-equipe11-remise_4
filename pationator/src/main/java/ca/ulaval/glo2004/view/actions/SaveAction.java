package ca.ulaval.glo2004.view.actions;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Shortcuts;
import ca.ulaval.glo2004.view.PationatorWindow;
import ca.ulaval.glo2004.view.dialog.SaveFileDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Save project action
 */
public class SaveAction extends AbstractAction {
    private static final String TEXT = LocaleText.getString("SAVE_FILE");
    private static final String ICON_PATH = "/png/menu-saveall.png";

    private final PationatorWindow window;
    private final PatioController patioController;

    /**
     * Create a save project action.
     * @param _window Main windows.
     * @param _patioController Controller to communicate with the domain.
     */
    public SaveAction(PationatorWindow _window, PatioController _patioController) {
        super(TEXT);
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(ICON_PATH)));
        putValue(Action.ACCELERATOR_KEY, Shortcuts.SAVE);
        putValue(Action.SHORT_DESCRIPTION, LocaleText.getString("SAVE_FILE"));

        window = _window;
        patioController = _patioController;
    }

    /**
     * Action behavior. Save project.
     *
     * @param _actionEvent Action event.
     */
    @Override
    public void actionPerformed(ActionEvent _actionEvent) {
        String currentFileName = patioController.getCurrentFileName();
        if (currentFileName == null) {
            openSaveDialog();
        } else {
            patioController.saveFile();
        }
    }

    /**
     * Open a save dialog to choose file path.
     */
    private void openSaveDialog() {
        SaveFileDialog saveFileDialog = new SaveFileDialog(window, patioController);
        if (saveFileDialog.open())
            patioController.saveFile(saveFileDialog.getNewPationatorFile(), saveFileDialog.getInfoToSave());
    }
}
