package ca.ulaval.glo2004.utils;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * This class contain shortcut use in the menu.
 */
public class Shortcuts {
    public static KeyStroke ABOUT = KeyStroke.getKeyStroke(
            KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK);
    public static KeyStroke CENTER = KeyStroke.getKeyStroke(
            KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    public static KeyStroke FACE_VIEW = KeyStroke.getKeyStroke(
            KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke HELP = KeyStroke.getKeyStroke(
            KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke IMAGE_EXPORT = KeyStroke.getKeyStroke(
            KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
    public static KeyStroke IMAGE_EXPORT_ALT = KeyStroke.getKeyStroke(
            KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    public static KeyStroke LAYOUT_SINGLE = KeyStroke.getKeyStroke(
            KeyEvent.VK_NUMPAD1, InputEvent.ALT_DOWN_MASK);
    public static KeyStroke LAYOUT_THREE_BOTTOM = KeyStroke.getKeyStroke(
            KeyEvent.VK_NUMPAD5, InputEvent.ALT_DOWN_MASK);
    public static KeyStroke LAYOUT_THREE_LEFT = KeyStroke.getKeyStroke(
            KeyEvent.VK_NUMPAD6, InputEvent.ALT_DOWN_MASK);
    public static KeyStroke LAYOUT_THREE_RIGHT = KeyStroke.getKeyStroke(
            KeyEvent.VK_NUMPAD7, InputEvent.ALT_DOWN_MASK);
    public static KeyStroke LAYOUT_THREE_TOP = KeyStroke.getKeyStroke(
            KeyEvent.VK_NUMPAD4, InputEvent.ALT_DOWN_MASK);
    public static KeyStroke LAYOUT_TWO_HORIZONTAL = KeyStroke.getKeyStroke(
            KeyEvent.VK_NUMPAD2, InputEvent.ALT_DOWN_MASK);
    public static KeyStroke LAYOUT_TWO_VERTICAL = KeyStroke.getKeyStroke(
            KeyEvent.VK_NUMPAD3, InputEvent.ALT_DOWN_MASK);
    public static KeyStroke NEW = KeyStroke.getKeyStroke(
            KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke OPEN = KeyStroke.getKeyStroke(
            KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke PIECES = KeyStroke.getKeyStroke(
            KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
    public static KeyStroke PRINT = KeyStroke.getKeyStroke(
            KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke QUIT = KeyStroke.getKeyStroke(
            KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke REDO = KeyStroke.getKeyStroke(
            KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke SAVE = KeyStroke.getKeyStroke(
            KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke SAVE_AS = KeyStroke.getKeyStroke(
            KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    public static KeyStroke SIDE_VIEW = KeyStroke.getKeyStroke(
            KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke STL_EXPORT = KeyStroke.getKeyStroke(
            KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
    public static KeyStroke STL_EXPORT_ALT = KeyStroke.getKeyStroke(
            KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    public static KeyStroke TOP_VIEW = KeyStroke.getKeyStroke(
            KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke UNDO = KeyStroke.getKeyStroke(
            KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke ZOOM_IN = KeyStroke.getKeyStroke(
            KeyEvent.VK_ADD, InputEvent.CTRL_DOWN_MASK);
    public static KeyStroke ZOOM_OUT = KeyStroke.getKeyStroke(
            KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK);
}
