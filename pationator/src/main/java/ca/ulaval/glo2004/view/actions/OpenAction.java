package ca.ulaval.glo2004.view.actions;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Shortcuts;
import ca.ulaval.glo2004.view.PationatorWindow;
import ca.ulaval.glo2004.view.dialog.OpenFileDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Open project.
 */
public class OpenAction extends AbstractAction {
    private static final String TEXT = LocaleText.getString("OPEN_FILE");
    private static final String ICON_PATH = "/png/menu-open.png";

    // Icons made by JetBrains can be found on https://jetbrains.design/intellij/resources/icons_list/.

    private final PationatorWindow window;
    private final PatioController patioController;

    /**
     * Create open project action.
     *
     * @param _window          Main windows.
     * @param _patioController Controller to communicate with the domain.
     */
    public OpenAction(PationatorWindow _window, PatioController _patioController) {
        super(TEXT);
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(ICON_PATH)));
        putValue(Action.ACCELERATOR_KEY, Shortcuts.OPEN);
        putValue(Action.SHORT_DESCRIPTION, LocaleText.getString("OPEN_FILE"));

        window = _window;
        patioController = _patioController;
    }

    /**
     * Action behavior. Open project.
     *
     * @param _actionEvent Action event.
     */
    @Override
    public void actionPerformed(ActionEvent _actionEvent) {
        OpenFileDialog openFileDialog = new OpenFileDialog(window, patioController);
        if (openFileDialog.open())
            patioController.openFile(openFileDialog.getPationatorFile());
    }
}
