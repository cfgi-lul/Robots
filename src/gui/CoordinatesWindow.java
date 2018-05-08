package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.text.DecimalFormat;

public class CoordinatesWindow extends JInternalFrame implements Observer {
    private TextField m_coordinatesContent;
    private DecimalFormat format = new DecimalFormat("##.00");
    CoordinatesWindow() {
        super("Координаты робота", true, true, true, true);
        m_coordinatesContent = new TextField("");
        m_coordinatesContent.setSize(463, 58);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_coordinatesContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void update(Observable o, Object arg) {
        Robot robotModel = (Robot) arg;
        m_coordinatesContent.setText("X: " + format.format(robotModel.getPositionX())
                + "\tY: " + format.format(robotModel.getPositionY())
                + "\tTarget X: " + robotModel.getTargetPositionX()
                + "\tTarget Y: " + robotModel.getTargetPositionY()
                + "\tDirection: " + format.format(robotModel.getDirection()));
    }
}