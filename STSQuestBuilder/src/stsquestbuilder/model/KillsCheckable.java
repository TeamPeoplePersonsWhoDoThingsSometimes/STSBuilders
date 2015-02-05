/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stsquestbuilder.model;

/**
 *
 * @author William
 */
public class KillsCheckable {
    
    private Enemy enemy;
    private int amount;
    
    public KillsCheckable() {
        enemy = null;
        amount = 0;
    }
    
    public KillsCheckable(Enemy killed, int number) {
        enemy = killed;
        amount = number;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        if (enemy == null) 
            return "Empty Check";
        return "Kills Check: " + enemy.getGeneralType() + " on " + enemy.getParticularType();
    }
    
}
