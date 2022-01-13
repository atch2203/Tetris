/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:

4x4 grid contains each tetramino
 */


public class Tetramino {
    private int x, y; // bottom left corner of the 4x4 grid
    private int orientation; // 1 - 4

    char c = '■';


    public void updateMino(int x, int y, int o){
        this.x = x;
        this.y = y;
        this.orientation = o;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getOrientation() {
        return orientation;
    }
}
