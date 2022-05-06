package ca.ulaval.glo2004.view.actions;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Shortcuts;
import ca.ulaval.glo2004.view.PationatorWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Zoom action.
 */
public class ZoomOutAction extends AbstractAction {
    private static final String TEXT = LocaleText.getString("ZOOM_OUT");
    private static final String ICON_PATH = "/png/zoomOut.png";

    private final PatioController patioController;
    private final PationatorWindow pationatorWindow;

    /**
     * Create a zoom action
     *
     * @param _patioController  Controller to communicate with the domain.
     * @param _pationatorWindow Main windows.
     */
    public ZoomOutAction(PatioController _patioController, PationatorWindow _pationatorWindow) {
        super(TEXT);
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(ICON_PATH)));
        putValue(Action.ACCELERATOR_KEY, Shortcuts.ZOOM_OUT);
        putValue(Action.SHORT_DESCRIPTION, LocaleText.getString("ZOOM_OUT"));

        patioController = _patioController;
        pationatorWindow = _pationatorWindow;
    }

    /**
     * Action behavior. Zoom out patio.
     *
     * @param _actionEvent Action event.
     */
    @Override
    public void actionPerformed(ActionEvent _actionEvent) {
        int index = 0;
        float zoomFactor = patioController.getZoomFactor(index);
        patioController.setZoomFactor(zoomFactor / 1.1f, index);

        pationatorWindow.getPrimaryDrawingPanel().repaint();
    }
}
