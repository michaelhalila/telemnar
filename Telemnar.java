/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telemnar;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

/**
 *
 * @author Michael Halila
 */
// roguelike project, started during the coronavirus quarantine, 20200325
//
// 0.1: display character, move around; finished 20200407
// 0.2: tilemap

public class Telemaer extends Application {

    public final static int TILESIZE = 32;
    public final static int MAPWIDTH = 50;
    public final static int MAPHEIGHT = 20;

    @Override
    public void start(Stage stage) {
        //defining the application window here
        stage.setTitle("Telemaer");

        BorderPane basicView = new BorderPane();
        
        //game logic components
        
        TurnOrder turnOrder = new TurnOrder();
        
        //generate map
        
        Tile[][] map = generateBlankMap();

        //bar at top of application window
        HBox menuBox = new HBox();
        menuBox.setSpacing(10);

        //quit button I copied from my previous project
        Button quitButton = new Button("Quit");
        DropShadow shadow = new DropShadow();
        quitButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            quitButton.setEffect(shadow);
        });
        quitButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            quitButton.setEffect(null);
        });
        quitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            System.exit(0);
        });
        menuBox.getChildren().add(quitButton);

        basicView.setTop(menuBox);

        //main game view
        Canvas canvas = new Canvas(1600, 640);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image basicFloor = new Image("/floor01.png");
        Image rock = new Image("/rock.png");
        Image wallSouth = new Image("/wall_s.png");

        Unit player = new Unit(5, 5, true);

        basicView.setCenter(canvas);
        Scene scene = new Scene(basicView);

        //bar at bottom of application window
        HBox statsBox = new HBox();
        statsBox.setSpacing(10);
        Label turnCounter = new Label();
        statsBox.getChildren().add(turnCounter);
        Label playerStats = new Label("Power level: 0");
        statsBox.getChildren().add(playerStats);
        Label message = new Label ("Welcome to Telemaer");
        statsBox.getChildren().add(message);

        basicView.setBottom(statsBox);
        
        Popup startPopup = new Popup();
        VBox startPopupBox = new VBox();
        startPopupBox.setStyle("-fx-background-color: lightgrey; -fx-padding: 10;");
        Label startPopupLabel = new Label("Welcome to Telemaer!");
        startPopupBox.getChildren().add(startPopupLabel);
        Button startPopupButton = new Button("Let's go!");
        startPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            startPopup.hide();
        });
        startPopupBox.getChildren().add(startPopupButton);
        startPopup.getContent().add(startPopupBox);        

        
        //keyboard commands
        
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                if (player.getX() < MAPWIDTH - 1) {
                    if (map[player.getX()+1][player.getY()].isPassable()) {
                        if (player.moveRight()) {
                            message.setText("");
                            turnOrder.endTurn();
                            return;
                        }
                    }
                }
                message.setText("You cannot go that way.");

            }
            if (event.getCode() == KeyCode.LEFT) {
               if (player.getX() > 0) {
                    if (map[player.getX()-1][player.getY()].isPassable()) {
                        if (player.moveLeft()) {
                            message.setText("");
                            turnOrder.endTurn();
                            return;
                        }
                    }
                }
                message.setText("You cannot go that way.");

            }
            
            if (event.getCode() == KeyCode.UP) {
               if (player.getY() > 0) {
                    if (map[player.getX()][player.getY()-1].isPassable()) {
                        if (player.moveUp()) {
                            message.setText("");
                            turnOrder.endTurn();
                            return;
                        }
                    }
                }
                message.setText("You cannot go that way.");

            }
            if (event.getCode() == KeyCode.DOWN) {
               if (player.getY() < MAPHEIGHT - 1) {
                    if (map[player.getX()][player.getY()+1].isPassable()) {
                        if (player.moveDown()) {
                            message.setText("");
                            turnOrder.endTurn();
                            return;
                        }
                    }
                }
                message.setText("You cannot go that way.");

            }

        });

        //loop
        new AnimationTimer() {

            private long previousUpdate;

            @Override
            public void handle(long rightAboutNow) {
                
                for (int y = 0; y < MAPHEIGHT; y++) {
                    for (int x = 0; x < MAPWIDTH; x++) {
                        if (map[x][y].getTileType() == 1) {
                            gc.drawImage(wallSouth, x * TILESIZE, y * TILESIZE);
                        } else if (map[x][y].getTileType() == 5) {
                            gc.drawImage(rock, x * TILESIZE, y * TILESIZE);
                        } else {
                            gc.drawImage(basicFloor, x * TILESIZE, y * TILESIZE);
                        }
                    }
                }

                gc.drawImage(player.getImage(), player.getX() * TILESIZE, player.getY() * TILESIZE);
                turnCounter.setText("Turn: " + turnOrder.getTurn());

            }
        }.start();

        stage.setScene(scene);
        stage.show();
        startPopup.show(stage);
    }

    public static void main(String[] args) {
        launch(Telemaer.class);
    }
    
    public Tile[][] generateBlankMap() {
        Tile[][] map = new Tile[MAPWIDTH][MAPHEIGHT];
        
        for (int x = 0; x < MAPWIDTH; x++) {
            Tile tile = new Tile(x,0,5);
            map[x][0] = tile;
        }
        for (int x = 0; x < MAPWIDTH; x++) {
            Tile tile = new Tile(x,1,1);
            map[x][1] = tile;
        }
        
        for (int y = 2; y < MAPHEIGHT; y++) {
            for (int x = 0; x < MAPWIDTH; x++) {
                Tile tile = new Tile(x,y);
                map[x][y] = tile;
            }
        }
        return map;
    }

}
