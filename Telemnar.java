/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telemnar;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
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

    public final static int MAXNAMELENGTH = 30;

    public boolean acceptingInput = false;

    @Override
    public void start(Stage stage) {
        //defining the application window here
        stage.setTitle("Telemnar");

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
        
        //naming the player

        Unit player = new Unit(5, 5, true);
        String playerName = generateHalflingName();
        String playerCalled = getFirstName(playerName);
        if (playerName.contains("\"")) {
            playerCalled = getNickname(playerName);
        }
        System.out.println("The player will be called " + playerCalled);
        
        String playerClass = "Wastrel";
        String playerAuntName = generateHobbitAuntName();
        
        //canvas

        basicView.setCenter(canvas);
        Scene scene = new Scene(basicView);

        //bar at bottom of application window
        HBox statsBox = new HBox();
        statsBox.setSpacing(10);
        Label name = new Label(playerName);
        statsBox.getChildren().add(name);
        Label classLabel = new Label(playerClass);
        statsBox.getChildren().add(classLabel);
        Label turnCounter = new Label();
        statsBox.getChildren().add(turnCounter);
        Label playerStats = new Label("Power level: 0");
        statsBox.getChildren().add(playerStats);
        Label message = new Label("Welcome to Telemnar");
        statsBox.getChildren().add(message);

        basicView.setBottom(statsBox);

        Popup startPopup = new Popup();
        VBox startPopupBox = new VBox();
        startPopupBox.setStyle("-fx-background-color: lightgrey; -fx-padding: 10;");
        Label startPopupLabel = new Label("Welcome to Telemnar! \nYou are " + playerName + ", a Hobbit " + playerClass + ".\nTomorrow is your aunt " + playerAuntName + "'s birthday.");
        startPopupBox.getChildren().add(startPopupLabel);
        Button startPopupButton = new Button("Let's go!");
        startPopupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            acceptingInput = true;
            startPopup.hide();
        });
        startPopupBox.getChildren().add(startPopupButton);
        startPopup.getContent().add(startPopupBox);

        //keyboard commands
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.RIGHT && acceptingInput) {
                if (player.getX() < MAPWIDTH - 1) {
                    if (map[player.getX() + 1][player.getY()].isPassable()) {
                        if (player.moveRight()) {
                            message.setText("");
                            turnOrder.endTurn();
                            return;
                        }
                    }
                }
                message.setText("You cannot go that way.");

            }
            if (event.getCode() == KeyCode.LEFT && acceptingInput) {
                if (player.getX() > 0) {
                    if (map[player.getX() - 1][player.getY()].isPassable()) {
                        if (player.moveLeft()) {
                            message.setText("");
                            turnOrder.endTurn();
                            return;
                        }
                    }
                }
                message.setText("You cannot go that way.");

            }

            if (event.getCode() == KeyCode.UP && acceptingInput) {
                if (player.getY() > 0) {
                    if (map[player.getX()][player.getY() - 1].isPassable()) {
                        if (player.moveUp()) {
                            message.setText("");
                            turnOrder.endTurn();
                            return;
                        }
                    }
                }
                message.setText("You cannot go that way.");

            }
            if (event.getCode() == KeyCode.DOWN && acceptingInput) {
                if (player.getY() < MAPHEIGHT - 1) {
                    if (map[player.getX()][player.getY() + 1].isPassable()) {
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
            Tile tile = new Tile(x, 0, 5);
            map[x][0] = tile;
        }
        for (int x = 0; x < MAPWIDTH; x++) {
            Tile tile = new Tile(x, 1, 1);
            map[x][1] = tile;
        }

        for (int y = 2; y < MAPHEIGHT; y++) {
            for (int x = 0; x < MAPWIDTH; x++) {
                Tile tile = new Tile(x, y);
                map[x][y] = tile;
            }
        }
        return map;
    }

    //name generator methods start here
    public static ArrayList<String> generateNameList(String filename) {

        //reads a .txt file consisting of line-separated names into an arraylist
        ArrayList<String> names = new ArrayList<>();

        try (Scanner fileReader = new Scanner(new File(filename))) {
            while (fileReader.hasNextLine()) {
                names.add(fileReader.nextLine());
            }
        } catch (Exception e) {
            System.out.println("File could not be read. " + e.getMessage());
        }

        return names;
    }

    public static String generateName(ArrayList<String> names) {

        //returns a random name from the arraylist
        if (names.size() > 0) {
            Random random = new Random();
            int index = random.nextInt(names.size());
            return names.get(index);
        } else {
            return "Name could not be generated.";
        }
    }

    public static boolean checkNameLength(String name, int length) {

        //check if the name length is equal or under to allowed length
        return name.length() <= length;
    }

    public static ArrayList<String> getNamesShorterThan(ArrayList<String> names, int length) {
        ArrayList<String> shorterNames = new ArrayList<>();
        for (String name : names) {
            if (name.length() < length) {
                shorterNames.add(name);
            }
        }
        return shorterNames;
    }

    //Hobbit methods
    public static String generateHalflingName() {

        ArrayList<String> hobbitLastNames = generateNameList("hobbitln.txt");
        String wholeName;
        String firstName;
        String lastName;
        String nameWithNickName;
        boolean isNickName = false;
        boolean isAddLastName = false;

        while (true) {

            //while loop generates name and checks its length
            ArrayList<String> hobbitFirstNames = generateNameList("hobbitfn.txt");
            firstName = generateName(hobbitFirstNames);

            //while loop to stop certain names from being generated
            while (true) {
                lastName = generateName(hobbitLastNames);

                if (firstName.equals("Guybrush") && lastName.equals("Threepwood")) {
                    lastName = generateName(hobbitLastNames);
                } else {
                    break;
                }
            }

            wholeName = firstName + " " + lastName;
            System.out.println(wholeName);

            if (checkNameLength(wholeName, Telemaer.MAXNAMELENGTH)) {
                break;
            } else {
                System.out.println("Name too long!");
            }
        }

        if (checkNameLength(wholeName, Telemaer.MAXNAMELENGTH)) {

            //generate nickname, checking it is not the same as the first name
            System.out.println("Trying nickname!");
            String nickname = generateHobbitNickname(firstName);
            nameWithNickName = firstName + " \"" + nickname + "\" " + lastName;
            System.out.println(nameWithNickName);

            //check combined name length, if not over maximum add nickname
            if (checkNameLength(nameWithNickName, Telemaer.MAXNAMELENGTH)) {
                wholeName = nameWithNickName;
                isNickName = true;
                System.out.println("Nickname generated!");
            } else {
                System.out.println("Name too long!");
            }

        }

        while (wholeName.length() <= Telemaer.MAXNAMELENGTH) {

            //try adding additional last names
            System.out.println("Trying additional surname!");
            String addLastName = generateName(hobbitLastNames);
            System.out.println(wholeName + "-" + addLastName);
            if (wholeName.length() + addLastName.length() + 1 <= Telemaer.MAXNAMELENGTH) {
                if (addLastName.equals("of the Marish")) {
                    wholeName = wholeName + " " + addLastName;
                    isAddLastName = true;
                    System.out.println("Surname incremented!");
                    break;
                } else if (lastName.equals("of the Marish")) {
                    wholeName = firstName + " " + addLastName + " " + lastName;
                    isAddLastName = true;
                    System.out.println("Surname incremented!");
                    break;
                } else {
                    wholeName = wholeName + "-" + addLastName;
                }
                isAddLastName = true;
                System.out.println("Surname incremented!");

            } else {
                System.out.println("Name too long!");
                break;
            }

        }

        // try to add a shorter nickname to names without additional surnames or nicknames
        if (isNickName == false && isAddLastName == false) {

            //LeCheck
            if (lastName.equals("LeChuck")) {
                if (wholeName.length() < (Telemaer.MAXNAMELENGTH - 6)) {
                    wholeName = firstName + " \"G.P.\" " + lastName;
                    System.out.println("LeCheck!");
                }
            } else {
                System.out.println("Trying shorter nickname!");
                int availableCharacters = Telemaer.MAXNAMELENGTH - wholeName.length();
                if (availableCharacters > 6) {
                    ArrayList<String> hobbitNickNames = generateNameList("hobbitnn.txt");
                    ArrayList<String> availableNickNames = getNamesShorterThan(hobbitNickNames, availableCharacters - 2);
                    if (!availableNickNames.isEmpty()) {
                        String newNick = generateName(availableNickNames);
                        if (!newNick.isEmpty()) {
                            wholeName = firstName + " \"" + newNick + "\" " + lastName;
                            System.out.println(wholeName);
                        } else {
                            System.out.println("No nicknames found! - although this should never happen");
                        }
                    } else {
                        System.out.println("No nicknames found!");
                    }
                } else {
                    System.out.println("No space!");
                }
            }
        }

        return wholeName;

    }

    public static String generateHobbitNickname(String noName) {
        ArrayList<String> hobbitNickNames = generateNameList("hobbitnn.txt");
        String name;
        while (true) {
            name = generateName(hobbitNickNames);
            if (!name.equals(noName)) {
                return name;
            }
        }
    }

    public static String generateHobbitAuntName() {
        ArrayList<String> hobbitAuntNames = generateNameList("hobbitan.txt");
        ArrayList<String> hobbitLastNames = generateNameList("hobbitln.txt");
        String name;
        String firstName;
        String lastName;
        String finalName;
        while (true) {
            firstName = generateName(hobbitAuntNames);
            lastName = generateName(hobbitLastNames);
            name = firstName + " " + lastName;
            finalName = augmentHobbitLastName(name,hobbitLastNames);
            if (checkNameLength(name, Telemaer.MAXNAMELENGTH)) {
                break;
            }
        }
        return finalName;
    }

    public static String augmentHobbitLastName(String name, ArrayList<String> lastNames) {
        String wholeName = name;
        
        while (wholeName.length() < Telemaer.MAXNAMELENGTH) {
            String addLastName = generateName(lastNames);
            if (wholeName.length() + addLastName.length() + 1 <= Telemaer.MAXNAMELENGTH) {
                if (addLastName.equals("of the Marish")) {
                    wholeName = wholeName + " " + addLastName;
                    System.out.println("Surname incremented!");
                    break;
                } else {
                    wholeName = wholeName + "-" + addLastName;
                }
                System.out.println("Surname incremented!");
                break;

            } else {
                System.out.println("Name too long!");
                break;
            }
        }

        return wholeName;
    }
    
    public static String getNickname(String name) {
        int start = name.indexOf("\"");
        int end = name.lastIndexOf("\"");
        String nickname = name.substring(start+1,end);
        return nickname;
    }
    
    public static String getFirstName(String name) {
        int end = name.indexOf(" ");
        String firstName = name.substring(0,end);
        return firstName;
    }

}
