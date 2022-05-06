package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.ComponentType;
import ca.ulaval.glo2004.patio.MeasureType;
import ca.ulaval.glo2004.patio.MeasureUnit;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.RedoActivatedListener;
import ca.ulaval.glo2004.utils.UndoActivatedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * TabsContent showing all the parameters concerning the patio components.
 */
public class SettingsTab extends TabsContent implements UndoActivatedListener, RedoActivatedListener {
    private final HashMap<ComponentType, ColorPicker> colorPickers;
    private final HashMap<ComponentType, JToggleButton> componentVisibilities;
    private final HashMap<ComponentType, JToggleButton> componentFillings;
    private JRadioButton nominalMeasureType;
    private JRadioButton realMeasureType;
    private JRadioButton metricUnit;
    private JRadioButton imperialUnit;
    private JRadioButton decimalFormat;
    private JRadioButton fractionFormat;
    private JToggleButton borderVisibilityButton;
    private boolean redoActivated = false;
    private boolean undoActivated = false;

    /**
     * Create a setting tab with the declared parent.
     *
     * @param _parent Parent of current tab.
     */
    public SettingsTab(Tabs _parent) {
        patioController = _parent.patioController;
        patioController.addRedoActivatedListener(this);
        patioController.addUndoActivatedListener(this);
        colorPickers = new HashMap<>();
        componentFillings = new HashMap<>();
        componentVisibilities = new HashMap<>();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        gridBagConstraints = new GridBagConstraints();

        add(createSettingForm());
    }

    /**
     * Build settings form.
     *
     * @return JPanel of the complete form.
     */
    private JPanel createSettingForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

        addSectionTitle(form, LocaleText.getString("GENERAL_SETTINGS_SECTION_TITLE"));
        createMesureUnitSection(form);
        createMesureTypeSection(form);
        createRationalFormatSection(form);
        addSeparator(form);

        addSectionTitle(form, LocaleText.getString("VIEW_SETTINGS_SECTION_TITLE"));
        createComponentDetailSection(form);
        addSeparator(form);
        createComponentSection(form, ComponentType.JOIST);
        addSeparator(form);
        createComponentSection(form, ComponentType.COVERING_PLANK);
        addSeparator(form);
        createComponentSection(form, ComponentType.BEAM);
        addSeparator(form);
        createComponentSection(form, ComponentType.POST);
        addSeparator(form);

        addEndFilling(form);

