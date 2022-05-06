package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.Vector3;

public class Post extends Component {
    private Dimensions postDimensions;
    private float postSpacing;
    private float joistSpan;
    private int idxSupportedBeam;
    private int idxPostInSupportedBeam;

    /**
     * The Post constructor.
     * @param _postDimensions The post height in actual inches
     * @param _postSpacing The post spacing in actual inches
     * @param _joistSpan The joist span in actual inches
     * @param _idxSupportedBeam The index of the beam being supported by the post
     * @param _idxPostInSupportedBeam The index of one of the the beam's support posts
     */
    public Post(Dimensions _postDimensions, float _postSpacing, float _joistSpan, int _idxSupportedBeam, int _idxPostInSupportedBeam) {
        postDimensions = _postDimensions;
        postSpacing = _postSpacing;
        joistSpan = _joistSpan;
        idxSupportedBeam = _idxSupportedBeam;
        idxPostInSupportedBeam = _idxPostInSupportedBeam;

        Vector3 postCentralPosition = getPostCentralPosition();
        woodPieces.add(new WoodPiece(postDimensions, postCentralPosition, ComponentType.POST));
    }

    /**
     * Obtains the central position of the post
     * @return A Vector3 representing the central position of the post
     */
    private Vector3 getPostCentralPosition() {
        //The X value is determined by the indexes of the support beam. The X position is multiplied by the index of
        //the supported beam with the joist span.
        float xPos = idxSupportedBeam * joistSpan;

        //We suppose that the bottom of a post is at Y level 0, so its middle position on the Y axis is at the middle
        //of its height.
        float yPos = postDimensions.getActualDepth() / 2;

        //Post indexes in the supported beam with the value of 0 are on the the Z value of 0, while other post indexes
        //get an offset equivalent to a fraction of the post spacing value (or its full value, if there are only 2 posts
        //supporting the beam), which pushes them to the upper side of the Z axis (in the positive values).
        //float zPos = idxPostInSupportedBeam * (postSpacing / (nbPostsPerBeam - 1));
        float zPos = idxPostInSupportedBeam * postSpacing;

        return new Vector3(xPos, yPos, zPos);
    }

    /**
     * Obtains the dimensions of the post
     * @return The dimensions of the post
     */
    public Dimensions getPostDimensions() {
        return postDimensions;
    }

    /**
     * Obtains the post spacing in actual inches
     * @return The post spacing in actual inches
     */
    public float getPostSpacing() {
        return postSpacing;
    }

    /**
     * Obtains the joist span in actual inches
     * @return The joist span in actual inches
     */
    public float getJoistSpan() {
        return joistSpan;
    }

    /**
     * Obtains the index of the supported beam
     * @return The index of the supported beam
     */
    public float getIdxSupportedBeam() {
        return idxSupportedBeam;
    }

    /**
     * Obtains the index of the post supporting the beam
     * @return The index of the post supporting the beam
     */
    public float getIdxPostInSupportedBeam() {
        return idxPostInSupportedBeam;
    }
}
