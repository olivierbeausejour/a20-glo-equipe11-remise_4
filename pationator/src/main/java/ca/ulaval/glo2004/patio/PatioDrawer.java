package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Vector3;
import ca.ulaval.glo2004.utils.ViewOrientation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Class to draw a patio on screen and in 2D file.
 */
public class PatioDrawer extends JPanel {
    private final PatioController patioController;
    private final ViewOrientation[] viewOrientationsWithIndex = new ViewOrientation[4];
    private float inchUnitInPixel;

    /**
     * Create a patio drawer.
     *
     * @param _patioController Controller used to interact with Pationator.
     */
    public PatioDrawer(PatioController _patioController) {
        patioController = _patioController;

        initializeViewOrientationsWithIndex();
    }

    /**
     * View orientation used for multiple drawing panel layout.
     */
    private void initializeViewOrientationsWithIndex() {
        viewOrientationsWithIndex[0] = ViewOrientation.FACE;
        viewOrientationsWithIndex[1] = ViewOrientation.SIDE;
        viewOrientationsWithIndex[2] = ViewOrientation.TOP;
    }

    /**
     * Get the view on the patio according to an index.
     *
     * @param _drawingPanelIndex Drawing panel index.
     * @return Current view use.
     */
    public ViewOrientation getViewOrientation(int _drawingPanelIndex) {
        return viewOrientationsWithIndex[_drawingPanelIndex];
    }

    /**
     * Changes the view on the patio.
     *
     * @param _viewOrientation Current view use.
     */
    public void setViewOrientation(ViewOrientation _viewOrientation) {
        setViewOrientation(_viewOrientation, 0);
    }

    /**
     * Changes the view on the patio.
     *
     * @param _viewOrientation   Current view use.
     * @param _drawingPanelIndex Drawing panel index.
     */
    public void setViewOrientation(ViewOrientation _viewOrientation, int _drawingPanelIndex) {
        viewOrientationsWithIndex[_drawingPanelIndex] = _viewOrientation;
    }

    /**
     * Draw the background, the grid, each component and their hidden edge.
     *
     * @param _graphics          Graphics object.
     * @param _unitInPixel       Conversion factor pixel/grid.
     * @param _isExport          True if the drawing is export to image file.
     * @param _drawingPanelIndex Drawing panel index.
     */
    public void draw(Graphics _graphics, float _unitInPixel, boolean _isExport, int _drawingPanelIndex) {
        inchUnitInPixel = _unitInPixel;

        drawGrid(_graphics);

        Patio patio = patioController.getPatio();
        ArrayList<Post> posts = patio.getPosts();
        ArrayList<Beam> beams = patio.getBeams();
        ArrayList<Span> spans = patio.getSpans();
        ArrayList<Covering> covering = patio.getCovering();

        boolean postAreVisible = patioController.getVisibility(ComponentType.POST);
        boolean beamAreVisible = patioController.getVisibility(ComponentType.BEAM);
        boolean joistAreVisible = patioController.getVisibility(ComponentType.JOIST);
        boolean coveringIsVisible = patioController.getVisibility(ComponentType.COVERING_PLANK);

        if (postAreVisible) {
            drawComponent(_graphics, posts, _drawingPanelIndex);
        }

        if (beamAreVisible) {
            drawComponent(_graphics, beams, _drawingPanelIndex);
        }

        if (joistAreVisible) {
            drawComponent(_graphics, spans, _drawingPanelIndex);
        }

        if (coveringIsVisible) {
            drawComponent(_graphics, covering, _drawingPanelIndex);
        }

        if (patioController.getHiddenBorderVisibility()) {
            if (coveringIsVisible) {
                drawHiddenEdge(_graphics, covering, _drawingPanelIndex);
            }

            if (joistAreVisible) {
                drawHiddenEdge(_graphics, spans, _drawingPanelIndex);
            }

            if (beamAreVisible) {
                drawHiddenEdge(_graphics, beams, _drawingPanelIndex);
            }

            if (postAreVisible) {
                drawHiddenEdge(_graphics, posts, _drawingPanelIndex);
            }
        }

        if(patioController.getArrowVisibility()) {
            drawArrowWithText(_graphics, patio, _drawingPanelIndex, _isExport);
        }
    }

