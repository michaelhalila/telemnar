/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telemnar;

import javafx.scene.image.Image;

/**
 *
 * @author Michael
 */
public class Unit {
    
    private int Xpos;
    private int Ypos;
    private boolean player;
    
    public Unit(int x, int y, boolean isPlayer) {
        this.Xpos = x;
        this.Ypos = y;
        this.player = isPlayer;
    }
    
    public Unit(int x, int y) {
        this.Xpos = x;
        this.Ypos = y;
        this.player = false;
    }
    
    public boolean moveRight() {
        if (this.Xpos < Telemaer.MAPWIDTH-1) {
            this.Xpos++;
            return true;
        }
        return false;
    }
    
    public boolean moveLeft() {
        if (this.Xpos > 0) {
            this.Xpos--;
            return true;
        }
        return false;
    }
    
    public boolean moveUp() {
        if (this.Ypos > 0) {
            this.Ypos--;
            return true;
        }
        return false;
    }
    
    public boolean moveDown() {
        if (this.Ypos < Telemaer.MAPHEIGHT - 1) {
            this.Ypos++;
            return true;
        }
        return false;
    }
    
    public Image getImage() {
        if (player) {
            Image image = new Image("/fella_tp.png");
            return image;
        }
        Image image = new Image("/fella_tp_red.png");
        return image;
    }
    
    public int getX() {
        return this.Xpos;
    }
    
    public int getY() {
        return this.Ypos;
    }
    
}
