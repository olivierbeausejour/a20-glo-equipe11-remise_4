package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Conversion;
import ca.ulaval.glo2004.utils.Dimensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class contains method to validate a patio in Patinator according the Canadian Wood Council Guide from 2020.
 */
public class Validator {
    /**
     * Validate the number of plies per beam.
     */
    public static final String PLIES_COUNT_KEY = "pliesCount";
    /**
     * Validate the beam width.
     */
    public static final String BEAMS_WIDTH_KEY = "beamsWidth";
    /**
     * Validate the beam height.
     */
    public static final String BEAMS_HEIGHT_KEY = "beamsHeight";
    /**
     * Validate the alternate plies count.
     */
    public static final String ALTERNATE_PLIES_COUNT_KEY = "alternatePliesCount";
    /**
     * Validate the alternate beam width.
     */
    public static final String ALTERNATE_BEAMS_WIDTH_KEY = "alternateBeamsWidth";
    /**
     * Validate the alternate beam height.
     */
    public static final String ALTERNATE_BEAMS_HEIGHT_KEY = "alternateBeamsHeight";

    private final Patio patio;
    private final HashSet<ValidationErrorType> errors;

    /**
     * Create a validator
     *
     * @param _patio Patio object to validate.
     */
    public Validator(Patio _patio) {
        patio = _patio;
        errors = new HashSet<>();
    }

    /**
     * Get all validation error.
     *
     * @return All validation error found.
     */
    public HashSet<ValidationErrorType> getErrors() {
        return errors;
    }

    /**
     * Validate the current patio.
     */
    public void validate() {
        errors.clear();

        validatePosts();
        validateSpans();
        validateCantilever();
        validateBeams();
    }

    /**
     * Validate patio post from beams dimensions.
     */
    private void validatePosts() {
        ArrayList<Beam> beams = patio.getBeams();

        for (Beam beam : beams) {
            //Because the first beam is not supported by any posts, we ignore it
            if (beam.isAttachedToHouse()) {
                continue;
            }

            Post post = beam.getSupportPosts().get(0);
            float postHeight = Conversion.inchesToFeet(post.getPostDimensions().getActualDepth());
            Dimensions validPostDimensions = getAllowedPostDimensions(postHeight, beam);

            if (validPostDimensions != null) {
                float validWidth = validPostDimensions.getNominalWidth();
                float validDepth = validPostDimensions.getNominalHeight();

                if (post.getPostDimensions().getNominalWidth() < validWidth ||
                        post.getPostDimensions().getNominalHeight() < validDepth) {
                    errors.add(ValidationErrorType.POST_DIMENSIONS);
                }

                if (postHeight > 12f) {
                    errors.add(ValidationErrorType.POST_HEIGHT);
                }
            } else {
                errors.add(ValidationErrorType.POST_HEIGHT);
            }
        }
    }

    /**
     * Validate patio spans from covering dimensions and joist attribute.
     */
    private void validateSpans() {
        ArrayList<Span> spans = patio.getSpans();

        for (Span span : spans) {
            float joistSpacing = span.getJoistSpacing();
            Dimensions joistDimensions = span.getJoistDimensions();
            Dimensions coveringDimensions = patio.getPatioInfo().getCoveringDimensions();

            float allowedSpanLength = getAllowedSpanLength(joistSpacing, joistDimensions);
            if (allowedSpanLength != -1) {
                if (Float.compare(span.getJoistSpan(), allowedSpanLength) > 0) {
                    errors.add(ValidationErrorType.SPAN_LENGTH);
                }
            } else if (joistSpacing > 24) {
                errors.add(ValidationErrorType.JOIST_SPACING);
            } else {
                errors.add(ValidationErrorType.JOIST_DIMENSIONS);
            }

            float allowedJoistSpacing = getAllowedJoistSpacing(coveringDimensions);
            if (allowedJoistSpacing != -1) {
                if (Float.compare(span.getJoistSpacing(), allowedJoistSpacing) > 0) {
                    errors.add(ValidationErrorType.JOIST_SPACING);
                }
            } else {
                errors.add(ValidationErrorType.COVERING_DIMENSIONS);
            }
        }
    }

