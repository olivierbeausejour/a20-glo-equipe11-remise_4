package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents the patio to generate in the application
 */
public class Patio {
    private final ArrayList<Component> components;
    private final ArrayList<Span> spans;
    private final ArrayList<Post> posts;
    private final ArrayList<Beam> beams;
    private final ArrayList<ArrayList<Span>> beamsSupportedSpans;
    private final ArrayList<ArrayList<Post>> beamsSupportPosts;
    private final Validator patioValidator;
    private final ArrayList<ErrorsFoundListener> errorsFoundListener;
    private final ArrayList<NoErrorsFoundListener> noErrorsFoundListener;
    private final ArrayList<Pair<Float, PatioInfo>> optimalPatioConfigurations;
    private final HashMap<String, HashMap<Float, Integer>> piecesQuantities;
    private ArrayList<Covering> covering;
    private PatioInfo patioInfo;
    private PatioInfo initialPatioInfo;
    private final HashMap<String, Float> totalPricesPerLumberDimensions;

    /**
     * The Patio Constructor. It instantiates the patio components and properties
     */
    public Patio() {
        components = new ArrayList<>();
        spans = new ArrayList<>();
        posts = new ArrayList<>();
        beams = new ArrayList<>();
        covering = new ArrayList<>();
        beamsSupportedSpans = new ArrayList<>();
        beamsSupportPosts = new ArrayList<>();
        patioInfo = new PatioInfo();
        patioValidator = new Validator(this);
        errorsFoundListener = new ArrayList<>();
        noErrorsFoundListener = new ArrayList<>();
        optimalPatioConfigurations = new ArrayList<>();
        totalPricesPerLumberDimensions = new HashMap<>();
        piecesQuantities = new HashMap<>();
    }

    /**
     * Adds an ErrorFoundListener object to the list
     *
     * @param _errorsFoundListener ErrorFoundListener object
     */
    public void addErrorsFoundListener(ErrorsFoundListener _errorsFoundListener) {
        errorsFoundListener.add(_errorsFoundListener);
    }

    /**
     * Adds a NoErrorFoundListener object to the list
     *
     * @param _noErrorsFoundListener NoErrorFoundListener object
     */
    public void addNoErrorsFoundListener(NoErrorsFoundListener _noErrorsFoundListener) {
        noErrorsFoundListener.add(_noErrorsFoundListener);
    }

    /**
     * Updates the patio with the newly modified PatioInfo property
     */
    public void updatePatio() {
        generatePatio();
        patioValidator.validate();

        if (!patioValidator.getErrors().isEmpty() && errorsFoundListener != null)
            notifyErrorsFound();
        else
            notifyNoErrorsFound();
    }

    /**
     * Notifies the listeners that errors were found while generating the patio
     */
    private void notifyErrorsFound() {
        for (ErrorsFoundListener errorFoundListener : errorsFoundListener)
            errorFoundListener.onErrorsFound(patioValidator.getErrors());
    }

    /**
     * Notifies the listeners that no errors were found while generating the patio
     */
    private void notifyNoErrorsFound() {
        for (NoErrorsFoundListener noErrorsFoundListener : noErrorsFoundListener)
            noErrorsFoundListener.onNoErrorsFound();
    }

    /**
     * Generates the patio with properties specified by the user. Calculates its total price according
     * to lumber prices specified by the user
     */
    private void generatePatio() {
        components.clear();

        generateSpans();
        generatePosts();
        generateBeams();
        generateCovering();

        calculateTotalPrice();
        calculatePiecesQuantities();
    }