    /**
     * Draw grid with color according to the current theme. Axis and grid in different color.
     *
     * @param _graphics Graphics object.
     */
    private void drawGrid(Graphics _graphics) {
        Color toolBarColor = UIManager.getDefaults().getColor("ToolBar.background");
        Color borderColor = UIManager.getDefaults().getColor("MenuBar.borderColor");

        Color gridColor = toolBarColor.equals(new Color(242, 242, 242)) ? Color.lightGray : toolBarColor;
        Color axisColor = borderColor.equals(new Color(205, 205, 205)) ? Color.black : borderColor;

        float inchesBetweenGridLine = 1f;
        float gridSize = 12000;
        for (int i = (int) -(gridSize / inchesBetweenGridLine); i < (gridSize / inchesBetweenGridLine); i++) {
            // Vertical lines
            drawPrimitiveLine(_graphics, -i, -(int) gridSize, -i, (int) gridSize, gridColor);
            // Horizontal lines
            drawPrimitiveLine(_graphics, -(int) gridSize, i, (int) gridSize, i, gridColor);
        }

        for (int i = (int) -(gridSize / inchesBetweenGridLine); i < (gridSize / inchesBetweenGridLine); i += 12) {
            // Vertical lines
            drawPrimitiveLine(_graphics, -i, -(int) gridSize, -i, (int) gridSize, axisColor);
            // Horizontal lines
            drawPrimitiveLine(_graphics, -(int) gridSize, i, (int) gridSize, i, axisColor);
        }
    }

    /**
     * Draw the selected component.
     *
     * @param _graphics          Graphics object.
     * @param components         Components to draw.
     * @param _drawingPanelIndex Drawing panel index.
     */
    private void drawComponent(Graphics _graphics, ArrayList<? extends Component> components, int _drawingPanelIndex) {
        try {
            for (Component component : components) {
                for (WoodPiece woodPiece : component.getWoodPieces()) {
                    drawPrimitiveRectangle(
                            _graphics,
                            woodPiece.getMinCornerPosition(),
                            woodPiece.getMaxCornerPosition(),
                            component.getColor(),
                            component.isColorFill(),
                            _drawingPanelIndex
                    );
                }
            }
        } catch (Exception _exception) {
            repaint();
        }
    }

    /**
     * Draw hidden edge for the selected component.
     *
     * @param _graphics          Graphics object.
     * @param components         Components to draw.
     * @param _drawingPanelIndex Drawing panel index.
     */
    private void drawHiddenEdge(Graphics _graphics, ArrayList<? extends Component> components, int _drawingPanelIndex) {
        if (components.size() > 0) {
            for (int i = components.size() - 1; i >= 0; i--) {
                if (components.get(i).isVisible()) {
                    for (WoodPiece woodPiece : components.get(i).getWoodPieces()) {
                        drawHiddenWoodPiece(
                                (Graphics2D) _graphics,
                                woodPiece.getMinCornerPosition(),
                                woodPiece.getMaxCornerPosition(),
                                _drawingPanelIndex
                        );
                    }
                }
            }
        }
    }

