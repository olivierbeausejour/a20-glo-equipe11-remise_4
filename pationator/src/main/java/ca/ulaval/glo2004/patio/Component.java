package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Vector3;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class represent a patio component.
 */
public abstract class Component {
    protected boolean isVisible;
    protected boolean isColorFill;
    protected Color color;
    protected ArrayList<WoodPiece> woodPieces;

    /**
     * Create a patio component.
     */
    public Component() {
        isVisible = true;
        woodPieces = new ArrayList<>();
    }

    /**
     * Get the Component's WoodPieces array.
     *
     * @return Array of WoodPieces.
     */
    public ArrayList<WoodPiece> getWoodPieces() {
        return woodPieces;
    }

    /**
     * Get the current visibility state of the component.
     *
     * @return True if the component is visible for the user.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Set the visibility of the component.
     *
     * @param _visible True if the component is visible for the user.
     */
    public void setVisible(boolean _visible) {
        isVisible = _visible;
    }

    /**
     * Get the color use for the component drawing.
     *
     * @return Current color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the component color use for drawing.
     *
     * @param _color Component new color.
     */
    public void setColor(Color _color) {
        color = _color;
    }

    /**
     * Fill component is displayed or only the border (wireframe).
     *
     * @param _isColorFill True if the component is full.
     */
    public void setColorFilling(boolean _isColorFill) {
        isColorFill = _isColorFill;
    }

    /**
     * Fill component is displayed or only the border (wireframe).
     *
     * @return True if the component is full.
     */
    public boolean isColorFill() {
        return isColorFill;
    }

    /**
     * Get total dimension of all the woodpieces composing the component.
     *
     * @return Dimensions of the global component.
     */
    public Dimensions getTotalDimension() {
        float width = 0, height = 0, depth = 0;

        for (WoodPiece woodPiece : woodPieces) {
            width += woodPiece.getDimensions().getActualWidth();
            height += woodPiece.getDimensions().getActualHeight();
            depth += woodPiece.getDimensions().getActualDepth();
        }

        return new Dimensions(width, height, depth);
    }

    /**
     * Get global position of all the woodpieces composing the component.
     *
     * @return Position of the global component.
     */
    public Vector3 getGlobalPosition() {
        float posX = 0, posY = 0, posZ = 0;

        for (WoodPiece woodPiece : woodPieces) {
            posX += woodPiece.getCentralPosition().x;
            posY += woodPiece.getCentralPosition().y;
            posZ += woodPiece.getCentralPosition().z;
        }
        int piecesQuantity = woodPieces.size();

        return new Vector3(posX / piecesQuantity, posY / piecesQuantity, posZ / piecesQuantity);
    }

    /**
     * Get global nominal dimensions of all the woodpieces composing the component.
     *
     * @return Nominal dimensions.
     */
    public Vector3 getGlobalNominal() {
        float width = 0, height = 0;

        for (WoodPiece woodPiece : woodPieces) {
            width = woodPiece.getDimensions().getNominalWidth();
            height = woodPiece.getDimensions().getNominalHeight();
        }

        return new Vector3(width, height, 0);
    }

    /**
     * Get component type.
     *
     * @return String of the component type.
     */
    public String getType() {
        return LocaleText.getString(woodPieces.get(0).getOrientation().toString());
    }

    /**
     * Change height, depth and width to match the correct orientation
     *
     * @param _dimensions    Current woodPieces orientation.
     * @param _componentType Current component type.
     * @return Dimensions object where height represent height in 3d, etc.
     */
    public static Dimensions getFixedOrientation(Dimensions _dimensions, ComponentType _componentType) {
        float height = _dimensions.getActualHeight();
        float depth = _dimensions.getActualDepth();
        float width = _dimensions.getActualWidth();

        float newHeight, newDepth, newWidth;

        switch (_componentType) {
            case COVERING_PLANK:
                newHeight = depth;
                newDepth = height;
                newWidth = width;
                break;
            case JOIST:
                newHeight = height;
                newDepth = width;
                newWidth = depth;
                break;
            case BEAM:
                newHeight = depth;
                newDepth = width;
                newWidth = height;
                break;
            case POST:
            default:
                newHeight = height;
                newDepth = depth;
                newWidth = width;
                break;
        }

        return new Dimensions(newWidth, newHeight, newDepth);
    }

    /**
     * Fix WoodPiece position for STL export.
     *
     * @param _woodPiece        WoodPiece to fix.
     * @param _fixedOrientation Component type.
     * @return WoodPiece with new position.
     */
    public static WoodPiece getFixedPosition(WoodPiece _woodPiece, Dimensions _fixedOrientation) {
        ComponentType componentType = _woodPiece.getOrientation();
        float xPos, yPos, zPos;

        xPos = _woodPiece.getCentralPosition().x;
        yPos = _woodPiece.getCentralPosition().z;
        zPos = _woodPiece.getCentralPosition().y;

        return new WoodPiece(_fixedOrientation, new Vector3(xPos, yPos, zPos), componentType);
    }
}






