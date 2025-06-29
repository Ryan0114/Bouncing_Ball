package com.binge;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

//Obstacle that need key to pass
public class Lock extends RectangleObstacle {
    Key key;

    Lock(Pane pane, double centerX, double centerY, double width, double height, Color color, double keyX, double keyY) {
        super(pane, centerX, centerY, width, height, 0, color, false, false);
        key = new Key(pane, keyX, keyY, color);
    }

    @Override
    boolean checkCollision(Character c, double dispX, double dispY, double deltaTime) {
        if (!this.key.collected) {
            super.checkCollision(c, dispX, dispY, deltaTime);
        } else {
            Shape intersection = Shape.intersect(c.body, this.body);
            boolean isIntersecting = intersection.getBoundsInLocal().getWidth() > 0 && intersection.getBoundsInLocal().getHeight() > 0;

            if (isIntersecting) {
                this.body.setFill(Color.TRANSPARENT);
                this.body.setStroke(Color.TRANSPARENT);
            }

            return isIntersecting;
        }
        return false;
    }
}

//Use it to pass lock
class Key {
    Point2D pos;
    Shape body;
    boolean collected = false;

    //draw key
    Key(Pane pane){
        Circle bigCircle = new Circle(50, 50, 10);
        Circle smallCircle = new Circle(50, 50, 3);
        Rectangle longRectangle = new Rectangle(47, 53, 6, 20);
        Rectangle sideRectangle = new Rectangle(50, 63, 10, 5);

        this.body = Shape.subtract(bigCircle, smallCircle);
        this.body = Shape.union(this.body, longRectangle);
        this.body = Shape.union(this.body, sideRectangle);
        pane.getChildren().add(this.body);
    };

    Key(Pane pane, double posX, double posY, Color color) {
        this(pane);

        this.pos = new Point2D(posX, posY);
        this.body.setLayoutX(this.pos.getX());
        this.body.setLayoutY(this.pos.getY());
        this.body.setFill(color);
        this.body.setStroke(Color.BLACK);
    }

    void checkCollision(Character c) {
        Shape intersection = Shape.intersect(c.body, this.body);
        boolean isIntersecting = intersection.getBoundsInLocal().getWidth() > 0 && intersection.getBoundsInLocal().getHeight() > 0;

        if (isIntersecting) {
            this.body.setFill(Color.TRANSPARENT);
            this.body.setStroke(Color.TRANSPARENT);
            this.collected = true;
        }
    }
}