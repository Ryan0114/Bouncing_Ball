package com.binge;

import javafx.scene.shape.Circle;

public class Character {
    Point2D pos, v;
    int radius, jumpCount;
    double mass;
    Circle body;
    boolean movingLeft, movingRight, movingUp, specialTransport;
    int coins;

    Character(double posX, double posY, int radius, double mass) {
        this.pos = new Point2D(posX, posY);
        this.v = new Point2D(0, 0);
        this.radius = radius;
        this.mass = mass;
        this.movingLeft = false;
        this.movingRight = false;
        this.movingUp = false;
        this.coins = 0;
        this.body = new Circle(posX, posY, radius);
        this.specialTransport = false;
        this.jumpCount = 0;
    }
}