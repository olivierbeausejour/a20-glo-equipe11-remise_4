package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.LocaleText;
import ca.ulaval.glo2004.utils.Shortcuts;
import ca.ulaval.glo2004.utils.ViewOrientation;
import ca.ulaval.glo2004.view.actions.*;
import ca.ulaval.glo2004.view.dialog.AboutUs;
import ca.ulaval.glo2004.view.dialog.SaveDialog;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.Normalizer;
import java.util.HashMap;

/**
 * Top windows Menu
 */
public class Menu extends JMenuBar {
    private final PationatorWindow window;
    private final PatioController patioController;
    private final PatioDrawingPanel[] patioDrawingPanelArray;
    private final HashMap<String, JMenu> submenuParentMenuItem = new HashMap<>();
    private final HashMap<String, JMenuItem> regularMenuItem = new HashMap<>();

    /**
     * Create a Menu object and add it to the specified frame.
     *
     * @param _frame                  window that will contain the menu.
     * @param _patioController        Parent patioController.
     * @param _patioDrawingPanelArray Parent drawing panel array.
     */
    public Menu(PationatorWindow _frame, PatioController _patioController,
                PatioDrawingPanel[] _patioDrawingPanelArray) {
        window = _frame;
        patioController = _patioController;
        patioDrawingPanelArray = _patioDrawingPanelArray;

        addToFrame();
    }

    /**
     * Add the menu to the top of the object frame.
     */
    public void addToFrame() {
        window.add(BorderLayout.NORTH, this);
    }

