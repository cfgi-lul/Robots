package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.geom.Ellipse2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class Robot extends Observable {

    private final Timer m_timer = initTimer();

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile ArrayList<Square> squares = new ArrayList<>();
    private volatile ArrayList<Circle> circles = new ArrayList<>();

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 150;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;
    public  Stack<Point> route = calculateRoute(new Point ((int)m_robotPositionX,(int)m_robotPositionY), new Point (m_targetPositionX,m_targetPositionY));

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

    public void addSquare(Point point) {
        squares.add(new Square(30,30,point.x, point.y));
    }

    public void addCircle(Point point) {
        circles.add(new Circle(point.x, point.y,30));
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
                if(!route.isEmpty()) {
                    tick(route.pop());
                }
                else tick(new Point((int)m_targetPositionX,(int)m_targetPositionY));

            }
        }, 0, 10);
    }

    protected void setTargetPosition(Point targetPoint) {
        m_targetPositionX = targetPoint.x;
        m_targetPositionY = targetPoint.y;
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

    public boolean collide(Point point){
        boolean collision = false;
        Ellipse2D.Double robot = new Ellipse2D.Double((int)point.getX(), (int)point.getY(), 15, 5);
        for(Square obs: squares) {
            if (robot.intersects(obs))
                collision = true;
        }
        for(Circle obs: circles) {
            if (robot.contains(new Point ((int)obs.getCenterX()+(int)obs.getRadius(), (int)obs.getCenterY()+(int)obs.getRadius()))
                    || robot.contains(new Point ((int)obs.getCenterX()-(int)obs.getRadius(), (int)obs.getCenterY()+(int)obs.getRadius()))
                    || robot.contains(new Point ((int)obs.getCenterX()+(int)obs.getRadius(), (int)obs.getCenterY()-(int)obs.getRadius()))
                    || robot.contains(new Point ((int)obs.getCenterX()-(int)obs.getRadius(), (int)obs.getCenterY()-(int)obs.getRadius()))
                    || robot.contains(new Point ((int)(obs.getCenterX()+obs.getRadius()*0.71), (int)(obs.getCenterY()+obs.getRadius()*0.71)))
                    || robot.contains(new Point ((int)(obs.getCenterX()+obs.getRadius()*0.71), (int)(obs.getCenterY()-obs.getRadius()*0.71)))
                    || robot.contains(new Point ((int)(obs.getCenterX()-obs.getRadius()*0.71), (int)(obs.getCenterY()+obs.getRadius()*0.71)))
                    || robot.contains(new Point ((int)(obs.getCenterX()-obs.getRadius()*0.71), (int)(obs.getCenterY()-obs.getRadius()*0.71))))
                collision = true;
        }
        if (m_robotPositionX < 0 || m_robotPositionY < 0 || m_robotPositionY > 470 || m_robotPositionX >  440)
            collision=true;

        return collision;
    }

    public void deleteAllHuinya(){
        squares.clear();
        circles.clear();
    }

    public void restart(){
        m_robotPositionX = 100;
        m_robotPositionY = 100;
        m_robotDirection = 0;
        m_targetPositionX = 100;
        m_targetPositionY = 100;
    }

    protected void tick(Point tp) {
        double distance = distance(tp.getX(), tp.getY(),
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) {
            return;
        }
        double velocity = maxVelocity;

        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, tp.getX(), tp.getY());
        m_robotDirection = angleToTarget;
        double angularVelocity = 0;

        moveRobot(velocity, angularVelocity, 10);
        setChanged();
    }

    public Stack<Point> calculateRoute(Point start, Point end) {
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> father = new HashMap<>();

        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()){
            Point point = queue.peek();
            if(point.equals(end)) break;
            queue.poll();
            Set<Point> incidentPoints = incidentPoints(point);
            for (Point w: incidentPoints){
                if (!visited.contains(w) && !collide(w)) {
                    visited.add(w);
                    queue.add(w);
                    father.put(w, point);
                }
            }
        }

        return  restoreRoute(father, start, end);
    }

    public Stack<Point> restoreRoute(Map<Point, Point> father, Point robot, Point target){
        Stack<Point> result = new Stack<>();

        Point currentPoint = new Point(target.x, target.y);
        while(!currentPoint.equals(robot)){
            result.push(currentPoint);
            currentPoint = father.get(currentPoint);
        }
        return result;
    }

    public Set<Point> incidentPoints(Point p){
        Integer X[] = {0,1,1,1,0,-1,-1,-1};
        Integer Y[] = {1,1,0,-1,-1,-1,0,1};
        Set<Point> incidentPoints = new HashSet<>();

        for(int i = 0; i < 8; i++)
            incidentPoints.add(new Point((int)(p.getX() + X[i]), (int)(p.getY() + Y[i])));

        return incidentPoints;
    }




    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;

//        if (temp != m_targetPositionX && temp1 != m_targetPositionY) {
//            route = calculateRoute(new Point((int) m_robotPositionX, (int) m_robotPositionY),
//                    new Point(m_targetPositionX, m_targetPositionY));
//        }
//        temp = m_targetPositionX;
//        temp1 = m_targetPositionY;

    }

    public void rebuild (){
        route = calculateRoute(new Point((int) m_robotPositionX, (int) m_robotPositionY),
                new Point(m_targetPositionX, m_targetPositionY));
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI) {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    public void saveCoordinates() throws IOException {
        FileWriter r = new FileWriter("robot.txt", true);
        r.write("Sq" + '\n');
        for (Square e : squares) {
            r.write(String.valueOf(e.x) + " ");
            r.write(String.valueOf(e.y) + '\n');
        }
        r.write("Ci" + '\n');
        for (Circle e : circles) {
            r.write(String.valueOf(e.getX()) + " ");
            r.write(String.valueOf(e.getY()) + '\n');
        }
        r.write("coord" + '\n');
        r.write((int) m_robotPositionX +"\n");
        r.write((int)m_robotPositionY + "\n");
        r.write(m_robotDirection +"\n");
        r.write(m_targetPositionX +"\n");
        r.write(m_targetPositionY +"\n");
        r.flush();
        r.close();
    }
}