package ca.ulaval.glo2004.view.actions;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Shortcuts;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Undo an action action.
 */
public class UndoAction extends AbstractAction {
    private static final String TEXT = LocaleText.getString("UNDO");
    private static final String ICON_PATH = "/png/undo.png";

    private final PatioController patioController;

    /**
     * Create an undo action action.
     * @param _patioController Controller to communicate with the domain.
     */
    public UndoAction(PatioController _patioController) {
        super(TEXT);
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(ICON_PATH)));
        putValue(Action.ACCELERATOR_KEY, Shortcuts.UNDO);
        putValue(Action.SHORT_DESCRIPTION, LocaleText.getString("UNDO"));

        patioController = _patioController;
    }

    /**
     * Action behavior. Undo the last action.
     *
     * @param _actionEvent Action event.
     */
    @Override
    public void actionPerformed(ActionEvent _actionEvent) {
        patioController.undo();
    }
}
