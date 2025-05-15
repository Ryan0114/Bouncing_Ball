package com.binge;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.*;

public abstract class Obstacle {
    Point2D pos;
    Shape body;
    Color color;

    abstract void checkCollision(Character c, double dispX, double dispY, double deltaTime);
    abstract void handleCollision(Character c, double deltaTime);
}

class CircleObstacle extends Obstacle {
    private int radius;
    Circle body;

    CircleObstacle(Pane pane, double posX, double posY, int radius, Color color) {
        pos = new Point2D(posX, posY);
        this.radius = radius;
        this.body = new Circle(posX, posY, radius);
        this.body.setFill(color);
        this.body.setStroke(Color.BLACK);
        pane.getChildren().add(this.body);
    }

    @Override
    void checkCollision(Character c, double dispX, double dispY, double deltaTime) {
        double slopeX = c.pos.getX()+dispX-this.pos.getX();
        double slopeY = c.pos.getY()+dispY-this.pos.getY();
        if (slopeX*slopeX + slopeY*slopeY < (c.radius + this.radius) * (c.radius + this.radius)) {
            c.jumpCount = 0;
            slopeX = c.pos.getX()-this.pos.getX();
            slopeY = c.pos.getY()-this.pos.getY();
            Point2D slope = new Point2D(slopeX, slopeY);

            double collisionPointX = this.pos.getX() + slope.getX() / slope.magnitude() * (this.radius + c.radius);
            double collisionPointY = this.pos.getY() + slope.getY() / slope.magnitude() * (this.radius + c.radius);
            c.pos.setX(collisionPointX);
            c.pos.setY(collisionPointY);
            c.body.setCenterX((int)c.pos.getX());
            c.body.setCenterY((int)c.pos.getY());

            double cosineTheta = slope.dot(c.v) / (slope.magnitude()*c.v.magnitude());
            double distance = c.v.magnitude() * cosineTheta;

            double projectionX = slopeX / slope.magnitude() * distance;
            double projectionY = slopeY / slope.magnitude() * distance;

            c.v.add(-2*projectionX, -2*projectionY);
        }
    }

    @Override
    void handleCollision(Character c, double deltaTime) {
        c.jumpCount = 0;
        double slopeX = c.pos.getX()-this.pos.getX();
        double slopeY = c.pos.getY()-this.pos.getY();
        Point2D slope = new Point2D(slopeX, slopeY);

        double collisionPointX = this.pos.getX() + slope.getX() / slope.magnitude() * (this.radius + c.radius);
        double collisionPointY = this.pos.getY() + slope.getY() / slope.magnitude() * (this.radius + c.radius);
        c.pos.setX(collisionPointX);
        c.pos.setY(collisionPointY);
        c.body.setCenterX((int)c.pos.getX());
        c.body.setCenterY((int)c.pos.getY());

        double cosineTheta = slope.dot(c.v) / (slope.magnitude()*c.v.magnitude());
        double distance = c.v.magnitude() * cosineTheta;

        double projectionX = slopeX / slope.magnitude() * distance;
        double projectionY = slopeY / slope.magnitude() * distance;

        c.v.add(-2*projectionX, -2*projectionY);
    }
}

class RectangleObstacle extends Obstacle {
    private final int width, height;
    final int cornerRadius = 5;
    ArrayList<CircleObstacle> corners = new ArrayList<>();

    RectangleObstacle(Pane pane, double posX, double posY, int width, int height, Color color) {
        this.pos = new Point2D(posX, posY);
        this.width = width;
        this.height = height;
        body = new Rectangle((int)posX, (int)posY, width, height);
        this.body.setFill(color);
        pane.getChildren().add(this.body);

        // 1 1      -1 1
        // 1 -1     -1 -1
        CircleObstacle temp_corner;
        for (int i=1; i>=-1; i-=2) {
            for (int j=1; j>=-1; j-=2) {
                double cornerPosX = this.pos.x + (i-1)/(-2)*this.width + i* cornerRadius;
                double cornerPosY = this.pos.y + (j-1)/(-2)*this.height + j* cornerRadius;
                temp_corner = new CircleObstacle(pane, cornerPosX, cornerPosY, cornerRadius, Color.TRANSPARENT);
                this.corners.add(temp_corner);
            }
        }
    }


    @Override
    void checkCollision(Character c, double dispX, double dispY, double deltaTime) {
        if (this.pos.getX()+this.cornerRadius <= c.pos.getX()+dispX && c.pos.getX()+dispX <= this.pos.getX()+this.width-this.cornerRadius
            && Math.abs(c.pos.getY()+dispY-(this.pos.getY()+this.height/2.0)) < c.radius + this.height/2.0) {
//            System.out.println("CollisionX");
            c.v.setY(-c.v.getY());
            c.jumpCount = 0;
        } else if (this.pos.getY()+this.cornerRadius <= c.pos.getY()+dispY && c.pos.getY()+dispY <= this.pos.getY()+this.height-this.cornerRadius
                && Math.abs(c.pos.getX()+dispX-(this.pos.getX()+this.width/2.0)) < c.radius + this.width/2.0) {
//            System.out.println("CollisionY");
            c.v.setX(-c.v.getX());
            c.jumpCount = 0;
//        } else return p.getDistance(lu) < radius || p.getDistance(ld) < radius || p.getDistance(ru) < radius || p.getDistance(rd) < radius;
        } else {
            boolean collide = false;
            for (CircleObstacle corner: corners) {
                corner.checkCollision(c, dispX, dispY, deltaTime);
            }
        }
    }


    @Override
    void handleCollision(Character c, double deltaTime) {
        if (this.pos.getX()+this.cornerRadius <= c.pos.getX() && c.pos.getX() <= this.pos.getX()+this.width-this.cornerRadius
                && Math.abs(c.pos.getY()-(this.pos.getY()+this.height/2.0)) < c.radius + this.height/2.0) {
            c.v.setY(-c.v.getY());
        } else if (this.pos.getY()+this.cornerRadius <= c.pos.getY() && c.pos.getY() <= this.pos.getY()+this.height-this.cornerRadius
                && Math.abs(c.pos.getX()-(this.pos.getX()+this.width/2.0)) < c.radius + this.width/2.0) {
            c.v.setX(-c.v.getX());
//        } else if (c.pos.getDistance(lu) < c.radius || c.pos.getDistance(ld) < c.radius || c.pos.getDistance(ru) < c.radius || c.pos.getDistance(rd) < c.radius) {
////            c.v.setX(-c.v.getX());
//            c.v.setY(-c.v.getY());
//        }
        }

        c.pos.setX((int) (c.body.getCenterX() + c.v.getX() * deltaTime));
        c.pos.setY((int) (c.body.getCenterY() + c.v.getY() * deltaTime));
        c.body.setCenterX(c.pos.getX());
        c.body.setCenterY(c.pos.getY());
    }
}

//class CutOffObstacle extends Obstacle {
//    Shape body;
//
//    CutOffObstacle(Pane pane, Shape main, Shape cut, Color color) {
//        this.body = Shape.subtract(main, cut);
//        this.body.setFill(color);
//        this.body.setStroke(Color.BLACK);
//        pane.getChildren().add(this.body);
//    }
//
//    boolean checkCollision(Point2D p, int radius) {
//        return false;
//    }
//
//    void handleCollision(Character c, double deltaTime) {
//
//    }
//}