    /**
     * Draw an arrow under and on the left of the patio drawing. Text show the current value for this properties (width,
     * height, depth)
     *
     * @param _graphics          Graphics object.
     * @param _patio             Patio object.
     * @param _drawingPanelIndex Drawing panel index.
     * @param _isExport          True if the drawing is export to image file.
     */
    private void drawArrowWithText(Graphics _graphics, Patio _patio, int _drawingPanelIndex, boolean _isExport) {
        Graphics2D graphics = (Graphics2D) _graphics;
        Color arrowAndTextColor = javax.swing.UIManager.getDefaults().getColor("MenuItem.selectionBackground");

        graphics.setStroke(new BasicStroke());
        graphics.setColor(arrowAndTextColor);

        Dimensions patioDimensions = _patio.getPatioInfo().getPatioDimensions();

        float depth = patioDimensions.getActualDepth();
        float width = patioDimensions.getActualWidth();
        float height = patioDimensions.getActualHeight();

        int margin = 125;
        int textMargin = margin;

        int xPos, yPos, arrowCenterX, arrowCenterY;

        String messageDepth = patioController.getValueWithMeasureUnit(depth);
        String messageWidth = patioController.getValueWithMeasureUnit(width);
        String messageHeight = patioController.getValueWithMeasureUnit(height);

        float zoomFactor;
        if (_isExport) {
            zoomFactor = 0.25f;
            textMargin = 10;
        } else {
            zoomFactor = patioController.getZoomFactor(_drawingPanelIndex);
        }

        graphics.setFont(new Font("SansSerif", Font.ITALIC, (int) (12 / zoomFactor)));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int fontHeight = fontMetrics.getHeight();
        int fontWidth;

        float depthArrowLength = depth * inchUnitInPixel;
        float widthArrowLength = width * inchUnitInPixel;
        float heightArrowLength = height * inchUnitInPixel;

        float postWidth = _patio.getPatioInfo().getPostDimensions().getActualWidth() * inchUnitInPixel;
        switch (viewOrientationsWithIndex[_drawingPanelIndex]) {
            case SIDE:
                xPos = 0;
                yPos = margin;

                drawArrow(_graphics, new Point(xPos, yPos), new Point((int) (xPos + depthArrowLength), yPos),
                        0);
                arrowCenterX = (int) (xPos + depthArrowLength) / 2;
                graphics.drawString(messageDepth, arrowCenterX, yPos + fontHeight);

                xPos = -margin;
                yPos = 0;
                drawArrow(_graphics, new Point(xPos, yPos), new Point((xPos), (int) (yPos - heightArrowLength)), 1);
                fontWidth = fontMetrics.stringWidth(messageHeight);
                arrowCenterY = (int) (yPos + heightArrowLength) / 2;
                graphics.drawString(messageHeight, xPos - fontWidth - textMargin, -arrowCenterY);

                break;
            case TOP:
                xPos = 0;
                yPos = margin;

                drawArrow(_graphics, new Point(xPos, yPos), new Point((int) (xPos + depthArrowLength), yPos),
                        0);
                arrowCenterX = (int) (xPos + depthArrowLength) / 2;
                graphics.drawString(messageDepth, arrowCenterX, yPos + fontHeight);

                xPos = -margin;
                yPos = 0;
                drawArrow(_graphics, new Point(xPos, (int) (yPos + postWidth / 2)),
                        new Point((xPos), (int) (yPos - widthArrowLength + postWidth / 2)), 1);
                fontWidth = fontMetrics.stringWidth(messageWidth);
                arrowCenterY = (int) (yPos + widthArrowLength) / 2;
                graphics.drawString(messageWidth, xPos - fontWidth - textMargin, -arrowCenterY);

                break;
            case FACE:
                xPos = 0;
                yPos = margin;

                drawArrow(_graphics, new Point((int) (xPos - postWidth / 2), yPos),
                        new Point((int) (xPos + widthArrowLength - postWidth / 2), yPos), 0);
                arrowCenterX = (int) (xPos + widthArrowLength - postWidth) / 2;
                graphics.drawString(messageWidth, arrowCenterX, yPos + fontHeight);

                xPos = -margin;
                yPos = 0;
                drawArrow(_graphics, new Point(xPos, yPos), new Point((xPos), (int) (yPos - heightArrowLength)), 1);
                fontWidth = fontMetrics.stringWidth(messageHeight);
                arrowCenterY = (int) (yPos + heightArrowLength) / 2;
                graphics.drawString(messageHeight, xPos - fontWidth - textMargin, -arrowCenterY);

                break;
        }
    }

