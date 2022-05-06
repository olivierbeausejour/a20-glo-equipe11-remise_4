package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PatioInfo implements Serializable {
    private static final float MIN_COVERING_SPACING_IN_INCHES = 0.01f;
    private static final float MIN_CANTILEVER_LENGTH_IN_INCHES = 0f;
    private static final float MIN_POST_DEPTH_IN_INCHES = 12f;
    private static final int MIN_PLIES_PER_BEAM = 1;
    private static final int MAX_PLIES_PER_BEAM = 3;
    private static final int MIN_POSTS_PER_BEAM = 2;
    private static final int MAX_POSTS_PER_BEAM = 99;
    private static final float FLOAT_IMPRECISION = 0.0001f;

    private MeasureUnit measureUnit = MeasureUnit.IMPERIAL;
    private MeasureType measureType = MeasureType.NOMINAL;

    private Dimensions patioDimensions;

    private Dimensions coveringDimensions;
    private float coveringSpacing;
    private Color coveringColor = Color.RED;
    private boolean coveringColorFilling = true;
    private boolean coveringVisibility  = true;

    private float joistSpanMaxLength;
    private Dimensions joistDimensions;
    private float joistSpacing;
    private Color joistColor = Color.GREEN;
    private boolean joistColorFilling = true;
    private boolean joistVisibility = true;

    private float cantileverLength;

    private Dimensions beamDimensions;
    private int pliesPerBeam;
    private Color beamColor = Color.BLUE;
    private boolean beamColorFilling = true;
    private boolean beamVisibility = true;

    private Dimensions postDimensions;
    private float postSpacing;
    private int postsPerBeam;
    private Color postColor = Color.MAGENTA;
    private boolean postColorFilling = true;
    private boolean postVisibility = true;

    private final HashMap<String, Float> lumberPrice;
    private boolean hiddenBorderVisibility = true;
    private String rationalFormat = "fraction";

    public PatioInfo() {
        patioDimensions = new Dimensions();
        coveringDimensions = new Dimensions();
        joistDimensions = new Dimensions();
        beamDimensions = new Dimensions();
        postDimensions = new Dimensions();

        lumberPrice = new HashMap<>();
    }

    public PatioInfo(PatioInfo _rhs) {
        measureUnit = _rhs.measureUnit;
        measureType = _rhs.measureType;
        patioDimensions = _rhs.patioDimensions;
        coveringDimensions = _rhs.coveringDimensions;
        coveringSpacing = _rhs.coveringSpacing;
        coveringColor = _rhs.coveringColor;
        coveringColorFilling = _rhs.coveringColorFilling;
        coveringVisibility = _rhs.coveringVisibility;
        joistSpanMaxLength = _rhs.joistSpanMaxLength;
        joistDimensions = _rhs.joistDimensions;
        joistSpacing = _rhs.joistSpacing;
        joistColor = _rhs.joistColor;
        joistColorFilling = _rhs.joistColorFilling;
        joistVisibility = _rhs.joistVisibility;
        cantileverLength = _rhs.cantileverLength;
        beamDimensions = _rhs.beamDimensions;
        pliesPerBeam = _rhs.pliesPerBeam;
        beamColor = _rhs.beamColor;
        beamColorFilling = _rhs.beamColorFilling;
        beamVisibility = _rhs.beamVisibility;
        postDimensions = _rhs.postDimensions;
        postSpacing = _rhs.postSpacing;
        postsPerBeam = _rhs.postsPerBeam;
        postColor = _rhs.postColor;
        postColorFilling = _rhs.postColorFilling;
        postVisibility = _rhs.postVisibility;

        lumberPrice = new HashMap<>();
        for (Map.Entry<String, Float> price : _rhs.lumberPrice.entrySet())
        {
            lumberPrice.put(price.getKey(), price.getValue());
        }

        hiddenBorderVisibility = _rhs.hiddenBorderVisibility;
        rationalFormat = _rhs.rationalFormat;
    }

    public boolean equals(PatioInfo _rhs) {
        return measureUnit.equals(_rhs.measureUnit) &&
                measureType.equals(_rhs.measureType) &&
                patioDimensions.isEquals(_rhs.patioDimensions) &&
                coveringDimensions.isEquals(_rhs.coveringDimensions) &&
                Float.compare(coveringSpacing, _rhs.coveringSpacing) == 0 &&
                coveringColor.equals(_rhs.coveringColor) &&
                coveringColorFilling == _rhs.coveringColorFilling &&
                coveringVisibility == _rhs.coveringVisibility &&
                Float.compare(joistSpanMaxLength, _rhs.joistSpanMaxLength) == 0 &&
                joistDimensions.isEquals(_rhs.getJoistDimensions()) &&
                Float.compare(joistSpacing, _rhs.joistSpacing) == 0 &&
                joistColor.equals(_rhs.joistColor) &&
                joistColorFilling == _rhs.joistColorFilling &&
                joistVisibility == _rhs.joistVisibility &&
                Float.compare(cantileverLength, _rhs.cantileverLength) == 0 &&
                beamDimensions.isEquals(_rhs.beamDimensions) &&
                pliesPerBeam == _rhs.pliesPerBeam &&
                beamColor.equals(_rhs.beamColor) &&
                beamColorFilling == _rhs.beamColorFilling &&
                beamVisibility == _rhs.beamVisibility &&
                postDimensions.isEquals(_rhs.postDimensions) &&
                Float.compare(postSpacing, _rhs.postSpacing) == 0 &&
                postsPerBeam == _rhs.postsPerBeam &&
                postColor.equals(_rhs.postColor) &&
                postColorFilling == _rhs.postColorFilling &&
                postVisibility == _rhs.postVisibility &&
                lumberPrice.equals(_rhs.lumberPrice) &&
                hiddenBorderVisibility == _rhs.hiddenBorderVisibility &&
                rationalFormat.equals(_rhs.rationalFormat);
    }

    public MeasureUnit getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(MeasureUnit _measureUnit) {
        measureUnit = _measureUnit;
    }

    public MeasureType getMeasureType() {
        return measureType;
    }

    public void setMeasureType(MeasureType _measureType) {
        measureType = _measureType;
    }

    public Dimensions getPatioDimensions() {
        return patioDimensions;
    }

    public float getMinPatioWidth() {
        return 2 * postDimensions.getActualWidth();
    }

    public void setPatioDimensions(Dimensions _patioDimensions, boolean _resizeAffectingComponents) {
        patioDimensions = _patioDimensions;

        if (!_resizeAffectingComponents)
            return;

        float patioDepth = patioDimensions.getActualDepth();
        if (cantileverLength > patioDepth) {
            cantileverLength = patioDepth;
        }

        postSpacing = (patioDimensions.getActualWidth() - postDimensions.getActualWidth()) / (postsPerBeam - 1);

        float beamWidth = getBeamPlieDimensions().getActualWidth();
        float beamHeight = getBeamPlieDimensions().getActualHeight();
        float beamDepth = patioDimensions.getActualWidth() - postDimensions.getActualWidth();

        beamDimensions = new Dimensions(beamWidth, beamHeight, beamDepth);

        float coveringWidth = getCoveringDimensions().getActualWidth();
        float coveringHeight = getCoveringDimensions().getActualHeight();
        float coveringDepth = patioDimensions.getActualWidth() - postDimensions.getActualWidth();

        coveringDimensions = new Dimensions(coveringWidth, coveringHeight, coveringDepth);

        float postWidth = getPostDimensions().getActualWidth();
        float postHeight = getPostDimensions().getActualHeight();
        float postDepth = Math.max(getMinPostDepth(), patioDimensions.getActualHeight() - getPatioHeightExcludingComponent(ComponentType.POST));

        postDimensions = new Dimensions(postWidth, postHeight, postDepth);

        float patioDepthWithoutCantilever = patioDimensions.getActualDepth() - cantileverLength;
        float spanAmountRatio = patioDepthWithoutCantilever / joistSpanMaxLength;
        int joistsAmountWide = (int) Math.ceil(spanAmountRatio);
        float joistActualDepth = joistsAmountWide == 0 ? 0 : (patioDepthWithoutCantilever / joistsAmountWide) + getBeamPlieDimensions().getActualHeight();

        float joistWidth = joistDimensions.getActualWidth();
        float joistHeight = joistDimensions.getActualHeight();

        joistDimensions = new Dimensions(joistWidth, joistHeight, joistActualDepth);
    }

    public float getMinPatioHeight() {
        return Math.max(getMinPostDepth(), getPatioHeightExcludingComponent(ComponentType.POST) + getMinPostDepth());
    }

    public float getPatioHeightExcludingComponent(ComponentType _componentType) {
        switch (_componentType) {
            case POST:
                return beamDimensions.getActualWidth() + joistDimensions.getActualWidth() + coveringDimensions.getActualHeight();
            case BEAM:
                return postDimensions.getActualDepth() + joistDimensions.getActualWidth() + coveringDimensions.getActualHeight();
            case JOIST:
                return postDimensions.getActualDepth() + beamDimensions.getActualWidth() + coveringDimensions.getActualHeight();
            case COVERING_PLANK:
                return postDimensions.getActualDepth() + joistDimensions.getActualWidth() + beamDimensions.getActualWidth();
        }

        return 0f;
    }

    public Dimensions getCoveringDimensions() {
        return coveringDimensions;
    }

    public void setCoveringDimensions(Dimensions _coveringDimensions, boolean _resizeAffectingComponents) {
        coveringDimensions = _coveringDimensions;

        if (!_resizeAffectingComponents)
            return;

        float patioWidth = patioDimensions.getActualWidth();
        float patioHeight = getPatioHeightExcludingComponent(ComponentType.COVERING_PLANK) + coveringDimensions.getActualHeight();
        float patioDepth = patioDimensions.getActualDepth();

        patioDimensions = new Dimensions(patioWidth, patioHeight, patioDepth);
    }

    public float getCoveringSpacing() {
        return coveringSpacing;
    }

    public float getMinCoveringSpacing() {
        return MIN_COVERING_SPACING_IN_INCHES;
    }

    public void setCoveringSpacing(float _coveringSpacing) {
        coveringSpacing = _coveringSpacing;
    }

    public Color getCoveringColor() {
        return coveringColor;
    }

    public void setCoveringColor(Color _coveringColor) {
        coveringColor = _coveringColor;
    }

    public boolean isCoveringColorFilling() {
        return coveringColorFilling;
    }

    public void setCoveringColorFilling(boolean _coveringColorFilling) {
        coveringColorFilling = _coveringColorFilling;
    }

    public boolean isCoveringVisibility() {
        return coveringVisibility;
    }

    public void setCoveringVisibility(boolean _coveringVisibility) {
        coveringVisibility = _coveringVisibility;
    }

    public float getJoistSpanMaxLength() {
        return joistSpanMaxLength;
    }

    public float getMinJoistSpanLength() {
        return postDimensions.getActualWidth();
    }

    public void setJoistSpanMaxLength(float _joistSpanMaxLength, boolean _resizeAffectingComponents) {
        joistSpanMaxLength = _joistSpanMaxLength;

        if (!_resizeAffectingComponents)
            return;

        float patioDepthWithoutCantilever = patioDimensions.getActualDepth() - cantileverLength;
        float spanAmountRatio = patioDepthWithoutCantilever / joistSpanMaxLength;
        int joistsAmountWide = (int) Math.ceil(spanAmountRatio);
        float joistActualDepth = joistsAmountWide == 0 ? 0 : (patioDepthWithoutCantilever / joistsAmountWide) + getBeamPlieDimensions().getActualHeight();

        float joistWidth = joistDimensions.getActualWidth();
        float joistHeight = joistDimensions.getActualHeight();

        joistDimensions = new Dimensions(joistWidth, joistHeight, joistActualDepth);
    }

    public Dimensions getJoistDimensions() {
        return joistDimensions;
    }

    public void setJoistDimensions(Dimensions _joistDimensions , boolean _resizeAffectingComponents) {
        joistDimensions = _joistDimensions;

        if (!_resizeAffectingComponents)
            return;

        float patioWidth = patioDimensions.getActualWidth();
        float patioHeight = getPatioHeightExcludingComponent(ComponentType.JOIST) + joistDimensions.getActualWidth();
        float patioDepth = patioDimensions.getActualDepth();

        patioDimensions = new Dimensions(patioWidth, patioHeight, patioDepth);
    }

    public float getJoistSpacing() {
        return joistSpacing;
    }

    public float getMinJoistSpacing() {
        return joistDimensions.getActualHeight() * 2;
    }

    public void setJoistSpacing(float _joistSpacing) {
        joistSpacing = _joistSpacing;
    }

    public int getJoistAmountPerSpan() {
        return 1 + (int) (((getBeamPlieDimensions().getActualDepth()) - (2 * joistDimensions.getActualHeight())) / joistSpacing);
    }

    public float getJoistSpan() {
        return Math.max(getBeamPlieDimensions().getActualHeight(), joistDimensions.getActualDepth() - getBeamPlieDimensions().getActualHeight());
    }

    public Color getJoistColor() {
        return joistColor;
    }

    public void setJoistColor(Color _joistColor) {
        joistColor = _joistColor;
    }

    public boolean isJoistColorFilling() {
        return joistColorFilling;
    }

    public void setJoistColorFilling(boolean _joistColorFilling) {
        joistColorFilling = _joistColorFilling;
    }

    public boolean isJoistVisibility() {
        return joistVisibility;
    }

    public void setJoistVisibility(boolean _joistVisibility) {
        joistVisibility = _joistVisibility;
    }

    public int getSpanAmount() {
        return Math.max(1, getBeamAmount() - 1);
    }

    public float getCantileverLength() {
        return cantileverLength;
    }

    public float getMinCantileverLength() {
        return MIN_CANTILEVER_LENGTH_IN_INCHES;
    }

    public void setCantileverLength(float _cantileverLength, boolean _resizeAffectingComponents) {
        cantileverLength = _cantileverLength;

        if (!_resizeAffectingComponents)
            return;

        float patioDepth = patioDimensions.getActualDepth();
        if (patioDepth < cantileverLength) {
            float patioWidth = patioDimensions.getActualWidth();
            float patioHeight = patioDimensions.getActualHeight();
            patioDepth = cantileverLength;

            patioDimensions = new Dimensions(patioWidth, patioHeight, patioDepth);
        }

        float patioDepthWithoutCantilever = patioDimensions.getActualDepth() - cantileverLength;
        float spanAmountRatio = patioDepthWithoutCantilever / joistSpanMaxLength;
        int joistsAmountWide = (int) Math.ceil(spanAmountRatio);
        float joistActualDepth = joistsAmountWide == 0 ? 0 : (patioDepthWithoutCantilever / joistsAmountWide) + getBeamPlieDimensions().getActualHeight();

        float joistWidth = joistDimensions.getActualWidth();
        float joistHeight = joistDimensions.getActualHeight();

        joistDimensions = new Dimensions(joistWidth, joistHeight, joistActualDepth);
    }

    public Dimensions getBeamPlieDimensions() {
        return beamDimensions;
    }

    public float getMinBeamLength() {
        return postDimensions.getActualWidth();
    }

    public Dimensions getBeamCombinedDimensions() {
        float width = getBeamPlieDimensions().getActualWidth();
        float height = getBeamPlieDimensions().getActualHeight() * getPliesPerBeam();
        float depth = getBeamPlieDimensions().getActualDepth();

        return new Dimensions(width, height, depth);
    }

    public void setBeamDimensions(Dimensions _beamDimensions, boolean _resizeAffectingComponents) {
        beamDimensions = _beamDimensions;

        if (!_resizeAffectingComponents)
            return;

        postSpacing = beamDimensions.getActualDepth() / (postsPerBeam - 1);

        float patioWidth = beamDimensions.getActualDepth() + postDimensions.getActualWidth();
        float patioHeight = getPatioHeightExcludingComponent(ComponentType.BEAM) + beamDimensions.getActualWidth();
        float patioDepth = getPatioDimensions().getActualDepth();

        patioDimensions = new Dimensions(patioWidth, patioHeight, patioDepth);

        float coveringPlankWidth = getCoveringDimensions().getActualWidth();
        float coveringPlankHeight = getCoveringDimensions().getActualHeight();
        float coveringPlankDepth = beamDimensions.getActualDepth();

        coveringDimensions = new Dimensions(coveringPlankWidth, coveringPlankHeight, coveringPlankDepth);
    }

    public int getBeamAmount() {
        return 1 + (int) Math.floor((patioDimensions.getActualDepth() - cantileverLength + FLOAT_IMPRECISION) / getJoistSpan());
    }

    public Color getBeamColor() {
        return beamColor;
    }

    public void setBeamColor(Color _beamColor) {
        beamColor = _beamColor;
    }

    public boolean isBeamColorFilling() {
        return beamColorFilling;
    }

    public void setBeamColorFilling(boolean _beamColorFilling) {
        beamColorFilling = _beamColorFilling;
    }

    public boolean isBeamVisibility() {
        return beamVisibility;
    }

    public void setBeamVisibility(boolean _beamVisibility) {
        beamVisibility = _beamVisibility;
    }

    public int getPliesPerBeam() {
        return pliesPerBeam;
    }

    public int getMinPliesPerBeam() {
        return MIN_PLIES_PER_BEAM;
    }

    public int getMaxPliesPerBeam() {
        return MAX_PLIES_PER_BEAM;
    }

    public void setPliesPerBeam(int _pliesPerBeam) {
        pliesPerBeam = _pliesPerBeam;
    }

    public Dimensions getPostDimensions() {
        return postDimensions;
    }

    public float getMinPostDepth() {
        return MIN_POST_DEPTH_IN_INCHES;
    }

    public void setPostDimensions(Dimensions _postDimensions, boolean _resizeAffectingComponents) {
        postDimensions = _postDimensions;

        if (!_resizeAffectingComponents)
            return;

        float patioWidth = getBeamPlieDimensions().getActualDepth() + postDimensions.getActualWidth();
        float patioHeight = getPatioHeightExcludingComponent(ComponentType.POST) + postDimensions.getActualDepth();
        float patioDepth = patioDimensions.getActualDepth();

        patioDimensions = new Dimensions(patioWidth, patioHeight, patioDepth);
    }

    public float getPostSpacing() {
        return postSpacing;
    }

    public float getMinPostSpacing() {
        return postDimensions.getActualHeight();
    }

    public void setPostSpacing(float _postSpacing, boolean _resizeAffectingComponents) {
        postSpacing = _postSpacing;

        if (!_resizeAffectingComponents)
            return;

        float combinedPostSpacing = postSpacing * (postsPerBeam - 1);

        float patioWidth = combinedPostSpacing + postDimensions.getActualWidth();
        float patioHeight = getPatioDimensions().getActualHeight();
        float patioDepth = getPatioDimensions().getActualDepth();

        patioDimensions = new Dimensions(patioWidth, patioHeight, patioDepth);

        float coveringPlankWidth = getCoveringDimensions().getActualWidth();
        float coveringPlankHeight = getCoveringDimensions().getActualHeight();
        float coveringPlankDepth = combinedPostSpacing;

        coveringDimensions = new Dimensions(coveringPlankWidth, coveringPlankHeight, coveringPlankDepth);

        float beamWidth = getBeamPlieDimensions().getActualWidth();
        float beamHeight = getBeamPlieDimensions().getActualHeight();
        float beamDepth = combinedPostSpacing;

        beamDimensions = new Dimensions(beamWidth, beamHeight, beamDepth);
    }

    public int getPostsPerBeam() {
        return postsPerBeam;
    }

    public int getMinPostsPerBeam() {
        return MIN_POSTS_PER_BEAM;
    }

    public int getMaxPostsPerBeam() {
        /*return (int) Math.floor(beamDimensions.getActualDepth() / postDimensions.getActualWidth());*/
        return MAX_POSTS_PER_BEAM;
    }

    public void setPostsPerBeam(int _postsPerBeam, boolean _resizeAffectingComponents) {
        postsPerBeam = _postsPerBeam;

        if (!_resizeAffectingComponents)
            return;

        postSpacing = (patioDimensions.getActualWidth() - postDimensions.getActualWidth()) / (postsPerBeam - 1);
    }

    public Color getPostColor() {
        return postColor;
    }

    public void setPostColor(Color _postColor) {
        postColor = _postColor;
    }

    public boolean isPostColorFilling() {
        return postColorFilling;
    }

    public void setPostColorFilling(boolean _postColorFilling) {
        postColorFilling = _postColorFilling;
    }

    public boolean isPostVisibility() {
        return postVisibility;
    }

    public void setPostVisibility(boolean _postVisibility) {
        postVisibility = _postVisibility;
    }

    public void setLumberPricePerDimensions(String _lumberDimension, float _newValue) {
        lumberPrice.put(_lumberDimension, _newValue);
    }

    public float getLumberPricePerDimensions(String _lumberDimension) {
        if (lumberPrice.containsKey(_lumberDimension)) {
            return lumberPrice.get(_lumberDimension);
        }

        return 0f;
    }

    public boolean getHiddenBorderVisibility() {
        return hiddenBorderVisibility;
    }

    public void setHiddenBorderVisibility(boolean _hiddenBorderVisibility) {
        hiddenBorderVisibility = _hiddenBorderVisibility;
    }

    /**
     * Get the current rationnal value string format.
     *
     * @return 'decimal' or 'fraction'.
     */
    public String getRationalFormat() {
        if (!rationalFormat.equals("fraction") && !rationalFormat.equals("decimal")) {
            setRationalFormat("fraction");
        }

        return rationalFormat;
    }

    /**
     * Set the current rationnal value string format.
     *
     * @param _rationalFormat 'decimal' or 'fraction'.
     */
    public void setRationalFormat(String _rationalFormat) {
        if (_rationalFormat.equals("decimal") || _rationalFormat.equals("fraction"))
            rationalFormat = _rationalFormat;
        else
            rationalFormat = "fraction";
    }
}
