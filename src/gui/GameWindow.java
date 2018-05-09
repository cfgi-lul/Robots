package gui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.*;

class GameWindow extends JInternalFrame
{
    private Robot robot;
    GameWindow(Robot robot)
    {
        super("Игровое поле", false, true, true, true);
        GameVisualizer m_visualizer = new GameVisualizer(robot);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        setJMenuBar(createMenuBar(robot));
        this.robot = robot;
    }

    void saveCoordinates() throws IOException {
        robot.saveCoordinates();
    }

    private JMenu createMenu(Robot robot) {
        JMenu menu = new JMenu("Документы");
        JMenuItem menuItem = new JMenuItem("Удалить препятствия");
        menuItem.addActionListener((event) -> robot.deleteAllHuinya());
        menu.add(menuItem);
        JMenuItem menuItem2 = new JMenuItem("Рестарт");
        menuItem2.addActionListener((event) -> robot.restart());
        menu.add(menuItem2);
        return menu;
    }

    private JMenuBar createMenuBar(Robot robot) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createMenu(robot));
        return menuBar;
    }


}