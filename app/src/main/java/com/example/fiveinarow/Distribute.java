package com.example.fiveinarow;

/**
 * Created by wz649 on 2017/4/24.
 */

public class Distribute {
    private String email;
    private double ran;
    private int lost = 0;
    private int regret = 0;
    private int agreeRegret = 0;//3 disagree, 2 agree
    private int offline = 0;
    private int color = 0;//100 white, 200 black
    private int mark = 0;

    public Distribute(){
    }

    public Distribute(String email) {
        this.email = email;
        this.ran = Math.random();
    }

    public Distribute(String email, double ran) {
        this.email = email;
        this.ran = ran;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getOffline() {
        return offline;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public int getAgreeRegret() {
        return agreeRegret;
    }

    public void setAgreeRegret(int agreeRegret) {
        this.agreeRegret = agreeRegret;
    }

    public int getRegret() {
        return regret;
    }

    public void setRegret(int regret) {
        this.regret = regret;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public String getEmail() {
        return email;
    }

    public double getRan() {
        return ran;
    }
}
