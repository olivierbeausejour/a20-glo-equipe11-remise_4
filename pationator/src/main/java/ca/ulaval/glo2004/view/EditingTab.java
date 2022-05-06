package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.ComponentType;
import ca.ulaval.glo2004.patio.MeasureUnit;
import ca.ulaval.glo2004.patio.PatioInfo;
import ca.ulaval.glo2004.patio.ValidationErrorType;
import ca.ulaval.glo2004.utils.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * TabsContent showing the dimension and specification of the patio component.
 */
public class EditingTab extends TabsContent implements ItemListener, ErrorsFoundListener, NoErrorsFoundListener,
        UndoActivatedListener, RedoActivatedListener, Scrollable {
    private static final String VALIDATION_ERROR_SUFFIX = "_ERROR";

    private final JPanel cardOptimalForm;
    private final HashMap<String, SpinnerModel> spinnerModel = new HashMap<>();
    private boolean isOptimalMode;
    private int postPerBeamLastValue;
    private int pliesPerBeamLastValue;

    private Border defaultBorder;
    private JComboBox<String> notOpCoveringThickness;
    private JComboBox<String> opCoveringThickness;
    private JComboBox<String> joistDimension;
    private JComboBox<String> postDimension;
    private JComboBox<String> beamDimension;
    private JTextField notOpCantileverLength;
    private JTextField notOpCoveringSpacing;
    private JTextField notOpPatioLength;
    private JTextField notOpPatioWidth;
    private JTextField notOpPatioHeight;
    private JTextField opCoveringSpacing;
    private JTextField opPatioLength;
    private JTextField opPatioWidth;
    private JTextField opPatioHeight;
    private JTextField beamLength;
    private JTextField postHeight;
    private JTextField joistSpacing;
    private JTextField joistSpanMaxLength;
    private JTextField postSpacing;
    private JSpinner pliesPerBeam;
    private JSpinner postsPerBeam;
    private JTextArea notOpErrorTextArea;
    private JTextArea opErrorTextArea;

    private double beamLengthValue;
    private double cantileverLengthValue;
    private double coveringSpacingValue;
    private double joistSpacingValue;
    private double joistSpanMaxLengthValue;
    private double patioLengthValue;
    private double patioWidthValue;
    private double patioHeightValue;
    private double postHeightValue;
    private double postSpacingValue;

    private String[] coveringThicknessPossibility;
    private String[] joistDimensionPossibility;
    private String[] beamDimensionPossibility;
    private String[] postDimensionPossibility;

    private PatioInfo previousPatioState;
    private boolean undoActivated = false;
    private boolean redoActivated = false;

    /**
     * Create an editing tab with the declared parent.
     *
     * @param _parent        Parent of current tab.
     * @param _isOptimalMode Default optimal design mode.
     */
    public EditingTab(Tabs _parent, boolean _isOptimalMode) {
        patioController = _parent.patioController;
        patioController.getPatio().addErrorsFoundListener(this);
        patioController.getPatio().addNoErrorsFoundListener(this);
        patioController.addUndoActivatedListener(this);
        patioController.addRedoActivatedListener(this);
        previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
        isOptimalMode = _isOptimalMode;

        createSpinnerModelBank();
        componentInitialization();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JToggleButton togOptimalMode = new JToggleButton(LocaleText.getString("OPTIMAL_MODE_LABEL"), _isOptimalMode);
        togOptimalMode.addItemListener(this);
        add(togOptimalMode);

        cardOptimalForm = new JPanel(new CardLayout());

        if (_isOptimalMode) {
            cardOptimalForm.add(buildOptimalForm(), "true");
            cardOptimalForm.add(buildNotOptimalForm(), "false");
        } else {
            cardOptimalForm.add(buildNotOptimalForm(), "false");
            cardOptimalForm.add(buildOptimalForm(), "true");
        }

        add(cardOptimalForm, BorderLayout.CENTER);
    }

    /**
     * Create spinner model for each numeric input in the tabs. The same spinner model can be use in multiple place.
     */
    private void createSpinnerModelBank() {
        final SpinnerNumberModel postPerBeam = new SpinnerNumberModel(
                2, patioController.getMinPostPerBeam(), patioController.getMaxPostPerBeam(), 1);

        postPerBeam.setValue(patioController.getPostPerBeam());
        postPerBeam.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent _changeEvent) {
                if (!postPerBeam.getValue().equals(postPerBeamLastValue)) {
                    if (!redoActivated && !undoActivated)
                        patioController.addPatioState();

                    patioController.setPostPerBeam((int) postPerBeam.getValue());

                    patioController.updateTabsContent();
                }
                postPerBeamLastValue = (int) postPerBeam.getValue();
            }
        });
        spinnerModel.put("postPerBeam", postPerBeam);

        final SpinnerNumberModel pliesPerBeam = new SpinnerNumberModel(
                1, patioController.getMinPliesPerBeam(), patioController.getMaxPliesPerBeam(), 1);

        pliesPerBeam.setValue(patioController.getPliesPerBeam());
        pliesPerBeam.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent _changeEvent) {
                if (!pliesPerBeam.getValue().equals(pliesPerBeamLastValue)) {
                    if (!redoActivated && !undoActivated)
                        patioController.addPatioState();
                    patioController.setPliesPerBeam((int) pliesPerBeam.getValue());

                    patioController.updateTabsContent();
                }
                pliesPerBeamLastValue = (int) pliesPerBeam.getValue();
            }
        });
        spinnerModel.put("pliesPerBeam", pliesPerBeam);
    }

    /**
     * Creates all visual components.
     */
    protected void componentInitialization() {
        joistSpanMaxLength = new JTextField();
        joistSpacing = new JTextField();
        postSpacing = new JTextField();
        beamLength = new JTextField();
        pliesPerBeam = new JSpinner(spinnerModel.get("pliesPerBeam"));
        postsPerBeam = new JSpinner(spinnerModel.get("postPerBeam"));
        postHeight = new JTextField();

        opPatioWidth = new JTextField();
        notOpPatioWidth = new JTextField();
        opPatioLength = new JTextField();
        notOpPatioLength = new JTextField();
        opPatioHeight = new JTextField();
        notOpPatioHeight = new JTextField();

        notOpCoveringSpacing = new JTextField();
        opCoveringSpacing = new JTextField();
        notOpCantileverLength = new JTextField();

        joistDimensionPossibility = patioController.getDimensionPosibility(ComponentType.JOIST);
        beamDimensionPossibility = patioController.getDimensionPosibility(ComponentType.BEAM);
        postDimensionPossibility = patioController.getDimensionPosibility(ComponentType.POST);
        coveringThicknessPossibility = patioController.getDimensionPosibility(ComponentType.COVERING_PLANK);

        joistDimension = new JComboBox<>(joistDimensionPossibility);
        beamDimension = new JComboBox<>(beamDimensionPossibility);
        postDimension = new JComboBox<>(postDimensionPossibility);
        opCoveringThickness = new JComboBox<>(coveringThicknessPossibility);
        notOpCoveringThickness = new JComboBox<>(coveringThicknessPossibility);

        notOpErrorTextArea = new JTextArea();
        notOpErrorTextArea.setEditable(false);
        notOpErrorTextArea.setLineWrap(true);
        notOpErrorTextArea.setWrapStyleWord(true);

        opErrorTextArea = new JTextArea();
        opErrorTextArea.setEditable(false);
        opErrorTextArea.setLineWrap(true);
        opErrorTextArea.setWrapStyleWord(true);

        defaultBorder = UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border");

        componentSetText();
        onNoErrorsFound();
    }

    /**
     * Create the form for the Optimal Mode patio conception.
     *
     * @return JPanel with component to create an optimal build form.
     */
    private JPanel buildOptimalForm() {
        JPanel optimalForm = new JPanel();
        optimalForm.setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

        addListenerForOptimalForm();

        addSectionTitle(optimalForm, LocaleText.getString("DIMENSIONS_SECTION_TITLE"));
        addComponent(optimalForm, new JLabel(LocaleText.getString("PATIO_WIDTH_LABEL")), opPatioWidth);
        addComponent(optimalForm, new JLabel(LocaleText.getString("PATIO_DEPTH_LABEL")), opPatioLength);
        addComponent(optimalForm, new JLabel(LocaleText.getString("PATIO_HEIGHT_LABEL")), opPatioHeight);
        addSeparator(optimalForm);

        addSectionTitle(optimalForm, LocaleText.getString("COVERING_SECTION_TITLE"));
        addComponent(optimalForm, new JLabel(LocaleText.getString("COVERING_THICKNESS_LABEL")), opCoveringThickness);
        addComponent(optimalForm, new JLabel(LocaleText.getString("COVERING_SPACING_LABEL")), opCoveringSpacing);
        addSeparator(optimalForm);

        JButton generateOptimalPatioButton = new JButton(LocaleText.getString("GENERATE_OPTIMAL_PATIO_BUTTON"));
        generateOptimalPatioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!redoActivated && !undoActivated)
                    patioController.addPatioState();
                patioController.generateOptimalPatio();
                patioController.updateTabsContent();
            }
        });
        addButton(optimalForm, generateOptimalPatioButton);
        addSeparator(optimalForm);

        addTextArea(optimalForm, opErrorTextArea);

        addEndFilling(optimalForm);

        return optimalForm;
    }

    /**
     * Create the form for the Standard Mode patio conception.
     *
     * @return JPanel with elements to create a form.
     */
    private JPanel buildNotOptimalForm() {
        JPanel notOptimalForm = new JPanel();
        notOptimalForm.setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

        addListenerForNotOptimalForm();

        addSectionTitle(notOptimalForm, LocaleText.getString("DIMENSIONS_SECTION_TITLE"));

        addComponent(notOptimalForm, new JLabel(LocaleText.getString("PATIO_WIDTH_LABEL")), notOpPatioWidth);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("PATIO_DEPTH_LABEL")), notOpPatioLength);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("PATIO_HEIGHT_LABEL")), notOpPatioHeight);
        addComponent(notOptimalForm,
                new JLabel(LocaleText.getString("CANTILEVER_LENGTH_LABEL")), notOpCantileverLength);
        addSeparator(notOptimalForm);

        addSectionTitle(notOptimalForm, LocaleText.getString("COVERING_SECTION_TITLE"));
        addComponent(notOptimalForm,
                new JLabel(LocaleText.getString("COVERING_THICKNESS_LABEL")), notOpCoveringThickness);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("COVERING_SPACING_LABEL")), notOpCoveringSpacing);
        addSeparator(notOptimalForm);

        addSectionTitle(notOptimalForm, LocaleText.getString("JOIST_SECTION_TITLE"));
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("JOISTS_DIMENSIONS")), joistDimension);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("JOIST_SPACING_LABEL")), joistSpacing);
        addComponent(
                notOptimalForm, new JLabel(LocaleText.getString("JOIST_SPAN_MAX_LENGTH_LABEL")), joistSpanMaxLength);
        addSeparator(notOptimalForm);

        addSectionTitle(notOptimalForm, LocaleText.getString("BEAMS_SECTION_TITLE"));
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("BEAMS_DIMENSIONS")), beamDimension);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("BEAM_LENGTH_LABEL")), beamLength);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("PLIES_QUANTITY_LABEL")), pliesPerBeam);
        addSeparator(notOptimalForm);

        addSectionTitle(notOptimalForm, LocaleText.getString("POSTS_SECTION_TITLE"));
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("POST_HEIGHT")), postHeight);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("POSTS_DIMENSIONS")), postDimension);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("POSTS_SPACING_LABEL")), postSpacing);
        addComponent(notOptimalForm, new JLabel(LocaleText.getString("POSTS_PER_BEAM")), postsPerBeam);
        addSeparator(notOptimalForm);

        addTextArea(notOptimalForm, notOpErrorTextArea);

        addEndFilling(notOptimalForm);

        return notOptimalForm;
    }

    /**
     * Fill in the components with the updated values. Necessary to refresh the values after an event.
     */
    protected void componentSetText() {
        coveringThicknessPossibility = patioController.getDimensionPosibility(ComponentType.COVERING_PLANK);
        joistDimensionPossibility = patioController.getDimensionPosibility(ComponentType.JOIST);
        beamDimensionPossibility = patioController.getDimensionPosibility(ComponentType.BEAM);
        postDimensionPossibility = patioController.getDimensionPosibility(ComponentType.POST);

        if (!opCoveringThickness.getItemAt(0).equals(coveringThicknessPossibility[0])) {
            refreshDropDownList();
        }

        String patioWidthValueInit = patioController.getValueWithMeasureUnit(patioController.getPatioWidth());
        if (!opPatioWidth.getText().equals(patioWidthValueInit)) {
            opPatioWidth.setText(patioWidthValueInit);
        }
        if (!notOpPatioWidth.getText().equals(patioWidthValueInit)) {
            notOpPatioWidth.setText(patioWidthValueInit);
        }

        String patioLengthValueInit = patioController.getValueWithMeasureUnit(patioController.getPatioLength());
        if (!opPatioLength.getText().equals(patioLengthValueInit)) {
            opPatioLength.setText(patioLengthValueInit);
        }
        if (!notOpPatioLength.getText().equals(patioLengthValueInit)) {
            notOpPatioLength.setText(patioLengthValueInit);
        }

        String actualCantileverLength = patioController.getValueWithMeasureUnit(patioController.getCantileverLength());
        if (!notOpCantileverLength.getText().equals(actualCantileverLength)) {
            notOpCantileverLength.setText(actualCantileverLength);
        }

        String patioHeightValueInit = patioController.getValueWithMeasureUnit(patioController.getPatioHeight());
        if (!opPatioHeight.getText().equals(patioHeightValueInit)) {
            opPatioHeight.setText(patioHeightValueInit);
        }
        if (!notOpPatioHeight.getText().equals(patioHeightValueInit)) {
            notOpPatioHeight.setText(patioHeightValueInit);
        }

        String coveringSpacingValueInit = patioController.getValueWithMeasureUnit(patioController.getCoveringSpacing());
        if (!notOpCoveringSpacing.getText().equals(coveringSpacingValueInit)) {
            notOpCoveringSpacing.setText(coveringSpacingValueInit);
        }
        if (!opCoveringSpacing.getText().equals(coveringSpacingValueInit)) {
            opCoveringSpacing.setText(coveringSpacingValueInit);
        }

        int coveringThicknessIndex = getDimensionIndex(patioController.getComponentDimension(
                ComponentType.COVERING_PLANK), coveringThicknessPossibility);

        if (opCoveringThickness.getSelectedIndex() != coveringThicknessIndex) {
            opCoveringThickness.setSelectedIndex(coveringThicknessIndex);
        }
        if (notOpCoveringThickness.getSelectedIndex() != coveringThicknessIndex) {
            notOpCoveringThickness.setSelectedIndex(coveringThicknessIndex);
        }

        int joistDimensionIndex = getDimensionIndex(patioController.getComponentDimension(
                ComponentType.JOIST), joistDimensionPossibility);

        if (joistDimension.getSelectedIndex() != joistDimensionIndex) {
            joistDimension.setSelectedIndex(joistDimensionIndex);
        }

        int beamDimensionIndex = getDimensionIndex(patioController.getComponentDimension(
                ComponentType.BEAM), beamDimensionPossibility);

        if (beamDimension.getSelectedIndex() != beamDimensionIndex) {
            beamDimension.setSelectedIndex(beamDimensionIndex);
        }

        int postDimensionIndex = getDimensionIndex(patioController.getComponentDimension(
                ComponentType.POST), postDimensionPossibility);

        if (postDimension.getSelectedIndex() != postDimensionIndex) {
            postDimension.setSelectedIndex(postDimensionIndex);
        }

        String actualJoistMaxLength = patioController.getValueWithMeasureUnit(patioController.getJoistSpanMaxLength());
        if (!joistSpanMaxLength.getText().equals(actualJoistMaxLength)) {
            joistSpanMaxLength.setText(actualJoistMaxLength);
        }

        String actualJoistSpacing = patioController.getValueWithMeasureUnit((float) patioController.getJoistSpacing());
        if (!joistSpacing.getText().equals(actualJoistSpacing)) {
            joistSpacing.setText(actualJoistSpacing);
        }

        String actualPostSpacing = patioController.getValueWithMeasureUnit(patioController.getPostSpacing());
        if (!postSpacing.getText().equals(actualPostSpacing)) {
            postSpacing.setText(actualPostSpacing);
        }

        String actualBeamLength = patioController.getValueWithMeasureUnit(patioController.getBeamLength());
        if (!beamLength.getText().equals(actualBeamLength)) {
            beamLength.setText(actualBeamLength);
        }

        String actualPostHeight = patioController.getValueWithMeasureUnit(patioController.getPostHeight());
        if (!postHeight.getText().equals(actualPostHeight)) {
            postHeight.setText(actualPostHeight);
        }

        int actualPliesPerBeam = patioController.getPliesPerBeam();
        if ((int) pliesPerBeam.getValue() != actualPliesPerBeam) {
            pliesPerBeam.setValue(actualPliesPerBeam);
        }

        int actualPostPerBeam = patioController.getPostPerBeam();
        if ((int) postsPerBeam.getValue() != actualPostPerBeam) {
            postsPerBeam.setValue(actualPostPerBeam);
        }

        resetComponentsBorder();
        undoActivated = false;
        redoActivated = false;
    }

    /**
     * Add listener on optimal form components
     */
    private void addListenerForOptimalForm() {
        opPatioWidth.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(opPatioWidth, patioWidthValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinPatioWidth());
                if (newValue != patioWidthValue) {
                    patioWidthValue = newValue;
                    notOpPatioWidth.setText(opPatioWidth.getText());
                    patioController.setPatioWidth(newValue);
                }
            }
        });
        opPatioLength.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(opPatioLength, patioLengthValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator());
                if (newValue != patioLengthValue) {
                    patioLengthValue = newValue;
                    notOpPatioLength.setText(opPatioLength.getText());
                    patioController.setPatioLength(newValue);
                }
            }
        });
        opPatioHeight.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(opPatioHeight, patioHeightValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinPatioHeight());
                if (newValue != patioHeightValue) {
                    patioHeightValue = newValue;
                    notOpPatioHeight.setText(opPatioHeight.getText());
                    patioController.setPatioHeight(newValue);
                }
            }
        });
        opCoveringSpacing.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(opCoveringSpacing, coveringSpacingValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator());
                if (newValue != coveringSpacingValue) {
                    coveringSpacingValue = newValue;
                    notOpCoveringSpacing.setText(opCoveringSpacing.getText());
                    patioController.setCoveringSpacing(newValue);
                }
            }
        });

        opCoveringThickness.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (opCoveringThickness.isPopupVisible()) {
                    patioController.addPatioState();
                    int arrayIndex = opCoveringThickness.getSelectedIndex();

                    String userChoice = coveringThicknessPossibility[arrayIndex];
                    String[] choiceSplit = userChoice.split("x");

                    double height = Fraction.fractionValidator(choiceSplit[0].replace("\"", ""));
                    double width = Fraction.fractionValidator(choiceSplit[1].replace("\"", ""));

                    patioController.setComponentDimension(width, height, ComponentType.COVERING_PLANK);

                    notOpCoveringThickness.setSelectedIndex(arrayIndex);

                    patioController.updateTabsContent();
                }
            }
        });

        opPatioWidth.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                opPatioWidth.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    opPatioWidth.setText(notOpPatioWidth.getText());
                    opPatioWidth.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        opPatioLength.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                opPatioLength.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    opPatioLength.setText(notOpPatioLength.getText());
                    opPatioLength.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        opPatioHeight.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                opPatioHeight.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    opPatioHeight.setText(notOpPatioHeight.getText());
                    opPatioHeight.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        opCoveringSpacing.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                opCoveringSpacing.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    opCoveringSpacing.setText(notOpCoveringSpacing.getText());
                    opCoveringSpacing.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });

        opPatioLength.setText(patioController.getValueWithMeasureUnit(patioController.getPatioLength()));
        opCoveringSpacing.setText(patioController.getValueWithMeasureUnit(patioController.getCoveringSpacing()));
    }

    /**
     * Add listener on not optimal form components
     */
    private void addListenerForNotOptimalForm() {
        beamLength.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(beamLength, beamLengthValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator());
                if (newValue != beamLengthValue) {
                    beamLengthValue = newValue;
                    patioController.setBeamLength(newValue);
                }
            }
        });
        joistSpacing.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(joistSpacing, joistSpacingValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinJoistSpacing());
                if (newValue != joistSpacingValue) {
                    joistSpacingValue = newValue;
                    patioController.setJoistSpacing(newValue);
                }
            }
        });
        joistSpanMaxLength.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(joistSpanMaxLength, joistSpanMaxLengthValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinJoistSpanLength());
                if (newValue != joistSpanMaxLengthValue) {
                    joistSpanMaxLengthValue = newValue;
                    patioController.setJoistSpanMaxLength(newValue);
                }
            }
        });
        notOpPatioWidth.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(notOpPatioWidth, patioWidthValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinPatioWidth());
                if (newValue != patioWidthValue) {
                    patioWidthValue = newValue;
                    opPatioWidth.setText(notOpPatioWidth.getText());
                    patioController.setPatioWidth(newValue);
                }
            }
        });
        notOpPatioLength.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(notOpPatioLength, patioLengthValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator());
                if (newValue != patioLengthValue) {
                    patioLengthValue = newValue;
                    opPatioLength.setText(notOpPatioLength.getText());
                    patioController.setPatioLength(newValue);
                }
            }
        });
        notOpCantileverLength.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(notOpCantileverLength, cantileverLengthValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinCantileverLength());
                if (newValue != cantileverLengthValue) {
                    cantileverLengthValue = newValue;
                    patioController.setCantileverLength(newValue);
                }
            }
        });
        notOpCoveringSpacing.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(notOpCoveringSpacing, coveringSpacingValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinCoveringSpacing());
                if (newValue != coveringSpacingValue) {
                    coveringSpacingValue = newValue;
                    opCoveringSpacing.setText(notOpCoveringSpacing.getText());
                    patioController.setCoveringSpacing(newValue);
                }
            }
        });
        notOpPatioHeight.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(notOpPatioHeight, patioHeightValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinPatioHeight());
                if (newValue != patioHeightValue) {
                    patioHeightValue = newValue;
                    opPatioHeight.setText(notOpPatioHeight.getText());
                    patioController.setPatioHeight(newValue);
                }
            }
        });
        postHeight.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(postHeight, postHeightValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinPostHeight());
                if (newValue != postHeightValue) {
                    postHeightValue = newValue;
                    patioController.setPostHeight(newValue);
                }
            }
        });
        postSpacing.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verification();
            }

            public void removeUpdate(DocumentEvent e) {
                verification();
            }

            public void changedUpdate(DocumentEvent e) {
                verification();
            }

            public void verification() {
                final double newValue = StringValidator.textFieldValidator(postSpacing, postSpacingValue,
                        patioController.getMeasureUnit(), patioController.isInitPationator(),
                        patioController.getMinPostSpacing());
                if (newValue != postSpacingValue) {
                    postSpacingValue = newValue;
                    patioController.setPostSpacing(newValue);
                }
            }
        });

        beamLength.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                beamLength.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    beamLength.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        joistSpacing.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                joistSpacing.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    joistSpacing.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        joistSpanMaxLength.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                joistSpanMaxLength.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    joistSpanMaxLength.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        notOpPatioWidth.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                notOpPatioWidth.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    notOpPatioWidth.setText(opPatioWidth.getText());
                    notOpPatioWidth.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        notOpPatioLength.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                notOpPatioLength.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    notOpPatioLength.setText(opPatioLength.getText());
                    notOpPatioLength.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        notOpCantileverLength.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                notOpCantileverLength.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    notOpPatioLength.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        notOpCoveringSpacing.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                notOpCoveringSpacing.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    notOpCoveringSpacing.setText(opCoveringSpacing.getText());
                    notOpCoveringSpacing.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        notOpPatioHeight.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                notOpPatioHeight.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    notOpPatioHeight.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        postHeight.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                postHeight.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    postHeight.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });
        postSpacing.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                postSpacing.selectAll();
            }

            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()))
                        patioController.addPatioState(previousPatioState);

                    postSpacing.setBorder(defaultBorder);

                    patioController.updateTabsContent();
                }
            }
        });

        beamDimension.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (beamDimension.isPopupVisible()) {
                    patioController.addPatioState();
                    int arrayIndex = beamDimension.getSelectedIndex();

                    String userChoice = beamDimensionPossibility[arrayIndex];
                    String[] choiceSplit = userChoice.split("x");

                    double height = Fraction.fractionValidator(choiceSplit[0].replace("\"", ""));
                    double width = Fraction.fractionValidator(choiceSplit[1].replace("\"", ""));

                    patioController.setComponentDimension(width, height, ComponentType.BEAM);

                    patioController.updateTabsContent();
                }
            }
        });
        joistDimension.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (joistDimension.isPopupVisible()) {
                    patioController.addPatioState();
                    int arrayIndex = joistDimension.getSelectedIndex();

                    String userChoice = joistDimensionPossibility[arrayIndex];
                    String[] choiceSplit = userChoice.split("x");

                    double height = Fraction.fractionValidator(choiceSplit[0].replace("\"", ""));
                    double width = Fraction.fractionValidator(choiceSplit[1].replace("\"", ""));

                    patioController.setComponentDimension(width, height, ComponentType.JOIST);

                    patioController.updateTabsContent();
                }
            }
        });
        notOpCoveringThickness.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (notOpCoveringThickness.isPopupVisible()) {
                    patioController.addPatioState();
                    int arrayIndex = notOpCoveringThickness.getSelectedIndex();

                    String userChoice = coveringThicknessPossibility[arrayIndex];
                    String[] choiceSplit = userChoice.split("x");

                    double height = Fraction.fractionValidator(choiceSplit[0].replace("\"", ""));
                    double width = Fraction.fractionValidator(choiceSplit[1].replace("\"", ""));

                    patioController.setComponentDimension(width, height, ComponentType.COVERING_PLANK);

                    opCoveringThickness.setSelectedIndex(arrayIndex);

                    patioController.updateTabsContent();
                }
            }
        });
        postDimension.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (postDimension.isPopupVisible()) {
                    patioController.addPatioState();
                    int arrayIndex = postDimension.getSelectedIndex();

                    String userChoice = postDimensionPossibility[arrayIndex];
                    String[] choiceSplit = userChoice.split("x");

                    double height = Fraction.fractionValidator(choiceSplit[0].replace("\"", ""));
                    double width = Fraction.fractionValidator(choiceSplit[1].replace("\"", ""));

                    patioController.setComponentDimension(width, height, ComponentType.POST);

                    patioController.updateTabsContent();
                }
            }
        });
    }

    /**
     * Change drop down list items
     */
    private void refreshDropDownList() {
        opCoveringThickness.removeAllItems();
        notOpCoveringThickness.removeAllItems();
        for (String possibility : coveringThicknessPossibility) {
            opCoveringThickness.addItem(possibility);
            notOpCoveringThickness.addItem(possibility);
        }

        joistDimension.removeAllItems();
        for (String possibility : joistDimensionPossibility) {
            joistDimension.addItem(possibility);
        }

        beamDimension.removeAllItems();
        for (String possibility : beamDimensionPossibility) {
            beamDimension.addItem(possibility);
        }

        postDimension.removeAllItems();
        for (String possibility : postDimensionPossibility) {
            postDimension.addItem(possibility);
        }
    }

    /**
     * Get array index from the corresponding numeric value.
     *
     * @param _heightWidth Float array with height and width value (in specific order).
     * @param _possibility String array with all corresponding possibility.
     * @return Array index if something is found. Return -1 otherwise.
     */
    private int getDimensionIndex(float[] _heightWidth, String[] _possibility) {
        float height = _heightWidth[0];
        float width = _heightWidth[1];

        if (height == 0 || width == 0) {
            return -1;
        }

        if (patioController.getMeasureUnit() == MeasureUnit.METRIC) {
            for (int i = 0; i < _possibility.length; i++) {
                String string = _possibility[i];
                string = string.replace(" mm", "");
                String[] metricDimension = string.split(" x ");

                if (height == Fraction.fractionValidator(metricDimension[0])
                        && width == Fraction.fractionValidator(metricDimension[1])) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < _possibility.length; i++) {
                String string = _possibility[i];
                string = string.replace("\"", "");
                String[] imperialDimension = string.split(" x ");

                if (height == Fraction.fractionValidator(imperialDimension[0])
                        && width == Fraction.fractionValidator(imperialDimension[1])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Set a default border on components.
     */
    private void resetComponentsBorder() {
        beamDimension.setBorder(defaultBorder);
        joistDimension.setBorder(defaultBorder);
        joistSpacing.setBorder(defaultBorder);
        joistSpanMaxLength.setBorder(defaultBorder);
        notOpCantileverLength.setBorder(defaultBorder);
        notOpCoveringThickness.setBorder(defaultBorder);
        opCoveringThickness.setBorder(defaultBorder);
        pliesPerBeam.setBorder(defaultBorder);
        postDimension.setBorder(defaultBorder);
        postHeight.setBorder(defaultBorder);
        postSpacing.setBorder(defaultBorder);
    }

    /**
     * Detect if the "Optimal Mode" toggle button is press. The designated form is displayed.
     *
     * @param _itemEvent Item event fired by the toggle button.
     */
    public void itemStateChanged(ItemEvent _itemEvent) {
        AbstractButton tgbOptimalCost = (AbstractButton) (_itemEvent.getSource());

        isOptimalMode = tgbOptimalCost.isSelected();
        patioController.setIsOptimalMode();
        patioController.updateTabsContent();

        CardLayout layout = (CardLayout) (cardOptimalForm.getLayout());
        layout.show(cardOptimalForm, String.valueOf(tgbOptimalCost.isSelected()));
    }

    /**
     * Behavior when the tab refresh.
     */
    @Override
    void refreshContent() {
        componentSetText();
    }

    /**
     * Behavior when an error is found.
     *
     * @param _errors Errors set.
     */
    @Override
    public void onErrorsFound(HashSet<ValidationErrorType> _errors) {
        StringBuilder notOpMessages = new StringBuilder();
        StringBuilder opMessages = new StringBuilder();

        for (ValidationErrorType error : _errors) {
            String errorText = getValidationErrorMessage(error);

            if (error != ValidationErrorType.OPTIMAL_PATIO_CONFIGURATION)
                notOpMessages.append(errorText).append("\n\n");
            opMessages.append(errorText).append("\n\n");
        }

        opErrorTextArea.setForeground(Color.red);
        opErrorTextArea.setText(opMessages.toString());

        notOpErrorTextArea.setForeground(Color.red);
        notOpErrorTextArea.setText(notOpMessages.toString());

        setErrorBorderOnComponents(_errors);
    }

    /**
     * Behavior when no errors are found.
     */
    @Override
    public void onNoErrorsFound() {
        notOpErrorTextArea.setForeground(new Color(35, 165, 55));  // Dark green
        notOpErrorTextArea.setText(LocaleText.getString("NO_ERROR"));

        opErrorTextArea.setForeground(new Color(35, 165, 55));  // Dark green
        opErrorTextArea.setText(LocaleText.getString("NO_ERROR"));
    }

    /**
     * Get an error message corresponding a validation type error.
     *
     * @param _error A validation type error.
     * @return Message corresponding an error.
     */
    private String getValidationErrorMessage(ValidationErrorType _error) {
        String messageKey = _error.name() + VALIDATION_ERROR_SUFFIX;

        return LocaleText.getString(messageKey);
    }

    /**
     * Put an error border on components related to detected validation type error.
     *
     * @param _errors Container of errors detected.
     */
    private void setErrorBorderOnComponents(HashSet<ValidationErrorType> _errors) {
        Border errorBorder = BorderFactory.createLineBorder(Color.RED);
        Border paddingBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        Border compoundErrorBorder = BorderFactory.createCompoundBorder(errorBorder, paddingBorder);

        if (_errors.contains(ValidationErrorType.POST_HEIGHT))
            postHeight.setBorder(compoundErrorBorder);
        if (_errors.contains(ValidationErrorType.POST_DIMENSIONS))
            postDimension.setBorder(compoundErrorBorder);
        if (_errors.contains(ValidationErrorType.SPAN_LENGTH))
            joistSpanMaxLength.setBorder(compoundErrorBorder);
        if (_errors.contains(ValidationErrorType.JOIST_SPACING))
            joistSpacing.setBorder(compoundErrorBorder);

        if (_errors.contains(ValidationErrorType.COVERING_DIMENSIONS)) {
            opCoveringThickness.setBorder(compoundErrorBorder);
            notOpCoveringThickness.setBorder(compoundErrorBorder);
        }
        if (_errors.contains(ValidationErrorType.JOIST_DIMENSIONS))
            joistDimension.setBorder(compoundErrorBorder);
        if (_errors.contains(ValidationErrorType.CANTILEVER_LENGTH))
            notOpCantileverLength.setBorder(compoundErrorBorder);
        if (_errors.contains(ValidationErrorType.POST_SPACING))
            postSpacing.setBorder(compoundErrorBorder);
        if (_errors.contains(ValidationErrorType.PLIES_PER_BEAM_SINGLE_SPAN) ||
                _errors.contains(ValidationErrorType.PLIES_PER_BEAM_TWO_SPANS))
            pliesPerBeam.setBorder(compoundErrorBorder);
        if (_errors.contains(ValidationErrorType.BEAM_DIMENSIONS_SINGLE_SPAN) ||
                _errors.contains(ValidationErrorType.BEAM_DIMENSIONS_TWO_SPANS))
            beamDimension.setBorder(compoundErrorBorder);
    }

    @Override
    public void onRedoActivated() {
        redoActivated = true;
    }

    @Override
    public void onUndoActivated() {
        undoActivated = true;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return null;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}