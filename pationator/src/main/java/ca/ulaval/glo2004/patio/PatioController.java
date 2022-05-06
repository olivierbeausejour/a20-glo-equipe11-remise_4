package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.*;
import ca.ulaval.glo2004.view.dialog.SaveChangesWarning;
import ca.ulaval.glo2004.view.dialog.SaveFileDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Class that interacts with the sub-system in order to modify or access any patio domain properties.
 */
public class PatioController {
    private final Patio patio;
    private final PatioDrawer patioDrawer;
    private final ArrayList<ChangeMadeListener> changeMadeListeners;
    private final ArrayList<HoveredWoodPieceListener> hoveredWoodPieceListeners;
    private final ArrayList<UndoActivatedListener> undoActivatedListeners;
    private final ArrayList<RedoActivatedListener> redoActivatedListeners;
    private final ArrayList<DefaultPatioGeneratedListener> defaultPatioGeneratedListeners;
    private final Vector2[] inchMousePositionWithIndex = new Vector2[]{
            new Vector2(0.0f, 0.0f), new Vector2(0.0f, 0.0f), new Vector2(0.0f, 0.0f),
            new Vector2(0.0f, 0.0f)};
    private final Vector2[] viewCenterWithIndex = new Vector2[]{
            new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0)};
    private final float[] zoomFactorWithIndex = new float[]{0.15f, 0.15f, 0.15f, 0.15f};
    private final ArrayList<ExportDoneListener> exportDoneListeners;
    private boolean isOptimalMode;
    private final ArrayList<FileLoadedListener> fileLoadedListeners;
    private final ArrayList<FileSavedListener> fileSavedListeners;
    private String currentFileName;
    private String piecesList;
    private boolean changesToSave;
    private PatioInfo defaultPatioInfo;
    private boolean initPationator = true;
    private boolean arrowVisibility = true;
    private String priceText;

    /**
     * The PatioController constructor
     */
    public PatioController() {
        patio = new Patio();
        patioDrawer = new PatioDrawer(this);

        changeMadeListeners = new ArrayList<>();
        undoActivatedListeners = new ArrayList<>();
        redoActivatedListeners = new ArrayList<>();
        defaultPatioGeneratedListeners = new ArrayList<>();
        fileLoadedListeners = new ArrayList<>();
        fileSavedListeners = new ArrayList<>();
        exportDoneListeners = new ArrayList<>();

        hoveredWoodPieceListeners = new ArrayList<>();
    }

    /**
     * Sets a new pieces list
     *
     * @param _piecesList a new pieces list as a String
     */
    public void setPiecesList(String _piecesList) {
        piecesList = _piecesList;
    }

    /**
     * Add a new listener to the export event.
     *
     * @param _exportDoneListener New export listener
     */
    public void addExportDoneListener(ExportDoneListener _exportDoneListener) {
        exportDoneListeners.add(_exportDoneListener);
    }

    /**
     * Assigns a position to the relative position of the cursor on the screen
     *
     * @param _inchMousePosition the relative position of the cursor on the screen
     * @param _index             the index of the cursor
     */
    public void setGridMousePositionInches(Vector2 _inchMousePosition, int _index) {
        inchMousePositionWithIndex[_index] = _inchMousePosition;
    }

    /**
     * Returns the properties of the component pointed by the cursor
     *
     * @return a specific component pointed by the cursor
     */
    public Pair<Component, WoodPiece> getComponentAndPieceAtMouse(int _index) {
        Pair<Component, WoodPiece> intersectedWoodPiece = getVisibility(ComponentType.COVERING_PLANK) ?
                FindComponentAtMousePosition(patio.getCovering(), _index) : null;

        intersectedWoodPiece = getVisibility(ComponentType.JOIST) ? intersectedWoodPiece != null ?
                intersectedWoodPiece : FindComponentAtMousePosition(patio.getSpans(), _index) : intersectedWoodPiece;
        intersectedWoodPiece = getVisibility(ComponentType.BEAM) ? intersectedWoodPiece != null ?
                intersectedWoodPiece : FindComponentAtMousePosition(patio.getBeams(), _index) : intersectedWoodPiece;
        intersectedWoodPiece = getVisibility(ComponentType.POST) ? intersectedWoodPiece != null ?
                intersectedWoodPiece : FindComponentAtMousePosition(patio.getPosts(), _index) : intersectedWoodPiece;

        return intersectedWoodPiece;
    }

    /**
     * Obtains patio's component visibility state. If the value is set to False, the component will not be visible.
     *
     * @param _componentType Patio's component.
     * @return Current visibility state.
     */
    public boolean getVisibility(ComponentType _componentType) {
        switch (_componentType) {
            case COVERING_PLANK:
                return patio.getPatioInfo().isCoveringVisibility();
            case JOIST:
                return patio.getPatioInfo().isJoistVisibility();
            case BEAM:
                return patio.getPatioInfo().isBeamVisibility();
            case POST:
                return patio.getPatioInfo().isPostVisibility();
            default:
                return false;
        }
    }

    /**
     * Finds if a component is being pointed by the cursor
     *
     * @param _components The components list
     * @param _index      The index
     * @return null if no component is being pointed by the cursor, or else a Pair<Component, WoodPiece> containing the
     * component and its specific wood piece
     */
    private Pair<Component, WoodPiece> FindComponentAtMousePosition(
            ArrayList<? extends Component> _components, int _index) {
        for (Component component : _components) {
            for (WoodPiece woodPiece : component.getWoodPieces()) {
                Vector3 min = woodPiece.getMinCornerPosition();
                Vector3 max = woodPiece.getMaxCornerPosition();
                float mouseX = inchMousePositionWithIndex[_index].x;
                float mouseY = inchMousePositionWithIndex[_index].y;
                switch (patioDrawer.getViewOrientation(_index)) {
                    case SIDE:
                        if (coordinateRectangleIntersect(mouseX, mouseY, min.x, max.x, min.y, max.y))
                            return new Pair(component, woodPiece);
                        break;
                    case TOP:
                        if (coordinateRectangleIntersect(mouseX, mouseY, min.x, max.x, min.z, max.z))
                            return new Pair(component, woodPiece);
                        break;
                    case FACE:
                        if (coordinateRectangleIntersect(mouseX, mouseY, min.z, max.z, min.y, max.y))
                            return new Pair(component, woodPiece);
                        break;
                }
            }
        }

        return null;
    }

    /**
     * Returns true if the coordinates of a rectangle intersect with specific coordinates, else false
     *
     * @param _coordX The X coordinates
     * @param _coordY The Y coordinates
     * @param _minX   The min X coordinates
     * @param _maxX   The max X coordinates
     * @param _minY   The min Y coordinates
     * @param _maxY   The max Y coordinates
     * @return True if the coordinates of a rectangle intersect with specific coordinates, else false
     */
    private boolean coordinateRectangleIntersect(
            float _coordX, float _coordY, float _minX, float _maxX, float _minY, float _maxY) {
        return _coordX >= _minX && _coordX <= _maxX && _coordY >= _minY && _coordY <= _maxY;
    }

    /**
     * Sets the FileLoadedListener
     *
     * @param _fileLoadedListener the FileLoadedListener
     */
    public void addFileLoaderListener(FileLoadedListener _fileLoadedListener) {
        fileLoadedListeners.add(_fileLoadedListener);
    }

    /**
     * Adds a ChangeMadeListener to the list
     *
     * @param _changeMadeListener The ChangeMadeListener to add to the list
     */
    public void addChangeMadeListener(ChangeMadeListener _changeMadeListener) {
        changeMadeListeners.add(_changeMadeListener);
    }

    /**
     * Adds a DefaultPatioGeneratedListener to the list
     *
     * @param _defaultPatioGeneratedListener The DefaultPatioGeneratedListener to add to the list
     */
    public void addDefaultPatioGeneratedListener(DefaultPatioGeneratedListener _defaultPatioGeneratedListener) {
        defaultPatioGeneratedListeners.add(_defaultPatioGeneratedListener);
    }

    /**
     * Sets the FileSavedListener
     *
     * @param _fileSavedListener the FileSavedListener
     */
    public void addFileSavedListener(FileSavedListener _fileSavedListener) {
        fileSavedListeners.add(_fileSavedListener);
    }

    /**
     * Returns the current patio instance
     *
     * @return the current patio instance
     */
    public Patio getPatio() {
        return patio;
    }

    /**
     * Returns the current measure type
     *
     * @param _measureType the current measure type
     */
    public void setMesureType(MeasureType _measureType) {
        if (_measureType == MeasureType.NOMINAL) {
            patio.getPatioInfo().setMeasureUnit(MeasureUnit.IMPERIAL);
        }

        patio.getPatioInfo().setMeasureType(_measureType);
    }

    /**
     * Returns the current joist span max length
     *
     * @return the current joist span max length
     */
    public float getJoistSpanMaxLength() {
        return patio.getPatioInfo().getJoistSpanMaxLength();
    }

    /**
     * Sets the current joist span max length
     *
     * @param _joistSpanMaxLength the new joist span max length
     */
    public void setJoistSpanMaxLength(double _joistSpanMaxLength) {
        patio.getPatioInfo().setJoistSpanMaxLength((float) _joistSpanMaxLength, true);
    }

    /**
     * Returns the current joist spacing
     *
     * @return the current joist spacing
     */
    public double getJoistSpacing() {
        return patio.getPatioInfo().getJoistSpacing();
    }

    /**
     * Sets the current joist spacing
     *
     * @param _joistSpacing the new joist spacing
     */
    public void setJoistSpacing(double _joistSpacing) {
        patio.getPatioInfo().setJoistSpacing((float) _joistSpacing);
    }

    /**
     * Returns the current min joist spacing
     *
     * @return the current min joist spacing
     */
    public float getMinJoistSpacing() {
        return patio.getPatioInfo().getMinJoistSpacing();
    }

    /**
     * Returns the current beam length
     *
     * @return the current beam length
     */
    public float getBeamLength() {
        return patio.getPatioInfo().getBeamPlieDimensions().getActualDepth();
    }

    /**
     * Sets the current beam length
     *
     * @param _beamLength the new beam length
     */
    public void setBeamLength(double _beamLength) {
        float beamWidth = patio.getPatioInfo().getBeamPlieDimensions().getActualWidth();
        float beamHeight = patio.getPatioInfo().getBeamPlieDimensions().getActualHeight();
        float beamDepth = (float) _beamLength;

        patio.getPatioInfo().setBeamDimensions(new Dimensions(beamWidth, beamHeight, beamDepth), true);
    }

    /**
     * Obtains the length of the patio's cantilever in inches.
     *
     * @return Length in inches.
     */
    public float getCantileverLength() {
        return patio.getPatioInfo().getCantileverLength();
    }

    /**
     * Changes the cantilever length.
     *
     * @param _cantileverLength Cantilever length in inches.
     */
    public void setCantileverLength(double _cantileverLength) {
        patio.getPatioInfo().setCantileverLength((float) _cantileverLength, true);
    }

    public float getMinCantileverLength() {
        return patio.getPatioInfo().getMinCantileverLength();
    }

    /**
     * Obtains the value of the spacing between cover boards.
     *
     * @return Gap width in inches.
     */
    public float getCoveringSpacing() {
        return patio.getPatioInfo().getCoveringSpacing();
    }

    /**
     * Changes the value of the spacing between cover boards.
     *
     * @param _coveringSpacing Gap width in inches.
     */
    public void setCoveringSpacing(double _coveringSpacing) {
        patio.getPatioInfo().setCoveringSpacing((float) _coveringSpacing);
    }

    /**
     * Returns the min covering spacing
     *
     * @return the min covering spacing
     */
    public float getMinCoveringSpacing() {
        return patio.getPatioInfo().getMinCoveringSpacing();
    }

    /**
     * Get a patio's component color.
     *
     * @param _componentType Patio's component.
     * @return Color object.
     */
    public Color getColor(ComponentType _componentType) {
        switch (_componentType) {
            case COVERING_PLANK:
                return patio.getPatioInfo().getCoveringColor();
            case JOIST:
                return patio.getPatioInfo().getJoistColor();
            case BEAM:
                return patio.getPatioInfo().getBeamColor();
            case POST:
                return patio.getPatioInfo().getPostColor();
            default:
                return Color.BLACK;
        }
    }

    /**
     * Obtains current patio's component color filling state. If the value is set to False, only the component border
     * will be visible.
     *
     * @param _componentType Patio's component.
     * @return Color filling state.
     */
    public boolean getColorFilling(ComponentType _componentType) {
        switch (_componentType) {
            case COVERING_PLANK:
                return patio.getPatioInfo().isCoveringColorFilling();
            case JOIST:
                return patio.getPatioInfo().isJoistColorFilling();
            case BEAM:
                return patio.getPatioInfo().isBeamColorFilling();
            case POST:
                return patio.getPatioInfo().isPostColorFilling();
            default:
                return false;
        }
    }

    /**
     * Returns the min patio height
     *
     * @return the min patio height
     */
    public float getMinPatioHeight() {
        return patio.getPatioInfo().getMinPatioHeight();
    }

    /**
     * Obtains the number of plies per beam.
     *
     * @return Plies number.
     */
    public int getPliesPerBeam() {
        return patio.getPatioInfo().getPliesPerBeam();
    }

    /**
     * Changes the number of plies per beam.
     *
     * @param _pliesPerBeam Number of plies.
     */
    public void setPliesPerBeam(int _pliesPerBeam) {
        patio.getPatioInfo().setPliesPerBeam(_pliesPerBeam);
    }

    /**
     * Obtains the min number of plies per beam.
     *
     * @return Min plies number.
     */
    public int getMinPliesPerBeam() {
        return patio.getPatioInfo().getMinPliesPerBeam();
    }

    /**
     * Obtains the max number of plies per beam.
     *
     * @return Max plies number.
     */
    public int getMaxPliesPerBeam() {
        return patio.getPatioInfo().getMaxPliesPerBeam();
    }

    /**
     * Obtains the number of post under one beam.
     *
     * @return Post number under one beam.
     */
    public int getPostPerBeam() {
        return patio.getPatioInfo().getPostsPerBeam();
    }

    /**
     * Changes the number of post under one beam
     *
     * @param _postPerBeam Number of post
     */
    public void setPostPerBeam(int _postPerBeam) {
        patio.getPatioInfo().setPostsPerBeam(_postPerBeam, true);
    }

    /**
     * Obtains the min number of post under one beam.
     *
     * @return Min number of post under one beam.
     */
    public int getMinPostPerBeam() {
        return patio.getPatioInfo().getMinPostsPerBeam();
    }

    /**
     * Obtains the max number of post under one beam.
     *
     * @return Max number of post under one beam.
     */
    public int getMaxPostPerBeam() {
        return patio.getPatioInfo().getMaxPostsPerBeam();
    }

    /**
     * Changes patio's component color.
     *
     * @param _componentType Patio's component.
     * @param _color         Updated color.
     */
    public void setColor(ComponentType _componentType, Color _color) {
        switch (_componentType) {
            case COVERING_PLANK:
                patio.getPatioInfo().setCoveringColor(_color);
                break;
            case JOIST:
                patio.getPatioInfo().setJoistColor(_color);
                break;
            case BEAM:
                patio.getPatioInfo().setBeamColor(_color);
                break;
            case POST:
                patio.getPatioInfo().setPostColor(_color);
                break;
            default:
                break;
        }
    }

    /**
     * Changes patio's component color filling state. If the value is set to False, only the component border will be
     * visible.
     *
     * @param _componentType Patio's component.
     * @param _selected      Color filling state.
     */
    public void setColorFilling(ComponentType _componentType, boolean _selected) {
        switch (_componentType) {
            case COVERING_PLANK:
                patio.getPatioInfo().setCoveringColorFilling(_selected);
                break;
            case JOIST:
                patio.getPatioInfo().setJoistColorFilling(_selected);
                break;
            case BEAM:
                patio.getPatioInfo().setBeamColorFilling(_selected);
                break;
            case POST:
                patio.getPatioInfo().setPostColorFilling(_selected);
                break;
            default:
                break;
        }
    }

    /**
     * Returns the minimimum width of the patio in actual inches.
     *
     * @return The minimimum width of the patio in actual inches.
     */
    public float getMinPatioWidth() {
        return patio.getPatioInfo().getMinPatioWidth();
    }

    /**
     * Changes patio's component visibility state. If the value is set to False, the component will not be visible.
     *
     * @param _componentType Patio's component.
     * @param _selected      Visibility state.
     */
    public void setVisibility(ComponentType _componentType, boolean _selected) {
        switch (_componentType) {
            case COVERING_PLANK:
                patio.getPatioInfo().setCoveringVisibility(_selected);
                break;
            case JOIST:
                patio.getPatioInfo().setJoistVisibility(_selected);
                break;
            case BEAM:
                patio.getPatioInfo().setBeamVisibility(_selected);
                break;
            case POST:
                patio.getPatioInfo().setPostVisibility(_selected);
                break;
            default:
                break;
        }
    }

    /**
     * Opens a .ptor file and loads it
     *
     * @param _filename The file path
     */
    public void openFile(String _filename) {
        PatioInfo patioInfo = FileHandler.openFile(_filename);

        if (patioInfo != null) {
            patio.setPatioInfo(patioInfo);

            currentFileName = _filename;

            for (FileLoadedListener listener : fileLoadedListeners)
                listener.onFileLoaded();

            patio.updatePatio();
            changesToSave = false;
        }
    }

    /**
     * Adds a HoveredWoodPieceListener to the list
     *
     * @param _hoveredWoodPieceListener to add to the list
     */
    public void addHoveredWoodPiecesListener(HoveredWoodPieceListener _hoveredWoodPieceListener) {
        hoveredWoodPieceListeners.add(_hoveredWoodPieceListener);
    }

    /**
     * Sets the hovered wood pieces
     *
     * @param _woodPiece The hovered wood pieces
     */
    public void setHoveredWoodPieces(WoodPiece _woodPiece) {
        for (HoveredWoodPieceListener hoveredWoodPieceListener : hoveredWoodPieceListeners) {
            hoveredWoodPieceListener.setHoveredWoodPiece(_woodPiece);
        }
    }

    public void drawHoveredWoodPieces(Graphics _graphics, WoodPiece _woodPiece, int _index) {
        patioDrawer.drawHoverWoodPiece(_graphics, _woodPiece, _index);
    }

    /**
     * Creates a new pationator project
     *
     * @param _frame The frame to show the potential save changes window dialog
     */
    public void create(JFrame _frame) {
        if (shouldShowSavePopup()) {
            SaveChangesWarning saveChangesWarning = new SaveChangesWarning(_frame);

            if (saveChangesWarning.open()) {
                if (getCurrentFileName() == null) {
                    SaveFileDialog saveFileDialog =
                            new SaveFileDialog(saveChangesWarning.getParentFrame(), this);

                    if (saveFileDialog.open())
                        saveFile(saveFileDialog.getNewPationatorFile(), saveFileDialog.getInfoToSave());
                } else
                    saveFile();
            }
        }
        generateDefaultPatio();
    }

    /**
     * Returns true if changes were done to the initial patio, else false
     *
     * @return true if changes were done to the initial patio, else false
     */
    public boolean shouldShowSavePopup() {
        return (changesToSave || currentFileName == null) && defaultPatioInfo != null &&
                !patio.getPatioInfo().equals(defaultPatioInfo);
    }

    /**
     * Returns the current file save name
     *
     * @return The current file save name
     */
    public String getCurrentFileName() {
        return currentFileName;
    }

    /**
     * Saves the current project as a .ptor file
     *
     * @param _filename  The file name
     * @param _patioInfo The patio info
     */
    public void saveFile(String _filename, PatioInfo _patioInfo) {
        boolean saveDone = FileHandler.saveFile(_filename, _patioInfo);

        if (saveDone) {
            currentFileName = _filename;
            changesToSave = false;

            for (FileSavedListener listener : fileSavedListeners)
                listener.onFileSaved();
        }
    }

    /**
     * Save the patio in a Pationator file.
     */
    public void saveFile() {
        saveFile(currentFileName, patio.getPatioInfo());
    }

    /**
     * Generates the cheapest patio as possible
     */
    public void generateDefaultPatio() {
        PatioInfo patioInfo = PatioInfoFactory.getInstance().createDefaultPatio();
        patio.setPatioInfo(patioInfo);

        currentFileName = null;
        changesToSave = false;

        updateTabsContent();
        defaultPatioInfo = new PatioInfo(patio.getPatioInfo());
        UndoManager.clearAll();

        for (DefaultPatioGeneratedListener listener : defaultPatioGeneratedListeners)
            listener.onDefaultPatioGenerated();
    }

    /**
     * Update tabs values.
     */
    public void updateTabsContent() {
        setInitPationator(false);
        changesToSave = true;

        for (ChangeMadeListener changeMadeListener : changeMadeListeners) {
            changeMadeListener.onChangeMade();
        }

        updatePatio();
    }

    /**
     * Update patio.
     */
    private void updatePatio() {
        patio.updatePatio();
    }

    /**
     * Quits the application
     *
     * @param _frame The frame to show the potential save changes window dialog
     */
    public void quit(JFrame _frame) {
        if (shouldShowSavePopup()) {
            SaveChangesWarning saveChangesWarning = new SaveChangesWarning(_frame);

            if (saveChangesWarning.open()) {
                if (getCurrentFileName() == null) {
                    SaveFileDialog saveFileDialog =
                            new SaveFileDialog(saveChangesWarning.getParentFrame(), this);

                    if (saveFileDialog.open())
                        saveFile(saveFileDialog.getNewPationatorFile(), saveFileDialog.getInfoToSave());
                } else
                    saveFile();
            }
        }
        System.exit(0);
    }

    /**
     * Creates a wood pieces file, with prices
     *
     * @param _pathAndExtension The path and extension of the list file to create
     */
    public void createPiecesFile(Pair<String, String> _pathAndExtension) {
        CreateFiles createFiles = new CreateFiles(this);
        createFiles.setModifierValue(false);
        createFiles.createPiecesFile(_pathAndExtension, piecesList, priceText);
    }

    /**
     * Undo an action (restore the previous patio)
     */
    public void undo() {
        for (UndoActivatedListener listener : undoActivatedListeners)
            listener.onUndoActivated();

        PatioInfo previousPatioState = UndoManager.undo(patio.getPatioInfo());
        if (previousPatioState != null)
            restorePatio(previousPatioState);
    }

    /**
     * Restores a specific patio info state
     *
     * @param _patioState A specific patio info state
     */
    public void restorePatio(PatioInfo _patioState) {
        patio.setPatioInfo(_patioState);
        updateTabsContent();
    }

    /**
     * Redo an action (restore the next patio)
     */
    public void redo() {
        for (RedoActivatedListener listener : redoActivatedListeners)
            listener.onRedoActivated();

        PatioInfo nextPatioState = UndoManager.redo(patio.getPatioInfo());
        if (nextPatioState != null)
            restorePatio(nextPatioState);
    }

    /**
     * Adds the current patio state to the saved patio states
     */
    public void addPatioState() {
        UndoManager.addPatioStateForUndo(patio.getPatioInfo());
    }

    /**
     * Adds a specific patio state to the saved patio states
     *
     * @param _patioState A specific patio state
     */
    public void addPatioState(PatioInfo _patioState) {
        UndoManager.addPatioStateForUndo(_patioState);
    }

    /**
     * Adds a redo activated listener
     *
     * @param _redoListener A redo activated listener
     */
    public void addRedoActivatedListener(RedoActivatedListener _redoListener) {
        redoActivatedListeners.add(_redoListener);
    }

    /**
     * Adds an undo activated listener
     *
     * @param _undoListener An undo activated listener
     */
    public void addUndoActivatedListener(UndoActivatedListener _undoListener) {
        undoActivatedListeners.add(_undoListener);
    }

    /**
     * Returns all the wood piece dimension possibilities according to a specific component type
     *
     * @param _componentType A specific component type
     * @return All the wood piece dimension possibilities according to a specific component type
     */
    public String[] getDimensionPosibility(ComponentType _componentType) {
        if (getMeasureUnit() == MeasureUnit.METRIC) {
            return LumberDimension.getPossibility(MeasureUnit.METRIC, _componentType);
        } else {
            return LumberDimension.getPossibility(getMeasureType(), _componentType);
        }
    }

    /**
     * Returns the current measure unit chosen by the user
     *
     * @return the current measure unit chosen by the user
     */
    public MeasureUnit getMeasureUnit() {
        if (patio.getPatioInfo().getMeasureUnit() == null) {
            return MeasureUnit.IMPERIAL;
        }
        return patio.getPatioInfo().getMeasureUnit();
    }

    /**
     * Sets the current measure unit
     *
     * @param _measureUnit the current measure unit to set
     */
    public void setMeasureUnit(MeasureUnit _measureUnit) {
        if (_measureUnit == MeasureUnit.METRIC) {
            patio.getPatioInfo().setMeasureType(MeasureType.REAL);
            patio.getPatioInfo().setRationalFormat("decimal");
        }

        patio.getPatioInfo().setMeasureUnit(_measureUnit);
    }

    /**
     * Returns the current measure type chosen by the user
     *
     * @return the current measure type chosen by the user
     */
    public MeasureType getMeasureType() {
        if (patio.getPatioInfo().getMeasureType() == null) {
            return MeasureType.NOMINAL;
        }

        return patio.getPatioInfo().getMeasureType();
    }

    /**
     * Returns a specific component dimensions
     *
     * @param _component a specific component
     * @return a specific component dimensions
     */
    public float[] getComponentDimension(ComponentType _component) {
        Dimensions dimensions;
        switch (_component) {
            case JOIST:
                dimensions = patio.getPatioInfo().getJoistDimensions();
                break;
            case BEAM:
                dimensions = patio.getPatioInfo().getBeamPlieDimensions();
                break;
            case POST:
                dimensions = patio.getPatioInfo().getPostDimensions();
                break;
            case COVERING_PLANK:
                dimensions = patio.getPatioInfo().getCoveringDimensions();
                break;
            default:
                return null;
        }

        float[] dimensionHeightWidth;

        if (getMeasureUnit() == MeasureUnit.METRIC) {
            dimensionHeightWidth = new float[]{
                    Conversion.getMillimeterFromNominal(dimensions.getNominalHeight()),
                    Conversion.getMillimeterFromNominal(dimensions.getNominalWidth())};
        } else {
            switch (getMeasureType()) {
                case REAL:
                    dimensionHeightWidth = new float[]{dimensions.getActualHeight(), dimensions.getActualWidth()};
                    break;
                case NOMINAL:
                default:
                    dimensionHeightWidth = new float[]{dimensions.getNominalHeight(),
                            dimensions.getNominalWidth()};
                    break;
            }
        }

        return dimensionHeightWidth;
    }

    /**
     * Returns the min joist span length
     *
     * @return the min joist span length
     */
    public float getMinJoistSpanLength() {
        return patio.getPatioInfo().getMinJoistSpanLength();
    }

    /**
     * Sets the current dimensions for a specific component
     *
     * @param _width     The width
     * @param _height    The height
     * @param _component The specific component
     */
    public void setComponentDimension(double _width, double _height, ComponentType _component) {
        float actualWidth, actualHeight;

        if (getMeasureUnit() == MeasureUnit.METRIC) {
            float nominalWidth = Conversion.getNominalFromMillimeter((float) _width);
            float nominalHeight = Conversion.getNominalFromMillimeter((float) _height);
            actualWidth = Conversion.getActualInchesFromNominal(nominalWidth);
            actualHeight = Conversion.getActualInchesFromNominal(nominalHeight);
        } else if (getMeasureType() == MeasureType.NOMINAL) {
            actualWidth = Conversion.getActualInchesFromNominal((float) _width);
            actualHeight = Conversion.getActualInchesFromNominal((float) _height);
        } else {
            actualWidth = (float) _width;
            actualHeight = (float) _height;
        }

        switch (_component) {
            case JOIST:
                patio.getPatioInfo().setJoistDimensions(new Dimensions(actualWidth, actualHeight,
                        patio.getPatioInfo().getJoistDimensions().getActualDepth()), true);
                break;
            case BEAM:
                patio.getPatioInfo().setBeamDimensions(new Dimensions(actualWidth, actualHeight,
                        patio.getPatioInfo().getBeamPlieDimensions().getActualDepth()), true);
                break;
            case POST:
                patio.getPatioInfo().setPostDimensions(new Dimensions(actualWidth, actualHeight,
                        patio.getPatioInfo().getPostDimensions().getActualDepth()), true);
                break;
            case COVERING_PLANK:
                patio.getPatioInfo().setCoveringDimensions(new Dimensions(actualWidth, actualHeight,
                        patio.getPatioInfo().getCoveringDimensions().getActualDepth()), true);
                break;
            default:
                break;
        }
    }

    /**
     * Draws the patio with its components and the grid
     *
     * @param _graphics        The graphics object to use for drawing
     * @param _inchUnitInPixel The ratio of pixels per inches
     * @param _isImageExport   True if the drawing is for an image exporte, else false
     */
    public void drawPatio(Graphics _graphics, int _inchUnitInPixel, boolean _isImageExport) {
        drawPatio(_graphics, _inchUnitInPixel, _isImageExport, 0);
    }

    /**
     * Draws the patio with its components and the grid
     *
     * @param _graphics        The graphics object to use for drawing
     * @param _inchUnitInPixel The ratio of pixels per inches
     * @param _isImageExport   True if the drawing is for an image exporte, else false
     * @param _index           The lowest Z index possible
     */
    public void drawPatio(Graphics _graphics, int _inchUnitInPixel, boolean _isImageExport, int _index) {
        patioDrawer.draw(_graphics, _inchUnitInPixel, _isImageExport, _index);
    }

    /**
     * Obtain a text with a numerical value according to the designated unit of measure.
     *
     * @param _value Numeric value to convert.
     * @return Text representing the numeric value.
     */
    public String getValueWithMeasureUnit(float _value) {
        switch (getMeasureUnit()) {
            case METRIC:
                DecimalFormat decimalFormat = new DecimalFormat("0.0###");
                return decimalFormat.format(_value * 2.54) + LocaleText.getString("CENTIMETRE_SUFFIX");
            case IMPERIAL:
                switch (getRationalFormat()) {
                    case "decimal":
                        return new Fraction(_value).toFloat() + "\"";
                    case "fraction":
                    default:
                        return new Fraction(_value).toString() + "\"";
                }
            default:
                return LocaleText.getString("UNDEFINED");
        }
    }

    /**
     * Get the current rationnal value string format.
     *
     * @return 'decimal' or 'fraction'.
     */
    public String getRationalFormat() {
        return patio.getPatioInfo().getRationalFormat();
    }

    /**
     * Set the current rational value string format.
     *
     * @param _rationalFormat 'decimal' or 'fraction'.
     */
    public void setRationalFormat(String _rationalFormat) {
        if (getMeasureUnit() != MeasureUnit.METRIC) {
            patio.getPatioInfo().setRationalFormat(_rationalFormat);
        }
    }

    /**
     * Draws the patio with its components and the grid
     *
     * @param _graphics        The graphics object to use for drawing
     * @param _inchUnitInPixel The ratio of pixels per inches
     * @param _index           The lowest Z index possible
     */
    public void drawPatio(Graphics _graphics, int _inchUnitInPixel, int _index) {
        patioDrawer.draw(_graphics, _inchUnitInPixel, false, _index);
    }

    /**
     * Sets a new price for a specific lumber dimensions
     *
     * @param _lumberDimension a specific lumber dimensions
     * @param _newValue        new price
     */
    public void setLumberDimensionPrice(String _lumberDimension, float _newValue) {
        patio.getPatioInfo().setLumberPricePerDimensions(_lumberDimension, _newValue);
    }

    /**
     * Gets the price for a specific lumber dimensions
     *
     * @param _lumberDimension a specific lumber dimensions
     * @return the price for a specific lumber dimensions
     */
    public float getLumberDimensionPrice(String _lumberDimension) {
        return patio.getPatioInfo().getLumberPricePerDimensions(_lumberDimension);
    }

    /**
     * Returns the short file name
     *
     * @return the short file name
     */
    public String getShortFileName() {
        Path path = Paths.get(currentFileName);
        return path.getFileName().toString();
    }

    /**
     * Get hidden edge visibility on the patio drawing.
     *
     * @return True if the hidden edge should be visible.
     */
    public boolean getHiddenBorderVisibility() {
        return patio.getPatioInfo().getHiddenBorderVisibility();
    }

    /**
     * Set hidden edge visibility on the patio drawing.
     *
     * @param _selected True if the hidden edge should be visible.
     */
    public void setHiddenBorderVisibility(boolean _selected) {
        patio.getPatioInfo().setHiddenBorderVisibility(_selected);
    }

    /**
     * Get arrow visibility on the patio drawing.
     *
     * @return True if arrow should be visible.
     */
    public boolean getArrowVisibility() {
        return arrowVisibility;
    }

    /**
     * Set arrow visibility on the patio drawing.
     *
     * @param _isVisible True if arrow should be visible.
     */
    public void setArrowVisibility(boolean _isVisible) {
        arrowVisibility = _isVisible;
    }

    /**
     * Get current post height in inches.
     *
     * @return Post height in inches.
     */
    public float getPostHeight() {
        return patio.getPatioInfo().getPostDimensions().getActualDepth();
    }

    /**
     * Set the post height in inches.
     *
     * @param _postHeight Post height in inches.
     */
    public void setPostHeight(double _postHeight) {
        float width = patio.getPatioInfo().getPostDimensions().getActualWidth();
        float height = patio.getPatioInfo().getPostDimensions().getActualHeight();
        float depth = (float) _postHeight;

        patio.getPatioInfo().setPostDimensions(new Dimensions(width, height, depth), true);
    }

    /**
     * Get minimum post height in inches.
     *
     * @return Minimum post height.
     */
    public float getMinPostHeight() {
        return patio.getPatioInfo().getMinPostDepth();
    }

    /**
     * Get current post spacing in inches.
     *
     * @return Post spacing in inches.
     */
    public float getPostSpacing() {
        return patio.getPatioInfo().getPostSpacing();
    }

    /**
     * Set the post spacing value.
     *
     * @param _postSpacing Post spacing in inches.
     */
    public void setPostSpacing(double _postSpacing) {
        patio.getPatioInfo().setPostSpacing((float) _postSpacing, true);
    }

    /**
     * Get the minimum post spacing value accepted.
     *
     * @return Minimum post spacing in inches.
     */
    public float getMinPostSpacing() {
        return patio.getPatioInfo().getMinPostSpacing();
    }

    /**
     * Know if pationator is initializing new project.
     *
     * @return True if pationator is loading value.
     */
    public boolean isInitPationator() {
        return initPationator;
    }

    /**
     * Set value when Pationator finish initializing.
     *
     * @param _initPationator True if pationator is loading value.
     */
    public void setInitPationator(boolean _initPationator) {
        initPationator = _initPationator;
    }

    /**
     * Get current view origin.
     *
     * @param _index Drawing panel index.
     * @return Vector2 with X and Y position of the origin.
     */
    public Vector2 getViewCenter(int _index) {
        return viewCenterWithIndex[_index];
    }

    /**
     * Set the user view at patio middle coordinate.
     */
    public void setCenterView(Point _screenMiddle, int _index) {
        Point patioCenter = getPatioCenter(_index);
        float zoomFactor = getZoomFactor(_index);

        int xPos = _screenMiddle.x - (int) (patioCenter.x * zoomFactor * 24);
        int yPos = _screenMiddle.y + (int) (patioCenter.y * zoomFactor * 24);

        setViewCenter(new Vector2(xPos, yPos), _index);
        updateTabsContent();
    }

    /**
     * Get current pation center according to the view.
     *
     * @param _index Drawing panel index.
     * @return Point with X and Y position of the center.
     */
    public Point getPatioCenter(int _index) {
        return getPatioCenter(getViewOrientation(_index));
    }

    /**
     * Get the current drawing panel scale factor.
     *
     * @return Scale factor value.
     */
    public float getZoomFactor(int _index) {
        return zoomFactorWithIndex[_index];
    }

    /**
     * Set view origin.
     *
     * @param _viewCenter Point with X and Y position of the origin.
     */
    public void setViewCenter(Vector2 _viewCenter, int _index) {
        viewCenterWithIndex[_index] = _viewCenter;
    }

    /**
     * Get current patio center according to the view.
     *
     * @return Point with X and Y position of the center.
     */
    public Point getPatioCenter(ViewOrientation _viewOrientation) {
        float patioLength, patioWidth, patioHeight;

        switch (_viewOrientation) {
            case TOP:
                patioLength = getPatioLength();
                patioWidth = getPatioWidth();
                return new Point((int) patioLength / 2, (int) patioWidth / 2);
            case SIDE:
                patioHeight = getPatioHeight();
                patioLength = getPatioLength();
                return new Point((int) patioLength / 2, (int) patioHeight / 2);
            case FACE:
                patioHeight = getPatioHeight();
                patioWidth = getPatioWidth();
                return new Point((int) patioWidth / 2, (int) patioHeight / 2);
            default:
                return new Point(0, 0);
        }
    }

    /**
     * Get the current view orientation of the main Patinator windows according to drawing panel index.
     *
     * @param _index Index of the drawing panel.
     * @return Current view.
     */
    public ViewOrientation getViewOrientation(int _index) {
        return patioDrawer.getViewOrientation(_index);
    }

    /**
     * Obtains the length of the patio in inches.
     *
     * @return Length in inches.
     */
    public float getPatioLength() {
        return patio.getPatioInfo().getPatioDimensions().getActualDepth();
    }

    /**
     * Changes the patio length.
     *
     * @param _patioLength Patio length in inches.
     */
    public void setPatioLength(double _patioLength) {
        float width = patio.getPatioInfo().getPatioDimensions().getActualWidth();
        float height = patio.getPatioInfo().getPatioDimensions().getActualHeight();
        float depth = (float) _patioLength;

        patio.getPatioInfo().setPatioDimensions(new Dimensions(width, height, depth), true);
    }

    /**
     * Obtains the width of the patio in inches.
     *
     * @return Width in inches.
     */
    public float getPatioWidth() {
        return patio.getPatioInfo().getPatioDimensions().getActualWidth();
    }

    /**
     * Changes the patio width.
     *
     * @param _patioWidth Patio width in inches.
     */
    public void setPatioWidth(double _patioWidth) {
        float width = (float) _patioWidth;
        float height = patio.getPatioInfo().getPatioDimensions().getActualHeight();
        float depth = patio.getPatioInfo().getPatioDimensions().getActualDepth();

        patio.getPatioInfo().setPatioDimensions(new Dimensions(width, height, depth), true);
    }

    /**
     * Obtains the height of the patio in inches. The height is taken from the ground to covering surface.
     *
     * @return Height in inches.
     */
    public float getPatioHeight() {
        return patio.getPatioInfo().getPatioDimensions().getActualHeight();
    }

    /**
     * Changes the patio height.
     *
     * @param _patioHeight Patio height in inches.
     */
    public void setPatioHeight(double _patioHeight) {
        float width = patio.getPatioInfo().getPatioDimensions().getActualWidth();
        float height = (float) _patioHeight;
        float depth = patio.getPatioInfo().getPatioDimensions().getActualDepth();

        patio.getPatioInfo().setPatioDimensions(new Dimensions(width, height, depth), true);
    }

    /**
     * Get the current view orientation of the main Patinator windows.
     *
     * @return Current view.
     */
    public ViewOrientation getViewOrientation() {
        return patioDrawer.getViewOrientation(0);
    }

    /**
     * Set the current view orientation of the main Patinator windows.
     *
     * @param _viewOrientation Desired view orientation.
     */
    public void setViewOrientation(ViewOrientation _viewOrientation) {
        setViewOrientation(_viewOrientation, 0);
    }

    /**
     * Set the current view orientation of the main Patinator windows.
     *
     * @param _viewOrientation Desired view orientation.
     * @param _index           Index of the drawing panel.
     */
    public void setViewOrientation(ViewOrientation _viewOrientation, int _index) {
        patioDrawer.setViewOrientation(_viewOrientation, _index);
    }

    /**
     * Set the current drawing panel scale factor.
     *
     * @param _factor Scale factor use.
     */
    public void setZoomFactor(float _factor) {
        setZoomFactor(_factor, 0);
    }

    /**
     * Set the specified drawing panel scale factor.
     *
     * @param _factor Scale factor use.
     * @param _index  Drawing panel index.
     */
    public void setZoomFactor(float _factor, int _index) {
        zoomFactorWithIndex[_index] = _factor;
    }

    /**
     * Set pationator in optimal mode.
     */
    public void setIsOptimalMode() {
        patio.setAsInitialPatioInfo();
    }

    /**
     * Generate an optimal patio; Cheapest possible with the specified parameter.
     */
    public void generateOptimalPatio() {
        patio.generateOptimalPatio();
    }

    /**
     * Draw an information panel on the specified graphics object from
     *
     * @param _graphics2D               Graphics object to draw the panel on.
     * @param _informationPanelPosition Top left corner of the panel.
     * @param _viewOrientation          Desired view of the patio.
     */
    public void drawInformationPanel(
            Graphics2D _graphics2D, Point _informationPanelPosition, ViewOrientation _viewOrientation) {
        patioDrawer.drawInformationPanel(_graphics2D, _informationPanelPosition, _viewOrientation);
    }

    /**
     * Get an image of the patio according to a view orientation.
     *
     * @param _imageWidth        Desired width of the image.
     * @param _imageHeight       Desired height of the image.
     * @param _bufferedImageType Buffered image type.
     * @param _viewOrientation   View of the patio.
     * @return Buffered image of specified size with the patio drawing from the specified view.
     */
    public BufferedImage getPatioViewImage(
            int _imageWidth, int _imageHeight, int _bufferedImageType, ViewOrientation _viewOrientation) {
        CreateFiles createFiles = new CreateFiles(this);

        return createFiles.getPatioViewImage(_imageWidth, _imageHeight, _bufferedImageType, _viewOrientation);
    }

    /**
     * Set new price text.
     *
     * @param _priceText Price text.
     */
    public void setPriceText(String _priceText) {
        this.priceText = _priceText;
    }

    /**
     * Notify an export.
     *
     * @param _fileFormat File format.
     * @param _path       File path.
     */
    public void notifyExport(String _fileFormat, String _path) {
        for (ExportDoneListener listener : exportDoneListeners) {
            listener.onExportDone(_fileFormat, _path);
        }
    }
}