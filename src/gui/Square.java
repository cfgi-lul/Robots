package gui;

import java.awt.*;

public class Square extends Rectangle {
    private int X;
    private int Y;
    private int posX;
    private int posY;

    Square(int X, int Y, int posX, int posY) {
        super(posX, posY, Y, X);
        this.X = X;
        this.Y = Y;
        this.posX = posX;
        this.posY = posY;
    }

    private int getSquarePositionX() {
        return X;
    }

    private int getSquarePositionY() {
        return Y;
    }

    private int getSquareX() {
        return posX;
    }

    private int getSquareY() {
        return posY;
    }
}
