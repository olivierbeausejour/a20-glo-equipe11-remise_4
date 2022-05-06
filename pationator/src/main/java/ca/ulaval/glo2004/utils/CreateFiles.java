package ca.ulaval.glo2004.utils;

import ca.ulaval.glo2004.patio.Component;
import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.patio.WoodPiece;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

// https://danbscott.ghost.io/writing-an-stl-file-from-scratch/

/**
 * File creator.
 */
public class CreateFiles {
    private final PatioController patioController;
    private boolean isModified;
    private int inchUnitInPixel = 24;

    /**
     * Create a file creator.
     */
    public CreateFiles(PatioController _patioController) {
        patioController = _patioController;
    }

    /**
     * Determine if the file is export as standard or if it apply modification.
     * <p>
     *     <ul>
     *         <li>STL file export with only visible part if the modifier is true.
     *         <li>Image export with all view in one file if the modifier is true.
     *     </ul>
     * <p>
     *
     * @param modifierValue True if the file is create using an alternate behavior.
     */
    public void setModifierValue(boolean modifierValue) {
        isModified = modifierValue;
    }

    public void createPiecesFile(Pair<String, String> _pathAndExtension, String _piecesList, String _prices) {
        String path = _pathAndExtension.first;
        String extensionString = _pathAndExtension.second;

        if (path.endsWith("/")) {
            path += "default." + extensionString;
        } else if (!path.endsWith("." + extensionString)) {
            path += "." + extensionString;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            OutputStreamWriter fileWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            BufferedWriter textWriter = new BufferedWriter(fileWriter);

            textWriter.write("==========================");
            textWriter.write(Objects.requireNonNull(LocaleText.getString("MATERIAL_LIST_TITLE")));
            textWriter.write("==========================");
            textWriter.newLine();
            textWriter.write(_piecesList);
            textWriter.newLine();
            textWriter.newLine();
            textWriter.write("==========================");
            textWriter.write(Objects.requireNonNull(LocaleText.getString("PATIO_PRICE_TITLE")));
            textWriter.write("==========================");
            textWriter.newLine();
            textWriter.write(_prices);

            textWriter.close();
            fileWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a Ascii STL file.
     *
     * @param _path       Path and name for the file writing.
     * @param _components Every component to insert in STL file.
     */
    private void createAsciiSTL(String _path, ArrayList<Component> _components) throws IOException {
        int i = 0;

        StringBuilder stringBuilder = new StringBuilder();

        for (Component component : _components) {
            if (component.isVisible() || isModified) {
                for (WoodPiece woodPiece : component.getWoodPieces()) {
                    ArrayList<String> lines = getStlAsciiFileLines(woodPiece, "woodPiece_" + ++i);

                    for (String line : lines) {
                        stringBuilder.append(line);
                        stringBuilder.append("\n");
                    }
                }
            }
        }

        FileWriter myWriter = new FileWriter(_path);
        myWriter.write(stringBuilder.toString());
        myWriter.close();
    }

    /**
     * STL Ascii file structuration.
     *
     * @param _woodPiece     Component to insert in the file.
     * @param _componentName Unique component name.
     * @return ArrayList of each file line.
     */
    private ArrayList<String> getStlAsciiFileLines(WoodPiece _woodPiece, String _componentName) {
        ArrayList<String> lines = new ArrayList<>();
        float xPos, yPos, zPos, height, depth, width;

        Dimensions fixedOrientation =
                Component.getFixedOrientation(_woodPiece.getDimensions(), _woodPiece.getOrientation());

        height = fixedOrientation.getActualHeight();
        depth = fixedOrientation.getActualDepth();
        width = fixedOrientation.getActualWidth();

        WoodPiece fixedPosition = Component.getFixedPosition(_woodPiece, fixedOrientation);

        xPos = fixedPosition.getCentralPosition().x - (width / 2);
        yPos = fixedPosition.getCentralPosition().y - (height / 2);
        zPos = fixedPosition.getCentralPosition().z - (depth / 2);

        lines.add("solid " + _componentName);
        lines.add("\tfacet normal 0.0 0.0 1.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + yPos + " " + zPos);
        lines.add("\t\tendloop");
        lines.add("\tendfacet");
        lines.add("\tfacet normal 0.0 0.0 1.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + xPos + " " + (yPos + height) + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + zPos);
        lines.add("\t\tendloop");
        lines.add("\tendfacet");

        lines.add("\tfacet normal 0.0 0.0 -1.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + (xPos + width) + " " + yPos + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\tendloop");
        lines.add("\tendfacet");
        lines.add("\tfacet normal 0.0 0.0 -1.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + xPos + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\tendloop");
        lines.add("\tendfacet");

        lines.add("\tfacet normal 0.0 1.0 0.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + yPos + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + (zPos + depth));
        lines.add("\t\tendloop");
        lines.add("\tendfacet");
        lines.add("\tfacet normal 0.0 1.0 0.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + yPos + " " + (zPos + depth));
        lines.add("\t\tendloop");
        lines.add("\tendfacet");

        lines.add("\tfacet normal 0.0 -1.0 0.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + (yPos + height) + " " + zPos);
        lines.add("\t\t\tvertex " + xPos + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\tendloop");
        lines.add("\tendfacet");
        lines.add("\tfacet normal 0.0 -1.0 0.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + (yPos + height) + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + zPos);
        lines.add("\t\tendloop");
        lines.add("\tendfacet");

        lines.add("\tfacet normal 1.0 0.0 0.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + xPos + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\tendloop");
        lines.add("\tendfacet");
        lines.add("\tfacet normal 1.0 0.0 0.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + xPos + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + xPos + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + xPos + " " + (yPos + height) + " " + zPos);
        lines.add("\t\tendloop");
        lines.add("\tendfacet");

        lines.add("\tfacet normal -1.0 0.0 0.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + (xPos + width) + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\t\tvertex " + (xPos + width) + " " + yPos + " " + (zPos + depth));
        lines.add("\t\tendloop");
        lines.add("\tendfacet");
        lines.add("\tfacet normal -1.0 0.0 0.0");
        lines.add("\t\touter loop");
        lines.add("\t\t\tvertex " + (xPos + width) + " " + yPos + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + zPos);
        lines.add("\t\t\tvertex " + (xPos + width) + " " + (yPos + height) + " " + (zPos + depth));
        lines.add("\t\tendloop");
        lines.add("\tendfacet");

        lines.add("endsolid");

        return lines;
    }

    /**
     * Extract component values to write them to the STL file format.
     *
     * @param _pathAndExtension Path and name for the file writing.
     * @param _components       Every component to insert in STL file.
     */
    public void createSTL(Pair<String, String> _pathAndExtension, ArrayList<Component> _components) {
        try {
            String path = _pathAndExtension.first;

            if (_pathAndExtension.first.endsWith("/")) {
                path = _pathAndExtension.first + "default.stl";
            } else if (!_pathAndExtension.first.endsWith(".stl")) {
                path = _pathAndExtension + "." + _pathAndExtension.second;
            }

            createBinarySTL(path, _components);
//            createAsciiSTL(path, _components);

            patioController.notifyExport("STL", path);
        } catch (IOException ignored) {
        }
    }

    /**
     * Write a binary STL file.
     *
     * @param _path       Path and name for the file writing.
     * @param _components Every component to insert in STL file.
     */
    private void createBinarySTL(String _path, ArrayList<Component> _components) throws IOException {
        new RandomAccessFile(_path, "rw").setLength(0);

        try (FileChannel stlFile = new RandomAccessFile(_path, "rw").getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(10000).order(ByteOrder.LITTLE_ENDIAN);

            int triangleQuantity = 0;
            for (Component component : _components) {
                if (component.isVisible() || isModified) {
                    triangleQuantity += component.getWoodPieces().size() * 12;
                }
            }

            byteBuffer.put(new byte[80]);
            byteBuffer.putInt(triangleQuantity);

            for (Component component : _components) {
                if (component.isVisible() || isModified) {
                    for (WoodPiece woodPiece : component.getWoodPieces()) {
                        float xPos, yPos, zPos, height, depth, width;

                        Dimensions fixedOrientation = Component.getFixedOrientation(
                                woodPiece.getDimensions(), woodPiece.getOrientation());

                        height = fixedOrientation.getActualHeight();
                        depth = fixedOrientation.getActualDepth();
                        width = fixedOrientation.getActualWidth();

                        WoodPiece fixedPosition = Component.getFixedPosition(woodPiece, fixedOrientation);

                        xPos = fixedPosition.getCentralPosition().x - (width / 2);
                        yPos = fixedPosition.getCentralPosition().y - (height / 2);
                        zPos = fixedPosition.getCentralPosition().z - (depth / 2);

                        Color color = component.getColor();
                        int red = (color.getRed() / 255) * 15;
                        int green = (color.getGreen() / 255) * 15;
                        int blue = (color.getBlue() / 255) * 15;
                        int rgb = (red << 10) | (green << 5) | blue;

                        byteBuffer.putFloat(0).putFloat(0).putFloat(1);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();
                        byteBuffer.putFloat(0).putFloat(0).putFloat(1);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat(xPos).putFloat((yPos + height)).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat(zPos);
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();

                        byteBuffer.putFloat(0).putFloat(0).putFloat(-1);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat((zPos + depth));
                        byteBuffer.putFloat((xPos + width)).putFloat(yPos).putFloat((zPos + depth));
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();
                        byteBuffer.putFloat(0).putFloat(0).putFloat(-1);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat((zPos + depth));
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putFloat(xPos).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();

                        byteBuffer.putFloat(0).putFloat(1).putFloat(0);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat(yPos).putFloat((zPos + depth));
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat((zPos + depth));
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();
                        byteBuffer.putFloat(0).putFloat(1).putFloat(0);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat(yPos).putFloat((zPos + depth));
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();

                        byteBuffer.putFloat(0).putFloat(-1).putFloat(0);
                        byteBuffer.putFloat(xPos).putFloat((yPos + height)).putFloat(zPos);
                        byteBuffer.putFloat(xPos).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();
                        byteBuffer.putFloat(0).putFloat(-1).putFloat(0);
                        byteBuffer.putFloat(xPos).putFloat((yPos + height)).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat(zPos);
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();

                        byteBuffer.putFloat(1).putFloat(0).putFloat(0);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat((zPos + depth));
                        byteBuffer.putFloat(xPos).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();
                        byteBuffer.putFloat(1).putFloat(0).putFloat(0);
                        byteBuffer.putFloat(xPos).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat(xPos).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putFloat(xPos).putFloat((yPos + height)).putFloat(zPos);
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();

                        byteBuffer.putFloat(-1).putFloat(0).putFloat(0);
                        byteBuffer.putFloat((xPos + width)).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putFloat((xPos + width)).putFloat(yPos).putFloat((zPos + depth));
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();
                        byteBuffer.putFloat(-1).putFloat(0).putFloat(0);
                        byteBuffer.putFloat((xPos + width)).putFloat(yPos).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat(zPos);
                        byteBuffer.putFloat((xPos + width)).putFloat((yPos + height)).putFloat((zPos + depth));
                        byteBuffer.putShort((short) rgb);
                        byteBuffer.flip();
                        stlFile.write(byteBuffer);
                        byteBuffer.clear();
                    }
                }
            }

            isModified = false;
        } catch (FileNotFoundException _fileNotFoundException) {
            _fileNotFoundException.printStackTrace();
        }
    }

    /**
     * Export an image in JPG or PNG according to user choice.
     *
     * @param _pathAndExtension Pair of full file path and used extension.
     */
    public void createImage(Pair<String, String> _pathAndExtension) {
        int imageWidth = 2400;
        int imageHeight = 1600;

        try {
            String extensionString, fileFormat = _pathAndExtension.second;

            int bufferedImageType;
            if (fileFormat.equals("png")) {
                extensionString = "png";
                bufferedImageType = BufferedImage.TYPE_INT_ARGB;
            } else {
                extensionString = "jpg";
                bufferedImageType = BufferedImage.TYPE_INT_RGB;
            }

            String path = _pathAndExtension.first;
            if (path.endsWith("/")) {
                path += "default." + extensionString;
            } else if (!path.endsWith("." + extensionString)) {
                path += "." + extensionString;
            }

            File file = new File(path);
            BufferedImage bufferedImage;

            if (!isModified) {
                bufferedImage =
                        getPatioViewImage(
                                imageWidth, imageHeight, bufferedImageType, patioController.getViewOrientation());
            } else {
                BufferedImage faceView =
                        getPatioViewImage(imageWidth, imageHeight, bufferedImageType, ViewOrientation.FACE);
                BufferedImage sideView =
                        getPatioViewImage(imageWidth, imageHeight, bufferedImageType, ViewOrientation.SIDE);
                BufferedImage topView =
                        getPatioViewImage(imageWidth, imageHeight, bufferedImageType, ViewOrientation.TOP);
                BufferedImage blankView = getBlankViewImage(imageWidth, imageHeight, bufferedImageType);

                BufferedImage sideBySideTop = joinImages(sideView, faceView, JoinOrientation.HORIZONTAL);
                BufferedImage sideBySideBottom = joinImages(topView, blankView, JoinOrientation.HORIZONTAL);
                bufferedImage = joinImages(sideBySideTop, sideBySideBottom, JoinOrientation.VERTICAL);
            }

            ImageIO.write(bufferedImage, extensionString, file);

            patioController.notifyExport(fileFormat, path);
        } catch (Exception ignored) {
        }
    }

    /**
     * Get an image of the patio from a designated view.
     *
     * @param _imageWidth        Image width in pixel.
     * @param _imageHeight       Image height in pixel.
     * @param _bufferedImageType Type of image (with Alpha channel or not).
     * @param _viewOrientation   Desired patio view
     * @return Buffered image of the patio.
     */
    public BufferedImage getPatioViewImage(
            int _imageWidth, int _imageHeight, int _bufferedImageType, ViewOrientation _viewOrientation) {
        ViewOrientation oldViewOrientation = patioController.getViewOrientation();

        boolean sameView = oldViewOrientation.equals(_viewOrientation);
        if (!sameView) {
            patioController.setViewOrientation(_viewOrientation);
        }

        int drawingWidth, drawingHeight;
        final int margin = 100 * inchUnitInPixel;

        switch (_viewOrientation) {
            case SIDE:
                drawingWidth = (int) patioController.getPatioLength() * inchUnitInPixel + margin;
                drawingHeight = (int) patioController.getPatioHeight() * inchUnitInPixel + margin;
                break;
            case TOP:
                drawingWidth = (int) patioController.getPatioLength() * inchUnitInPixel + margin;
                drawingHeight = (int) patioController.getPatioWidth() * inchUnitInPixel + margin;
                break;
            case FACE:
                drawingWidth = (int) patioController.getPatioWidth() * inchUnitInPixel + margin;
                drawingHeight = (int) patioController.getPatioHeight() * inchUnitInPixel + margin;
                break;
            default:
                drawingWidth = (int) Math.max(patioController.getPatioLength(),
                        Math.max(patioController.getPatioHeight(), patioController.getPatioWidth())
                ) * inchUnitInPixel + margin;
                drawingHeight = (int) Math.max(patioController.getPatioLength(),
                        Math.max(patioController.getPatioHeight(), patioController.getPatioWidth())
                ) * inchUnitInPixel + margin;
                break;
        }

        float scale, scaleX = 1, scaleY = 1;
        if (drawingWidth > _imageWidth) {
            scaleX = (float) _imageWidth / (float) drawingWidth;
        }

        if (drawingHeight > _imageHeight) {
            scaleY = (float) _imageHeight / (float) drawingHeight;
        }

        scale = Math.min(scaleX, scaleY);
        inchUnitInPixel *= scale;

        Point patioCenter = patioController.getPatioCenter(_viewOrientation);
        BufferedImage bufferedImage = new BufferedImage(_imageWidth, _imageHeight, _bufferedImageType);

        double translateValueX = ((float) _imageWidth) / 2 - patioCenter.getX() * inchUnitInPixel;
        double translateValueY = ((float) _imageHeight) / 2 + patioCenter.getY() * inchUnitInPixel;

        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.translate(translateValueX, translateValueY);

        Color backgroundColor = javax.swing.UIManager.getDefaults().getColor("TitlePane.background");

        graphics2D.setColor(backgroundColor);
        graphics2D.fillRect(-(_imageWidth * 2), -(_imageHeight * 2), _imageWidth * 4, _imageHeight * 4);

        patioController.drawPatio(graphics2D, inchUnitInPixel, true);
        patioController.drawInformationPanel(graphics2D, new Point(-(int) translateValueX,
                -(int) translateValueY + _imageHeight), _viewOrientation);

        if (!sameView) {
            patioController.setViewOrientation(oldViewOrientation);
        }

        return bufferedImage;
    }

    /**
     * Get a blank image with the same background color as the patio view. Can be use as a filling image for a complete
     * patio image export.
     *
     * @param _imageWidth        Image width in pixel.
     * @param _imageHeight       Image height in pixel.
     * @param _bufferedImageType Type of image (with Alpha channel or not).
     * @return Blank colored buffered image.
     */
    private BufferedImage getBlankViewImage(int _imageWidth, int _imageHeight, int _bufferedImageType) {
        BufferedImage bufferedImage = new BufferedImage(_imageWidth, _imageHeight, _bufferedImageType);
        Graphics2D graphics2D = bufferedImage.createGraphics();

        Color backgroundColor = javax.swing.UIManager.getDefaults().getColor("TitlePane.background");
        graphics2D.setColor(backgroundColor);
        graphics2D.fillRect(-(_imageWidth * 2), -(_imageHeight * 2), _imageWidth * 4, _imageHeight * 4);

        return bufferedImage;
    }

    /**
     * Join two buffered image together side by side or one over the others.
     *
     * @param _image1      Image on top or on the left.
     * @param _image2      Image at bottom or on the right.
     * @param _orientation Determine if image are side by side or one over the other.
     * @return BufferedImage with two image together.
     */
    private BufferedImage joinImages(BufferedImage _image1, BufferedImage _image2, JoinOrientation _orientation) {
        int padding = 3;
        int width, height;

        if (_orientation == JoinOrientation.HORIZONTAL) {
            width = _image1.getWidth() + padding + _image2.getWidth();
            height = Math.max(_image1.getHeight(), _image2.getHeight());
        } else {
            width = Math.max(_image1.getWidth(), _image2.getWidth());
            height = _image1.getHeight() + padding + _image2.getHeight();
        }

        BufferedImage joinImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = joinImage.createGraphics();

        Color backgroundColor = javax.swing.UIManager.getDefaults().getColor("MenuBar.borderColor");
        graphics2D.setPaint(backgroundColor);
        graphics2D.fillRect(0, 0, width, height);

        if (_orientation == JoinOrientation.HORIZONTAL) {
            graphics2D.drawImage(_image1, null, 0, 0);
            graphics2D.drawImage(_image2, null, padding + _image1.getWidth(), 0);
        } else {
            graphics2D.drawImage(_image1, null, 0, 0);
            graphics2D.drawImage(_image2, null, 0, _image1.getHeight() + padding);
        }

        return joinImage;
    }

    /**
     * Enum with different join orientation type.
     */
    private enum JoinOrientation {
        HORIZONTAL,
        VERTICAL
    }
}
