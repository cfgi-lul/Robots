package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private File file  = new File("Windows.txt");
    private Scanner sc = new Scanner(file);

    MainApplicationFrame() throws IOException {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        //reading windows.txt for reestablish closed windows


        while(sc.hasNextLine()){
            String tempScanner = sc.nextLine();
            if (tempScanner.equals("Игровое поле")){
                GameWindow gameWindow = new GameWindow();
                gameWindow.setLocation(sc.nextInt(), sc.nextInt());
                int t1 = sc.nextInt();
                int t2 = sc.nextInt();
                gameWindow.setSize(t2, t1);
                addWindow(gameWindow);
            }
            else if (tempScanner.equals("Протокол работы")){
                addWindow(createLogWindow());
            }
            else if (tempScanner.equals("Координаты робота")){
                addWindow(createCoordinatesWindow());
            }
        }

        addWindow(new CoordinatesWindow(new GameWindow()));
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

    }

    private CoordinatesWindow createCoordinatesWindow(){
        CoordinatesWindow coordinatesWindow = new CoordinatesWindow(new GameWindow());
        if(sc.hasNextInt()) {
            coordinatesWindow.setLocation(sc.nextInt(), sc.nextInt());
            int t1 = sc.nextInt();
            int t2 = sc.nextInt();
            coordinatesWindow.setSize(t2, t1);
        }
        return coordinatesWindow;

    }

    private LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        if(sc.hasNextInt()) {
            logWindow.setLocation(sc.nextInt(), sc.nextInt());
            int t1 = sc.nextInt();
            int t2 = sc.nextInt();
            logWindow.setSize(t2, t1);
        }
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    private void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void addWindow(JInternalFrame frame, JInternalFrame frame1)
    {
        desktopPane.add(frame);
        desktopPane.add(frame1);
        frame.setVisible(true);
        frame1.setVisible(true);
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
                GameWindow gameWindow = new GameWindow();
                LogWindow logWindow = createLogWindow();
                gameWindow.setSize(400, 400);
                addWindow(logWindow, gameWindow);
                this.invalidate();
            } else if (n == 1) {
                GameWindow gameWindow = new GameWindow();
                gameWindow.setSize(400, 400);
                addWindow(gameWindow);
                this.invalidate();
            } else if (n == 2) {
                addWindow(createLogWindow());
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

//    this method for going out of program
//    and saving mesurements of closed windows

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