    private void calculatePiecesQuantities() {
        piecesQuantities.clear();

        for (Component component : components) {
            for (WoodPiece woodPiece : component.getWoodPieces()) {
                String nominalHeightString =
                        new Fraction(woodPiece.getBuyingDimensions().getNominalHeight()).toString();
                String nominalWidthString = new Fraction(woodPiece.getBuyingDimensions().getNominalWidth()).toString();
                String lumberDimensions =
                        LumberDimension.formatToImperialLumberDimensions(nominalHeightString, nominalWidthString);


                float depth = Conversion.inchesToFeet(woodPiece.getDimensions().getActualDepth());
                int quantity = 1;

                if (piecesQuantities.containsKey(lumberDimensions)) {
                    if (piecesQuantities.get(lumberDimensions).containsKey(depth))
                        quantity = piecesQuantities.get(lumberDimensions).get(depth) + 1;
                } else
                    piecesQuantities.put(lumberDimensions, new HashMap<Float, Integer>());
                piecesQuantities.get(lumberDimensions).put(depth, quantity);
            }
        }
    }

    public HashMap<String, HashMap<Float, Integer>> getPiecesQuantities() {
        return piecesQuantities;
    }

    /**
     * Generates the spans
     */
    private void generateSpans() {
        spans.clear();
        beamsSupportedSpans.clear();

        int nbSpans = patioInfo.getSpanAmount();
        float joistSpacing = patioInfo.getJoistSpacing();
        Dimensions joistDimensions = patioInfo.getJoistDimensions();
        float joistSpan = patioInfo.getJoistSpan();
        float joistYPos = patioInfo.getPostDimensions().getActualDepth() +
                patioInfo.getBeamPlieDimensions().getActualWidth() + joistDimensions.getActualWidth() / 2;
        int nbJoistsPerSpan = patioInfo.getJoistAmountPerSpan();

        for (int idxSpan = 0; idxSpan < nbSpans; idxSpan++) {
            Span span = new Span(joistSpacing, joistDimensions, joistSpan, joistYPos, nbJoistsPerSpan, idxSpan);

            if (idxSpan == nbSpans - 1) {
                float cantileverLength = patioInfo.getCantileverLength();
                span.setCantileverLength(cantileverLength);
            }

            span.setColor(patioInfo.getJoistColor());
            span.setVisible(patioInfo.isJoistVisibility());
            span.setColorFilling(patioInfo.isJoistColorFilling());

            components.add(span);
            spans.add(idxSpan, span);
        }

        int nbBeams = patioInfo.getBeamAmount();
        for (int idxBeam = 0; idxBeam < nbBeams; idxBeam++) {
            ArrayList<Span> supportedSpansForCurrentBeam = new ArrayList<>();

            if (idxBeam < nbBeams - 1) {
                supportedSpansForCurrentBeam.add(spans.get(idxBeam));
            }
            if (idxBeam > 0 && idxBeam <= nbBeams - 1) {
                supportedSpansForCurrentBeam.add(spans.get(idxBeam - 1));
            }

            beamsSupportedSpans.add(idxBeam, supportedSpansForCurrentBeam);
        }
    }

    /**
     * Generates the posts
     */
    private void generatePosts() {
        posts.clear();
        beamsSupportPosts.clear();

        //We set an empty array of support posts for the first beam, because the first beam is only supported by
        //the house, and not beams
        beamsSupportPosts.add(0, new ArrayList<Post>());

        //There are at least two posts for every beam, and there aren't any posts
        //supporting the beam attached to the house
        int nbBeams = patioInfo.getBeamAmount();
        int nbPostsPerBeam = patioInfo.getPostsPerBeam();

        Dimensions postDimensions = patioInfo.getPostDimensions();
        float postSpacing = patioInfo.getPostSpacing();
        float joistSpan = patioInfo.getJoistSpan();

        //We start adding posts for the second beam only, because the first beam is attached to the house, and thus
        //doesn't need any support posts
        for (int idxSupportedBeam = 1; idxSupportedBeam < nbBeams; idxSupportedBeam++) {
            ArrayList<Post> supportPostsForCurrentBeam = new ArrayList<>();

            for (int idxPostInSupportedBeam = 0; idxPostInSupportedBeam < nbPostsPerBeam; idxPostInSupportedBeam++) {
                Post post = new Post(postDimensions, postSpacing, joistSpan, idxSupportedBeam, idxPostInSupportedBeam);

                post.setColor(patioInfo.getPostColor());
                post.setVisible(patioInfo.isPostVisibility());
                post.setColorFilling(patioInfo.isPostColorFilling());

                components.add(post);
                posts.add(post);
                supportPostsForCurrentBeam.add(idxPostInSupportedBeam, post);
            }

            beamsSupportPosts.add(idxSupportedBeam, supportPostsForCurrentBeam);
        }
    }

