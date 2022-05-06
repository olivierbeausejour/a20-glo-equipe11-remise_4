package ca.ulaval.glo2004.utils;

import ca.ulaval.glo2004.patio.PatioInfo;

import java.io.*;

public class FileHandler {
    public static boolean saveFile(String _filename, PatioInfo _patioInfo) {
        try {
            FileOutputStream fileData = new FileOutputStream(_filename);
            ObjectOutputStream fileSaver = new ObjectOutputStream(fileData);

            fileSaver.writeObject(_patioInfo);
            fileSaver.close();
            fileData.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static PatioInfo openFile(String _filename) {
        try {
            FileInputStream fileData = new FileInputStream(_filename);
            ObjectInputStream fileReader = new ObjectInputStream(fileData);

            PatioInfo patioInfo = (PatioInfo) fileReader.readObject();
            fileReader.close();
            fileData.close();

            return patioInfo;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
