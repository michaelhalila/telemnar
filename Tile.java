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
public class Tile {
    
    private final int Xpos;
    private final int Ypos;
    private final int tileType;
    
    public Tile(int x, int y) {
        //the default tile is open floor space
        this.Xpos = x;
        this.Ypos = y;
        this.tileType = 0;
    }
    
    public Tile(int x, int y, int type) {
        this.Xpos = x;
        this.Ypos = y;
        this.tileType = type;
    }
    
    public int getTileType() {
        return this.tileType;
    }
    
    public boolean isPassable() {
        if(this.tileType > 0) {
            return false;
        }
        return true;
    }
    
    public Image tileImage() {
        if (this.tileType == 1) {
            Image image = new Image("/wall_s.png");
            return image;
        }
        if (this.tileType == 5) {
            Image image = new Image("/rock.png");
            return image;
        }
        Image image = new Image("/floor01.png");
        return image;
    }
    
}