    /**
     * Create a non-customizable standard menu.
     */
    public void getStandardMenu() {
        addSubmenuParentMenuItem(LocaleText.getString("FILE_MENU"));
        addSubmenuParentMenuItem(LocaleText.getString("EDIT_MENU"));
        addSubmenuParentMenuItem(LocaleText.getString("VIEW_MENU"));
        addSubmenuParentMenuItem(LocaleText.getString("HELP_MENU"));

        addRegularMenuItem(LocaleText.getString("NEW_FILE"),
                submenuParentMenuItem.get(LocaleText.getString("FILE_MENU")), Shortcuts.NEW);
        addRegularMenuItem(LocaleText.getString("OPEN_FILE"),
                submenuParentMenuItem.get(LocaleText.getString("FILE_MENU")), Shortcuts.OPEN);
        addRegularMenuItem(LocaleText.getString("SAVE_FILE"),
                submenuParentMenuItem.get(LocaleText.getString("FILE_MENU")), Shortcuts.SAVE);
        addRegularMenuItem(LocaleText.getString("SAVE_AS_FILE"),
                submenuParentMenuItem.get(LocaleText.getString("FILE_MENU")), Shortcuts.SAVE_AS);
        submenuParentMenuItem.get(LocaleText.getString("FILE_MENU")).add(new JSeparator());
        addSubmenuParentMenuItem(LocaleText.getString("EXPORT_FILE"),
                submenuParentMenuItem.get(LocaleText.getString("FILE_MENU")));
        addRegularMenuItem(LocaleText.getString("PRINT_FILE"),
                submenuParentMenuItem.get(LocaleText.getString("FILE_MENU")), Shortcuts.PRINT);
        addRegularMenuItem(LocaleText.getString("QUIT"),
                submenuParentMenuItem.get(LocaleText.getString("FILE_MENU")), Shortcuts.QUIT);

        addRegularMenuItem(LocaleText.getString("EXPORT_STL_MENU"),
                submenuParentMenuItem.get(LocaleText.getString("EXPORT_FILE")), Shortcuts.STL_EXPORT);
        addRegularMenuItem(LocaleText.getString("EXPORT_PARTIAL_MENU"),
                submenuParentMenuItem.get(LocaleText.getString("EXPORT_FILE")), Shortcuts.STL_EXPORT_ALT);
        submenuParentMenuItem.get(LocaleText.getString("EXPORT_FILE")).add(new JSeparator());
        addRegularMenuItem(LocaleText.getString("EXPORT_IMAGE_MENU"),
                submenuParentMenuItem.get(LocaleText.getString("EXPORT_FILE")), Shortcuts.IMAGE_EXPORT);
        addRegularMenuItem(LocaleText.getString("EXPORT_IMAGE_ALL_VIEW_MENU"),
                submenuParentMenuItem.get(LocaleText.getString("EXPORT_FILE")), Shortcuts.IMAGE_EXPORT_ALT);
        submenuParentMenuItem.get(LocaleText.getString("EXPORT_FILE")).add(new JSeparator());
        addRegularMenuItem(LocaleText.getString("EXPORT_PIECES_MENU"),
                submenuParentMenuItem.get(LocaleText.getString("EXPORT_FILE")), Shortcuts.PIECES);

        addRegularMenuItem(LocaleText.getString("UNDO"),
                submenuParentMenuItem.get(LocaleText.getString("EDIT_MENU")), Shortcuts.UNDO);
        addRegularMenuItem(LocaleText.getString("REDO"),
                submenuParentMenuItem.get(LocaleText.getString("EDIT_MENU")), Shortcuts.REDO);
        submenuParentMenuItem.get(LocaleText.getString("EDIT_MENU")).add(new JSeparator());
        addRegularMenuItem(LocaleText.getString("ZOOM_IN"),
                submenuParentMenuItem.get(LocaleText.getString("EDIT_MENU")), Shortcuts.ZOOM_IN);
        addRegularMenuItem(LocaleText.getString("ZOOM_OUT"),
                submenuParentMenuItem.get(LocaleText.getString("EDIT_MENU")), Shortcuts.ZOOM_OUT);
        addRegularMenuItem(LocaleText.getString("CENTER_VIEW"),
                submenuParentMenuItem.get(LocaleText.getString("EDIT_MENU")), Shortcuts.CENTER);
        submenuParentMenuItem.get(LocaleText.getString("EDIT_MENU")).add(new JSeparator());

        addSubmenuParentMenuItem(LocaleText.getString("THEME_MENU"),
                submenuParentMenuItem.get(LocaleText.getString("EDIT_MENU")));
        addCheckBoxMenuItem(LocaleText.getString("FLATDARCULALAF"),
                submenuParentMenuItem.get(LocaleText.getString("THEME_MENU")));
        regularMenuItem.get(LocaleText.getString("FLATDARCULALAF")).setSelected(true);
        addCheckBoxMenuItem(LocaleText.getString("FLATDARKLAF"),
                submenuParentMenuItem.get(LocaleText.getString("THEME_MENU")));
        addCheckBoxMenuItem(LocaleText.getString("FLATLIGHTLAF"),
                submenuParentMenuItem.get(LocaleText.getString("THEME_MENU")));

        addRegularMenuItem(LocaleText.getString("FACE_VIEW"),
                submenuParentMenuItem.get(LocaleText.getString("VIEW_MENU")), Shortcuts.FACE_VIEW);
        addRegularMenuItem(LocaleText.getString("SIDE_VIEW"),
                submenuParentMenuItem.get(LocaleText.getString("VIEW_MENU")), Shortcuts.SIDE_VIEW);
        addRegularMenuItem(LocaleText.getString("TOP_VIEW"),
                submenuParentMenuItem.get(LocaleText.getString("VIEW_MENU")), Shortcuts.TOP_VIEW);
        submenuParentMenuItem.get(LocaleText.getString("VIEW_MENU")).add(new JSeparator());

        addSubmenuParentMenuItem(LocaleText.getString("LAYOUT_MENU"),
                submenuParentMenuItem.get(LocaleText.getString("VIEW_MENU")));
        addRegularMenuItem(LocaleText.getString("LAYOUT_SINGLE"),
                submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")), Shortcuts.LAYOUT_SINGLE);
        submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")).add(new JSeparator());
        addRegularMenuItem(LocaleText.getString("LAYOUT_TWO_HORIZONTAL"),
                submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")), Shortcuts.LAYOUT_TWO_HORIZONTAL);
        addRegularMenuItem(LocaleText.getString("LAYOUT_TWO_VERTICAL"),
                submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")), Shortcuts.LAYOUT_TWO_VERTICAL);
        submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")).add(new JSeparator());
        addRegularMenuItem(LocaleText.getString("LAYOUT_THREE_TOP"),
                submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")), Shortcuts.LAYOUT_THREE_TOP);
        addRegularMenuItem(LocaleText.getString("LAYOUT_THREE_BOTTOM"),
                submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")), Shortcuts.LAYOUT_THREE_BOTTOM);
        addRegularMenuItem(LocaleText.getString("LAYOUT_THREE_LEFT"),
                submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")), Shortcuts.LAYOUT_THREE_LEFT);
        addRegularMenuItem(LocaleText.getString("LAYOUT_THREE_RIGHT"),
                submenuParentMenuItem.get(LocaleText.getString("LAYOUT_MENU")), Shortcuts.LAYOUT_THREE_RIGHT);

        addRegularMenuItem(LocaleText.getString("ABOUT"),
                submenuParentMenuItem.get(LocaleText.getString("HELP_MENU")), Shortcuts.ABOUT);

        addMenuListeners();
    }

    /**
     * Add a menu title in the menu bar.
     *
     * @param _title Menu title name.
     */
    public void addSubmenuParentMenuItem(String _title) {
        JMenu menu = new JMenu(_title);

        String simpleTitle = _title;

        simpleTitle = Normalizer.normalize(simpleTitle, Normalizer.Form.NFD);
        simpleTitle = simpleTitle.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        menu.setMnemonic(simpleTitle.charAt(0));

        submenuParentMenuItem.put(_title, menu);
        add(menu);
    }

    /**
     * Add a basic menu item.
     *
     * @param _title     Menu item name.
     * @param _menuTitle Parent menu title.
     */
    public void addRegularMenuItem(String _title, JMenu _menuTitle, KeyStroke shortcut) {
        JMenuItem menu = new JMenuItem(_title);

        if (shortcut != null) {
            menu.setAccelerator(shortcut);
        }

        regularMenuItem.put(_title, menu);
        _menuTitle.add(menu);
    }

    /**
     * Add a menu item that may contain a submenu.
     *
     * @param _title     Menu item name.
     * @param _menuTitle Parent menu title.
     */
    public void addSubmenuParentMenuItem(String _title, JMenu _menuTitle) {
        JMenu menu = new JMenu(_title);

        String simpleTitle = _title;

        simpleTitle = Normalizer.normalize(simpleTitle, Normalizer.Form.NFD);
        simpleTitle = simpleTitle.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        menu.setMnemonic(simpleTitle.charAt(0));

        submenuParentMenuItem.put(_title, menu);
        _menuTitle.add(menu);
    }

    /**
     * Add a checkbox menu item. Used to display selection made to the user with a checkbox next to the selected item.
     *
     * @param _title Menu item name.
     * @param _menu  Parent menu title.
     */
    private void addCheckBoxMenuItem(String _title, JMenu _menu) {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(_title);

        regularMenuItem.put(_title, menuItem);
        _menu.add(menuItem);
    }

    /**
     * Add listener on menu item to perform action.
     */
    private void addMenuListeners() {
        regularMenuItem.get(LocaleText.getString("QUIT")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        regularMenuItem.get(LocaleText.getString("SAVE_AS_FILE")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SaveDialog saveFileDialog = new SaveDialog(window, patioController, SaveDialog.FileFormat.PTOR);
                saveFileDialog.open();
            }
        });

        regularMenuItem.get(LocaleText.getString("SAVE_FILE")).setAction(new SaveAction(window, patioController));
        regularMenuItem.get(LocaleText.getString("OPEN_FILE")).setAction(new OpenAction(window, patioController));
        regularMenuItem.get(LocaleText.getString("NEW_FILE")).setAction(new CreateAction(window, patioController));
        regularMenuItem.get(LocaleText.getString("PRINT_FILE")).setAction(new PrintAction(window, patioController));

        regularMenuItem.get(LocaleText.getString("QUIT")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                patioController.quit(window);
            }
        });

        regularMenuItem.get(LocaleText.getString("UNDO")).setAction(new UndoAction(patioController));
        regularMenuItem.get(LocaleText.getString("REDO")).setAction(new RedoAction(patioController));

        regularMenuItem.get(LocaleText.getString("FACE_VIEW")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateView(ViewOrientation.FACE);
            }
        });
        regularMenuItem.get(LocaleText.getString("SIDE_VIEW")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateView(ViewOrientation.SIDE);
            }
        });
        regularMenuItem.get(LocaleText.getString("TOP_VIEW")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateView(ViewOrientation.TOP);
            }
        });
        regularMenuItem.get(LocaleText.getString("LAYOUT_SINGLE")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.changeLayout(ViewLayout.SINGLE_VIEW);
                for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                    if (patioDrawingPanel.isVisible()) {
                        patioDrawingPanel.repaint();
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("LAYOUT_TWO_HORIZONTAL")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.changeLayout(ViewLayout.TWO_VIEW_HORIZONTAL);
                for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                    if (patioDrawingPanel.isVisible()) {
                        patioDrawingPanel.repaint();
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("LAYOUT_TWO_VERTICAL")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.changeLayout(ViewLayout.TWO_VIEW_VERTICAL);
                for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                    if (patioDrawingPanel.isVisible()) {
                        patioDrawingPanel.repaint();
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("LAYOUT_THREE_TOP")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.changeLayout(ViewLayout.THREE_VIEW_BIG_TOP);
                for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                    if (patioDrawingPanel.isVisible()) {
                        patioDrawingPanel.repaint();
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("LAYOUT_THREE_BOTTOM")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.changeLayout(ViewLayout.THREE_VIEW_BIG_BOTTOM);
                for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                    if (patioDrawingPanel.isVisible()) {
                        patioDrawingPanel.repaint();
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("LAYOUT_THREE_LEFT")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.changeLayout(ViewLayout.THREE_VIEW_BIG_LEFT);
                for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                    if (patioDrawingPanel.isVisible()) {
                        patioDrawingPanel.repaint();
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("LAYOUT_THREE_RIGHT")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.changeLayout(ViewLayout.THREE_VIEW_BIG_RIGHT);
                for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                    if (patioDrawingPanel.isVisible()) {
                        patioDrawingPanel.repaint();
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("ABOUT")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AboutUs aboutUs = new AboutUs(window);
                aboutUs.open();
            }
        });
        regularMenuItem.get(LocaleText.getString("EXPORT_STL_MENU")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SaveDialog saveDialog = new SaveDialog(window, patioController, SaveDialog.FileFormat.STL);
                saveDialog.open();
            }
        });
        regularMenuItem.get(LocaleText.getString("EXPORT_PARTIAL_MENU")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SaveDialog saveDialog = new SaveDialog(window, patioController, SaveDialog.FileFormat.STL);
                saveDialog.setModifierValue(true);
                saveDialog.open();
            }
        });
        regularMenuItem.get(LocaleText.getString("EXPORT_IMAGE_MENU")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SaveDialog saveDialog = new SaveDialog(window, patioController, SaveDialog.FileFormat.IMAGE);
                saveDialog.setModifierValue(false);
                saveDialog.open();
            }
        });
        regularMenuItem.get(LocaleText.getString("EXPORT_IMAGE_ALL_VIEW_MENU")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SaveDialog saveDialog = new SaveDialog(window, patioController, SaveDialog.FileFormat.IMAGE);
                saveDialog.setModifierValue(true);
                saveDialog.open();
            }
        });

        regularMenuItem.get(LocaleText.getString("EXPORT_PIECES_MENU")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SaveDialog saveDialog = new SaveDialog(window, patioController, SaveDialog.FileFormat.PIECES);
                saveDialog.setModifierValue(false);
                saveDialog.open();
            }
        });

        regularMenuItem.get(LocaleText.getString("ZOOM_IN")).setAction(
                new ZoomInAction(patioController, window));
        regularMenuItem.get(LocaleText.getString("ZOOM_OUT")).setAction(
                new ZoomOutAction(patioController, window));
        regularMenuItem.get(LocaleText.getString("CENTER_VIEW")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                PatioDrawingPanel drawingPanel = patioDrawingPanelArray[window.getActiveDrawingPanelArrayIndex()];

                int xPos = drawingPanel.getWidth() / 2;
                int yPos = drawingPanel.getHeight() / 2;

                for (int i = 0; i < 3; ++i) {
                    patioController.setCenterView(new Point(xPos, yPos), i);
                }
            }
        });

        regularMenuItem.get(LocaleText.getString("FLATDARCULALAF")).addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    changeThemeColor(window, "FlatDarculaLaf");

                    regularMenuItem.get(LocaleText.getString("FLATDARKLAF")).setSelected(false);
                    regularMenuItem.get(LocaleText.getString("FLATLIGHTLAF")).setSelected(false);

                    for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                        if (patioDrawingPanel.isVisible()) {
                            patioDrawingPanel.setPatioDrawingAreaBackground();
                        }
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("FLATDARKLAF")).addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    changeThemeColor(window, "FlatDarkLaf");

                    regularMenuItem.get(LocaleText.getString("FLATDARCULALAF")).setSelected(false);
                    regularMenuItem.get(LocaleText.getString("FLATLIGHTLAF")).setSelected(false);

                    for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                        if (patioDrawingPanel.isVisible()) {
                            patioDrawingPanel.setPatioDrawingAreaBackground();
                        }
                    }
                }
            }
        });
        regularMenuItem.get(LocaleText.getString("FLATLIGHTLAF")).addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    changeThemeColor(window, "FlatLightLaf");

                    regularMenuItem.get(LocaleText.getString("FLATDARKLAF")).setSelected(false);
                    regularMenuItem.get(LocaleText.getString("FLATDARCULALAF")).setSelected(false);

                    for (PatioDrawingPanel patioDrawingPanel : patioDrawingPanelArray) {
                        if (patioDrawingPanel.isVisible()) {
                            patioDrawingPanel.setPatioDrawingAreaBackground();
                        }
                    }
                }
            }
        });
    }

    /**
     * Update the view orientation.
     *
     * @param _viewOrientation New view orientation.
     */
    private void updateView(ViewOrientation _viewOrientation) {
        int arrayIndex = window.getActiveDrawingPanelArrayIndex();
        PatioDrawingPanel activeDrawingPanel = patioDrawingPanelArray[arrayIndex];

        patioController.setViewOrientation(_viewOrientation, activeDrawingPanel.getIndex());
        activeDrawingPanel.updateViewButtons(_viewOrientation);
        activeDrawingPanel.repaint();
    }

    /**
     * Change Pationator theme. Possible theme are :
     * <ul>
     *     <li>Flat Darcula : Dark theme using popular Darcula color scheme.</li>
     *     <li>Flat Dark : Standard dark theme.</li>
     *     <li>Flat Light : Standard light theme.</li>
     * </ul>
     *
     * @param frame Main Pationator windows.
     * @param theme Selected theme.
     */
    public static void changeThemeColor(JFrame frame, String theme) {
        switch (theme) {
            case "FlatDarculaLaf":
                try {
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                } catch (UnsupportedLookAndFeelException _exception) {
                    _exception.printStackTrace();
                }
                break;
            case "FlatDarkLaf":
                try {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } catch (UnsupportedLookAndFeelException _exception) {
                    _exception.printStackTrace();
                }
                break;
            case "FlatLightLaf":
                try {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } catch (UnsupportedLookAndFeelException _exception) {
                    _exception.printStackTrace();
                }
                break;
            default:
                break;
        }

        SwingUtilities.updateComponentTreeUI(frame);
    }

    /**
     * Add a basic menu item.
     *
     * @param _title     Menu item name.
     * @param _menuTitle Parent menu title.
     */
    public void addRegularMenuItem(String _title, JMenu _menuTitle) {
        JMenuItem menu = new JMenuItem(_title);

        regularMenuItem.put(_title, menu);
        _menuTitle.add(menu);
    }
}
