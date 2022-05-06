package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.ChangeMadeListener;
import ca.ulaval.glo2004.utils.LocaleText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

/**
 * Main view of Pationator. Contains a drawing panel and several entries to design a patio.
 */
public class PationatorWindow extends JFrame implements ChangeMadeListener {
    private static final String ICON_PATH = "/png/icon.png";

    private final PatioController patioController;
    private final PatioDrawingPanel[] patioDrawingPanelArray = new PatioDrawingPanel[17];
    private JPanel cardView;
    private ViewLayout layoutValue;
    private int drawingPanelArrayIndex;

    /**
     * Creates the main view of Pationator.
     */
    public PationatorWindow(String filename) {
        patioController = new PatioController();

        initializeEachDrawingPanel();

        if (filename.isEmpty()) {
            patioController.generateDefaultPatio();
        } else {
            patioController.openFile(filename);
        }

        Menu menuBar = new Menu(this, patioController, patioDrawingPanelArray);
        menuBar.getStandardMenu();
        setJMenuBar(menuBar);

        ToolBar toolBar = new ToolBar(this, patioController);
        toolBar.create();

        patioController.addChangeMadeListener(this);
        Tabs tabs = new Tabs(this);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createDifferentLayout(), tabs);

        setDefaultLayout();

        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.9);

        add(splitPane);

        StatusBar statusBar = new StatusBar(this, patioController);
        statusBar.setText(LocaleText.getString("LAST_ACTION_INFO"));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                patioController.quit((JFrame) we.getComponent());
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(400, 200));
        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        setTitle(LocaleText.getString("APP_TITLE"));

        URL imageIconPath = getClass().getResource(ICON_PATH);
        setIconImage(new ImageIcon(imageIconPath).getImage());
    }

    /**
     * Initialize drawing panel inside an array.
     */
    private void initializeEachDrawingPanel() {
        patioDrawingPanelArray[0] = new PatioDrawingPanel(this, 0, 0);

        patioDrawingPanelArray[1] = new PatioDrawingPanel(this, 0, 1);
        patioDrawingPanelArray[2] = new PatioDrawingPanel(this, 1, 2);

        patioDrawingPanelArray[3] = new PatioDrawingPanel(this, 0, 3);
        patioDrawingPanelArray[4] = new PatioDrawingPanel(this, 1, 4);

        patioDrawingPanelArray[5] = new PatioDrawingPanel(this, 0, 5);
        patioDrawingPanelArray[6] = new PatioDrawingPanel(this, 1, 6);
        patioDrawingPanelArray[7] = new PatioDrawingPanel(this, 2, 7);

        patioDrawingPanelArray[8] = new PatioDrawingPanel(this, 0, 8);
        patioDrawingPanelArray[9] = new PatioDrawingPanel(this, 1, 9);
        patioDrawingPanelArray[10] = new PatioDrawingPanel(this, 2, 10);

        patioDrawingPanelArray[11] = new PatioDrawingPanel(this, 0, 11);
        patioDrawingPanelArray[12] = new PatioDrawingPanel(this, 1, 12);
        patioDrawingPanelArray[13] = new PatioDrawingPanel(this, 2, 13);

        patioDrawingPanelArray[14] = new PatioDrawingPanel(this, 0, 14);
        patioDrawingPanelArray[15] = new PatioDrawingPanel(this, 1, 15);
        patioDrawingPanelArray[16] = new PatioDrawingPanel(this, 2, 16);
    }

    /**
     * Creates different layout with different patio drawing panel.
     *
     * @return Panel with CardLayout.
     */
    private JPanel createDifferentLayout() {
        JSplitPane twoViewHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                patioDrawingPanelArray[1], patioDrawingPanelArray[2]);
        twoViewHorizontal.setOneTouchExpandable(true);
        twoViewHorizontal.setResizeWeight(0.5);

        JSplitPane twoViewVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                patioDrawingPanelArray[3], patioDrawingPanelArray[4]);
        twoViewVertical.setOneTouchExpandable(true);
        twoViewVertical.setResizeWeight(0.5);

        JSplitPane threeViewBigTopBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                patioDrawingPanelArray[6], patioDrawingPanelArray[7]);
        threeViewBigTopBottom.setOneTouchExpandable(true);
        threeViewBigTopBottom.setResizeWeight(0.5);
        JSplitPane threeViewBigTop = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                patioDrawingPanelArray[5], threeViewBigTopBottom);
        threeViewBigTop.setOneTouchExpandable(true);
        threeViewBigTop.setResizeWeight(0.5);

        JSplitPane threeViewBigRightLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                patioDrawingPanelArray[9], patioDrawingPanelArray[10]);
        threeViewBigRightLeft.setOneTouchExpandable(true);
        threeViewBigRightLeft.setResizeWeight(0.5);
        JSplitPane threeViewBigRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                threeViewBigRightLeft, patioDrawingPanelArray[8]);
        threeViewBigRight.setOneTouchExpandable(true);
        threeViewBigRight.setResizeWeight(0.5);

        JSplitPane threeViewBigLeftRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                patioDrawingPanelArray[12], patioDrawingPanelArray[13]);
        threeViewBigLeftRight.setOneTouchExpandable(true);
        threeViewBigLeftRight.setResizeWeight(0.5);
        JSplitPane threeViewBigLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                patioDrawingPanelArray[11], threeViewBigLeftRight);
        threeViewBigLeft.setOneTouchExpandable(true);
        threeViewBigLeft.setResizeWeight(0.5);

        JSplitPane threeViewBigBottomTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                patioDrawingPanelArray[15], patioDrawingPanelArray[16]);
        threeViewBigBottomTop.setOneTouchExpandable(true);
        threeViewBigBottomTop.setResizeWeight(0.5);
        JSplitPane threeViewBigBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                threeViewBigBottomTop, patioDrawingPanelArray[14]);
        threeViewBigBottom.setOneTouchExpandable(true);
        threeViewBigBottom.setResizeWeight(0.5);

        cardView = new JPanel(new CardLayout());
        cardView.add(patioDrawingPanelArray[0], String.valueOf(ViewLayout.SINGLE_VIEW));

        cardView.add(twoViewHorizontal, String.valueOf(ViewLayout.TWO_VIEW_HORIZONTAL));
        cardView.add(twoViewVertical, String.valueOf(ViewLayout.TWO_VIEW_VERTICAL));

        cardView.add(threeViewBigTop, String.valueOf(ViewLayout.THREE_VIEW_BIG_TOP));
        cardView.add(threeViewBigBottom, String.valueOf(ViewLayout.THREE_VIEW_BIG_BOTTOM));
        cardView.add(threeViewBigLeft, String.valueOf(ViewLayout.THREE_VIEW_BIG_LEFT));
        cardView.add(threeViewBigRight, String.valueOf(ViewLayout.THREE_VIEW_BIG_RIGHT));

        return cardView;
    }

    /**
     * Set the default layout.
     */
    private void setDefaultLayout() {
        layoutValue = ViewLayout.THREE_VIEW_BIG_TOP;
        changeLayout(layoutValue);
    }

    /**
     * Change layout value.
     *
     * @param _newLayout New layout.
     */
    public void changeLayout(ViewLayout _newLayout) {
        layoutValue = _newLayout;
        changeViewCard(_newLayout);
    }

    /**
     * Change layout.
     *
     * @param _viewLayout New layout.
     */
    public void changeViewCard(ViewLayout _viewLayout) {
        CardLayout layout = (CardLayout) (cardView.getLayout());
        layout.show(cardView, String.valueOf(_viewLayout));
    }

    /**
     * Get primary PatioDrawingPanel from the Pationator main window
     *
     * @return PatioDrawingPanel object.
     */
    public PatioDrawingPanel getPrimaryDrawingPanel() {
        switch (layoutValue) {
            case SINGLE_VIEW:
            default:
                return patioDrawingPanelArray[0];
            case TWO_VIEW_HORIZONTAL:
                return patioDrawingPanelArray[1];
            case TWO_VIEW_VERTICAL:
                return patioDrawingPanelArray[3];
            case THREE_VIEW_BIG_TOP:
                return patioDrawingPanelArray[5];
            case THREE_VIEW_BIG_RIGHT:
                return patioDrawingPanelArray[8];
            case THREE_VIEW_BIG_LEFT:
                return patioDrawingPanelArray[11];
            case THREE_VIEW_BIG_BOTTOM:
                return patioDrawingPanelArray[14];
        }
    }

    /**
     * Get current PatioController from PationatorWindow.
     *
     * @return Current PatioController.
     */
    public PatioController getPatioController() {
        return patioController;
    }

    /**
     * Behavior when changes are made inside PationatorWindow
     */
    @Override
    public void onChangeMade() {
        for (PatioDrawingPanel drawingPanel : patioDrawingPanelArray) {
            if (drawingPanel.isVisible()) {
                drawingPanel.repaint();
            }
        }
    }

    /**
     * Set active drawing panel index.
     *
     * @param _drawingPanelIndex Active drawing panel index.
     */
    public void setActiveDrawingPanelIndex(int _drawingPanelIndex) {
        drawingPanelArrayIndex = _drawingPanelIndex;
    }

    /**
     * Get active drawing panel index.
     *
     * @return Drawing panel array index.
     */
    public int getActiveDrawingPanelArrayIndex() {
        return drawingPanelArrayIndex;
    }
}
