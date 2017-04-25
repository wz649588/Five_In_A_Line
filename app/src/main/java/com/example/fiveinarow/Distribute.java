package com.example.fiveinarow;

/**
 * Created by wz649 on 2017/4/24.
 */

public class Distribute {
    private String email;
    private double ran;

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


    public String getEmail() {
        return email;
    }

    public double getRan() {
        return ran;
    }
}
