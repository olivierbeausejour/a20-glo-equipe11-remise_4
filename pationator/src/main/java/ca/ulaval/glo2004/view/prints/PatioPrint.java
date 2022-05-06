package ca.ulaval.glo2004.view.prints;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.ViewOrientation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

/**
 * PatioPrint is used to print a patio plan.
 */
public class PatioPrint implements Printable {
    private final PatioController patioController;
    private final ViewOrientation viewOrientation;

    /**
     * Create the printable object with a select view orientation.
     *
     * @param _patioController Controller to communicate with the domain layer and call a drawing method.
     * @param _viewOrientation Desired view.
     */
    public PatioPrint(PatioController _patioController, ViewOrientation _viewOrientation) {
        patioController = _patioController;
        viewOrientation = _viewOrientation;
    }

    /**
     * Behavior when the class is print
     *
     * @param _graphics   Graphics object containing the patio drawing.
     * @param _pageFormat Page dimension.
     * @param _pageIndex  Page identifier for multiple page print.
     * @return 0 if the requested page was rendered. 1 otherwise.
     */
    @Override
    public int print(Graphics _graphics, PageFormat _pageFormat, int _pageIndex) {
        int pixelWidth = 2400;

        double pageWidth = _pageFormat.getWidth();
        double pageHeight = _pageFormat.getHeight();

        double resultPixelHeight = (pageHeight / pageWidth) * pixelWidth;

        double scaleFactorWidth = _pageFormat.getWidth() / pixelWidth;
        double scaleFactorHeight = _pageFormat.getHeight() / resultPixelHeight;

        Graphics2D graphics2D = (Graphics2D) _graphics;

        graphics2D.scale(scaleFactorWidth, scaleFactorHeight);

        BufferedImage patioViewImage = patioController.getPatioViewImage(pixelWidth, (int) resultPixelHeight,
                BufferedImage.TYPE_INT_RGB, viewOrientation);

        graphics2D.drawImage(patioViewImage, null, 0, 0);

        return PAGE_EXISTS;
    }
}
