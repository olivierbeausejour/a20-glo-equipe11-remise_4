package ca.ulaval.glo2004.view.actions;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Shortcuts;
import ca.ulaval.glo2004.view.PationatorWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * New file action
 */
public class CreateAction extends AbstractAction {
    private static final String TEXT = LocaleText.getString("NEW_FILE");
    private static final String ICON_PATH = "/png/annotate.png";

    private final PationatorWindow window;
    private final PatioController patioController;

    /**
     * Create new file action.
     *
     * @param _window          Main windows.
     * @param _patioController Controller to communicate with the domain.
     */
    public CreateAction(PationatorWindow _window, PatioController _patioController) {
        super(TEXT);
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(ICON_PATH)));
        putValue(Action.ACCELERATOR_KEY, Shortcuts.NEW);
        putValue(Action.SHORT_DESCRIPTION, LocaleText.getString("NEW_FILE"));

        window = _window;
        patioController = _patioController;
    }

    /**
     * Behavior when new file is called.
     *
     * @param _actionEvent Action event.
     */
    @Override
    public void actionPerformed(ActionEvent _actionEvent) {
        patioController.create(window);
    }
}