    /**
     * Draw a line.
     *
     * @param _graphics Graphic objet.
     * @param _startX   Starting X position of the line.
     * @param _startY   Starting Y position of the line.
     * @param _endX     Ending X position of the line.
     * @param _endY     Ending Y position of the line.
     * @param _color    Line color.
     */
    private void drawPrimitiveLine(Graphics _graphics, int _startX, int _startY, int _endX, int _endY, Color _color) {
        _graphics.setColor(_color);
        _graphics.drawLine(
                _startX * (int) inchUnitInPixel, _startY * (int) inchUnitInPixel,
                _endX * (int) inchUnitInPixel, _endY * (int) inchUnitInPixel
        );
    }

    /**
     * Draw component rectangle. Represent visible pieces.
     *
     * @param _graphics          Graphic objet.
     * @param _minPosition       Mix corner position.
     * @param _maxPosition       Max corner position.
     * @param _color             Component color.
     * @param _fill              True if the component is colored fill.
     * @param _drawingPanelIndex Drawing panel index.
     */
    private void drawPrimitiveRectangle(Graphics _graphics, Vector3 _minPosition, Vector3 _maxPosition, Color _color,
                                        boolean _fill, int _drawingPanelIndex) {
        Graphics2D graphics2D = (Graphics2D) _graphics;
        graphics2D.setColor(_color);

        Rectangle primitiveRectangle = getRectangle(_minPosition, _maxPosition, _drawingPanelIndex);

        if (_fill) {
            graphics2D.fillRect(
                    primitiveRectangle.x, primitiveRectangle.y, primitiveRectangle.width, primitiveRectangle.height);
        } else {
            int validStrokeSize = 6;

            graphics2D.setStroke(new BasicStroke(validStrokeSize));
            graphics2D.drawRect(
                    primitiveRectangle.x + validStrokeSize / 2, primitiveRectangle.y + validStrokeSize / 2,
                    primitiveRectangle.width - validStrokeSize, primitiveRectangle.height - validStrokeSize);
        }

        graphics2D.setColor(Color.BLACK);
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.drawRect(
                primitiveRectangle.x, primitiveRectangle.y, primitiveRectangle.width, primitiveRectangle.height);
    }

