package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.Vector3;

public class Covering extends Component {
    int nbCoveringPlanks;
    float coveringSpacing;
    Dimensions coveringPlankDimensions;
    Dimensions firstCoveringPlankDimensions;
    float beginSpacing;
    float beamWidth;
    float coveringYPos;
    float coveringLength;

    /**
     * The covering Constructor
     * @param _nbCoveringPlanks The number of covering planks
     * @param _coveringSpacing The spacing between every covering plank in actual inches
     * @param _coveringPlankDimensions The dimensions of every covering plank
     * @param _firstCoveringPlankWidth The width of the first covering plank
     * @param _beginSpacing The The spacing at the start of the patio, from the house
     * @param _beamWidth The width of the beam
     * @param _coveringYPost The Y position of every covering plank in actual inches
     * @param _coveringLength The post spacing in actual inches
     */
    public Covering(int _nbCoveringPlanks, float _coveringSpacing, Dimensions _coveringPlankDimensions,
                    float _firstCoveringPlankWidth, float _beginSpacing, float _beamWidth,
                    float _coveringYPost, float _coveringLength) {
        nbCoveringPlanks = _nbCoveringPlanks;
        coveringSpacing = _coveringSpacing;
        coveringPlankDimensions = _coveringPlankDimensions;
        firstCoveringPlankDimensions = new Dimensions(_firstCoveringPlankWidth, coveringPlankDimensions.getActualHeight(), coveringPlankDimensions.getActualDepth());
        beginSpacing = _beginSpacing;
        beamWidth = _beamWidth;
        coveringYPos = _coveringYPost;
        coveringLength = _coveringLength;

        for (int idxPlank = 0; idxPlank < nbCoveringPlanks; idxPlank++) {
            Dimensions currentPlankDimensions = coveringPlankDimensions;
            if (idxPlank == 0) {
                currentPlankDimensions = firstCoveringPlankDimensions;
            }

            Vector3 coveringPlankCentralPosition = getCoveringPlankCentralPosition(idxPlank);
            WoodPiece coveringPlank = new WoodPiece(currentPlankDimensions, coveringPlankCentralPosition, ComponentType.COVERING_PLANK);
            coveringPlank.setBuyingDimensions(coveringPlankDimensions);

            woodPieces.add(coveringPlank);
        }
    }

    /**
     * Obtains the central position for a given covering plank
     * @param _idxPlank The index of the covering plank
     * @return A Vector3 representing the central position of the covering plank
     */
    private Vector3 getCoveringPlankCentralPosition(int _idxPlank) {
        //The X position is calculated by summing the different spacings and widths of the planks used on the covering
        //floor. This makes the planks perfectly fit inside the patio depth.
        float posX = 0;
        for (int k = 0; k <= _idxPlank; k++) {
            if (k == 0) {
                //We iterate over the first plank, and we add the little spacing at the beginning, if necessary
                //(sometimes, it is 0), we also sum the width of the first plank, that might be cut on its width.
                //Finally, we subtract the beam width, to make the end of the plank meet the beam below.
                posX += beginSpacing + (firstCoveringPlankDimensions.getActualWidth() / 2) - (beamWidth);
            }
            else if (k == 1) {
                //We iterate over the second beam, we add half of the first covering plank, that might be cut on its
                //width. We add the covering spacing, and finally, half the actual plank to place itself.
                posX += (firstCoveringPlankDimensions.getActualWidth() / 2) + coveringSpacing + (coveringPlankDimensions.getActualWidth() / 2);
            }
            else {
                //For every next plank, we add the whole width of a covering plank, plus the covering spacing.
                posX += coveringPlankDimensions.getActualWidth() + coveringSpacing;
            }
        }

        //The Z position is calculated by dividing the post spacing by two, and lowering it down int the -Z zone
        //(because the top of the plank is on Z level 0).
        float posZ = coveringLength / 2;

        return new Vector3(posX, coveringYPos, posZ);
    }

    /**
     * Obtains the number of covering planks
     * @return The number of covering planks
     */
    public int getNbCoveringPlanks() {
        return nbCoveringPlanks;
    }

    /**
     * Obtains the spacing between every covering plank in actual inches
     * @return The spacing between every covering plank in actual inches
     */
    public float getCoveringSpacing() {
        return coveringSpacing;
    }

    /**
     * Obtains the covering plank dimensions
     * @return The covering plank dimensions
     */
    public Dimensions getCoveringPlankDimensions() {
        return coveringPlankDimensions;
    }
}