    /**
     * Generates the beams
     */
    private void generateBeams() {
        beams.clear();

        int nbBeams = patioInfo.getBeamAmount();
        int pliesPerBeam = patioInfo.getPliesPerBeam();
        Dimensions beamPliesDimensions = patioInfo.getBeamPlieDimensions();
        float joistSpan = patioInfo.getJoistSpan();
        float beamYPos = patioInfo.getPostDimensions().getActualDepth() + (beamPliesDimensions.getActualWidth() / 2);

        for (int idxBeam = 0; idxBeam < nbBeams; idxBeam++) {
            boolean isAttachedToHouse = idxBeam == 0;

            Beam beam = new Beam(pliesPerBeam, beamPliesDimensions, joistSpan, beamYPos, isAttachedToHouse, idxBeam);
            beam.setSupportedSpans(beamsSupportedSpans.get(idxBeam));
            beam.setSupportPosts(beamsSupportPosts.get(idxBeam));

            beam.setColor(patioInfo.getBeamColor());
            beam.setVisible(patioInfo.isBeamVisibility());
            beam.setColorFilling(patioInfo.isBeamColorFilling());

            components.add(beam);
            beams.add(idxBeam, beam);
        }
    }

    /**
     * Generates the covering
     */
    private void generateCovering() {
        Dimensions coveringPlankDimensions = patioInfo.getCoveringDimensions();
        float patioDepth = patioInfo.getPatioDimensions().getActualDepth() +
                patioInfo.getBeamPlieDimensions().getActualHeight();
        float coveringSpacing = patioInfo.getCoveringSpacing();
        float coveringYPos = patioInfo.getPostDimensions().getActualDepth() +
                patioInfo.getBeamPlieDimensions().getActualWidth() +
                patioInfo.getJoistDimensions().getActualWidth() +
                (coveringPlankDimensions.getActualHeight() / 2);
        float coveringLength = patioInfo.getBeamPlieDimensions().getActualDepth();
        float coveringWidth = coveringPlankDimensions.getActualWidth();
        float beamWidth = patioInfo.getBeamPlieDimensions().getActualHeight() / 2;

        float plankUnit = coveringWidth + coveringSpacing;
        float coveringPlanksRatio = (patioDepth) / plankUnit;
        int nbCoveringPlanks = (int) Math.ceil(coveringPlanksRatio);
        float firstCoveringPlankWidth = patioDepth - (plankUnit * (nbCoveringPlanks - 1));
        float beginSpacing = 0f;
        if (firstCoveringPlankWidth > coveringWidth) {
            beginSpacing = firstCoveringPlankWidth - coveringWidth;
            firstCoveringPlankWidth = coveringWidth;
        }

        Covering newCovering = new Covering(
                nbCoveringPlanks, coveringSpacing, coveringPlankDimensions, firstCoveringPlankWidth,
                beginSpacing, beamWidth, coveringYPos, coveringLength);

        newCovering.setColor(patioInfo.getCoveringColor());
        newCovering.setVisible(patioInfo.isCoveringVisibility());
        newCovering.setColorFilling(patioInfo.isCoveringColorFilling());

        components.add(newCovering);

        ArrayList<Covering> coverings = new ArrayList<>();
        coverings.add(newCovering);
        covering = coverings;
    }

