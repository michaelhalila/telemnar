/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telemnar;

/**
 *
 * @author Michael
 */
public class TurnOrder {
    
    private int turnCounter;
    
    public TurnOrder() {
        this.turnCounter = 0;
    }
    
    public int getTurn() {
        return this.turnCounter;
    }
    
    public void endTurn() {
        this.turnCounter++;
    }
    
}
