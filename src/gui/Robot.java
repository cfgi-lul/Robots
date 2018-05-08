package gui;

import java.awt.*;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;

public class Robot extends Observable {

    private final Timer m_timer = initTimer();

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;


    private volatile ArrayList<Square> squares = new ArrayList<>();
    private volatile ArrayList<Circle> circles = new ArrayList<>();

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;
    private int m_figurePositionX;
    private int m_figurePositionY;

    public double getPositionX() {
        return m_robotPositionX;
    }

    public double getPositionY() {
        return m_robotPositionY;
    }

    public double getDirection() {
        return m_robotDirection;
    }

    public int getTargetPositionX() {
        return m_targetPositionX;
    }

    public int getTargetPositionY() {
        return m_targetPositionY;
    }


    public int getFigurePositionY() {
        return m_targetPositionY;
    }

    public int getFigurePositionX() {
        return m_targetPositionY;
    }



    public void addSquare(Point point) {
        squares.add(new Square(10,10,point.x, point.y));
    }

    public void addCircle(Point point) {
        circles.add(new Circle(point.x, point.y,10));
    }

    public ArrayList<Square> getSquares() {
        return squares;
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }


    private static Timer initTimer()
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public Robot(){
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                notifyObservers(Robot.this);
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                tick();
            }
        }, 0, 10);
    }

    protected void setTargetPosition(Point targetPoint) {
        m_targetPositionX = targetPoint.x;
        m_targetPositionY = targetPoint.y;
    }

    protected void setFigure(Point targetPoint){
        m_figurePositionX = targetPoint.x;
        m_figurePositionY = targetPoint.y;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void tick() {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5)
        {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > m_robotDirection)
        {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < m_robotDirection)
        {
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(velocity, angularVelocity, 10);
        setChanged();
    }

    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection  + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }
}