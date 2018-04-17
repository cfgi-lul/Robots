package gui;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.*;

public class RobotsProgram
{
    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
//        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      SwingUtilities.invokeLater(() -> {
        MainApplicationFrame frame = null;
        try {
          frame = new MainApplicationFrame();
        } catch (IOException e) {
          e.printStackTrace();
        }
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
      });
    }}
