package gui;
import com.intellij.ui.JBColor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class GameVisualizer extends JPanel {
    private final Robot robotModel;

    GameVisualizer(Robot robotModel) {
        this.robotModel = robotModel;
        robotModel.addObserver((o, arg) -> onRedrawEvent());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    robotModel.setTargetPosition(e.getPoint());
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    robotModel.addSquare(e.getPoint());
                } else if(SwingUtilities.isMiddleMouseButton(e)){
                    robotModel.addCircle(e.getPoint());
                }
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    private void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, robotModel.getDirection());
        drawTarget(g2d, robotModel.getTargetPositionX(), robotModel.getTargetPositionY());
        drawSquare(g2d);
        drawCircle(g2d);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawCircle(Graphics2D g) {
        for (Circle circle : robotModel.getCircles()) {
            g.setColor(Color.ORANGE);
            g.fillOval((int)circle.getCenterX(), (int)circle.getCenterY(), (int)circle.getRadius(), (int)circle.getRadius());
            g.setColor(Color.BLACK);
            g.drawOval((int)circle.getCenterX(), (int)circle.getCenterY(), (int)circle.getRadius(), (int)circle.getRadius());
        }
    }

    private void drawSquare(Graphics2D g) {
        for (Square square : robotModel.getSquares()) {
            Rectangle rect = square;
            g.setColor(Color.ORANGE);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
            g.setColor(Color.BLACK);
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
        }
    }

    private void drawRobot(Graphics2D g, double direction) {
        int robotCenterX = round(robotModel.getPositionX());
        int robotCenterY = round(robotModel.getPositionY());
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(JBColor.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(JBColor.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(JBColor.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(JBColor.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(JBColor.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(JBColor.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}