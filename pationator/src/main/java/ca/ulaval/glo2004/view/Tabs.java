package ca.ulaval.glo2004.view;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.utils.*;

import javax.swing.*;
import java.util.ArrayList;

/**
 * JTabbedPane containing all the main settings tabs.
 */
public class Tabs extends JTabbedPane implements FileLoadedListener, ChangeMadeListener, FileSavedListener, DefaultPatioGeneratedListener {
    private static final String UNSAVED_CHARACTER = "*";
    private ArrayList<TabsContent> tabsContents = new ArrayList<>();

    private PationatorWindow parent;
    protected PatioController patioController;

    /**
     * Creates JTabbedPane with the main tabs and declares the current parent.
     * @param _parent Parent of current tab.
     */
    public Tabs(PationatorWindow _parent) {
        parent = _parent;
        patioController = parent.getPatioController();
        patioController.addFileLoaderListener(this);
        patioController.addChangeMadeListener(this);
        patioController.addFileSavedListener(this);
        patioController.addDefaultPatioGeneratedListener(this);

        setTabPlacement(JTabbedPane.TOP);
        setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

        newTab("EDIT_TAB_TITLE", new EditingTab(this, false));
        newTab("PRICE_TAB_TITLE", new PriceTab(this));
        newTab("MATERIALS_TAB_TITLE", new MaterialTab(this));
        newTab("SETTINGS_TAB_TITLE", new SettingsTab(this));
    }

    /**
     * Add a new tab in the JTabbedPane.
     * @param _localeTextKey Key in LocalText to display tab title.
     * @param _tabsContent TabsContent tab.
     */
    private void newTab(String _localeTextKey, TabsContent _tabsContent) {
        ArrayList<TabsContent> newArrayList = getTabsContents();
        newArrayList.add(_tabsContent);
        setTabsContents(newArrayList);

        addTab(LocaleText.getString(_localeTextKey), new JScrollPane(_tabsContent));
    }

    /**
     * Get current tabsContents. tabsContents include every tab display in the JTabbedPane.
     * @return Current arrayList.
     */
    private ArrayList<TabsContent> getTabsContents() {
        return tabsContents;
    }

    /**
     * Set tabsContents. tabsContents include every tab display in the JTabbedPane.
     * @param _tabsContents New arrayList.
     */
    private void setTabsContents(ArrayList<TabsContent> _tabsContents) {
        tabsContents = _tabsContents;
    }

    /**
     * Update value in each tabs input
     */
    protected void componentRefresh() {
        for (TabsContent tab : getTabsContents()) {
            tab.refreshContent();
        }
    }

    /**
     * Creates JTabbedPane with the main tabs and declares the current parent.
     */
    public Tabs() {
        setTabPlacement(JTabbedPane.TOP);
        setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

        PriceTab tabPrices = new PriceTab(this);
        MaterialTab tabMaterial = new MaterialTab(this);
        EditingTab tabEditing = new EditingTab(this, true);
        SettingsTab tabSettings = new SettingsTab(this);

        addTab(LocaleText.getString("EDIT_TAB_TITLE"), tabEditing);
        addTab(LocaleText.getString("PRICE_TAB_TITLE"), tabPrices);
        addTab(LocaleText.getString("MATERIALS_TAB_TITLE"), tabMaterial);
        addTab(LocaleText.getString("SETTINGS_TAB_TITLE"), tabSettings);
    }

    /**
     * Change the window title to suit the chosen file or add an unsaved symbol based on the current state of the
     * project.
     * @param _onFileLoaded True if the project is load from file.
     */
    private void updateWindowTitle(boolean _onFileLoaded, boolean _onNewFile) {
        if (patioController.getCurrentFileName() != null)
            parent.setTitle(patioController.getShortFileName() + " - " + LocaleText.getString("APP_TITLE"));

        if (!_onFileLoaded && patioController.shouldShowSavePopup() && !parent.getTitle().contains(UNSAVED_CHARACTER))
            parent.setTitle(UNSAVED_CHARACTER + parent.getTitle());

        if (_onNewFile)
            parent.setTitle(LocaleText.getString("APP_TITLE"));
    }

    /**
     * Behavior when the project is load from file.
     */
    @Override
    public void onFileLoaded() {
        componentRefresh();
        updateWindowTitle(true, false);
    }

    /**
     * Behavior when the project value change.
     */
    @Override
    public void onChangeMade() {
        componentRefresh();
        updateWindowTitle(false, false);
    }

    /**
     * Behavior when the file is save
     */
    @Override
    public void onFileSaved() {
        updateWindowTitle(false, false);
    }

    @Override
    public void onDefaultPatioGenerated() {
        updateWindowTitle(false, true);
    }
}
