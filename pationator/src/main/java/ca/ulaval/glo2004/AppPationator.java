package ca.ulaval.glo2004;

import javax.swing.*;

import ca.ulaval.glo2004.view.*;
import com.formdev.flatlaf.FlatDarculaLaf;

public class AppPationator {
    public static void main(String[] args) {
        FlatDarculaLaf.install();
        JFrame.setDefaultLookAndFeelDecorated(true);

        String filename = "";
        if (args.length > 0)
            filename = args[0];

        new PationatorWindow(filename);
    }
}
