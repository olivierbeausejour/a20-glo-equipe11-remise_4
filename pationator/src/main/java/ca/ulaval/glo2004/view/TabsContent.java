package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.PatioController;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * General class for tabs allowing sharing of form building methods
 */
public abstract class TabsContent extends JPanel {
    protected PatioController patioController;
    protected GridBagConstraints gridBagConstraints;

    private int lineNumber;


    /**
     * Creates an editing tab with the declared parent.
     */
    protected TabsContent() {
        lineNumber = 0;
    }

    abstract void refreshContent();

    /**
     * Add a section title to the form.
     *
     * @param _form JPanel representing the form.
     * @param _text Section title text.
     */
    protected void addSectionTitle(JPanel _form, String _text) {
        JLabel sectionTitle = new JLabel(" " + _text);

        Font labelFont = sectionTitle.getFont();
        sectionTitle.setFont(new Font(labelFont.getName(), Font.BOLD, 12));

        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(8, 0, 0, 0);

        _form.add(sectionTitle, gridBagConstraints);

        ++lineNumber;
    }

    /**
     * Add a not working button to the form.
     *
     * @param _form JPanel representing the form.
     * @param _text Button text.
     */
    protected void addDummyButton(JPanel _form, String _text) {
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(8, 0, 0, 0);

        _form.add(new JButton(_text), gridBagConstraints);

        ++lineNumber;
    }

    /**
     * Add a button to the form.
     *
     * @param _form   JPanel representing the form.
     * @param _button Button to add on the form.
     */
    protected void addButton(JPanel _form, JButton _button) {
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(8, 0, 0, 0);

        _form.add(_button, gridBagConstraints);

        ++lineNumber;
    }

    /**
     * Add a separator between the lines of the form.
     *
     * @param _form JPanel representing the form.
     */
    protected void addSeparator(JPanel _form) {
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new Insets(8, 0, 0, 0);

        _form.add(new JSeparator(JSeparator.HORIZONTAL), gridBagConstraints);

        ++lineNumber;
    }

    /**
     * Add a new row to the form containing a label and a Spinner for the number entry.
     *
     * @param _form      JPanel representing the form.
     * @param _label     JLabel for the JSpinner.
     * @param _component JComponent to add to the form.
     */
    protected void addComponent(JPanel _form, JLabel _label, JComponent _component) {
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;

        gridBagConstraints.gridx = 0;
        _form.add(_label, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        _form.add(_component, gridBagConstraints);

        ++lineNumber;
    }

    /**
     * Add a new row to the form containing a label, a text field, and a Spinner for numeric input.
     *
     * @param _form      JPanel representing the form.
     * @param _label     JLabel for the JSpinner.
     * @param _textField JTextField to insert a quantity.
     * @param _spinner   JSpinner to add to the form.
     */
    protected void addSpinnerWithText(JPanel _form, JLabel _label, JTextField _textField, JSpinner _spinner) {
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.weighty = 0;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 5;
        _form.add(_label, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1;
        _textField.setHorizontalAlignment(SwingConstants.CENTER);
        _form.add(_textField, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.weightx = 5;
        _form.add(_spinner, gridBagConstraints);

        ++lineNumber;
    }

    /**
     * Add a new row to the form containing a label, a text field and a Spinner for numeric input.
     *
     * @param _form      JPanel representing the form.
     * @param _label     JLabel for the JSpinner.
     * @param _textField JTextField to insert a quantity.
     */
    protected void addReadonlyTextfield(JPanel _form, JLabel _label, JTextField _textField) {
        gridBagConstraints.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.weighty = 0;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 3;
        _form.add(_label, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.weightx = 1;
        _textField.setHorizontalAlignment(SwingConstants.CENTER);
        _textField.setEditable(false);
        _form.add(_textField, gridBagConstraints);

        ++lineNumber;
    }

    /**
     * Add a new row to the form containing a label and a radio button pair in a button group.
     *
     * @param _form          JPanel representing the form.
     * @param _label         JLabel for the radio button group.
     * @param _radioBtnLeft  JRadioButton displayed on the left.
     * @param _radioBtnRight JRadioButton displayed on the right.
     */
    protected void addRadioButtonPair(JPanel _form, JLabel _label,
                                      JRadioButton _radioBtnLeft, JRadioButton _radioBtnRight) {
        ButtonGroup unitType = new ButtonGroup();
        unitType.add(_radioBtnLeft);
        unitType.add(_radioBtnRight);

        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.gridwidth = 1;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 2;
        _form.add(_label, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1;
        _form.add(_radioBtnLeft, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.weightx = 1;
        _form.add(_radioBtnRight, gridBagConstraints);

        ++lineNumber;
    }

    /**
     * Add a blank fill at the end of the form to keep the content on top.
     *
     * @param _form JPanel representing the form.
     */
    protected void addEndFilling(JPanel _form) {
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1;

        JLabel blankSpace = new JLabel("");
        _form.add(blankSpace, gridBagConstraints);
    }

    protected void addTextArea(JPanel _form, JTextComponent _textArea) {
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = lineNumber;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(8, 0, 0, 0);

        _form.add(_textArea, gridBagConstraints);
        ++lineNumber;
    }
}
