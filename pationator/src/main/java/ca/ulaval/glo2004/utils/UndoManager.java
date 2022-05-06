package ca.ulaval.glo2004.utils;

import ca.ulaval.glo2004.patio.PatioInfo;

import java.io.*;
import java.util.Stack;

public class UndoManager {
    private static Stack<ByteArrayOutputStream> previousPatioStates = new Stack<>();
    private static Stack<ByteArrayOutputStream> nextPatioStates = new Stack<>();

    public static void addPatioStateForUndo(PatioInfo _previousPatioState) {
        nextPatioStates.clear();  // Because there is a new action in the sequence, so we cannot redo previous ones.

        ByteArrayOutputStream patioInfoState = getPatioState(_previousPatioState);
        if (patioInfoState != null)
            previousPatioStates.push(patioInfoState);
    }

    public static void clearAll() {
        previousPatioStates.clear();
        nextPatioStates.clear();
    }

    public static PatioInfo undo(PatioInfo _currentState) {
        if (!previousPatioStates.empty()) {
            PatioInfo previousPatioState = readPatioState(previousPatioStates.peek());
            nextPatioStates.push(getPatioState(_currentState));
            previousPatioStates.pop();
            return previousPatioState;
        }

        return null;
    }

    public static boolean canStillUndo() {
        return !previousPatioStates.empty();
    }

    public static boolean canStillRedo() {
        return !nextPatioStates.empty();
    }

    public static PatioInfo redo(PatioInfo _currentState) {
        if (!nextPatioStates.empty()) {
            PatioInfo nextPatioState = readPatioState(nextPatioStates.peek());
            previousPatioStates.push(getPatioState(_currentState));
            nextPatioStates.pop();
            return nextPatioState;
        }

        return null;
    }

    private static ByteArrayOutputStream getPatioState(PatioInfo _patioInfo) {
        try {
            ByteArrayOutputStream patioInfoState = new ByteArrayOutputStream();
            ObjectOutputStream patioInfoSerializer = new ObjectOutputStream(patioInfoState);

            patioInfoSerializer.writeObject(_patioInfo);
            patioInfoSerializer.close();
            patioInfoState.close();

            return patioInfoState;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PatioInfo readPatioState(ByteArrayOutputStream _savedPatioState) {
        try {
            ByteArrayInputStream patioStateStream = new ByteArrayInputStream(_savedPatioState.toByteArray());
            ObjectInputStream patioStateReader = new ObjectInputStream(patioStateStream);

            PatioInfo restoredPatioState = (PatioInfo) patioStateReader.readObject();
            patioStateReader.close();
            patioStateStream.close();

            return restoredPatioState;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
