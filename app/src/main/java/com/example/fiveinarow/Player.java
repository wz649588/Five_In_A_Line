package com.example.fiveinarow;

/**
 * Created by wz649 on 2017/4/29.
 */

public class Player {
    private String userName;
    private int inBattle = 0;//0 no battle 1 wait for battle 2 inbattle 3 掉线
    private String enemy;
    public Player(){
    }
    public Player(String userName) {
        this.userName = userName;
    }

    public Player(String userName, int inBattle, String enemy) {
        this.userName = userName;
        this.inBattle = inBattle;
        this.enemy = enemy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getInBattle() {
        return inBattle;
    }

    public void setInBattle(int inBattle) {
        this.inBattle = inBattle;
    }

    public String getEnemy() {
        return enemy;
    }

    public void setEnemy(String enemy) {
        this.enemy = enemy;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;
        if (object != null && object instanceof Player)
        {
            Player player2 = (Player) object;
            sameSame = this.getUserName().equals(player2.getUserName());
        }
        return sameSame;
    }
}