        return form;
    }

    /**
     * Create mesure unit section inside the form.
     *
     * @param _form JPanel where we want the section.
     */
    private void createMesureUnitSection(JPanel _form) {
        MeasureUnit currentMeasureUnit = patioController.getMeasureUnit();

        metricUnit = new JRadioButton();
        metricUnit.setText(LocaleText.getString("METRIC_UNIT_LABEL"));
        metricUnit.setSelected(currentMeasureUnit == MeasureUnit.METRIC);
        metricUnit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patioController.addPatioState();
                patioController.setMeasureUnit(MeasureUnit.METRIC);
                patioController.updateTabsContent();
            }
        });

        imperialUnit = new JRadioButton();
        imperialUnit.setText(LocaleText.getString("IMPERIAL_UNIT_LABEL"));
        imperialUnit.setSelected(currentMeasureUnit == MeasureUnit.IMPERIAL);
        imperialUnit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patioController.addPatioState();
                patioController.setMeasureUnit(MeasureUnit.IMPERIAL);
                patioController.updateTabsContent();
            }
        });

        addRadioButtonPair(_form, new JLabel(LocaleText.getString("MEASURE_UNIT_LABEL")), metricUnit, imperialUnit);
    }

    /**
     * Create mesure type section inside the form.
     *
     * @param _form JPanel where we want the section.
     */
    private void createMesureTypeSection(JPanel _form) {
        MeasureType currentMeasureType = patioController.getMeasureType();

        nominalMeasureType = new JRadioButton();
        nominalMeasureType.setText(LocaleText.getString("NOMINAL_UNIT_LABEL"));
        nominalMeasureType.setSelected(currentMeasureType == MeasureType.NOMINAL);
        nominalMeasureType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patioController.addPatioState();
                patioController.setMesureType(MeasureType.NOMINAL);
                patioController.updateTabsContent();
            }
        });

        realMeasureType = new JRadioButton();
        realMeasureType.setText(LocaleText.getString("ACTUAL_UNIT_LABEL"));
        realMeasureType.setSelected(currentMeasureType == MeasureType.REAL);
        realMeasureType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patioController.addPatioState();
                patioController.setMesureType(MeasureType.REAL);
                patioController.updateTabsContent();
            }
        });

        addRadioButtonPair(
                _form, new JLabel(LocaleText.getString("MEASURE_TYPE_LABEL")), nominalMeasureType, realMeasureType);
    }

    /**
     * Create rational format section inside the form.
     *
     * @param _form JPanel where we want the section.
     */
    private void createRationalFormatSection(JPanel _form) {
        String currentFormat = patioController.getRationalFormat();
        final String fractionString = "fraction", decimalString = "decimal";

        decimalFormat = new JRadioButton();
        decimalFormat.setText(LocaleText.getString("RATIONAL_DECIMAL"));
        decimalFormat.setSelected(currentFormat.equals(decimalString));
        decimalFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patioController.addPatioState();
                patioController.setRationalFormat(decimalString);
                patioController.updateTabsContent();
            }
        });

        fractionFormat = new JRadioButton();
        fractionFormat.setText(LocaleText.getString("RATIONAL_FRACTION"));
        fractionFormat.setSelected(currentFormat.equals(fractionString));
        fractionFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patioController.addPatioState();
                patioController.setRationalFormat(fractionString);
                patioController.updateTabsContent();
            }
        });

        addRadioButtonPair(_form, new JLabel(LocaleText.getString("RATIONAL_LABEL")), decimalFormat, fractionFormat);
    }

    /**
     * Create general component detail section.
     *
     * @param _form JPanel where we want the section.
     */
    private void createComponentDetailSection(JPanel _form) {
        borderVisibilityButton = new JToggleButton(LocaleText.getString("VISIBLE"));
        borderVisibilityButton.setSelected(patioController.getHiddenBorderVisibility());

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                patioController.addPatioState();
                AbstractButton tgbIsFill = (AbstractButton) actionEvent.getSource();
                boolean selected = tgbIsFill.getModel().isSelected();

                patioController.setHiddenBorderVisibility(selected);
                patioController.updateTabsContent();
            }
        };
        borderVisibilityButton.addActionListener(actionListener);
        addComponent(_form, new JLabel(LocaleText.getString("DASH_BORDER_LABEL")), borderVisibilityButton);

        JToggleButton arrowVisibilityButton = new JToggleButton(LocaleText.getString("VISIBLE"));
        arrowVisibilityButton.setSelected(patioController.getArrowVisibility());

        ActionListener actionListenerArrow = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton tgbIsFill = (AbstractButton) actionEvent.getSource();
                boolean selected = tgbIsFill.getModel().isSelected();

                patioController.setArrowVisibility(selected);
                patioController.updateTabsContent();
            }
        };
        arrowVisibilityButton.addActionListener(actionListenerArrow);
        addComponent(_form, new JLabel(LocaleText.getString("ARROW_LABEL")), arrowVisibilityButton);
    }

    /**
     * Create a component settings section inside the form.
     *
     * @param _form          JPanel where we want the section.
     * @param _componentType Designed component
     */
    private void createComponentSection(JPanel _form, final ComponentType _componentType) {
        String visibilityText, colorText, fillText;

        switch (_componentType) {
            case COVERING_PLANK:
                visibilityText = LocaleText.getString("COVERING_VISIBILITY_LABEL");
                colorText = LocaleText.getString("COVERING_COLOR_LABEL");
                fillText = LocaleText.getString("COVERING_FILLING_LABEL");
                break;
            case JOIST:
                visibilityText = LocaleText.getString("JOIST_VISIBILITY_LABEL");
                colorText = LocaleText.getString("JOIST_COLOR_LABEL");
                fillText = LocaleText.getString("JOIST_FILLING_LABEL");
                break;
            case BEAM:
                visibilityText = LocaleText.getString("BEAM_VISIBILITY_LABEL");
                colorText = LocaleText.getString("BEAM_COLOR_LABEL");
                fillText = LocaleText.getString("BEAM_FILLING_LABEL");
                break;
            case POST:
                visibilityText = LocaleText.getString("POST_VISIBILITY_LABEL");
                colorText = LocaleText.getString("POST_COLOR_LABEL");
                fillText = LocaleText.getString("POST_FILLING_LABEL");
                break;
            default:
                visibilityText = LocaleText.getString("UNDEFINED");
                colorText = LocaleText.getString("UNDEFINED");
                fillText = LocaleText.getString("UNDEFINED");
                break;
        }

        final Color color = patioController.getColor(_componentType);

        ColorPicker colorPicker = new ColorPicker(color);
        colorPicker.addCurrentColorChangeListener(new ColorPicker.CurrentColorChangeListener() {
            @Override
            public void colorChanged(Color _color) {
                if (color != _color) {
                    if (!undoActivated && !redoActivated)
                        patioController.addPatioState();
                    patioController.setColor(_componentType, _color);
                    patioController.updateTabsContent();
                }
            }
        });
        colorPickers.put(_componentType, colorPicker);

        JToggleButton isFill = new JToggleButton(LocaleText.getString("FILL"));
        isFill.setSelected(patioController.getColorFilling(_componentType));

        ActionListener fillingActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton tgbIsFill = (AbstractButton) actionEvent.getSource();
                boolean selected = tgbIsFill.getModel().isSelected();

                patioController.addPatioState();
                patioController.setColorFilling(_componentType, selected);
                patioController.updateTabsContent();
            }
        };
        isFill.addActionListener(fillingActionListener);
        componentFillings.put(_componentType, isFill);

        JToggleButton isVisible = new JToggleButton(LocaleText.getString("VISIBLE"));
        isVisible.setSelected(patioController.getVisibility(_componentType));

        ActionListener visibilityActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton tgbIsVisible = (AbstractButton) actionEvent.getSource();
                boolean selected = tgbIsVisible.getModel().isSelected();

                patioController.addPatioState();
                patioController.setVisibility(_componentType, selected);
                patioController.updateTabsContent();
            }
        };
        isVisible.addActionListener(visibilityActionListener);
        componentVisibilities.put(_componentType, isVisible);

        addComponent(_form, new JLabel(visibilityText), isVisible);
        addComponent(_form, new JLabel(colorText), colorPicker);
        addComponent(_form, new JLabel(fillText), isFill);
    }

    /**
     * Behavior when the tab refresh
     */
    @Override
    void refreshContent() {
        updateComponents();
    }

    /**
     * Update component if the value displayed is different than the patioController value.
     */
    protected void updateComponents() {
        nominalMeasureType.setSelected(patioController.getMeasureType() == MeasureType.NOMINAL);
        realMeasureType.setSelected(patioController.getMeasureType() == MeasureType.REAL);

        metricUnit.setSelected(patioController.getMeasureUnit() == MeasureUnit.METRIC);
        imperialUnit.setSelected(patioController.getMeasureUnit() == MeasureUnit.IMPERIAL);

        decimalFormat.setSelected(patioController.getRationalFormat().equals("decimal"));
        fractionFormat.setSelected(patioController.getRationalFormat().equals("fraction"));

        borderVisibilityButton.setSelected(patioController.getHiddenBorderVisibility());

        for (ComponentType componentType : ComponentType.values()) {
            Color actualColor = patioController.getColor(componentType);
            if (!colorPickers.get(componentType).getColor().equals(actualColor)) {
                colorPickers.get(componentType).setColor(actualColor);
            }

            boolean actualVisibility = patioController.getVisibility(componentType);
            if (componentVisibilities.get(componentType).getModel().isSelected() != actualVisibility) {
                componentVisibilities.get(componentType).getModel().setSelected(actualVisibility);
            }

            boolean actualFilling = patioController.getColorFilling(componentType);
            if (componentFillings.get(componentType).getModel().isSelected() != actualFilling) {
                componentFillings.get(componentType).getModel().setSelected(actualFilling);
            }
        }

        redoActivated = false;
        undoActivated = false;
    }

    /**
     * Behavior when redo is activated.
     */
    @Override
    public void onRedoActivated() {
        redoActivated = true;
    }

    /**
     * Behavior when undo is activated.
     */
    @Override
    public void onUndoActivated() {
        undoActivated = true;
    }
}
