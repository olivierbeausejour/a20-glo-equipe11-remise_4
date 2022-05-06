package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.Component;
import ca.ulaval.glo2004.patio.MeasureUnit;
import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.patio.WoodPiece;
import ca.ulaval.glo2004.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * JPanel where Pationator draw patio.
 */
public class PatioDrawingPanel extends JPanel implements ComponentListener, Serializable {
    private final int inchUnitInPixel = 24;
    private final Vector2 pixelMousePosition = new Vector2(0.0f, 0.0f);
    private final Vector2 pixelLastMousePosition = new Vector2(0.0f, 0.0f);
    private final PatioDrawingArea patioDrawingArea;
    private final ViewButtons viewButtons;
    private final ComponentInformation componentInformation;
    private final AffineTransform viewTransform = new AffineTransform();
    private final PationatorWindow pationatorWindow;
    private final PatioController patioController;
    private final int drawingPanelIndex;
    private final int drawingPanelArrayIndex;
    private boolean scaling = false;
    private float lastViewScale = 0;

    /**
     * Create a PatioDrawingPanel.
     *
     * @param _pationatorWindow Windows containing the drawing panel.
     * @param _index            Drawing panel index. Used to have mutliple drawing panel on the screen.
     */
    PatioDrawingPanel(PationatorWindow _pationatorWindow, int _index, int _arrayIndex) {
        pationatorWindow = _pationatorWindow;
        patioController = _pationatorWindow.getPatioController();

        drawingPanelIndex = _index;
        drawingPanelArrayIndex = _arrayIndex;

        patioDrawingArea = new PatioDrawingArea();
        patioDrawingArea.setBackground(javax.swing.UIManager.getDefaults().getColor("TitlePane.background"));

        setLayout(new BorderLayout());
        addComponentListener(this);

        viewButtons = new ViewButtons(patioDrawingArea);
        componentInformation = new ComponentInformation();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(patioDrawingArea, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(viewButtons, JLayeredPane.POPUP_LAYER);
        layeredPane.add(componentInformation, JLayeredPane.DRAG_LAYER);

        add(layeredPane);
    }

    /**
     * Get drawing panel index.
     *
     * @return Index of the drawing panel.
     */
    public int getIndex() {
        return drawingPanelIndex;
    }

    /**
     * Behavior when the panel is resize.
     *
     * @param _componentEvent Java component event.
     */
    @Override
    public void componentResized(ComponentEvent _componentEvent) {
        Vector2 viewCenter = new Vector2((float) getWidth() * 0.05f, (float) getHeight() * 0.8f);

        patioController.setViewCenter(viewCenter, drawingPanelIndex);

        patioDrawingArea.setBounds(0, 0, getWidth(), getHeight());
        viewButtons.setBounds(getWidth() - 200, 0, 200, 40);
        componentInformation.setBounds(0, 0, getWidth(), getHeight());
    }

    /**
     * Behavior when the panel move.
     *
     * @param _componentEvent Java component event.
     */
    @Override
    public void componentMoved(ComponentEvent _componentEvent) {
    }

    /**
     * Behavior when the panel is show.
     *
     * @param _componentEvent Java component event.
     */
    @Override
    public void componentShown(ComponentEvent _componentEvent) {
    }

    /**
     * Behavior when the panel is hide.
     *
     * @param _componentEvent Java component event.
     */
    @Override
    public void componentHidden(ComponentEvent _componentEvent) {
    }

    /**
     * Detects if the mouse is over a component and displays information about the component if there is one.
     *
     * @param _mouse Mouse custom grid position.
     */
    private void showComponentInformationPanel(Point _mouse) {
        Pair<Component, WoodPiece> componentPiecePair = patioController.getComponentAndPieceAtMouse(drawingPanelIndex);

        if (componentPiecePair != null) {
            componentInformation.setVisible(true);
            componentInformation.setMouse(_mouse);
            componentInformation.setComponent(componentPiecePair.first);
            componentInformation.setWoodPiece(componentPiecePair.second);
        } else {
            componentInformation.setVisible(false);
        }

        componentInformation.repaint();
    }

    /**
     * Update the view selection button.
     *
     * @param _viewOrientation Current view orientation.
     */
    public void updateViewButtons(ViewOrientation _viewOrientation) {
        viewButtons.updateButtons(_viewOrientation);
    }

    // Inner classes of objects to draw in the PatioDrawingPanel

    /**
     * Refresh patioDrawingArea background color.
     */
    public void setPatioDrawingAreaBackground() {
        patioDrawingArea.setBackground(javax.swing.UIManager.getDefaults().getColor("TitlePane.background"));
    }

    /**
     * JPanel containing view buttons.
     */
    private class ViewButtons extends JPanel {
        private final JRadioButton optionFace;
        private final JRadioButton optionSide;
        private final JRadioButton optionTop;

        private final Container parent;

        /**
         * Create a JPanel containing a view selector button.
         */
        public ViewButtons(PatioDrawingArea _patioDrawingArea) {
            parent = _patioDrawingArea;

            this.setOpaque(false);

            ButtonGroup group = new ButtonGroup();

            // Create view radio buttons
            optionFace = new JRadioButton(LocaleText.getString("FACE_VIEW"));
            optionFace.setOpaque(false);
            optionFace.setContentAreaFilled(false);
            optionFace.setBorderPainted(false);
            optionSide = new JRadioButton(LocaleText.getString("SIDE_VIEW"));
            optionSide.setOpaque(false);
            optionSide.setContentAreaFilled(false);
            optionSide.setBorderPainted(false);
            optionTop = new JRadioButton(LocaleText.getString("TOP_VIEW"));
            optionTop.setOpaque(false);
            optionTop.setContentAreaFilled(false);
            optionTop.setBorderPainted(false);

            // Add buttons to group
            group.add(optionFace);
            group.add(optionSide);
            group.add(optionTop);

            // Initialize radio buttons values
            optionFace.setSelected(drawingPanelIndex == 0);
            optionSide.setSelected(drawingPanelIndex == 1);
            optionTop.setSelected(drawingPanelIndex == 2);

            // Add buttons to self
            this.add(optionFace);
            this.add(optionSide);
            this.add(optionTop);

            //Add listener to buttons
            optionFace.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent _actionEvent) {
                    patioController.setViewOrientation(ViewOrientation.FACE, drawingPanelIndex);
                    centerView();
                    parent.repaint();
                }
            });

