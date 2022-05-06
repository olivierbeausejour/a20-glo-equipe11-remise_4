package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.Vector3;

import java.util.ArrayList;

public class Beam extends Component {
    private ArrayList<Post> supportPosts;
    private ArrayList<Span> supportedSpans;

    private int nbPlies;
    private Dimensions pliesDimensions;
    private float joistSpan;
    private float beamYPost;
    private boolean isAttachedToHouse;
    private int idxBeam;

    /**
     * The Beam constructor
     * @param _nbPlies The number of plies on the beam. A represents a single wood plank, that can be attached to
     *                 an other one in order to reinforce the beam
     * @param _pliesDimensions The dimensions of each beam plie
     * @param _joistSpan The joist span in actual inches
     * @param _beamYPost The Y position of the beam in actual inches
     * @param _isAttachedToHouse A boolean representing whether the beam is attached to the house or not
     * @param _idxBeam The index of the beam (its beam order in the patio)
     */
    public Beam(int _nbPlies, Dimensions _pliesDimensions, float _joistSpan, float _beamYPost, boolean _isAttachedToHouse, int _idxBeam) {
        supportPosts = new ArrayList<>();
        supportedSpans = new ArrayList<>();

        nbPlies = _nbPlies;
        pliesDimensions = _pliesDimensions;
        joistSpan = _joistSpan;
        beamYPost = _beamYPost;
        isAttachedToHouse = _isAttachedToHouse;
        idxBeam = _idxBeam;

        for (int idxPlie = 0; idxPlie < nbPlies; idxPlie++) {
            Vector3 plieCentralPosition = getPlieCentralPosition(idxPlie);
            WoodPiece plie = new WoodPiece(pliesDimensions, plieCentralPosition, ComponentType.BEAM);

            woodPieces.add(plie);
        }
    }

    /**
     * Obtains the central position according to a specific plie
     * @param _idxPlie The index of the plie
     * @return A Vector3 representing the central position according to a specific plie
     */
    private Vector3 getPlieCentralPosition(int _idxPlie) {
        //The beam plie has an offset to the right or the left, or none at all. This offset depends on the number of
        //total plies.
        float xOffsetMax = ((nbPlies - 1) * pliesDimensions.getActualHeight()) / 2;
        float xOffsetMin = -xOffsetMax;
        float xOffset = nbPlies == 1 ? 0 : lerp(xOffsetMin, xOffsetMax, ((float) _idxPlie / (float) (nbPlies - 1)));

        //We add an offset if this is the first beam next to the house. This offset is added so the beam does not "pass"
        //through the wall of the house
        if (idxBeam == 0) {
            xOffset += (((float) (nbPlies - 1)) * pliesDimensions.getActualHeight()) / 2;
        }

        //The X position is determined by the index of the beam, which is multiplied with the joist span. We add the
        //offset to the X value to point at the plie.
        float xPos = (idxBeam * joistSpan) + xOffset;

        //The Z position is calculated by dividing the beam depth value by 2, because the top of the beams are aligned
        //with 0 on the Z axis.
        float zPos = pliesDimensions.getActualDepth() / 2;

        return new Vector3(xPos, beamYPost, zPos);
    }

    /**
     * Obtains the interpolation value between two points
     * @param a Point A
     * @param b Point B
     * @param f Interpolation
     * @return The interpolation value between two points
     */
    private float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    /**
     * Obtains the posts supporting the beam
     * @return An ArrayList containing the posts supporting the beam
     */
    public ArrayList<Post> getSupportPosts() {
        return supportPosts;
    }

    /**
     * Sets the new support posts for the beam
     * @param _supportPosts An ArrayList containing the support posts
     */
    public void setSupportPosts(ArrayList<Post> _supportPosts) {
        supportPosts = _supportPosts;
    }

    /**
     * Obtains the spans being supported by the beam
     * @return An ArrayList containing the spans being supported by the beam
     */
    public ArrayList<Span> getSupportedSpans() {
        return supportedSpans;
    }

    /**
     * Sets the new spans being supported by the beam
     * @param _supportedSpans An ArrayList containing the spans being supported by the beam
     */
    public void setSupportedSpans(ArrayList<Span> _supportedSpans) {
        supportedSpans = _supportedSpans;
    }

    /**
     * Obtains the number of plies for the beam
     * @return The number of plies for the beam
     */
    public int getNbPlies() {
        return nbPlies;
    }

    /**
     * Obtains the status of the beam supporting a single span or not
     * @return A boolean representing whether the beam is supporting a single span or not
     */
    public boolean isSupportingOneSpan() {
        return supportedSpans.size() == 1;
    }

    /**
     * Obtains the status of the beam supporting two spans or not
     * @return A boolean representing whether the beam is supporting two spans or not
     */
    public boolean isSupportingTwoSpans() {
        return supportedSpans.size() == 2;
    }

    /**
     * Obtains the Dimensions of every Plie
     * @return The Dimensions of every Plie
     */
    public Dimensions getPliesDimensions() {
        return pliesDimensions;
    }

    /**
     * Obtains the status of the beam being attached to the house or not
     * @return A boolean representing whether the beam is attached to the house or not
     */
    public boolean isAttachedToHouse() {
        return isAttachedToHouse;
    }

    /**
     * Obtains the index of the beam
     * @return The index of the beam
     */
    public int getIdxBeam() {
        return idxBeam;
    }
}