    /**
     * Calculates the total price with the lumber prices specified by the user
     */
    public float calculateTotalPrice() {
        totalPricesPerLumberDimensions.clear();

        for (Component component : components) {
            for (WoodPiece woodPiece : component.getWoodPieces()) {
                String nominalHeightString =
                        new Fraction(woodPiece.getBuyingDimensions().getNominalHeight()).toString();
                String nominalWidthString = new Fraction(woodPiece.getBuyingDimensions().getNominalWidth()).toString();
                String lumberDimensions =
                        LumberDimension.formatToImperialLumberDimensions(nominalHeightString, nominalWidthString);

                float previousSubTotal = totalPricesPerLumberDimensions.containsKey(lumberDimensions) ?
                        totalPricesPerLumberDimensions.get(lumberDimensions) : 0f;
                float newSubTotal = previousSubTotal + patioInfo.getLumberPricePerDimensions(lumberDimensions) *
                        Conversion.inchesToFeet(woodPiece.getDimensions().getActualDepth());
                totalPricesPerLumberDimensions.put(lumberDimensions, newSubTotal);
            }
        }

        return getTotalPrice();
    }

    /**
     * Returns the total price of the patio
     *
     * @return The total price of the patio
     */
    public float getTotalPrice() {
        float totalPrice = 0f;

        Iterator it = totalPricesPerLumberDimensions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry lumberSubTotal = (Map.Entry) it.next();
            totalPrice += (Float) lumberSubTotal.getValue();
        }