            optionSide.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent _actionEvent) {
                    patioController.setViewOrientation(ViewOrientation.SIDE, drawingPanelIndex);
                    centerView();
                    parent.repaint();
                }
            });

            optionTop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent _actionEvent) {
                    patioController.setViewOrientation(ViewOrientation.TOP, drawingPanelIndex);
                    centerView();
                    parent.repaint();
                }
            });
        }

        /**
         * Bring the patio in the center of the drawing panel.
         */
        private void centerView() {
            int xPos = parent.getWidth() / 2;
            int yPos = parent.getHeight() / 2;

            patioController.setCenterView(new Point(xPos, yPos), drawingPanelIndex);
        }

        /**
         * Update view selection buttons.
         *
         * @param _viewOrientation Current view orientation.
         */
        public void updateButtons(ViewOrientation _viewOrientation) {
            optionFace.setSelected(_viewOrientation == ViewOrientation.FACE);
            optionSide.setSelected(_viewOrientation == ViewOrientation.SIDE);
            optionTop.setSelected(_viewOrientation == ViewOrientation.TOP);
        }
    }

    /**
     * JPanel containing the patio drawing
     */
    private class PatioDrawingArea extends JPanel implements MouseMotionListener, MouseWheelListener, MouseListener,
            HoveredWoodPieceListener {
        private WoodPiece woodPiece;

        public PatioDrawingArea() {
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            addMouseListener(this);

            patioController.addHoveredWoodPiecesListener(this);
        }

        /**
         * Behavior when the patio drawing panel repaint his content.
         *
         * @param _graphics Graphics object.
         */
        @Override
        protected void paintComponent(Graphics _graphics) {
            if (pationatorWindow != null) {
                float viewScale = patioController.getZoomFactor(drawingPanelIndex);
                Vector2 viewCenter = patioController.getViewCenter(drawingPanelIndex);

                Graphics2D _g2D = (Graphics2D) _graphics;
                super.paintComponent(_g2D);

                viewTransform.setToIdentity();
                Vector2 mousePosGridBeforeScale = getGridMousePositionFromScale(lastViewScale);
                Vector2 mousePosGridAfterScale = getGridMousePositionFromScale(viewScale);

                float offsetX = (mousePosGridAfterScale.x - mousePosGridBeforeScale.x) * (inchUnitInPixel * viewScale);
                float offsetY = -(mousePosGridAfterScale.y - mousePosGridBeforeScale.y) * (inchUnitInPixel * viewScale);

                if (scaling) {
                    viewTransform.scale(viewScale, viewScale);
                    viewCenter.x += offsetX;
                    viewCenter.y += offsetY;
                    scaling = false;
                } else {
                    viewTransform.scale(viewScale, viewScale);
                }

                _g2D.translate(viewCenter.x, viewCenter.y);
                _g2D.transform(viewTransform);

                patioController.drawPatio(_graphics, inchUnitInPixel, drawingPanelIndex);

                if (woodPiece != null) {
                    patioController.drawHoveredWoodPieces(_graphics, woodPiece, drawingPanelIndex);
                }

                _g2D.dispose();
            }
        }

        /**
         * Get the mouse position inside the grid according to scale.
         *
         * @param _scale Used scale factor.
         * @return Mouse position inside the grid.
         */
        private Vector2 getGridMousePositionFromScale(float _scale) {
            return new Vector2(
                    (pixelMousePosition.x - patioController.getViewCenter(drawingPanelIndex).x) /
                            (float) inchUnitInPixel / _scale,
                    (pixelMousePosition.y + patioController.getViewCenter(drawingPanelIndex).y) /
                            (float) inchUnitInPixel / _scale
            );
        }

        /**
         * Behavior when mouse drag is detected.
         *
         * @param _mouseEvent Java mouse event.
         */
        @Override
        public void mouseDragged(MouseEvent _mouseEvent) {
            updateMouseData(_mouseEvent.getPoint());

            Vector2 viewCenter = patioController.getViewCenter(drawingPanelIndex);

            viewCenter.x -= pixelLastMousePosition.x - pixelMousePosition.x;
            viewCenter.y += pixelLastMousePosition.y - pixelMousePosition.y;

            patioController.setViewCenter(viewCenter, drawingPanelIndex);

            componentInformation.setVisible(false);

            repaint();
        }

        /**
         * Update the mouse information based on the view scale and the view center translation.
         *
         * @param _mousePosition Point with X and Y mouse position.
         */
        private void updateMouseData(Point _mousePosition) {
            pixelLastMousePosition.x = pixelMousePosition.x;
            pixelLastMousePosition.y = pixelMousePosition.y;

            pixelMousePosition.x = _mousePosition.x;
            pixelMousePosition.y = -_mousePosition.y;

            Vector2 gridMousePosition =
                    getGridMousePositionFromScale(pationatorWindow.getPatioController().getZoomFactor(drawingPanelIndex));

            patioController.setGridMousePositionInches(gridMousePosition, drawingPanelIndex);

            showComponentInformationPanel(_mousePosition);
        }

        /**
         * Behavior when mouse movement is detected.
         *
         * @param _mouseEvent Java mouse event.
         */
        @Override
        public void mouseMoved(MouseEvent _mouseEvent) {
            updateMouseData(_mouseEvent.getPoint());

            Pair<Component, WoodPiece> woodPiece = patioController.getComponentAndPieceAtMouse(drawingPanelIndex);
            if (woodPiece != null) {
                patioController.setHoveredWoodPieces(woodPiece.second);
            } else {
                patioController.setHoveredWoodPieces(null);
            }
        }

        /**
         * Behavior when mouse wheel movement is detected.
         *
         * @param _mouseWheelEvent Java mouse wheel event.
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent _mouseWheelEvent) {
            if (scaling)
                return;

            float viewScale = patioController.getZoomFactor(drawingPanelIndex);
            lastViewScale = viewScale;

            componentInformation.setVisible(false);


            if (_mouseWheelEvent.getWheelRotation() < 0) {
                scaling = true;
                patioController.setZoomFactor(viewScale * 1.1f, drawingPanelIndex);
            } else if (_mouseWheelEvent.getWheelRotation() > 0) {
                scaling = true;
                patioController.setZoomFactor(viewScale / 1.1f, drawingPanelIndex);
            }

            repaint();
        }

        /**
         * Behavior when mouse is click.
         *
         * @param _mouseEvent MouseEvent
         */
        @Override
        public void mouseClicked(MouseEvent _mouseEvent) {

        }

        /**
         * Behavior when mouse button is press.
         *
         * @param _mouseEvent MouseEvent
         */
        @Override
        public void mousePressed(MouseEvent _mouseEvent) {

        }

        /**
         * Behavior when mouse button is release.
         *
         * @param _mouseEvent MouseEvent
         */
        @Override
        public void mouseReleased(MouseEvent _mouseEvent) {

        }

        /**
         * Behavior when mouse enter in JPanel. Change parent drawing panel index.
         *
         * @param _mouseEvent MouseEvent
         */
        @Override
        public void mouseEntered(MouseEvent _mouseEvent) {
            pationatorWindow.setActiveDrawingPanelIndex(drawingPanelArrayIndex);
        }

        /**
         * Behavior when mouse exit the JPanel. Hide the component information.
         *
         * @param _mouseEvent MouseEvent
         */
        @Override
        public void mouseExited(MouseEvent _mouseEvent) {
            componentInformation.setVisible(false);
            repaint();
        }

        @Override
        public void setHoveredWoodPiece(WoodPiece _woodPiece) {
            woodPiece = _woodPiece;
            repaint();
        }
    }

    /**
     * Represents the information of the component being hovered over.
     */
    private class ComponentInformation extends JComponent {
        private Component component;
        private WoodPiece woodPiece;
        private Vector3 globalNominal;
        private Point mouse;
        private boolean isVisible;

        /**
         * Creates a component information panel.
         */
        public ComponentInformation() {
        }

        /**
         * Paint information panel on the drawing panel.
         *
         * @param _graphics ComponentInformation graphics.
         */
        @Override
        protected void paintComponent(Graphics _graphics) {
            super.paintComponent(_graphics);

            if (isVisible && component.isVisible()) {
                String tooltipText = getComponentInformationString();

                int rectWidth = 250, rectHeight = 150;
                int alpha = 200;

                int currentMouseY = mouse.y;

                Color text = javax.swing.UIManager.getDefaults().getColor("MenuItem.selectionForeground");
                Color base = javax.swing.UIManager.getDefaults().getColor("MenuItem.selectionBackground");
                Color backgroundColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);

                _graphics.setColor(backgroundColor);
                _graphics.fillRect(mouse.x, currentMouseY, rectWidth, rectHeight);

                _graphics.setColor(text);
                _graphics.drawRect(mouse.x + 2, currentMouseY + 2, rectWidth - 5, rectHeight - 5);

                for (String line : tooltipText.split("\n")) {
                    _graphics.drawString(line, mouse.x + 10, currentMouseY += _graphics.getFontMetrics().getHeight());
                }
            }
        }

        /**
         * Create a string with every information needed from the component.
         *
         * @return Complete string to display.
         */
        private String getComponentInformationString() {
            MeasureUnit measureUnit = patioController.getMeasureUnit();

            NumberFormat numberFormat = NumberFormat.getInstance(Locale.CANADA);
            numberFormat.setMaximumFractionDigits(3);

            String dimension;
            float width, height, depth;

            Dimensions dimensions = woodPiece.getDimensions();

            if (measureUnit == MeasureUnit.METRIC) {
                width = Conversion.getMillimeterFromNominal(dimensions.getNominalWidth());
                height = Conversion.getMillimeterFromNominal(dimensions.getNominalHeight());
                depth = Conversion.getMillimeterFromActual(dimensions.getActualDepth());

                String widthString, heightString, depthString;
                switch (patioController.getRationalFormat()) {
                    case "decimal":
                        widthString = numberFormat.format(width);
                        heightString = numberFormat.format(height);
                        depthString = numberFormat.format(depth);
                        break;
                    case "fraction":
                    default:
                        widthString = new Fraction(width).toString();
                        heightString = new Fraction(height).toString();
                        depthString = new Fraction(depth).toString();
                        break;
                }

                dimension = LocaleText.getString("COMPONENT_DIMENSIONS_LABEL") + "\n    " +
                        LocaleText.getString("COMPONENT_ACTUAL_WIDTH") + " " + widthString + " " +
                        LocaleText.getString("MILLIMETRE_SUFFIX") + "\n    " +
                        LocaleText.getString("COMPONENT_ACTUAL_HEIGHT") + " " + heightString + " " +
                        LocaleText.getString("MILLIMETRE_SUFFIX") + "\n    " +
                        LocaleText.getString("COMPONENT_ACTUAL_DEPTH") + " " + depthString + " " +
                        LocaleText.getString("MILLIMETRE_SUFFIX");
            } else {
                width = dimensions.getActualWidth();
                height = dimensions.getActualHeight();
                depth = dimensions.getActualDepth();

                String widthString, heightString, depthString;
                switch (patioController.getRationalFormat()) {
                    case "decimal":
                        widthString = numberFormat.format(width);
                        heightString = numberFormat.format(height);
                        depthString = numberFormat.format(depth);
                        break;
                    case "fraction":
                    default:
                        widthString = new Fraction(width).toString();
                        heightString = new Fraction(height).toString();
                        depthString = new Fraction(depth).toString();
                        break;
                }

                dimension = LocaleText.getString("COMPONENT_DIMENSIONS_LABEL") + "\n    " +
                        LocaleText.getString("COMPONENT_ACTUAL_WIDTH") + " " + widthString +
                        LocaleText.getString("INCHES") + "\n    " +
                        LocaleText.getString("COMPONENT_ACTUAL_HEIGHT") + " " + heightString +
                        LocaleText.getString("INCHES") + "\n    " +
                        LocaleText.getString("COMPONENT_ACTUAL_DEPTH") + " " + depthString +
                        LocaleText.getString("INCHES");
            }

            String type = component.getType() + "\n";

            String position = LocaleText.getString("COMPONENT_POSITION_LABEL") + " (" +
                    woodPiece.getCentralPosition().x +
                    ", " + woodPiece.getCentralPosition().y +
                    ", " + woodPiece.getCentralPosition().z + ")";

            String nominal = LocaleText.getString("COMPONENT_NOMINAL_LABEL") + " " +
                    globalNominal.y + "\" x " + globalNominal.x + "\"";

            String spaceStr = "                         ";

            return spaceStr + type + "\n" + position + "\n" + dimension + "\n" + nominal;
        }

        /**
         * Set information panel visibility.
         */
        @Override
        public void setVisible(boolean _visible) {
            isVisible = _visible;
        }

        /**
         * Position of the information panel.
         *
         * @param _mouse Mouse position.
         */
        public void setMouse(Point _mouse) {
            mouse = _mouse;
        }

        /**
         * Transfers information from the component to the information panel.
         *
         * @param _component Patio component hover over by mouse.
         */
        public void setComponent(Component _component) {
            component = _component;
            globalNominal = component.getGlobalNominal();
        }

        /**
         * Transfers information from the woodpiece to the information panel.
         *
         * @param _woodPiece Patio component woodpiece hover over by mouse.
         */
        public void setWoodPiece(WoodPiece _woodPiece) {
            woodPiece = _woodPiece;
        }
    }
}
