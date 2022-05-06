package ca.ulaval.glo2004.view.actions;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Shortcuts;
import ca.ulaval.glo2004.utils.ViewOrientation;
import ca.ulaval.glo2004.view.PationatorWindow;
import ca.ulaval.glo2004.view.dialog.PrinterError;
import ca.ulaval.glo2004.view.prints.PatioPrint;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.print.*;

/**
 * Print action
 */
public class PrintAction extends AbstractAction {
    private static final String TEXT = LocaleText.getString("PRINT_FILE");
    private static final String ICON_PATH = "/png/print.png";
    private final PatioController patioController;
    private final PationatorWindow pationatorWindow;

    /**
     * Create a print action
     *
     * @param _window          Parent windows.
     * @param _patioController PatioController to communicate with domain layer.
     */
    public PrintAction(PationatorWindow _window, PatioController _patioController) {
        super(TEXT);
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(ICON_PATH)));
        putValue(Action.ACCELERATOR_KEY, Shortcuts.PRINT);
        putValue(Action.SHORT_DESCRIPTION, LocaleText.getString("PRINT_FILE"));

        patioController = _patioController;
        pationatorWindow = _window;
    }

    /**
     * Behavior when the action is performed.
     *
     * @param _actionEvent Event.
     */
    @Override
    public void actionPerformed(ActionEvent _actionEvent) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName(LocaleText.getString("PRINTER_JOB_TITLE"));

        PageFormat pageFormat = printerJob.defaultPage();
        pageFormat.setOrientation(PageFormat.LANDSCAPE);

        Paper paper = new Paper();

        double margin = 4.5;

        paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
        pageFormat.setPaper(paper);

        Book patioPlanBook = new Book();

        patioPlanBook.append(new PatioPrint(patioController, ViewOrientation.FACE), pageFormat);
        patioPlanBook.append(new PatioPrint(patioController, ViewOrientation.SIDE), pageFormat);
        patioPlanBook.append(new PatioPrint(patioController, ViewOrientation.TOP), pageFormat);

        printerJob.setPageable(patioPlanBook);

        if (printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (PrinterException _printerException) {
                PrinterError printerError = new PrinterError(pationatorWindow, _printerException.getMessage());
                printerError.open();
            }
        }
    }
}