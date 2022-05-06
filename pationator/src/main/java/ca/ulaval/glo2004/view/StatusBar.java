package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * StatusBar displaying the Pationator status message
 */
public class StatusBar extends JPanel implements RedoActivatedListener, UndoActivatedListener, FileLoadedListener,
        FileSavedListener, ExportDoneListener {
    private final JLabel label;
    private final PatioController patioController;
    private String text;

    /**
     * Creates StatusBar without text and with designated frame.
     *
     * @param _frame Frame for mounting the status bar.
     */
    public StatusBar(JFrame _frame, PatioController _patioController) {
        label = new JLabel();

        label.setHorizontalTextPosition(SwingConstants.LEFT);

        _frame.add(this, BorderLayout.SOUTH);

        add(label);
        setBorder(new BevelBorder(BevelBorder.LOWERED));

        setPreferredSize(new Dimension(_frame.getWidth(), 23));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        patioController = _patioController;
        patioController.addUndoActivatedListener(this);
        patioController.addRedoActivatedListener(this);
        patioController.addFileLoaderListener(this);
        patioController.addFileSavedListener(this);
        patioController.addExportDoneListener(this);
    }

    /**
     * Returns the current text of the status bar.
     *
     * @return Current text of the status bar.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text for the status bar.
     *
     * @param _text Text that will be displayed in the status bar.
     */
    public void setText(String _text) {
        text = _text;

        label.setText(_text);
        add(label);
    }

    /**
     * Behavior when redo is activate.
     */
    @Override
    public void onRedoActivated() {
        setText(LocaleText.getString("REDO_ACTIVATED"));
    }

    /**
     * Behavior when undo is activate.
     */
    @Override
    public void onUndoActivated() {
        setText(LocaleText.getString("UNDO_ACTIVATED"));
    }

    /**
     * Behavior on project open.
     */
    @Override
    public void onFileLoaded() {
        setText(LocaleText.getString("FILE_MENU") + " " + patioController.getShortFileName() +
                LocaleText.getString("OPENED_FILE"));
    }

    /**
     * Behavior on project save.
     */
    @Override
    public void onFileSaved() {
        setText(LocaleText.getString("FILE_MENU") + " " + patioController.getShortFileName() +
                LocaleText.getString("SAVED_FILE"));
    }

    /**
     * Behavior on export complete.
     *
     * @param _fileFormat Exported file format.
     * @param _path       File path.
     */
    @Override
    public void onExportDone(String _fileFormat, String _path) {
        if (_path.isEmpty()) {
            setText(LocaleText.getString("FILE_MENU") +
                    LocaleText.getString("EXPORTED_FILE") + " " + _fileFormat.toUpperCase());
        } else {
            setText(LocaleText.getString("FILE_MENU") + " " + _path +
                    LocaleText.getString("EXPORTED_FILE") + " " + _fileFormat.toUpperCase());
        }
    }
}
