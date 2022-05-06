package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.Vector3;

public class WoodPiece {
    private Dimensions dimensions;
    private Dimensions buyingDimensions;
    private Vector3 centralPosition;
    private ComponentType componentType;
    private Vector3 minCornerPosition;
    private Vector3 maxCornerPosition;

    /**
     * The WoodPiece constructor
     * @param _dimensions The dimensions of the WoodPiece
     * @param _centralPosition The central position of the WoodPiece
     * @param _componentType The orientation of the WoodPiece
     */
    public WoodPiece(Dimensions _dimensions, Vector3 _centralPosition, ComponentType _componentType) {
        dimensions = _dimensions;
        buyingDimensions = _dimensions;
        centralPosition = _centralPosition;
        componentType = _componentType;
        computeCornerPositions();
    }

    /**
     * Obtains the dimensions of the WoodPiece
     * @return The dimensions of the WoodPiece
     */
    public Dimensions getDimensions() {
        return dimensions;
    }

    /**
     * Sets the dimensions of the WoodPiece
     * @param _dimensions The dimensions of the WoodPiece
     */
    public void setDimensions(Dimensions _dimensions) {
        dimensions = _dimensions;
        computeCornerPositions();
    }

    /**
     * Sets the dimensions of the WoodPiece
     * @param _width The width of the WoodPiece in actual inches
     * @param _height The height of the WoodPiece in actual inches
     * @param _depth The depth of the WoodPiece in actual inches
     */
    public void setDimensions(float _width, float _height, float _depth) {
        dimensions = new Dimensions(_width, _height, _depth);
        computeCornerPositions();
    }

    /**
     * Obtains the buying dimensions (the dimensions of the WoodPiece when it will be bought) of the WoodPiece
     * @return The buying dimensions
     */
    public Dimensions getBuyingDimensions() {
        return buyingDimensions;
    }

    /**
     * Sets the buying dimensions (the dimensions of the WoodPiece when it will be bought) of the WoodPiece
     * @param _buyingDimensions The dimensions of the WoodPiece when it will be bought
     */
    public void setBuyingDimensions(Dimensions _buyingDimensions) {
        buyingDimensions = _buyingDimensions;
    }

    /**
     * Sets the buying dimensions of the WoodPiece
     * @param _width The width of the WoodPiece in actual inches
     * @param _height The height of the WoodPiece in actual inches
     * @param _depth The depth of the WoodPiece in actual inches
     */
    public void setBuyingDimensions(float _width, float _height, float _depth) {
        buyingDimensions = new Dimensions(_width, _height, _depth);
        computeCornerPositions();
    }

    /**
     * Obtains the central position of the WoodPiece
     * @return A Vector3 representing the central position of the WoodPiece
     */
    public Vector3 getCentralPosition() {
        return centralPosition;
    }

    /**
     * Sets the central position of the WoodPiece
     * @param _centralPosition A Vector3 representing the central position of the WoodPiece
     */
    public void setCentralPosition(Vector3 _centralPosition) {
        centralPosition = _centralPosition;
        computeCornerPositions();
    }

    /**
     * Sets the central position of the WoodPiece
     * @param x The X position of the WoodPiece in actual inches
     * @param y The Y position of the WoodPiece in actual inches
     * @param z The Z position of the WoodPiece in actual inches
     */
    public void setCentralPosition(float x, float y, float z) {
        setCentralPosition(new Vector3(x, y, z));
    }

    /**
     * Obtains the orientation of the WoodPiece
     * @return The orientation of the WoodPiece
     */
    public ComponentType getOrientation() {
        return componentType;
    }

    /**
     * Sets the orientation of the WoodPiece
     * @param _componentType The orientation of the WoodPiece
     */
    public void setOrientation(ComponentType _componentType) {
        componentType = _componentType;
        computeCornerPositions();
    }

    /**
     * Obtains the minimum corner position of the WoodPiece, being the corner with the minimum values on
     * the X, Y and Z axis
     * @return A Vector3 representing the minimum corner position of the WoodPiece
     */
    public Vector3 getMinCornerPosition() {
        return minCornerPosition;
    }

    /**
     * Sets the minimum corner position of the WoodPiece, being the corner with the minimum values on
     * the X, Y and Z axis
     * @param _minCornerPosition A Vector3 representing the minimum corner position of the WoodPiece
     */
    public void setMinCornerPosition(Vector3 _minCornerPosition) {
        minCornerPosition = _minCornerPosition;
    }

    /**
     * Obtains the maximum corner position of the WoodPiece, being the corner with the maximum values on
     * the X, Y and Z axis
     * @return A Vector3 representing the maximum corner position of the WoodPiece
     */
    public Vector3 getMaxCornerPosition() {
        return maxCornerPosition;
    }

    /**
     * Sets the maximum corner position of the WoodPiece, being the corner with the maximum values on
     * the X, Y and Z axis
     * @param _maxCornerPosition A Vector3 representing the maximum corner position of the WoodPiece
     */
    public void setMaxCornerPosition(Vector3 _maxCornerPosition) {
        maxCornerPosition = _maxCornerPosition;
    }

    /**
     * Calculates the minimum and maximum corner positions of the WoodPiece
     */
    public void computeCornerPositions() {
        Vector3 vectorOffset;
        float widthOffset = dimensions.getActualWidth() / 2;
        float heightOffset = dimensions.getActualHeight() / 2;
        float depthOffset = dimensions.getActualDepth() / 2;

        switch (componentType) {
            case COVERING_PLANK:
            default:
                vectorOffset = new Vector3(widthOffset, heightOffset, depthOffset);
                break;
            case JOIST:
                vectorOffset = new Vector3(depthOffset, widthOffset, heightOffset);
                break;
            case BEAM:
                vectorOffset = new Vector3(heightOffset, widthOffset, depthOffset);
                break;
            case POST:
                vectorOffset = new Vector3(widthOffset, depthOffset, heightOffset);
                break;
        }

        setMaxCornerPosition(Vector3.add(centralPosition, vectorOffset));
        setMinCornerPosition(Vector3.subtract(centralPosition, vectorOffset));
    }
}

