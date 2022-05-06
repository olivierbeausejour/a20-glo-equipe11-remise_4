package ca.ulaval.glo2004.utils;

import java.io.Serializable;

/**
 * This class represent a dimension. Dimensions sizes should always be considered as inches.
 */
public class Dimensions implements Serializable {
    private final Vector3 size;

    /**
     * Create a Dimensions with a default size (0, 0, 0).
     */
    public Dimensions() {
        size = new Vector3(0, 0, 0);
    }

    /**
     * Create a Dimensions with a specified size.
     *
     * @param _size New dimensions size.
     */
    public Dimensions(Vector3 _size) {
        size = _size;
    }

    /**
     * Create a Dimensions from a Dimensions object.
     *
     * @param _rhs Dimensions object to copy.
     */
    public Dimensions(Dimensions _rhs) {
        size = new Vector3(_rhs.getActualWidth(), _rhs.getActualHeight(), _rhs.getActualDepth());
    }

    /**
     * Get width in inches
     *
     * @return Width in inches.
     */
    public float getActualWidth() {
        return size.x;
    }

    /**
     * Get height in inches.
     *
     * @return Height in inches.
     */
    public float getActualHeight() {
        return size.y;
    }

    /**
     * Get depth in inches.
     *
     * @return Depth in inches.
     */
    public float getActualDepth() {
        return size.z;
    }

    /**
     * Create Dimensions object.
     *
     * @param _widthInInchesActual  Width in real inches.
     * @param _heightInInchesActual Height in real inches.
     * @param _depthInInchesActual  Depth in real inches.
     */
    public Dimensions(float _widthInInchesActual, float _heightInInchesActual, float _depthInInchesActual) {
        size = new Vector3(_widthInInchesActual, _heightInInchesActual, _depthInInchesActual);
    }

    /**
     * Get depth in inches.
     *
     * @return Depth in inches.
     */
    public float getNominalDepth() {
        return size.z;
    }

    /**
     * Verify if Dimensions is not valid.
     *
     * @return True if Dimensions object size are not valid.
     */
    public boolean isNotValid() {
        return !(size.x >= 0) || !(size.y >= 0) || !(size.z >= 0);
    }

    /**
     * Compare Dimensions object.
     *
     * @param _rhs Dimensions object to compare.
     * @return True if Dimensions object have same size.
     */
    public boolean isEquals(Dimensions _rhs) {
        return size.equals(_rhs.size);
    }

    /**
     * Get nominal value from dimensions width
     *
     * @return Nominal value
     */
    public float getNominalWidth() {
        return Conversion.getNominalFromActualInches(size.x);
    }

    /**
     * Get nominal value from dimensions height
     *
     * @return Nominal value
     */
    public float getNominalHeight() {
        return Conversion.getNominalFromActualInches(size.y);
    }
}