    /**
     * Validate patio cantilever from joist dimensions.
     */
    private void validateCantilever() {
        Dimensions joistDimensions = patio.getPatioInfo().getJoistDimensions();
        float cantileverLength = patio.getPatioInfo().getCantileverLength();
        float allowedCantileverLength = getAllowedCantileverLength(joistDimensions);

        if (allowedCantileverLength != -1) {
            if (Float.compare(cantileverLength, allowedCantileverLength) > 0) {
                errors.add(ValidationErrorType.CANTILEVER_LENGTH);
            }
        } else {
            errors.add(ValidationErrorType.JOIST_DIMENSIONS);
        }
    }

    /**
     * Validate patio beams from joist and post attribute. Also validate if the beam support multiple spans.
     */
    private void validateBeams() {
        ArrayList<Beam> beams = patio.getBeams();

        for (Beam beam : beams) {
            float postSpacing = patio.getPatioInfo().getPostSpacing();
            float spanLength = patio.getPatioInfo().getJoistSpan();
            boolean isSupportingTwoSpans = beam.isSupportingTwoSpans();

            Map<String, Integer> allowedBeamProperties =
                    getAllowedBeamProperties(postSpacing, spanLength, isSupportingTwoSpans);
            ValidationErrorType plieError =
                    isSupportingTwoSpans ? ValidationErrorType.PLIES_PER_BEAM_TWO_SPANS :
                            ValidationErrorType.PLIES_PER_BEAM_SINGLE_SPAN;
            ValidationErrorType dimensionsError =
                    isSupportingTwoSpans ? ValidationErrorType.BEAM_DIMENSIONS_TWO_SPANS :
                            ValidationErrorType.BEAM_DIMENSIONS_SINGLE_SPAN;

            if (allowedBeamProperties != null) {
                int pliesPerBeam = beam.getNbPlies();
                Dimensions plieDimension = beam.getPliesDimensions();

                ArrayList<ValidationErrorType> possibleErrors = new ArrayList<>();

                if (pliesPerBeam < allowedBeamProperties.get(PLIES_COUNT_KEY)) {
                    possibleErrors.add(plieError);
                }

                if (plieDimension.getNominalWidth() < allowedBeamProperties.get(BEAMS_WIDTH_KEY)) {
                    possibleErrors.add(dimensionsError);
                }

                if (plieDimension.getNominalHeight() < allowedBeamProperties.get(BEAMS_HEIGHT_KEY)) {
                    possibleErrors.add(dimensionsError);
                }

                // if alternate properties exist...
                if (allowedBeamProperties.get(ALTERNATE_PLIES_COUNT_KEY) != null) {
                    if (pliesPerBeam < allowedBeamProperties.get(ALTERNATE_PLIES_COUNT_KEY) ||
                            plieDimension.getNominalWidth() < allowedBeamProperties.get(ALTERNATE_BEAMS_WIDTH_KEY) ||
                            plieDimension.getNominalHeight() < allowedBeamProperties.get(ALTERNATE_BEAMS_HEIGHT_KEY)) {
                        errors.addAll(possibleErrors);
                    }
                } else {
                    errors.addAll(possibleErrors);
                }
            } else {
                if (postSpacing > Conversion.feetToInches(8)) {
                    errors.add(ValidationErrorType.POST_SPACING);
                } else {
                    errors.add(ValidationErrorType.SPAN_LENGTH);
                }
            }
        }
    }

    /**
     * Get allowed post dimensions according to his height and beam attribute.
     *
     * @param _postHeight Post height.
     * @param _beam       Supported beam.
     * @return Allowed post dimensions.
     */
    public Dimensions getAllowedPostDimensions(float _postHeight, Beam _beam) {
        if (_beam != null) {
            if (_beam.getWoodPieces().size() == 3) {
                float postWidthAndHeight = Conversion.getActualInchesFromNominal(6);
                return new Dimensions(postWidthAndHeight, postWidthAndHeight, _postHeight);
            }

            if (_postHeight < 1) {
                return null;

            } else if (_postHeight <= 6.5f) {
                float postWidthAndHeight = Conversion.getActualInchesFromNominal(4);
                return new Dimensions(postWidthAndHeight, postWidthAndHeight, _postHeight);

            } else if (_postHeight <= 12.0f) {
                float postWidthAndHeight = Conversion.getActualInchesFromNominal(6);
                return new Dimensions(postWidthAndHeight, postWidthAndHeight, _postHeight);

            }
        }
        return null;

    }

