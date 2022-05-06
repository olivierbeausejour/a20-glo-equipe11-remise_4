package ca.ulaval.glo2004.ddd;

import ca.ulaval.glo2004.patio.Component;
import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.patio.WoodPiece;
import ca.ulaval.glo2004.utils.Dimensions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * 3D view JPanel.
 */
public class Entry extends JPanel implements KeyListener, MouseWheelListener, MouseListener, MouseMotionListener {
    static public int polygonsNumber = 0;
    static public double[] viewFrom;
    static public double[] viewTo;
    static public Dimension dimension;
    static ArrayList<FacePolygon> drawableFace;
    static double zoom = 10, minZoomFactor = 7, maxDezoomFactor = 25;
    static double previousX;
    static double previousY;
    private final Vector<Face3dPolygon[]> woodPieces3d;
    double sleepTime = 1000.0 / 30.0, lastRefresh = 0;
    int[] newOrder;
    boolean[] keys = new boolean[10];
    boolean[] mouseBtn = new boolean[4];
    double verticalLook = 0, horizontalLook = 0, horizontalRotationSpeed = 5, verticalRotationSpeed = 5;
    Robot robot;
    PatioController patioController;
    private final Face3dPolygon[] ground;
    private int MouseX;
    private int MouseY;

    /**
     * Create a 3D view JPanel.
     */
    public Entry(PatioController _patioController) {
        patioController = _patioController;

        dimension = new Dimension();

        woodPieces3d = new Vector<>();
        drawableFace = new ArrayList<>();
//
//        float viewToZ = patioController.getPatio().getPatioInfo().getPostDimensions().getActualWidth() / 2;
//        float viewToY = patioController.getPatio().getPatioInfo().getPostDimensions().getActualDepth() / 2;
//        float viewToX = patioController.getPatio().getPatioInfo().getPostDimensions().getActualHeight() / 2;
//        viewTo = new double[]{viewToX, viewToY, viewToZ};
//        viewFrom = new double[]{viewToX + 45, viewToY + 45, viewToZ + 45};
//
        float viewFromX = -813.6680903622079f;
        float viewFromY = -824.2776743733772f;
        float viewFromZ = 1171.7418392731597f;

        float viewToX = -742.8706413809267f;
        float viewToY = -754.7296584321366f;
        float viewToZ = 1084.968321669038f;

        viewTo = new double[]{viewToX, viewToY, viewToZ};
        viewFrom = new double[]{viewFromX, viewFromY, viewFromZ};

        updateComponent(_patioController);
        ground = getGround();

//        addMouseListener(this);
//        addMouseMotionListener(this);
//        addMouseWheelListener(this);
        addKeyListener(this);
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent event) {
                setComponentInformationBounds();
            }

            @Override
            public void componentMoved(ComponentEvent event) {
            }

            @Override
            public void componentShown(ComponentEvent event) {
            }

            @Override
            public void componentHidden(ComponentEvent event) {
            }