        return totalPrice;
    }

    /**
     * Returns the total price of a specific wood piece lumber dimensions
     *
     * @param lumberDimensions The dimensions of a specific wood piece
     * @return The total price of a specific wood piece lumber dimensions
     */
    public float getLumberTotalPrice(String lumberDimensions) {
        return totalPricesPerLumberDimensions.containsKey(lumberDimensions) ?
                totalPricesPerLumberDimensions.get(lumberDimensions) : 0f;
    }

    /**
     * Generates the cheapest patio using the patio width, depth and height, plus the covering dimensions and its spacing
     */
    public void generateOptimalPatio() {
        optimalPatioConfigurations.clear();

        Dimensions optimalPatioDimensions = patioInfo.getPatioDimensions();
        Dimensions optimalCoveringDimensions = patioInfo.getCoveringDimensions();
        float optimalCoveringSpacing = patioInfo.getCoveringSpacing();
        boolean couldNotGenerateOptimalPatio = false;

        iterateThroughPossibleJoistDimensions(
                optimalPatioDimensions, optimalCoveringDimensions, optimalCoveringSpacing);

        if (optimalPatioConfigurations.isEmpty()) {
            couldNotGenerateOptimalPatio = true;
            patioInfo = initialPatioInfo;
        } else {
            patioInfo = getCheapestPatioConfiguration();
            setAsInitialPatioInfo();
        }
        generatePatio();

        if (couldNotGenerateOptimalPatio)
            addExternalError(ValidationErrorType.OPTIMAL_PATIO_CONFIGURATION);
        else
            notifyNoErrorsFound();
    }

    /**
     * Returns the cheapest patio configuration found
     *
     * @return The cheapest patio configuration found
     */
    private PatioInfo getCheapestPatioConfiguration() {
        float cheapestPrice = Float.MAX_VALUE;
        PatioInfo cheapestPatioConfiguration = null;

        for (Pair<Float, PatioInfo> patioConfiguration : optimalPatioConfigurations) {
            if (patioConfiguration.first < cheapestPrice) {
                cheapestPrice = patioConfiguration.first;
                cheapestPatioConfiguration = patioConfiguration.second;
            }
        }

        return cheapestPatioConfiguration;
    }

    /**
     * Identifies all the possible joist dimensions, and tries to create a valid patio with them
     *
     * @param _optimalPatioDimensions The overall patio dimensions specified by the user
     * @param _optimalCoveringDimensions The covering dimensions specified by the user
     * @param _optimalCoveringSpacing The covering spacing specified by the user
     */
    private void iterateThroughPossibleJoistDimensions(
            Dimensions _optimalPatioDimensions, Dimensions _optimalCoveringDimensions, float _optimalCoveringSpacing) {
        String[] realJoistPossibility = LumberDimension.getPossibility(MeasureType.REAL, ComponentType.JOIST);
        for (String joistDimensionsPossibility : realJoistPossibility) {
            PatioInfo patioConfiguration = new PatioInfo(patioInfo);

            patioConfiguration.setPatioDimensions(_optimalPatioDimensions, false);
            patioConfiguration.setCoveringDimensions(_optimalCoveringDimensions, false);
            patioConfiguration.setCoveringSpacing(_optimalCoveringSpacing);

            String[] joistDimensionsString = LumberDimension.splitLumberDimensions(joistDimensionsPossibility);
            float joistWidth = new Fraction(joistDimensionsString[1]).toFloat();
            float joistHeight = new Fraction(joistDimensionsString[0]).toFloat();
            float joistTempDepth = 0f;

            Dimensions joistDimensions = new Dimensions(joistWidth, joistHeight, joistTempDepth);
            patioConfiguration.setJoistDimensions(joistDimensions, false);

            iterateThroughPossibleCantileverLengths(patioConfiguration);
        }
    }

    /**
     * Identifies all the possible cantilever lengths, and tries to create a valid patio with them
     *
     * @param _patioConfiguration The current patio configuration being tested
     */
    private void iterateThroughPossibleCantileverLengths(PatioInfo _patioConfiguration) {
        float maxAllowedCantileverLength =
                patioValidator.getAllowedCantileverLength(_patioConfiguration.getJoistDimensions());
        if (maxAllowedCantileverLength != -1f) {
            _patioConfiguration.setCantileverLength(maxAllowedCantileverLength, false);
            iterateThroughPossiblePostDimensions(_patioConfiguration);
        }

        _patioConfiguration.setCantileverLength(0, false);
        iterateThroughPossiblePostDimensions(_patioConfiguration);
    }

    /**
     * Identifies all the possible post dimensions, and tries to create a valid patio with them
     *
     * @param _patioConfiguration The current patio configuration being tested
     */
    private void iterateThroughPossiblePostDimensions(PatioInfo _patioConfiguration) {
        float maxJoistSpacing = patioValidator.getAllowedJoistSpacing(_patioConfiguration.getCoveringDimensions());

        for (String postDimensionsPossibility : LumberDimension.getPossibility(MeasureType.REAL, ComponentType.POST)) {
            String[] postDimensionsString = LumberDimension.splitLumberDimensions(postDimensionsPossibility);
            float postWidth = new Fraction(postDimensionsString[1]).toFloat();
            float postHeight = new Fraction(postDimensionsString[0]).toFloat();
            float postTempDepth = 0f;

            Dimensions postDimensions = new Dimensions(postWidth, postHeight, postTempDepth);
            _patioConfiguration.setPostDimensions(postDimensions, false);

            float beamTempWidth = 0f;
            float beamTempHeight = 0f;
            float beamDepth =
                    _patioConfiguration.getPatioDimensions().getActualWidth() - postDimensions.getActualWidth();

            Dimensions beamDimensions = new Dimensions(beamTempWidth, beamTempHeight, beamDepth);
            _patioConfiguration.setBeamDimensions(beamDimensions, false);

            float coveringWidth = _patioConfiguration.getCoveringDimensions().getActualWidth();
            float coveringHeight = _patioConfiguration.getCoveringDimensions().getActualHeight();

            Dimensions newCoveringDimensions = new Dimensions(coveringWidth, coveringHeight, beamDepth);
            _patioConfiguration.setCoveringDimensions(newCoveringDimensions, false);

            float totalJoistSpacingLength =
                    beamDepth - (2 * _patioConfiguration.getJoistDimensions().getActualHeight());
            float joistSpacingRatio = totalJoistSpacingLength / maxJoistSpacing;
            int nbJoists = (int) Math.ceil(joistSpacingRatio);
            float actualJoistSpacing = totalJoistSpacingLength / nbJoists;
            _patioConfiguration.setJoistSpacing(actualJoistSpacing);

            float maxJoistSpan =
                    patioValidator.getAllowedSpanLength(actualJoistSpacing, _patioConfiguration.getJoistDimensions());
            _patioConfiguration.setJoistSpanMaxLength(maxJoistSpan, false);

            identifyAllowedBeamProperties(_patioConfiguration);
        }
    }

    /**
     * Identifies all the allowed beam properties (dimensions, number of plies)
     *
     * @param _patioConfiguration The current patio configuration being tested
     */
    private void identifyAllowedBeamProperties(PatioInfo _patioConfiguration) {
        float joistSpanRatio = (_patioConfiguration.getPatioDimensions().getActualDepth() -
                _patioConfiguration.getCantileverLength()) / _patioConfiguration.getJoistSpanMaxLength();
        int nbSections = (int) Math.ceil(joistSpanRatio);

        int nbPostsPerBeam = 2;
        float currentPostSpacing = _patioConfiguration.getBeamPlieDimensions().getActualDepth();
        float actualJoistSpan = (_patioConfiguration.getPatioDimensions().getActualDepth() -
                _patioConfiguration.getCantileverLength()) / nbSections;
        boolean presenceOfBeamsSupportingTwoSpans = nbSections > 1;

        Map<String, Integer> beamProperties = patioValidator.getAllowedBeamProperties(
                currentPostSpacing, actualJoistSpan, presenceOfBeamsSupportingTwoSpans);
        while (beamProperties == null) {
            if (currentPostSpacing < _patioConfiguration.getMinPostSpacing()) {
                return;
            }

            currentPostSpacing = _patioConfiguration.getBeamPlieDimensions().getActualDepth() / nbPostsPerBeam++;
            beamProperties = patioValidator.getAllowedBeamProperties(
                    currentPostSpacing, actualJoistSpan, presenceOfBeamsSupportingTwoSpans);
        }

        _patioConfiguration.setPostsPerBeam(nbPostsPerBeam, false);
        _patioConfiguration.setPostSpacing(currentPostSpacing, false);

        float minBeamHeight = Conversion.getActualInchesFromNominal(beamProperties.get(Validator.BEAMS_HEIGHT_KEY));
        float minBeamWidth = Conversion.getActualInchesFromNominal(beamProperties.get(Validator.BEAMS_WIDTH_KEY));
        int minPliesPerBeam = beamProperties.get(Validator.PLIES_COUNT_KEY);

        iterateThroughPossibleBeamProperties(_patioConfiguration, actualJoistSpan,
                minBeamHeight, minBeamWidth, minPliesPerBeam);

        if (beamProperties.get(Validator.ALTERNATE_BEAMS_HEIGHT_KEY) != null &&
                beamProperties.get(Validator.ALTERNATE_BEAMS_WIDTH_KEY) != null &&
                beamProperties.get(Validator.ALTERNATE_PLIES_COUNT_KEY) != null) {
            float alternateMinBeamHeight =
                    Conversion.getActualInchesFromNominal(beamProperties.get(Validator.ALTERNATE_BEAMS_HEIGHT_KEY));
            float alternateMinBeamWidth =
                    Conversion.getActualInchesFromNominal(beamProperties.get(Validator.ALTERNATE_BEAMS_WIDTH_KEY));
            int alternateMinPliesPerBeam = beamProperties.get(Validator.ALTERNATE_PLIES_COUNT_KEY);

            iterateThroughPossibleBeamProperties(_patioConfiguration, actualJoistSpan,
                    alternateMinBeamHeight, alternateMinBeamWidth, alternateMinPliesPerBeam);
        }
    }

    /**
     * Tries to create a valid patio with all the possible beam properties (dimensions, number of plies)
     *
     * @param _patioConfiguration The current patio configuration being tested
     * @param _actualJoistSpan The actual joist span in inches
     * @param _minBeamHeight The minimum beam height in inches
     * @param _minBeamWidth The minimum beam width in inches
     * @param _minPliesPerBeam The minimum beam plies
     */
    private void iterateThroughPossibleBeamProperties(PatioInfo _patioConfiguration, float _actualJoistSpan,
                                                      float _minBeamHeight, float _minBeamWidth, int _minPliesPerBeam) {
        for (String beamDimensionsPossibility : LumberDimension.getPossibility(MeasureType.REAL, ComponentType.BEAM)) {
            String[] beamDimensionsString = LumberDimension.splitLumberDimensions(beamDimensionsPossibility);
            float beamWidth = new Fraction(beamDimensionsString[1]).toFloat();
            float beamHeight = new Fraction(beamDimensionsString[0]).toFloat();

            if (beamWidth < _minBeamWidth || beamHeight < _minBeamHeight)
                continue;

            int maxPliesPerBeam = _patioConfiguration.getMaxPliesPerBeam();
            for (int pliesPerBeam = _minPliesPerBeam; pliesPerBeam <= maxPliesPerBeam; pliesPerBeam++) {
                _patioConfiguration.setBeamDimensions(new Dimensions(beamWidth, beamHeight,
                        _patioConfiguration.getBeamPlieDimensions().getActualDepth()), false);
                _patioConfiguration.setPliesPerBeam(_minPliesPerBeam);

                float joistWidth = _patioConfiguration.getJoistDimensions().getActualWidth();
                float joistHeight = _patioConfiguration.getJoistDimensions().getActualHeight();
                float joistDepth = _actualJoistSpan + beamHeight;

                _patioConfiguration.setJoistDimensions(new Dimensions(joistWidth, joistHeight, joistDepth), false);

                float patioHeightWithoutPosts =
                        _patioConfiguration.getPatioHeightExcludingComponent(ComponentType.POST);
                float postWidth = _patioConfiguration.getPostDimensions().getActualWidth();
                float postHeight = _patioConfiguration.getPostDimensions().getActualHeight();
                float postDepth = Math.max(
                        0, _patioConfiguration.getPatioDimensions().getActualHeight() - patioHeightWithoutPosts);

                _patioConfiguration.setPostDimensions(new Dimensions(postWidth, postHeight, postDepth), false);

                patioInfo = _patioConfiguration;
                generatePatio();
                patioValidator.validate();

                if (!patioValidator.getErrors().isEmpty())
                    continue;

                float totalPrice = getTotalPrice();
                optimalPatioConfigurations.add(new Pair<>(totalPrice, new PatioInfo(_patioConfiguration)));
            }
        }
    }

    /**
     * Obtains all the patio components
     *
     * @return An ArrayList containing all the patio components
     */
    public ArrayList<Component> getComponents() {
        return components;
    }

    /**
     * Obtains all the patio spans
     *
     * @return An ArrayList containing all the patio spans
     */
    public ArrayList<Span> getSpans() {
        return spans;
    }

    /**
     * Obtains all the patio posts
     *
     * @return An ArrayList containing all the patio posts
     */
    public ArrayList<Post> getPosts() {
        return posts;
    }

    /**
     * Obtains the patio covering
     *
     * @return An ArrayList containing all the patio coverings
     */
    public ArrayList<Covering> getCovering() {
        return covering;
    }

    /**
     * Obtains all the patio beams
     *
     * @return An ArrayList containing all the patio beams
     */
    public ArrayList<Beam> getBeams() {
        return beams;
    }

    /**
     * Obtains the PatioInfo DTO containing all the information about the patio
     *
     * @return The PatioInfo DTO containing all the information about the patio
     */
    public PatioInfo getPatioInfo() {
        return patioInfo;
    }

    /**
     * Sets the patioInfo to a new value
     *
     * @param _patioInfo The new patioInfo value
     */
    public void setPatioInfo(PatioInfo _patioInfo) {
        this.patioInfo = _patioInfo;
    }

    /**
     * Adds an external error the the patio validator
     *
     * @param _optimalPatioConfiguration The error to add to the patio validator
     */
    private void addExternalError(ValidationErrorType _optimalPatioConfiguration) {
        patioValidator.addExternalError(_optimalPatioConfiguration);
        notifyErrorsFound();
    }

    /**
     * Sets the current patioInfo as the patioInfo in case no optimal patio configurations were found
     */
    public void setAsInitialPatioInfo() {
        initialPatioInfo = new PatioInfo(patioInfo);
    }
}
