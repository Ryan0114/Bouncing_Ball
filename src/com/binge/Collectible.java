package com.binge;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

abstract class Collectible {
    Point2D pos;
    int radius;

    Collectible(double posX, double posY, int radius) {
        this.pos = new Point2D(posX, posY);
        this.radius = radius;
    }

    public boolean checkCollision(Character c) {
        double diffX = this.pos.getX() - c.pos.getX();
        double diffY = this.pos.getY() - c.pos.getY();
        return (diffX*diffX + diffY*diffY < (this.radius + c.radius) * (this.radius + c.radius));
    }

    abstract void handleCollision(Character c);
}

class Coin extends Collectible {
    int value;
    CircleObstacle hitbox;

    Coin(Pane pane, double posX, double posY, int radius, int value) {
        super(posX, posY, radius);
        this.value = value;
        hitbox = new CircleObstacle(pane, posX, posY, radius, Color.YELLOW);
    }

    @Override
    public void handleCollision(Character c) {
        c.coins += this.value;
        hitbox.color = Color.TRANSPARENT;
        hitbox.body.setFill(hitbox.color);
        hitbox.body.setStroke(Color.TRANSPARENT);
        System.out.println(c.coins);
    }
}