    /**
     * Get allowed span length according to joist spacing and dimension.
     *
     * @param _joistSpacing    Spacing of joist composing the span.
     * @param _joistDimensions Dimensions of joist composing the span.
     * @return Allowed span length value.
     */
    public float getAllowedSpanLength(float _joistSpacing, Dimensions _joistDimensions) {
        if (_joistDimensions != null) {
            float nominalHeight = _joistDimensions.getNominalHeight();
            float nominalWidth = _joistDimensions.getNominalWidth();

            if (_joistSpacing <= 0) {
                return -1;
            } else if (nominalHeight == 2) {
                if (_joistSpacing <= 8) {
                    switch ((int) nominalWidth) {
                        case 4:
                            return Conversion.feetToInches(7, 4);
                        case 6:
                            return Conversion.feetToInches(11, 6);
                        case 8:
                            return Conversion.feetToInches(15, 1);
                        case 10:
                            return Conversion.feetToInches(19, 3);
                        case 12:
                            return Conversion.feetToInches(23, 5);
                    }
                } else if (_joistSpacing <= 12) {
                    switch ((int) nominalWidth) {
                        case 4:
                            return Conversion.feetToInches(6, 5);
                        case 6:
                            return Conversion.feetToInches(10, 0);
                        case 8:
                            return Conversion.feetToInches(13, 2);
                        case 10:
                            return Conversion.feetToInches(16, 10);
                        case 12:
                            return Conversion.feetToInches(20, 4);
                    }
                } else if (_joistSpacing <= 16) {
                    switch ((int) nominalWidth) {
                        case 4:
                            return Conversion.feetToInches(5, 10);
                        case 6:
                            return Conversion.feetToInches(9, 1);
                        case 8:
                            return Conversion.feetToInches(12, 0);
                        case 10:
                            return Conversion.feetToInches(15, 2);
                        case 12:
                            return Conversion.feetToInches(17, 7);
                    }
                } else if (_joistSpacing <= 24) {
                    switch ((int) nominalWidth) {
                        case 4:
                            return Conversion.feetToInches(5, 1);
                        case 6:
                            return Conversion.feetToInches(8, 0);
                        case 8:
                            return Conversion.feetToInches(10, 2);
                        case 10:
                            return Conversion.feetToInches(12, 5);
                        case 12:
                            return Conversion.feetToInches(14, 4);
                    }
                }
            }
        }
        return -1;

    }

    /**
     * Get allowed joist spacing according to covering thickness.
     *
     * @param _coveringThickness Covering thickness dimensions.
     * @return Allowed joist spacing value.
     */
    public float getAllowedJoistSpacing(Dimensions _coveringThickness) {
        float coveringNominalHeight = _coveringThickness.getNominalHeight();
        float coveringNominalWidth = _coveringThickness.getNominalWidth();

        if (coveringNominalWidth == 6) {
            if (coveringNominalHeight == 5f / 4f) {
                return 12;

            } else if (coveringNominalHeight == 2) {
                return 18;

            }
        }
        return -1;

    }

    /**
     * Get allowed cantilever length according to joist dimensions.
     *
     * @param _joistDimensions Joist dimension.
     * @return Allowed cantilever length.
     */
    public float getAllowedCantileverLength(Dimensions _joistDimensions) {
        if (_joistDimensions != null) {
            float joistNominalHeight = _joistDimensions.getNominalHeight();
            float joistNominalWidth = _joistDimensions.getNominalWidth();

            if (joistNominalHeight == 2) {
                switch ((int) joistNominalWidth) {
                    case 4:
                        return 8;
                    case 6:
                    case 8:
                        return 16;
                    case 10:
                    case 12:
                        return 24;
                }
            }
        }
        return -1;

    }

    /**
     * Get allowed beam properties according to spans properties and post spacing.
     *
     * @param _postsSpacing         Spacing between posts.
     * @param _spanLength           Span length.
     * @param _isSupportingTwoSpans True if the beam support two spans.
     * @return Allowed beam properties.
     */
    public Map<String, Integer> getAllowedBeamProperties(
            float _postsSpacing, float _spanLength, boolean _isSupportingTwoSpans) {
        if (_isSupportingTwoSpans)
            return getAllowedTwoSpansBeamProperties(_postsSpacing, _spanLength);
        else
            return getAllowedOneSpanBeamProperties(_postsSpacing, _spanLength);
    }

