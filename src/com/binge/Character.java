package com.binge;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Character {
    Point2D pos, v;
    int radius, jumpCount;
    double mass;
    Circle body;
    boolean movingLeft, movingRight, movingUp, specialTransport;
    int coins;

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
}