    /**
     * Draw dashed border over already drawn rectangle. Represent hidden pieces.
     *
     * @param _graphics2D        Graphic objet.
     * @param _minPosition       Mix corner position.
     * @param _maxPosition       Max corner position.
     * @param _drawingPanelIndex Drawing panel index.
     */
    private void drawHiddenWoodPiece(Graphics2D _graphics2D, Vector3 _minPosition, Vector3 _maxPosition,
                                     int _drawingPanelIndex) {
        _graphics2D.setColor(Color.BLACK);
        _graphics2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                new float[]{10.0f}, 0.0f));

        Rectangle hiddenWoodPiece = new Rectangle();

        switch (viewOrientationsWithIndex[_drawingPanelIndex]) {
            case SIDE:
                hiddenWoodPiece.setBounds(
                        (int) (_minPosition.x * inchUnitInPixel),
                        (int) (-_maxPosition.y * inchUnitInPixel),
                        (int) (_maxPosition.x * inchUnitInPixel - _minPosition.x * inchUnitInPixel),
                        (int) (-_minPosition.y * inchUnitInPixel - -_maxPosition.y * inchUnitInPixel)
                );
                break;
            case TOP:
                hiddenWoodPiece.setBounds(
                        (int) (_minPosition.x * inchUnitInPixel),
                        (int) (-_maxPosition.z * inchUnitInPixel),
                        (int) (_maxPosition.x * inchUnitInPixel - _minPosition.x * inchUnitInPixel),
                        (int) (-_minPosition.z * inchUnitInPixel - -_maxPosition.z * inchUnitInPixel)
                );
                break;
            case FACE:
                hiddenWoodPiece.setBounds(
                        (int) (_minPosition.z * inchUnitInPixel),
                        (int) (-_maxPosition.y * inchUnitInPixel),
                        (int) (_maxPosition.z * inchUnitInPixel - _minPosition.z * inchUnitInPixel),
                        (int) (-_minPosition.y * inchUnitInPixel - -_maxPosition.y * inchUnitInPixel)
                );
                break;
        }

        _graphics2D.drawRect(hiddenWoodPiece.x, hiddenWoodPiece.y, hiddenWoodPiece.width, hiddenWoodPiece.height);
    }

    /**
     * Draw an arrow on the graphics object from a starting point and ending point.
     *
     * @param _graphics         Graphics object.
     * @param _startPos         Starting position of the arrow.
     * @param _endPos           End position of the arrow.
     * @param _arrowOrientation 0 if the arrow is horizontal and 1 if the arrow is vertical.
     */
    private void drawArrow(Graphics _graphics, Point _startPos, Point _endPos, int _arrowOrientation) {
        int arrowHeadLength = 40;

        _graphics.drawLine(_startPos.x, _startPos.y, _endPos.x, _endPos.y);

        if (_arrowOrientation == 0) {
            _graphics.drawLine(_startPos.x, _startPos.y, _startPos.x + arrowHeadLength, _startPos.y + arrowHeadLength);
            _graphics.drawLine(_startPos.x, _startPos.y, _startPos.x + arrowHeadLength, _startPos.y - arrowHeadLength);

            _graphics.drawLine(_endPos.x, _endPos.y, _endPos.x - arrowHeadLength, _endPos.y + arrowHeadLength);
            _graphics.drawLine(_endPos.x, _endPos.y, _endPos.x - arrowHeadLength, _endPos.y - arrowHeadLength);
        } else {
            _graphics.drawLine(_startPos.x, _startPos.y, _startPos.x - arrowHeadLength, _startPos.y - arrowHeadLength);
            _graphics.drawLine(_startPos.x, _startPos.y, _startPos.x + arrowHeadLength, _startPos.y - arrowHeadLength);

            _graphics.drawLine(_endPos.x, _endPos.y, _endPos.x - arrowHeadLength, _endPos.y + arrowHeadLength);
            _graphics.drawLine(_endPos.x, _endPos.y, _endPos.x + arrowHeadLength, _endPos.y + arrowHeadLength);
        }
    }

    /**
     * Get rectangle value according to view.
     *
     * @param _minPosition Minimal corner position.
     * @param _maxPosition Maximal corner position.
     * @return Rectangle size according to view orientation.
     */
    private Rectangle getRectangle(Vector3 _minPosition, Vector3 _maxPosition, int _index) {
        Rectangle rectangle = new Rectangle();

        switch (viewOrientationsWithIndex[_index]) {
            case SIDE:
                rectangle.setBounds(
                        (int) (_minPosition.x * inchUnitInPixel),
                        (int) (-_maxPosition.y * inchUnitInPixel),
                        (int) (_maxPosition.x * inchUnitInPixel - _minPosition.x * inchUnitInPixel),
                        (int) (-_minPosition.y * inchUnitInPixel - -_maxPosition.y * inchUnitInPixel)
                );
                break;
            case TOP:
                rectangle.setBounds(
                        (int) (_minPosition.x * inchUnitInPixel),
                        (int) (-_maxPosition.z * inchUnitInPixel),
                        (int) (_maxPosition.x * inchUnitInPixel - _minPosition.x * inchUnitInPixel),
                        (int) (-_minPosition.z * inchUnitInPixel - -_maxPosition.z * inchUnitInPixel)
                );
                break;
            case FACE:
                rectangle.setBounds(
                        (int) (_minPosition.z * inchUnitInPixel),
                        (int) (-_maxPosition.y * inchUnitInPixel),
                        (int) (_maxPosition.z * inchUnitInPixel - _minPosition.z * inchUnitInPixel),
                        (int) (-_minPosition.y * inchUnitInPixel - -_maxPosition.y * inchUnitInPixel)
                );
                break;
        }

        return rectangle;
    }

    /**
     * Draw a border around hover WoodPiece. The border take the actual theme selection color.
     *
     * @param _graphics          Graphics object.
     * @param _woodPiece         Hovered WoodPiece.
     * @param _drawingPanelIndex Drawing panel index.
     */
    public void drawHoverWoodPiece(Graphics _graphics, WoodPiece _woodPiece, int _drawingPanelIndex) {
        Graphics2D graphics2D = (Graphics2D) _graphics;

        Color selectionColor = javax.swing.UIManager.getDefaults().getColor("MenuItem.selectionBackground");
        graphics2D.setColor(selectionColor);

        Rectangle rectangle =
                getRectangle(_woodPiece.getMinCornerPosition(), _woodPiece.getMaxCornerPosition(), _drawingPanelIndex);

        graphics2D.setStroke(new BasicStroke(10));
        graphics2D.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    /**
     * Draw an information panel in the image corner.
     *
     * @param _graphics2D      Graphics object.
     * @param _position        Information panel's position.
     * @param _viewOrientation Current view orientation.
     */
    public void drawInformationPanel(Graphics2D _graphics2D, Point _position, ViewOrientation _viewOrientation) {
        _graphics2D.setFont(new Font("SansSerif", Font.BOLD, 24));
        FontMetrics fontMetrics = _graphics2D.getFontMetrics();
        int fontHeight = fontMetrics.getHeight();

        String fileName =
                patioController.getCurrentFileName() != null ? patioController.getCurrentFileName() : "patio.ptor";
        int filenameStartIndex = fileName.lastIndexOf("\\");
        String fileNameWithoutPath = fileName.substring(filenameStartIndex + 1);

        String informationPanelText =
                LocaleText.getString("PROJECT_FILE_LABEL") + " : " + fileNameWithoutPath +
                        "\n" + LocaleText.getString("PROJECT_VIEW_LABEL") + " : " +
                        LocaleText.getString(_viewOrientation.toString() + "_VIEW") +
                        "\n" + LocaleText.getString("IMAGE_EXPORT_FOOT_REPRESENTATION_LABEL") + " : ";

        int lineQuantity, maxLength = 0;

        String[] informationPanelTextArray = informationPanelText.split("\n");
        lineQuantity = informationPanelTextArray.length + 1;
        if (informationPanelTextArray.length > 0) {
            for (String line : informationPanelTextArray) {
                maxLength = Math.max(maxLength, _graphics2D.getFontMetrics().stringWidth(line));
            }
        }

        int panelMargin = 25;
        int panelWidth = 500;
        int panelHeight = fontHeight * lineQuantity + fontHeight * 2;

        Point panelPosition = new Point(_position.x + panelMargin, _position.y - panelMargin - panelHeight);
        Point textPosition = new Point(panelPosition.x + fontHeight, panelPosition.y);

        Color base = javax.swing.UIManager.getDefaults().getColor("MenuItem.selectionBackground");
        Color backgroundColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), 200);

        _graphics2D.setColor(backgroundColor);
        _graphics2D.fillRect(panelPosition.x, panelPosition.y, panelWidth, panelHeight);

        _graphics2D.setColor(javax.swing.UIManager.getDefaults().getColor("MenuItem.selectionForeground"));
        for (String line : informationPanelText.split("\n")) {
            _graphics2D.drawString(line, textPosition.x, textPosition.y += fontHeight);
        }

        _graphics2D.fillRect(textPosition.x, textPosition.y + fontHeight, (int) inchUnitInPixel * 12, fontHeight);
        _graphics2D.setStroke(new BasicStroke());
        _graphics2D.drawRect(panelPosition.x + 4, panelPosition.y + 4, panelWidth - 10, panelHeight - 10);
    }
}
