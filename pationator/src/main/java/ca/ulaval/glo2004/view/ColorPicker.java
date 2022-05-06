package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.utils.LocaleText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * JButton to choose a color with a color sample on it.
 */
public class ColorPicker extends JButton {
    private Color color;
    private final ArrayList<CurrentColorChangeListener> changeListeners = new ArrayList<>();

    /**
     * Create a color selector button.
     *
     * @param _color Default button color.
     */
    public ColorPicker(Color _color) {
        color = _color;
        createColorPickerButton();
    }

    /**
     * Add an action listener to the button and call 'createColorIcon()' method.
     */
    private void createColorPickerButton() {
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent _actionEvent) {
                Color userColor = JColorChooser.showDialog(null, LocaleText.getString("COLOR_PICKER_DIALOG_TITLE"), color);
                setColor(userColor);
            }
        });

        createColorIcon();
    }

    /**
     * Create the icon with the current color.
     */
    private void createColorIcon() {
        int radius = 14;

        BufferedImage bufferedImage = new BufferedImage(
                radius + 2,
                radius + 1,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setColor(color);
        graphics2D.fillOval(0, 0, radius, radius);

        bufferedImage.flush();
        ImageIcon imageIcon = new ImageIcon(bufferedImage);

        setIcon(imageIcon);
    }

    /**
     * Create a color selector button with black as default color.
     */
    public ColorPicker() {
        color = Color.BLACK;
        createColorPickerButton();
    }

    /**
     * Add a CurrentColorChangeListener on the ColorPicker button.
     *
     * @param _currentColorChangeListener CurrentColorChangeListener and his behavior.
     */
    public void addCurrentColorChangeListener(CurrentColorChangeListener _currentColorChangeListener) {
        changeListeners.add(_currentColorChangeListener);
    }

    /**
     * Get color attribute for a new color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Change color attribute for a new color.
     *
     * @param _color New color shown on the button
     */
    public void setColor(Color _color) {
        if (_color != null) {
            color = _color;
            createColorIcon();

            for (CurrentColorChangeListener changeListener : changeListeners) {
                changeListener.colorChanged(_color);
            }
        }
    }

    /**
     * Listen if the ColorPicker color change.
     */
    public interface CurrentColorChangeListener {
        void colorChanged(Color _color);
    }
}
