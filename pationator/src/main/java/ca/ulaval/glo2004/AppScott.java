package ca.ulaval.glo2004;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ca.ulaval.glo2004.patio.PatioController;
import ca.ulaval.glo2004.view.Tabs;
import ca.ulaval.glo2004.view.StatusBar;

public class AppScott {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
//
//        Menu menu = new Menu(frame);
//        menu.getStandardMenu();

        JPanel drawplace = new JPanel();
        Tabs tabs = new Tabs();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, drawplace, tabs);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.9);

        frame.add(splitPane);

        StatusBar statusBar = new StatusBar(frame, new PatioController());
        statusBar.setText("Info sur la dernière action");

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setTitle("Pationator");
    }
}

