package gui;

public class Circle extends javafx.scene.shape.Circle {
    public int X;
    public int Y;
    public int R;
    Circle(int X, int Y,int R){
        super(X,Y,R);
        this.X = X;
        this.Y = Y;
        this.R = R;
    }
    private int getCirclePositionX() {
        return X;
    }

    private int getCirclePositionY() {
        return Y;
    }

    private int getRadiusX() {
        return R;
    }
}
