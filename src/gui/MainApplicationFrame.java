package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import log.Logger;

class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    MainApplicationFrame() throws IOException {

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);
        setContentPane(desktopPane);
        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    exit();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        //reading from Windows.txt
        try (Scanner sc = new Scanner(new File("Windows.txt"))) {
            Robot robot = new Robot();
            boolean gameAttached = false, coordinatesAttached = false;
            while (sc.hasNextLine()) {

                String tempScanner = sc.nextLine();
                switch (tempScanner) {
                    case "Игровое поле":
                        if (!gameAttached) {
                            GameWindow gameWindow = new GameWindow(robot);
                            gameWindow.setLocation(sc.nextInt(), sc.nextInt());
                            int t1 = sc.nextInt();
                            int t2 = sc.nextInt();
                            gameWindow.setSize(t2, t1);
                            addWindow(gameWindow);
                            gameAttached = true;
                        } else {
                            robot = new Robot();
                            GameWindow gameWindow = new GameWindow(robot);
                            gameWindow.setLocation(sc.nextInt(), sc.nextInt());
                            int t1 = sc.nextInt();
                            int t2 = sc.nextInt();
                            gameWindow.setSize(t2, t1);
                            addWindow(gameWindow);
                            coordinatesAttached = false;
                        }
                        break;
                    case "Протокол работы":
                        addWindow(createLogWindow(sc.nextInt(),sc.nextInt(),sc.nextInt(),sc.nextInt()));
                        break;
                    case "Координаты робота":
                        CoordinatesWindow coordinatesWindow = createCoordinatesWindow();
                        if (!coordinatesAttached) {
                            robot.addObserver(coordinatesWindow);
                            coordinatesWindow.setLocation(sc.nextInt(), sc.nextInt());
                            int t1 = sc.nextInt();
                            int t2 = sc.nextInt();
                            coordinatesWindow.setSize(t2, t1);
                            addWindow(coordinatesWindow);
                            coordinatesAttached = true;
                        } else if (gameAttached) {
                            robot = new Robot();
                            robot.addObserver(coordinatesWindow);
                            coordinatesWindow.setLocation(sc.nextInt(), sc.nextInt());
                            int t1 = sc.nextInt();
                            int t2 = sc.nextInt();
                            coordinatesWindow.setSize(t2, t1);
                            addWindow(coordinatesWindow);
                            gameAttached = false;
                        }
                        break;
                }
            }
        }
        setJMenuBar(createMenuBar());
    }

    //create log window
    private LogWindow createLogWindow(int x, int y , int sizeX, int sizeY)
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(x,y);
        logWindow.setSize(sizeY, sizeX);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    //create CoordinetesWindow
    private CoordinatesWindow createCoordinatesWindow() {
        CoordinatesWindow coordinatesWindow = new CoordinatesWindow();
        coordinatesWindow.setLocation(222, 9);
        coordinatesWindow.setSize(475, 85);
        setMinimumSize(coordinatesWindow.getSize());
        coordinatesWindow.pack();
        return  coordinatesWindow;
    }

    private void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenu createMenu(int key) {
        JMenu menu = new JMenu("Документы");
        menu.setMnemonic(key);
        return menu;
    }

    private JMenu createMenu(String name, int key, String description) {
        JMenu menu = new JMenu(name);
        menu.setMnemonic(key);
        menu.getAccessibleContext().setAccessibleDescription(description);
        return menu;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Set up the lone menu.
        JMenu menu = createMenu(KeyEvent.VK_D);
        menuBar.add(menu);

        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("Новое окно");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, InputEvent.ALT_MASK));
        menuItem.addActionListener((event) -> {
            Object[] options = {"Оба окна",
                    "Игровое поле",
                    "Протокол работы"};
            int n = JOptionPane.showOptionDialog(new JFrame(),
                    "Какое окно вы хотите создать?",
                    "Выбор окна",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
            if (n == 0) {
                Robot robot = new Robot();
                CoordinatesWindow coordinatesWindow = createCoordinatesWindow();
                robot.addObserver(coordinatesWindow);
                addWindow(coordinatesWindow);

                GameWindow gameWindow = new GameWindow(robot);
                LogWindow logWindow = createLogWindow(222,9,475,82);
                gameWindow.setSize(475,  443);
                gameWindow.setLocation(222,92);
                addWindow(logWindow);
                addWindow(gameWindow);
                this.invalidate();
            } else if (n == 1) {
                Robot robot = new Robot();
                CoordinatesWindow coordinatesWindow = createCoordinatesWindow();
                robot.addObserver(coordinatesWindow);
                addWindow(coordinatesWindow);

                GameWindow gameWindow = new GameWindow(robot);
                gameWindow.setSize(475,  443);
                gameWindow.setLocation(222,92);
                addWindow(gameWindow);
                this.invalidate();
            } else if (n == 2) {
                addWindow(createLogWindow(222,9,475,82));
                this.invalidate();
            }
        });

        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Закрыть и Сохранить");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, InputEvent.ALT_MASK));
        menuItem.addActionListener((event) -> {
            try {
                exit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.add(menuItem);

        JMenu lookAndFeelMenu = createMenu("Режим отображения",
                KeyEvent.VK_V,"Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = createMenu("Тесты",
                KeyEvent.VK_T,"Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> Logger.debug("Новая строка"));
            testMenu.add(addLogMessageItem);
        }

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private void exit() throws IOException {
        Object[] options = {"Да",
                "Нет"};
        int n = JOptionPane.showOptionDialog(new JFrame(),
                "Вы точно хотите выйти?",
                "Выход",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n==0) {
            FileWriter windows = new FileWriter("Windows.txt", false);
            for (JInternalFrame e : desktopPane.getAllFrames()) {
                windows.write(e.getTitle() + '\n');
                windows.write(String.valueOf(e.getLocation().x) + " ");
                windows.write(String.valueOf(e.getLocation().y) + '\n');
                windows.write(String.valueOf(e.getSize().height) + '\n');
                windows.write(String.valueOf(e.getSize().width)+'\n');
            }
            windows.close();
            System.exit(0);
        }
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}