    /**
     * Get allowed beam supporting two spans properties according to spans properties and post spacing.
     *
     * @param _postsSpacing Spacing between posts.
     * @param _spanLength   Span length.
     * @return Allowed beam properties.
     */
    private Map<String, Integer> getAllowedTwoSpansBeamProperties(float _postsSpacing, float _spanLength) {
        Map<String, Integer> beamProperties = new HashMap<>();

        if (_postsSpacing < 0) {
            return null;

        } else if (_postsSpacing <= Conversion.feetToInches(4)) {
            if (_spanLength <= Conversion.feetToInches(7)) {
                beamProperties.put(PLIES_COUNT_KEY, 1);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 6);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(7) && _spanLength <= Conversion.feetToInches(15)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 6);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(15) && _spanLength <= Conversion.feetToInches(16)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 8);
                return beamProperties;

            }
        } else if (_postsSpacing <= Conversion.feetToInches(6)) {
            if (_spanLength <= Conversion.feetToInches(7)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 6);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(7) && _spanLength <= Conversion.feetToInches(10)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 8);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(10) && _spanLength <= Conversion.feetToInches(15)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 10);

                beamProperties.put(ALTERNATE_PLIES_COUNT_KEY, 3);
                beamProperties.put(ALTERNATE_BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(ALTERNATE_BEAMS_WIDTH_KEY, 8);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(15) && _spanLength <= Conversion.feetToInches(16)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 12);

                beamProperties.put(ALTERNATE_PLIES_COUNT_KEY, 3);
                beamProperties.put(ALTERNATE_BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(ALTERNATE_BEAMS_WIDTH_KEY, 10);
            }
        } else if (_postsSpacing <= Conversion.feetToInches(8)) {
            if (_spanLength <= Conversion.feetToInches(5)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 8);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(5) && _spanLength <= Conversion.feetToInches(8)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 10);

                beamProperties.put(ALTERNATE_PLIES_COUNT_KEY, 3);
                beamProperties.put(ALTERNATE_BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(ALTERNATE_BEAMS_WIDTH_KEY, 8);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(8) && _spanLength <= Conversion.feetToInches(11)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 12);

                beamProperties.put(ALTERNATE_PLIES_COUNT_KEY, 3);
                beamProperties.put(ALTERNATE_BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(ALTERNATE_BEAMS_WIDTH_KEY, 10);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(11) && _spanLength <= Conversion.feetToInches(14)) {
                beamProperties.put(PLIES_COUNT_KEY, 3);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 10);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(14) && _spanLength <= Conversion.feetToInches(16)) {
                beamProperties.put(PLIES_COUNT_KEY, 3);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 12);
                return beamProperties;

            }
        }
        return null;

    }

    private Map<String, Integer> getAllowedOneSpanBeamProperties(float _postsSpacing, float _spanLength) {
        Map<String, Integer> beamProperties = new HashMap<>();

        if (_postsSpacing < 0) {
            return null;

        } else if (_postsSpacing <= Conversion.feetToInches(4)) {
            if (_spanLength <= Conversion.feetToInches(15)) {
                beamProperties.put(PLIES_COUNT_KEY, 1);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 6);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(15) && _spanLength <= Conversion.feetToInches(16)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 6);
                return beamProperties;

            }
        } else if (_postsSpacing <= Conversion.feetToInches(6)) {
            if (_spanLength <= Conversion.feetToInches(7)) {
                beamProperties.put(PLIES_COUNT_KEY, 1);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 6);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(7) && _spanLength <= Conversion.feetToInches(14)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 6);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(14) && _spanLength <= Conversion.feetToInches(16)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 8);
                return beamProperties;

            }
        } else if (_postsSpacing <= Conversion.feetToInches(8)) {
            if (_spanLength <= Conversion.feetToInches(7)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 6);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(7) && _spanLength <= Conversion.feetToInches(11)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 8);
                return beamProperties;

            } else if (_spanLength > Conversion.feetToInches(11) && _spanLength <= Conversion.feetToInches(16)) {
                beamProperties.put(PLIES_COUNT_KEY, 2);
                beamProperties.put(BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(BEAMS_WIDTH_KEY, 10);

                beamProperties.put(ALTERNATE_PLIES_COUNT_KEY, 3);
                beamProperties.put(ALTERNATE_BEAMS_HEIGHT_KEY, 2);
                beamProperties.put(ALTERNATE_BEAMS_WIDTH_KEY, 8);
                return beamProperties;

            }
        }

        return null;
    }

    /**
     * Add an external error inside errors container.
     *
     * @param _externalError External error to add.
     */
    public void addExternalError(ValidationErrorType _externalError) {
        errors.add(_externalError);
    }
}
