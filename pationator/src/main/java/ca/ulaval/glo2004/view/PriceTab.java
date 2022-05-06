package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.PatioInfo;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.LumberDimension;
import ca.ulaval.glo2004.utils.StringValidator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * TabsContent showing the quantity and price of all patio components.
 */
public class PriceTab extends TabsContent {
    private final JTextArea currentPricesTextArea;
    private final boolean undoActivated = false;
    private final boolean redoActivated = false;
    private JTextField[] priceTextField;
    private Float[] priceArray;
    private Border defaultBorder;
    private PatioInfo previousPatioState;

    /**
     * Create a material tab with the declared parent.
     *
     * @param _parent Parent of current tab.
     */
    public PriceTab(Tabs _parent) {
        patioController = _parent.patioController;
        previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());

        currentPricesTextArea = new JTextArea();
        currentPricesTextArea.setEditable(false);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());

        addTextFieldSection(form);
        addSeparator(form);
        addSectionTitle(form, LocaleText.getString("CURRENT_PRICE"));
        addTextArea(form, currentPricesTextArea);
        addEndFilling(form);
        add(form);

        updateCurrentPrices();
    }

    /**
     * Add a new row to the form containing a label and a text field input for each lumberDimension possibility.
     *
     * @param _form JPanel representing the form.
     */
    private void addTextFieldSection(JPanel _form) {
        String[] lumberDimensions = LumberDimension.getPossibility();

        int arraySize = lumberDimensions.length;

        JTextField defaultTextField = new JTextField();
        defaultBorder = defaultTextField.getBorder();

        priceTextField = new JTextField[arraySize];
        priceArray = new Float[arraySize];

        addSectionTitle(_form, LocaleText.getString("LUMBER_DIMENSION_PRICE"));
        for (int i = 0; i < priceTextField.length; i++) {
            final int index = i;
            final String lumberDimension = lumberDimensions[index];

            float priceValue = patioController.getLumberDimensionPrice(lumberDimension);

            priceArray[i] = priceValue;
            priceTextField[i] = new JTextField(String.format("%.2f", priceValue) + "$");

            priceTextField[i].getDocument().addDocumentListener(new DocumentListener() {
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
                    Number value = StringValidator.priceTextFieldValidator(
                            priceTextField[index].getText(), priceArray[index]);

                    if (value == null) {
                        priceTextField[index].setBorder(new LineBorder(Color.RED, 1));
                    } else {
                        final float newValue = (float) value;

                        if (newValue != priceArray[index]) {
                            priceArray[index] = newValue;
                            patioController.setLumberDimensionPrice(lumberDimension, newValue);
                        }

                        priceTextField[index].setBorder(defaultBorder);
                    }
                }
            });

            final JTextField tempTextiField = priceTextField[i];
            priceTextField[i].addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
                    tempTextiField.selectAll();
                }

                public void focusLost(FocusEvent e) {
                    if (!e.isTemporary()) {
                        if (!previousPatioState.equals(patioController.getPatio().getPatioInfo()) &&
                                !redoActivated && !undoActivated)
                            patioController.addPatioState(previousPatioState);

                        priceTextField[index].setBorder(defaultBorder);
                        priceTextField[index].setText(String.format("%.2f", priceArray[index]) + "$");

                        patioController.getPatio().calculateTotalPrice();
                        patioController.updateTabsContent();
                    }
                }
            });

            addComponent(_form, new JLabel(lumberDimensions[i]), priceTextField[i]);
        }
    }

    /**
     * Updates the current prices in the price tab
     */
    private void updateCurrentPrices() {
        currentPricesTextArea.setText("");
        boolean addSpace = false;

        HashMap<String, HashMap<Float, Integer>> piecesQuantities = patioController.getPatio().getPiecesQuantities();
        for (Map.Entry<String, HashMap<Float, Integer>> lumberDimensionQuantity : piecesQuantities.entrySet()) {
            int totalLumberQuantity = 0;
            HashMap<Float, Integer> quantitiesPerDepth = lumberDimensionQuantity.getValue();

            for (Map.Entry<Float, Integer> quantity : quantitiesPerDepth.entrySet()) {
                int depthQuantity = quantity.getValue();
                totalLumberQuantity += depthQuantity;
            }

            addStringToTextArea(getLumberPriceText(lumberDimensionQuantity.getKey(), totalLumberQuantity), addSpace);
            addSpace = true;
        }

        addCurrentTotalPriceText();
        patioController.setPriceText(currentPricesTextArea.getText());
    }

    /**
     * Adds a string entry to the price tab
     * @param _text the text to write for the description
     * @param _lineReturn add a line return between the current price text area and the _text
     */
    private void addStringToTextArea(String _text, boolean _lineReturn) {
        if (_lineReturn)
            currentPricesTextArea.setText(currentPricesTextArea.getText() + "\n" + _text);
        else
            currentPricesTextArea.setText(currentPricesTextArea.getText() + _text);
    }

    /**
     * Get formated text for the lumber prices
     * @param _lumberDimension the lumber size
     * @param _quantity the amount of lumbers.
     * @return formated string
     */
    private String getLumberPriceText(String _lumberDimension, int _quantity) {
        String woodPieceText = _quantity > 1 ? LocaleText.getString("WOOD_PIECES") : LocaleText.getString("WOOD_PIECE");
        float price = patioController.getPatio().getLumberTotalPrice(_lumberDimension);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        return "• " + _quantity + " " + woodPieceText + " " + _lumberDimension + ' '
                + LocaleText.getString("SUBTOTAL") + " " + formatter.format(price);
    }

    /**
     * Add the current total price text to the price tab
     */
    private void addCurrentTotalPriceText() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        float totalPrice = patioController.getPatio().getTotalPrice();

        currentPricesTextArea.append("\n\n" + LocaleText.getString("TOTAL_PRICE_LABEL") + " " +
                formatter.format(totalPrice));
    }

    /**
     * Behavior when the tab refresh
     */
    @Override
    void refreshContent() {
        updatePrices();
        updateCurrentPrices();
    }

    /**
     * Update price input according to actual project value.
     */
    protected void updatePrices() {
        String[] lumberDimensions = LumberDimension.getPossibility();

        for (int i = 0; i < priceTextField.length; i++) {
            final String lumberDimension = lumberDimensions[i];

            float priceValue = patioController.getLumberDimensionPrice(lumberDimension);

            if (!priceTextField[i].getText().equals(String.format("%.2f", priceValue) + "$")) {
                priceArray[i] = priceValue;
                priceTextField[i].setText(String.format("%.2f", priceValue) + "$");
            }
        }

        previousPatioState = new PatioInfo(patioController.getPatio().getPatioInfo());
    }
}
