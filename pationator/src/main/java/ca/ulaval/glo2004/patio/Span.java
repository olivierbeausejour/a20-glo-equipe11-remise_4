package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.Vector3;

public class Span extends Component {
    private float joistSpacing;
    private Dimensions joistDimensions;
    private float joistSpan;
    private float cantileverLength;
    private float joistYPos;
    private int idxSpan;

    /**
     * The Span constructor
     * @param _joistSpacing The joist spacing in actual inches
     * @param _joistDimensions The joist dimensions
     * @param _joistSpan The joist span in actual inches
     * @param _joistYPos The joist Y position in actual inches
     * @param _nbJoists The number of joists in the span
     * @param _spanIndex The index of the span in the patio
     */
    public Span(float _joistSpacing, Dimensions _joistDimensions, float _joistSpan, float _joistYPos, int _nbJoists, int _spanIndex) {
        joistSpacing = _joistSpacing;
        joistDimensions = _joistDimensions;
        joistSpan = _joistSpan;
        joistYPos = _joistYPos;
        idxSpan = _spanIndex;

        generateJoists(_nbJoists);
    }

    /**
     * Generates the joists in the span
     * @param _nbJoists The number of joists to generate
     */
    private void generateJoists(int _nbJoists) {
        for (int idxJoist = 0; idxJoist < _nbJoists; idxJoist++) {
            Vector3 joistCentralPosition = getJoistCentralPosition(idxJoist);
            WoodPiece joist = new WoodPiece(joistDimensions, joistCentralPosition, ComponentType.JOIST);

            woodPieces.add(joist);
        }
    }

    /**
     * Obtains the central position of a given joist
     * @param _idxJoist The index of the joist
     * @return A Vector3 representing the central position of a given joist
     */
    private Vector3 getJoistCentralPosition(int _idxJoist) {
        //The joists are placed from lower Z axis to upper Z axis
        //The X position is determined by the span index. Index 0 represents the span right next to the house. Index 1,
        //the one after it, etc.
        float xPos = (idxSpan * joistSpan) + (joistSpan / 2);

        //An even span index implies that a joist will be aligned with the end of the lowest beam on
        //the Z axis. An odd span index implies that a joist will be aligned with the end of the highest beam on the Z
        //axis.
        float zOffset = idxSpan % 2 == 0 ? joistDimensions.getActualHeight() / 2 :
                (joistDimensions.getActualHeight()) + joistDimensions.getActualHeight() / 2;

        //The Z position of the joist is determined by its joist index in the span. The offset is always added.
        float zPos = (_idxJoist * joistSpacing) + zOffset;

        return new Vector3(xPos, joistYPos, zPos);
    }

    /**
     * Obtains the joist spacing in actual inches
     * @return The joist spacing in actual inches
     */
    public float getJoistSpacing() {
        return joistSpacing;
    }

    /**
     * Obtains the joist span in actual inches
     * @return The joist span in actual inches
     */
    public float getJoistSpan() {
        return joistSpan;
    }

    /**
     * Obtains the cantilever length in actual inches
     * @return The cantilever length in actual inches
     */
    public float getCantileverLength() {
        return cantileverLength;
    }

    /**
     * Sets a new cantilever length
     * @param _cantileverLength The new cantilever length in actual inches
     */
    public void setCantileverLength(float _cantileverLength) {
        cantileverLength = _cantileverLength;
        addCantileverLengthToJoists();
    }

    /**
     * Obtains the index of the span
     * @return The index of the span
     */
    public int getIdxSpan() {
        return idxSpan;
    }

    /**
     * Adds the cantilever length to the joists' length
     */
    private void addCantileverLengthToJoists() {
        for (WoodPiece joist : woodPieces) {
            float width = joist.getDimensions().getActualWidth();
            float height = joist.getDimensions().getActualHeight();
            float depth = joist.getDimensions().getActualDepth() + cantileverLength;
            joist.setDimensions(width, height, depth);
            joist.setBuyingDimensions(width, height, depth);

            float xPos = joist.getCentralPosition().x + (cantileverLength / 2);
            float yPos = joist.getCentralPosition().y;
            float zPos = joist.getCentralPosition().z;
            joist.setCentralPosition(xPos, yPos, zPos);
        }
    }

    public Dimensions getJoistDimensions() {
        return joistDimensions;
    }
}
