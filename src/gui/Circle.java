package gui;

class Circle extends javafx.scene.shape.Circle {
    private int X;
    private int Y;
    private int R;
    Circle(int X, int Y,int R){
        super(X,Y,R);
        this.X = X;
        this.Y = Y;
        this.R = R;
    }

    int getX(){
        return X;
    }

    int getY(){
        return Y;
    }

    int getR(){
        return R;
    }
}
