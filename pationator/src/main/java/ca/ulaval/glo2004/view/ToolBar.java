package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.patio.ValidationErrorType;
import ca.ulaval.glo2004.utils.*;
import ca.ulaval.glo2004.view.actions.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * Pationator main JToolbar.
 */
public class ToolBar extends JToolBar implements ErrorsFoundListener, NoErrorsFoundListener, ChangeMadeListener,
        DefaultPatioGeneratedListener {
    private final PationatorWindow window;
    private final PatioController patioController;
    private final UndoAction undoAction;
    private final RedoAction redoAction;
    private JLabel validationErrorIcon;

    /**
     * Create the ToolBar
     *
     * @param _frame           Pationator main windows.
     * @param _patioController Controller to communicate with the domain layer.
     */
    public ToolBar(PationatorWindow _frame, PatioController _patioController) {
        window = _frame;
        patioController = _patioController;
        patioController.getPatio().addErrorsFoundListener(this);
        patioController.getPatio().addNoErrorsFoundListener(this);
        patioController.addChangeMadeListener(this);
        patioController.addDefaultPatioGeneratedListener(this);

        undoAction = new UndoAction(patioController);
        undoAction.setEnabled(false);
        redoAction = new RedoAction(patioController);
        redoAction.setEnabled(false);
        initToolBar();
    }

    /**
     * Initialize the tool bar. Set orientation and border setting, and add it to the frame.
     */
    private void initToolBar() {
        window.add(BorderLayout.NORTH, this);

        setFloatable(false);
        setOrientation(JToolBar.HORIZONTAL);
        setBorder(BorderFactory.createEtchedBorder());
        setBorderPainted(true);

        validationErrorIcon = new JLabel(new ImageIcon(getClass().getResource("/png/idle.png")));
    }

    /**
     * Add action and the validation icon on the toolbar.
     */
    public void create() {
        add(new CreateAction(window, patioController));
        add(new OpenAction(window, patioController));
        add(new SaveAction(window, patioController));
        add(new PrintAction(window, patioController));
        addSeparator();
        add(undoAction);
        add(redoAction);
        addSeparator();

        add(new ZoomInAction(patioController, window));
        add(new ZoomOutAction(patioController, window));

        add(Box.createHorizontalGlue());
        add(validationErrorIcon);
        add(new JLabel("    "));  // Just to add right padding to toolbar, really not the best solution
    }

    /**
     * Behavior when error is detected in Patinator. The icon in the tool bar will change to error icon.
     *
     * @param _errors Validation error detected.
     */
    @Override
    public void onErrorsFound(HashSet<ValidationErrorType> _errors) {
        if (!_errors.isEmpty())
            validationErrorIcon.setIcon(new ImageIcon(getClass().getResource("/png/fatalError.png")));
    }

    /**
     * Behavior when no errors are detected in Patinator. The icon in the tool bar will change to a no error icon.
     */
    @Override
    public void onNoErrorsFound() {
        validationErrorIcon.setIcon(new ImageIcon(getClass().getResource("/png/idle.png")));
    }

    /**
     * Behavior when a change is made.
     */
    @Override
    public void onChangeMade() {
        setUndoRedoActionsEnabling();
    }

    /**
     * Enable undo redo action.
     */
    private void setUndoRedoActionsEnabling() {
        redoAction.setEnabled(UndoManager.canStillRedo());
        undoAction.setEnabled(UndoManager.canStillUndo());
    }

    /**
     * Behavior when a default patio is generated.
     */
    @Override
    public void onDefaultPatioGenerated() {
        setUndoRedoActionsEnabling();
    }
}
