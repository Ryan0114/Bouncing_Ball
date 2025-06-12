package com.binge;

import javafx.scene.paint.Color;

public class Projectile extends Character{
    boolean activate;

    Projectile(double posX, double posY, int radius) {
        super(posX, posY, radius, Color.PURPLE);
    }

    void vanish() {
        this.body.setFill(Color.TRANSPARENT);
        this.body.setStroke(Color.TRANSPARENT);
        this.activate = false;
    }
}
