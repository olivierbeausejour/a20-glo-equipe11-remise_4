package ca.ulaval.glo2004.view.actions;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Shortcuts;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Redo an action action.
 */
public class RedoAction extends AbstractAction {
    private static final String TEXT = LocaleText.getString("REDO");
    private static final String ICON_PATH = "/png/redo.png";

    private final PatioController patioController;

    /**
     * Create a redo action action.
     * @param _patioController Controller to communicate with the domain.
     */
    public RedoAction(PatioController _patioController) {
        super(TEXT);
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(ICON_PATH)));
        putValue(Action.ACCELERATOR_KEY, Shortcuts.REDO);
        putValue(Action.SHORT_DESCRIPTION, LocaleText.getString("REDO"));

        patioController = _patioController;
    }

    /**
     * Action behavior. Redo last undo action.
     *
     * @param _actionEvent Action event.
     */
    @Override
    public void actionPerformed(ActionEvent _actionEvent) {
        patioController.redo();
    }
}
