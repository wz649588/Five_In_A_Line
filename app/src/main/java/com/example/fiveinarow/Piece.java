package com.example.fiveinarow;

/**
 * Created by wz649 on 2017/4/18.
 */

public class Piece {
    private int x;
    private int y;
    private int type;//type=100 white; type = 200 black
    private int number;

    public Piece(){
    }

    public Piece(int x, int y, int type) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public Piece(int x, int y, int type, int number){
        this.type = type;
        this.x = x;
        this.y = y;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setType(int type) {
        this.type = type;
    }
}