            private void setComponentInformationBounds() {
                dimension.setSize(getWidth(), getHeight());
            }
        });

        setFocusable(true);
    }

    private WoodPiece fixWoodPieceFor3d(WoodPiece woodPiece) {
        float xPos, yPos, zPos, height, depth, width;

        Dimensions fixedOrientation = Component.getFixedOrientation(
                woodPiece.getDimensions(), woodPiece.getOrientation());

        height = fixedOrientation.getActualHeight();
        depth = fixedOrientation.getActualDepth();
        width = fixedOrientation.getActualWidth();

        WoodPiece fixedPosition = Component.getFixedPosition(woodPiece, fixedOrientation);

        xPos = fixedPosition.getCentralPosition().x - (width / 2);
        yPos = fixedPosition.getCentralPosition().y - (height / 2);
        zPos = fixedPosition.getCentralPosition().z - (depth / 2);
        fixedPosition.setCentralPosition(xPos, yPos, zPos);

        return fixedPosition;
    }

    private void updateComponent(PatioController _patioController) {
        for (Component component : _patioController.getPatio().getPosts()) {
            Color color = component.getColor();
            ArrayList<WoodPiece> woodPieces = component.getWoodPieces();

            for (WoodPiece woodPiece : woodPieces) {
                WoodPiece fixedWoodPiece = fixWoodPieceFor3d(woodPiece);
                woodPieces3d.add(get3dWoodPiece(fixedWoodPiece, color));
            }
        }

        for(Component component : _patioController.getPatio().getBeams()) {
            Color color = component.getColor();
            ArrayList<WoodPiece> woodPieces = component.getWoodPieces();

            for(WoodPiece woodPiece : woodPieces) {
                WoodPiece fixedWoodPiece = fixWoodPieceFor3d(woodPiece);
                woodPieces3d.add(get3dWoodPiece(fixedWoodPiece, color));
            }
        }

        for(Component component : _patioController.getPatio().getSpans()) {
            Color color = component.getColor();
            ArrayList<WoodPiece> woodPieces = component.getWoodPieces();

            for(WoodPiece woodPiece : woodPieces) {
                WoodPiece fixedWoodPiece = fixWoodPieceFor3d(woodPiece);
                woodPieces3d.add(get3dWoodPiece(fixedWoodPiece, color));
            }
        }

        for(Component component : _patioController.getPatio().getCovering()) {
            Color color = component.getColor();
            ArrayList<WoodPiece> woodPieces = component.getWoodPieces();

            for(WoodPiece woodPiece : woodPieces) {
                WoodPiece fixedWoodPiece = fixWoodPieceFor3d(woodPiece);
                woodPieces3d.add(get3dWoodPiece(fixedWoodPiece, color));
            }
        }
    }

    /**
     * Get a ground 3d element.
     *
     * @return face3dPolygon array representing a 3d element.
     */
    private Face3dPolygon[] getGround() {
        int groundPolygons = 0;
        int groundWidth = 10;
        int groundDepth = 10;

        Color base = Color.green;
        Color groundColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), 75);

        Face3dPolygon[] ground = new Face3dPolygon[groundWidth * groundDepth];

        for (int i = (groundWidth / 2) * -1; i < (groundWidth / 2); i++) {
            for (int j = (groundDepth / 2) * -1; j < (groundDepth / 2); j++) {
                ground[groundPolygons] = new Face3dPolygon(
                        new double[]{i, i, i + 1, i + 1},
                        new double[]{j, j + 1, j + 1, j},
                        new double[]{0, 0, 0, 0},
                        groundColor);
                ++groundPolygons;
            }
        }

        return ground;
    }

    /**
     * Get a ground 3d WoodPiece
     *
     * @return face3dPolygon array representing a 3d element.
     */
    private Face3dPolygon[] get3dWoodPiece(WoodPiece _woodPiece, Color _color) {
        Face3dPolygon[] woodPiece3d = new Face3dPolygon[6];

        double xPos = _woodPiece.getCentralPosition().x;
        double yPos = _woodPiece.getCentralPosition().y;
        double zPos = _woodPiece.getCentralPosition().z;
        double width = _woodPiece.getDimensions().getActualWidth();
        double height = _woodPiece.getDimensions().getActualHeight();
        double depth = _woodPiece.getDimensions().getActualDepth();

        woodPiece3d[0] = new Face3dPolygon(
                new double[]{xPos, xPos + width, xPos + width, xPos},
                new double[]{yPos, yPos, yPos + height, yPos + height},
                new double[]{zPos, zPos, zPos, zPos},
                _color);
        woodPiece3d[1] = new Face3dPolygon(
                new double[]{xPos, xPos + width, xPos + width, xPos},
                new double[]{yPos, yPos, yPos + height, yPos + height},
                new double[]{zPos + depth, zPos + depth, zPos + depth, zPos + depth},
                _color);
        woodPiece3d[2] = new Face3dPolygon(
                new double[]{xPos, xPos + width, xPos + width, xPos},
                new double[]{yPos, yPos, yPos, yPos},
                new double[]{zPos, zPos, zPos + depth, zPos + depth},
                _color);
        woodPiece3d[3] = new Face3dPolygon(
                new double[]{xPos, xPos + width, xPos + width, xPos},
                new double[]{yPos + height, yPos + height, yPos + height, yPos + height},
                new double[]{zPos, zPos, zPos + depth, zPos + depth},
                _color);
        woodPiece3d[4] = new Face3dPolygon(
                new double[]{xPos, xPos, xPos, xPos},
                new double[]{yPos, yPos + height, yPos + height, yPos},
                new double[]{zPos, zPos, zPos + depth, zPos + depth},
                _color);
        woodPiece3d[5] = new Face3dPolygon(
                new double[]{xPos + width, xPos + width, xPos + width, xPos + width},
                new double[]{yPos, yPos + height, yPos + height, yPos},
                new double[]{zPos, zPos, zPos + depth, zPos + depth},
                _color);

        return woodPiece3d;
    }

    /**
     * Paint drawable polygons.
     *
     * @param _graphics Graphics object.
     */
    public void paintComponent(Graphics _graphics) {
        controlsAccordingKeyboard();

        _graphics.clearRect(0, 0, this.getWidth(), this.getHeight());

        for (Face3dPolygon[] woodPiece : woodPieces3d) {
            for (int i = 0; i < 6; i++) {
                woodPiece[i].updatePolygon();
            }
        }

        for (Face3dPolygon groundPiece : ground) {
            groundPiece.updatePolygon();
        }

        updateDrawingOrder();

        for (int i = 0; i < polygonsNumber; i++) {
            drawableFace.get(newOrder[i]).drawPolygon(_graphics);
        }

        sleepAndRefresh();
    }

    /**
     * Move element according to keyboard input.
     */
    void controlsAccordingKeyboard() {
        Vector3D viewVector3D = new Vector3D(viewTo[0] - viewFrom[0],
                viewTo[1] - viewFrom[1],
                viewTo[2] - viewFrom[2]);

        Vector3D verticalVector3D = new Vector3D(0, 0, 1);
        Vector3D sideViewVector3D = viewVector3D.crossProduct(verticalVector3D);
        Vector3D topViewVector3D = viewVector3D.crossProduct(sideViewVector3D);

        boolean leftArrowKey = keys[0];
        boolean rightArrowKey = keys[1];
        boolean upArrowKey = keys[2];
        boolean downArrowKey = keys[3];
        boolean wKey = keys[4];
        boolean aKey = keys[5];
        boolean sKey = keys[6];
        boolean dKey = keys[7];

        if (wKey) {
            // Forward
            viewFrom[0] += viewVector3D.x;
            viewFrom[1] += viewVector3D.y;
            viewFrom[2] += viewVector3D.z;
            viewTo[0] += viewVector3D.x;
            viewTo[1] += viewVector3D.y;
            viewTo[2] += viewVector3D.z;
        }

        if (sKey) {
            // Backward
            viewFrom[0] -= viewVector3D.x;
            viewFrom[1] -= viewVector3D.y;
            viewFrom[2] -= viewVector3D.z;
            viewTo[0] -= viewVector3D.x;
            viewTo[1] -= viewVector3D.y;
            viewTo[2] -= viewVector3D.z;
        }

        if (aKey) {
            // Sideway left
            viewFrom[0] += sideViewVector3D.x;
            viewFrom[1] += sideViewVector3D.y;
            viewFrom[2] += sideViewVector3D.z;
            viewTo[0] += sideViewVector3D.x;
            viewTo[1] += sideViewVector3D.y;
            viewTo[2] += sideViewVector3D.z;
        }

        if (dKey) {
            // Sideway right
            viewFrom[0] -= sideViewVector3D.x;
            viewFrom[1] -= sideViewVector3D.y;
            viewFrom[2] -= sideViewVector3D.z;
            viewTo[0] -= sideViewVector3D.x;
            viewTo[1] -= sideViewVector3D.y;
            viewTo[2] -= sideViewVector3D.z;
        }

        if (upArrowKey) {
            // Vertical rotation up
            if (topViewVector3D.x + viewFrom[0] > 0 && viewFrom[0] > 0 ||
                    topViewVector3D.x + viewFrom[0] < 0 && viewFrom[0] < 0) {
                viewFrom[0] += topViewVector3D.x * verticalRotationSpeed;
                viewFrom[1] += topViewVector3D.y * verticalRotationSpeed;
                viewFrom[2] += topViewVector3D.z * verticalRotationSpeed;
            }
        }

        if (downArrowKey) {
            // Vertical rotation down
            if (viewFrom[0] - topViewVector3D.x > 0 && viewFrom[0] > 0 ||
                    viewFrom[0] - topViewVector3D.x < 0 && viewFrom[0] < 0) {
                viewFrom[0] -= topViewVector3D.x * verticalRotationSpeed;
                viewFrom[1] -= topViewVector3D.y * verticalRotationSpeed;
                viewFrom[2] -= topViewVector3D.z * verticalRotationSpeed;
            }
        }

        if (leftArrowKey) {
            // Vertical rotation left
            viewFrom[0] += sideViewVector3D.x * horizontalRotationSpeed;
            viewFrom[1] += sideViewVector3D.y * horizontalRotationSpeed;
            viewFrom[2] += sideViewVector3D.z * horizontalRotationSpeed;
        }

        if (rightArrowKey) {
            // Vertical rotation right
            viewFrom[0] -= sideViewVector3D.x * horizontalRotationSpeed;
            viewFrom[1] -= sideViewVector3D.y * horizontalRotationSpeed;
            viewFrom[2] -= sideViewVector3D.z * horizontalRotationSpeed;
        }
    }

    /**
     * Update drawing order.
     */
    void updateDrawingOrder() {
        double[] initialPolygonsDistance = new double[polygonsNumber];
        newOrder = new int[polygonsNumber];

        for (int i = 0; i < polygonsNumber; i++) {
            initialPolygonsDistance[i] = drawableFace.get(i).distance;
            newOrder[i] = i;
        }

        double tempDistance;
        int tempIndex;
        for (int i = 0; i < initialPolygonsDistance.length - 1; i++) {
            for (int j = 0; j < initialPolygonsDistance.length - 1; j++) {
                if (initialPolygonsDistance[j] < initialPolygonsDistance[j + 1]) {
                    tempDistance = initialPolygonsDistance[j];
                    tempIndex = newOrder[j];
                    newOrder[j] = newOrder[j + 1];
                    initialPolygonsDistance[j] = initialPolygonsDistance[j + 1];

                    newOrder[j + 1] = tempIndex;
                    initialPolygonsDistance[j + 1] = tempDistance;
                }
            }
        }
    }

    /**
     * 3d engine.
     */
    private void sleepAndRefresh() {
        while (true) {
            if ((System.currentTimeMillis() - lastRefresh) > sleepTime) {
                lastRefresh = System.currentTimeMillis();
//                updateComponent(patioController);
                repaint();

                break;
            } else {
                try {
                    Thread.sleep((long) (sleepTime - (System.currentTimeMillis() - lastRefresh)));
                } catch (Exception ignored) {

                }
            }
        }
    }

    /**
     * Behavior when a key is typed. Not in used.
     *
     * @param _keyEvent InputEvent.
     */
    @Override
    public void keyTyped(KeyEvent _keyEvent) {
    }

    /**
     * Behavior when a key is pressed. Change boolean value to true inside 'keys' array. Use to move camera according to
     * key input.
     *
     * @param _keyEvent InputEvent.
     */
    @Override
    public void keyPressed(KeyEvent _keyEvent) {
        if (_keyEvent.getKeyCode() == KeyEvent.VK_LEFT)
            keys[0] = true;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_RIGHT)
            keys[1] = true;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_UP)
            keys[2] = true;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_DOWN)
            keys[3] = true;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_W)
            keys[4] = true;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_A)
            keys[5] = true;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_S)
            keys[6] = true;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_D)
            keys[7] = true;
    }

    /**
     * Behavior when a key is released. Change boolean value to false inside 'keys' array. Use to stop moving camera.
     *
     * @param _keyEvent InputEvent.
     */
    @Override
    public void keyReleased(KeyEvent _keyEvent) {
        if (_keyEvent.getKeyCode() == KeyEvent.VK_LEFT)
            keys[0] = false;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_RIGHT)
            keys[1] = false;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_UP)
            keys[2] = false;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_DOWN)
            keys[3] = false;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_W)
            keys[4] = false;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_A)
            keys[5] = false;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_S)
            keys[6] = false;
        if (_keyEvent.getKeyCode() == KeyEvent.VK_D)
            keys[7] = false;
    }

    /**
     * Behavior when the mouse wheel is use. Mouse the user view but not the target.
     *
     * @param _mouseWheelEvent MouseEvent.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent _mouseWheelEvent) {
        if (_mouseWheelEvent.getWheelRotation() > 0) {
            if (zoom < maxDezoomFactor) {
                viewFrom[0] += 1;
                viewFrom[1] += 1;
                viewFrom[2] += 1;

                zoom += 1;
            }
        } else {
            if (zoom > minZoomFactor) {
                viewFrom[0] -= 1;
                viewFrom[1] -= 1;
                viewFrom[2] -= 1;

                zoom -= 1;
            }
        }
    }

    /**
     * Behavior when the mouse is dragged. Move camera sideways and vertically.
     *
     * @param _mouseEvent Java mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent _mouseEvent) {
        int newX = _mouseEvent.getX();
        int newY = _mouseEvent.getY();

        if (mouseBtn[3]) {
            Vector3D verticalVector3D = new Vector3D(0, 0, 1);

            double dragSpeed = 1.25;

            if (newY > previousY) {
                viewFrom[0] += verticalVector3D.x / dragSpeed;
                viewFrom[1] += verticalVector3D.y / dragSpeed;
                viewFrom[2] += verticalVector3D.z / dragSpeed;
                viewTo[0] += verticalVector3D.x / dragSpeed;
                viewTo[1] += verticalVector3D.y / dragSpeed;
                viewTo[2] += verticalVector3D.z / dragSpeed;
            }
            if (newY < previousY) {
                viewFrom[0] -= verticalVector3D.x / dragSpeed;
                viewFrom[1] -= verticalVector3D.y / dragSpeed;
                viewFrom[2] -= verticalVector3D.z / dragSpeed;
                viewTo[0] -= verticalVector3D.x / dragSpeed;
                viewTo[1] -= verticalVector3D.y / dragSpeed;
                viewTo[2] -= verticalVector3D.z / dragSpeed;
            }

            previousX = _mouseEvent.getX();
            previousY = _mouseEvent.getY();
        } else if (mouseBtn[1]) {
            updateMouseData(new Point(newX, newY));

            MouseX = _mouseEvent.getX();
            MouseY = _mouseEvent.getY();
            CenterMouse();
        }
    }

    /**
     * Update the mouse information.
     *
     * @param _mousePosition Point with X and Y mouse position.
     */
    void updateMouseData(Point _mousePosition) {
        double difX = (_mousePosition.getX() - Entry.dimension.getWidth() / 2);
        double difY = (_mousePosition.getY() - Entry.dimension.getHeight() / 2);
        difY *= 6 - Math.abs(verticalLook) * 5;
        verticalLook -= difY / verticalRotationSpeed;
        horizontalLook += difX / horizontalRotationSpeed;

        if (verticalLook > 0.999)
            verticalLook = 0.999;

        if (verticalLook < -0.999)
            verticalLook = -0.999;

        double r = Math.sqrt(1 - (verticalLook * verticalLook));
        viewTo[0] = viewFrom[0] + r * Math.cos(horizontalLook);
        viewTo[1] = viewFrom[1] + r * Math.sin(horizontalLook);
        viewTo[2] = viewFrom[2] + verticalLook;
    }

    /**
     * Center the mouse in the JPanel view.
     */
    private void CenterMouse() {
        try {
            robot = new Robot();
            robot.mouseMove((int) Entry.dimension.getWidth() / 2, (int) Entry.dimension.getHeight() / 2);
        } catch (AWTException _awtException) {
            _awtException.printStackTrace();
        }
    }

    /**
     * Behavior when mouse movement is detected. Only execute operation if the mouse button 1 is press.
     *
     * @param _mouseEvent Java mouse event.
     */
    @Override
    public void mouseMoved(MouseEvent _mouseEvent) {
        if (mouseBtn[1]) {
            updateMouseData(_mouseEvent.getPoint());

            MouseX = _mouseEvent.getX();
            MouseY = _mouseEvent.getY();
            CenterMouse();
        }
    }

    /**
     * Behavior when mouse click is detected. Not in used.
     *
     * @param _mouseEvent Java mouse event.
     */
    @Override
    public void mouseClicked(MouseEvent _mouseEvent) {

    }

    /**
     * Behavior when mouse button are press. Change 'mouseBtn' array value according the button press. Use to move
     * camera.
     *
     * @param _mouseEvent Java mouse event.
     */
    @Override
    public void mousePressed(MouseEvent _mouseEvent) {
        previousX = _mouseEvent.getX();
        previousY = _mouseEvent.getY();

        if (_mouseEvent.getButton() == MouseEvent.BUTTON1) {
            mouseBtn[1] = true;
        }
        if (_mouseEvent.getButton() == MouseEvent.BUTTON3) {
            mouseBtn[3] = true;
        }
    }

    /**
     * Behavior when mouse button are released. Change 'mouseBtn' array value to false according the button released.
     * Use to stop moving camera.
     *
     * @param _mouseEvent Java mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent _mouseEvent) {
        if (_mouseEvent.getButton() == MouseEvent.BUTTON1) {
            mouseBtn[1] = false;
        }

        if (_mouseEvent.getButton() == MouseEvent.BUTTON3) {
            mouseBtn[3] = false;
        }
    }

    /**
     * Behavior when mouse enter in JPanel. Not in used.
     *
     * @param _mouseEvent Java mouse event.
     */
    @Override
    public void mouseEntered(MouseEvent _mouseEvent) {

    }

    /**
     * Behavior when mouse exit the JPanel. Not in used.
     *
     * @param _mouseEvent Java mouse event.
     */
    @Override
    public void mouseExited(MouseEvent _mouseEvent) {

    }
}
