package ca.ulaval.glo2004.view.dialog;

import ca.ulaval.glo2004.patio.Component;
import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.CreateFiles;
import ca.ulaval.glo2004.utils.ExportDoneListener;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Pair;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;

/**
 * Dialog box for exporting data out of Pationator.
 */
public class SaveDialog implements Dialog {
    private final JFrame parentFrame;
    private final PatioController patioController;
    private final FileFormat fileFormat;

    private boolean modifierValue = false;

    /**
     * Create a dialog box to export data out of Pationator.
     *
     * @param _parentFrame     Frame of the current object parent.
     * @param _patioController Pationator patioController reference.
     * @param _fileFormat      File format of the export.
     */
    public SaveDialog(JFrame _parentFrame, PatioController _patioController, FileFormat _fileFormat) {
        parentFrame = _parentFrame;
        patioController = _patioController;

        fileFormat = _fileFormat;
    }

    /**
     * Prepare and open a save dialog box.
     *
     * @return True when no error is detected.
     */
    @Override
    public boolean open() {
        ArrayList<FileNameExtensionFilter> filter = new ArrayList<>();
        Pair<String, String> pathAndExtension = null;

        try {
            switch (fileFormat) {
                case PTOR:
                    filter.add(new FileNameExtensionFilter(
                            LocaleText.getString("PATIONATOR_FILE_DESCRIPTION"), "ptor"));
                    pathAndExtension = saveDialogBox(LocaleText.getString("STL_EXPORT_TITLE"), filter);
                    if (pathAndExtension != null) {
                        patioController.saveFile(pathAndExtension.first, patioController.getPatio().getPatioInfo());
                    }
                    break;
                case STL:
                    ArrayList<Component> components;
                    components = patioController.getPatio().getComponents();
                    filter.add(new FileNameExtensionFilter(LocaleText.getString("STL_FILENAME"), "stl"));
                    pathAndExtension = saveDialogBox(LocaleText.getString("STL_EXPORT_TITLE"), filter);
                    if (pathAndExtension != null) {
                        CreateFiles createFiles = new CreateFiles(patioController);
                        createFiles.setModifierValue(modifierValue);
                        createFiles.createSTL(pathAndExtension, components);
                    }
                    break;
                case IMAGE:
                    filter.add(new FileNameExtensionFilter(LocaleText.getString("JPG_FILENAME"), "jpg"));
                    filter.add(new FileNameExtensionFilter(LocaleText.getString("PNG_FILENAME"), "png"));
                    pathAndExtension = saveDialogBox(LocaleText.getString("IMAGE_EXPORT_TITLE"), filter);
                    if (pathAndExtension != null) {
                        CreateFiles createFiles = new CreateFiles(patioController);
                        createFiles.setModifierValue(modifierValue);
                        createFiles.createImage(pathAndExtension);
                    }
                    break;
                case PIECES:
                    filter.add(new FileNameExtensionFilter(
                            LocaleText.getString("PIECES_FILENAME"), "txt"));
                    pathAndExtension = saveDialogBox(LocaleText.getString("PIECES_EXPORT_TITLE"), filter);
                    if (pathAndExtension != null) {
                        patioController.createPiecesFile(pathAndExtension);
                    }
                    break;
                default:
                    break;
            }

            if (pathAndExtension != null)
                patioController.notifyExport(pathAndExtension.second, pathAndExtension.first);
            else
                patioController.notifyExport("", "");

            return true;
        } catch (Exception _exception) {
            return false;
        }
    }

    /**
     * Get a path and file name with a Windows explorer window.
     *
     * @param _title  Dialog title.
     * @param _filter File filter.
     * @return Complete path, name and extension of the new file.
     */
    private Pair<String, String> saveDialogBox(final String _title, ArrayList<FileNameExtensionFilter> _filter) {
        JFileChooser fileChooser = new JFileChooser(patioController.getCurrentFileName()) {
            @Override
            public void approveSelection() {
                File fileName = getSelectedFile();
                FileFilter fileFormat = getFileFilter();
                String extension = ((FileNameExtensionFilter) fileFormat).getExtensions()[0];

                if (!fileName.toString().toLowerCase().endsWith(extension))
                    fileName = new File(fileName.toString() + "." + extension);

                if (fileName.exists() && getDialogType() == SAVE_DIALOG) {
                    OverwriteFileWarning overwriteFileWarning = new OverwriteFileWarning(parentFrame);

                    if (!overwriteFileWarning.open())
                        return;
                }

                super.approveSelection();
            }
        };

        fileChooser.setDialogTitle(_title);
        String DEFAULT_FILENAME = "patio";
        fileChooser.setSelectedFile(new File(DEFAULT_FILENAME));

        for (FileFilter filter : _filter) {
            fileChooser.setFileFilter(filter);
        }

        if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            FileFilter fileFormat = fileChooser.getFileFilter();
            String extension = ((FileNameExtensionFilter) fileFormat).getExtensions()[0];
            String path = fileChooser.getSelectedFile().getPath();

            if (!path.toLowerCase().endsWith("." + extension)) {
                path += "." + extension;
            }

            return new Pair<>(path, extension);
        }

        return null;
    }

    /**
     * Determine if the file is export as standard or if it apply modification.
     * <p>
     *     <ul>
     *         <li>STL file export with only visible part if the modifier is true.
     *         <li>Image export with all view in one file if the modifier is true.
     *     </ul>
     * <p>
     *
     * @param _isModified True if the file is create using an alternate behavior.
     */
    public void setModifierValue(boolean _isModified) {
        modifierValue = _isModified;
    }

    /**
     * Possible files format
     */
    public enum FileFormat {
        STL,
        IMAGE,
        PTOR,
        PIECES
    }
}
