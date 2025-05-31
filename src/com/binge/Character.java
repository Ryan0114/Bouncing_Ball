package com.binge;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static com.binge.Main.character;
import static com.binge.Main.pane;

public class Character {
    Point2D pos, v;
    int radius, jumpCount;
    double mass;
    Circle body;
    boolean movingLeft, movingRight, movingUp, specialTransport;
    int coins;
    int levelNum, sublevelNum;
    Checkpoint lastCheckpoint;

    Character(double posX, double posY, int radius, Color color) {
        this.pos = new Point2D(posX, posY);
        this.v = new Point2D(0, 0);
        this.radius = radius;
        this.movingLeft = false;
        this.movingRight = false;
        this.movingUp = false;
        this.coins = 0;
        this.body = new Circle(posX, posY, radius);
        this.body.setFill(color);
        this.body.setStroke(Color.BLACK);
        this.specialTransport = false;
        this.jumpCount = 0;
    }


    void revive() {
        if (lastCheckpoint==null) {
            this.terminate();
        } else {
            character.sublevelNum = this.lastCheckpoint.substageNum;
            Main.currentSublevel = Main.currentLevel.sublevels.get(character.sublevelNum - 1);
            pane = Main.currentSublevel.pane;
            pane.getChildren().remove(Main.canvas);
            pane.getChildren().add(Main.canvas);
//            pane.getChildren().remove(character.body);
            character.pos.setX(character.lastCheckpoint.pos.getX());
            character.pos.setY(character.lastCheckpoint.pos.getY());
//            character.body.setLayoutX(character.lastCheckpoint.pos.getX());
//            character.body.setLayoutY(character.lastCheckpoint.pos.getY());
//            character.body.setFill(Color.YELLOW);
            if (!pane.getChildren().contains(character.body)) pane.getChildren().add(character.body);
            Main.scene.setRoot(pane);
        }
    }

    void terminate() {
        System.out.println("YOU DIED");
    }
}