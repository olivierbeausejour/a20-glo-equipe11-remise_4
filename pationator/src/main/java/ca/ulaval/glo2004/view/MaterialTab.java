package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.MeasureUnit;
import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.Conversion;
import ca.ulaval.glo2004.utils.Fraction;
import ca.ulaval.glo2004.utils.LocaleText;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * TabsContent showing material of the patio component.
 */
public class MaterialTab extends TabsContent {
    private final PatioController patioController;
    private final JTextArea materialTextZone;

    /**
     * Create a material tab with the declared parent.
     *
     * @param _parent Parent of current tab.
     */
    public MaterialTab(Tabs _parent) {
        patioController = _parent.patioController;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());

        materialTextZone = new JTextArea();
        materialTextZone.setEditable(false);

        addSectionTitle(form, LocaleText.getString("MATERIAL_LIST_TITLE"));
        addTextArea(form, materialTextZone);
        updateMaterialListSection();
        addEndFilling(form);
        add(form);
    }

    /**
     * Update material list.
     */
    private void updateMaterialListSection() {
        materialTextZone.setText("");
        boolean addSpace = false;

        HashMap<String, HashMap<Float, Integer>> piecesQuantities = patioController.getPatio().getPiecesQuantities();
        for (Map.Entry<String, HashMap<Float, Integer>> lumberDimensionQuantity : piecesQuantities.entrySet()) {
            HashMap<Float, Integer> quantitiesPerDepth = lumberDimensionQuantity.getValue();
            for (Map.Entry<Float, Integer> quantity : quantitiesPerDepth.entrySet()) {
                String woodPieceText = quantity.getValue() > 1 ? LocaleText.getString("WOOD_PIECES") : LocaleText.getString("WOOD_PIECE");

                String labelText = "• " + quantity.getValue() + " " + woodPieceText + " " +
                        lumberDimensionQuantity.getKey() + ' ' +
                        LocaleText.getString("OF") + " " +
                        getDepthString(quantity.getKey());

                addStringToTextArea(labelText, addSpace);
                addSpace = true;
            }
        }

        patioController.setPiecesList(materialTextZone.getText());
    }

    /**
     * Get depth string.
     *
     * @param _woodPieceDepth WoodPiece depth.
     * @return String with depth.
     */
    private String getDepthString(float _woodPieceDepth) {
        MeasureUnit measureUnit = patioController.getMeasureUnit();
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.CANADA);
        numberFormat.setMaximumFractionDigits(3);

        String depthString;
        String unitText = LocaleText.getString("FEET");

        if (measureUnit == MeasureUnit.METRIC) {
            _woodPieceDepth = Conversion.getMillimeterFromActual(Conversion.feetToInches(_woodPieceDepth));
            unitText = LocaleText.getString("MILLIMETRE_SUFFIX");
        } else if (_woodPieceDepth < 1) {
            unitText = LocaleText.getString("INCHES");
            _woodPieceDepth = Conversion.feetToInches(_woodPieceDepth);
        }

        switch (patioController.getRationalFormat()) {
            case "decimal":
                depthString = numberFormat.format(_woodPieceDepth).replace(",", " ");
                break;
            case "fraction":
            default:
                depthString = new Fraction(_woodPieceDepth).toString();
                break;
        }

        depthString += " " + unitText;

        return depthString;
    }

    /**
     * Add new string in text area.
     *
     * @param _text     New text.
     * @param _addSpace True if we add space.
     */
    private void addStringToTextArea(String _text, boolean _addSpace) {
        if (_addSpace)
            materialTextZone.setText(materialTextZone.getText() + "\n\n" + _text);
        else
            materialTextZone.setText(materialTextZone.getText() + _text);
    }

    /**
     * Behavior when we refresh content.
     */
    @Override
    void refreshContent() {
        updateMaterialListSection();
    }
}
