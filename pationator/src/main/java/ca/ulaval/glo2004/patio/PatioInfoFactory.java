package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;

public class PatioInfoFactory {
    private static PatioInfoFactory instance;

    /**
     * The PatioInfoFactory constructor.
     */
    private PatioInfoFactory() {
    }

    /**
     * Obtains the instance of the PatioInfoFactory singleton. If the object does not exist yet, it instantiates itself
     *
     * @return The instance of the PatioInfoFactory singleton.
     */
    public static PatioInfoFactory getInstance() {
        if (instance == null) {
            instance = new PatioInfoFactory();
        }

        return instance;
    }

    /**
     * Creates the default valid patio. ALl dimensions and measurements are in real inches
     *
     * @return The default valid patio.
     */
    public PatioInfo createDefaultPatio() {
        PatioInfo patioInfo = new PatioInfo();

        float patioWidth = 188.5f;
        float patioHeight = 70.5f;
        float patioDepth = 225.0f;
        patioInfo.setPatioDimensions(new Dimensions(patioWidth, patioHeight, patioDepth), false);

        float cantileverLength = 8f;
        patioInfo.setCantileverLength(cantileverLength, false);

        float coveringWidth = 5.5f;
        float coveringHeight = 1.5f;
        float coveringDepth = 183.0f;
        patioInfo.setCoveringDimensions(new Dimensions(coveringWidth, coveringHeight, coveringDepth), false);

        float coveringSpacing = 0.5f;
        patioInfo.setCoveringSpacing(coveringSpacing);

        float joistWidth = 11.25f;
        float joistHeight = 1.5f;
        float joistDepth = 73.833336f;
        float joistSpanMaxLength = 75.0f;
        patioInfo.setJoistDimensions(new Dimensions(joistWidth, joistHeight, joistDepth), false);
        patioInfo.setJoistSpanMaxLength(joistSpanMaxLength, false);

        float joistSpacing = 18.0f;
        patioInfo.setJoistSpacing(joistSpacing);

        float beamWidth = 11.25f;
        float beamHeight = 1.5f;
        float beamDepth = 183.0f;
        patioInfo.setBeamDimensions(new Dimensions(beamWidth, beamHeight, beamDepth), false);

        int pliesPerBeam = 2;
        patioInfo.setPliesPerBeam(pliesPerBeam);

        float postWidth = 5.5f;
        float postHeight = 5.5f;
        float postDepth = 46.5f;
        patioInfo.setPostDimensions(new Dimensions(postWidth, postHeight, postDepth), false);

        float postSpacing = 91.5f;
        patioInfo.setPostSpacing(postSpacing, false);

        int postsPerBeam = 3;
        patioInfo.setPostsPerBeam(postsPerBeam, false);

        patioInfo.setLumberPricePerDimensions("2\" x 4\"", 1.5f);
        patioInfo.setLumberPricePerDimensions("2\" x 6\"", 2.0f);
        patioInfo.setLumberPricePerDimensions("2\" x 8\"", 2.5f);
        patioInfo.setLumberPricePerDimensions("2\" x 10\"", 3.0f);
        patioInfo.setLumberPricePerDimensions("2\" x 12\"", 3.5f);
        patioInfo.setLumberPricePerDimensions("4\" x 4\"", 4.0f);
        patioInfo.setLumberPricePerDimensions("5/4\" x 6\"", 1.0f);
        patioInfo.setLumberPricePerDimensions("6\" x 6\"", 6.0f);

        return patioInfo;
    }
}
