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
public class ZoomInAction extends AbstractAction {
    private static final String TEXT = LocaleText.getString("ZOOM_IN");
    private static final String ICON_PATH = "/png/zoomIn.png";

    private final PatioController patioController;
    private final PationatorWindow pationatorWindow;

    /**
     * Create a zoom action
     *
     * @param _patioController  Controller to communicate with the domain.
     * @param _pationatorWindow Main windows.
     */
    public ZoomInAction(PatioController _patioController, PationatorWindow _pationatorWindow) {
        super(TEXT);
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(ICON_PATH)));
        putValue(Action.ACCELERATOR_KEY, Shortcuts.ZOOM_IN);
        putValue(Action.SHORT_DESCRIPTION, LocaleText.getString("ZOOM_IN"));

        patioController = _patioController;
        pationatorWindow = _pationatorWindow;
    }

    /**
     * Actvion behavior. Zoom in patio.
     *
     * @param _actionEvent Action event.
     */
    @Override
    public void actionPerformed(ActionEvent _actionEvent) {
        int index = 0;
        float zoomFactor = patioController.getZoomFactor(index);
        patioController.setZoomFactor(zoomFactor * 1.1f, index);

        pationatorWindow.getPrimaryDrawingPanel().repaint();
    }
}
