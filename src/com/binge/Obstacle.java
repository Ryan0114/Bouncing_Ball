package com.binge;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public abstract class Obstacle {
    Point2D pos;
    Shape body;
    Color color;

    abstract boolean checkCollision(Point2D p, int radius);
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
    boolean checkCollision(Point2D p, int radius) {
        double slopeX = p.getX()-this.pos.getX();
        double slopeY = p.getY()-this.pos.getY();
        return (slopeX*slopeX + slopeY*slopeY < (radius + this.radius) * (radius + this.radius));
//        return (Math.sqrt((c.pos.getX()-this.pos.getX())*(c.pos.getX()-this.pos.getX()) + (c.pos.getY()-this.pos.getY())*(c.pos.getY()-this.pos.getY())) < c.radius + this.radius);
    }

    @Override
    void handleCollision(Character c, double deltaTime) {
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
    private int width, height;

    RectangleObstacle(Pane pane, double posX, double posY, int width, int height, Color color) {
        this.pos = new Point2D(posX, posY);
        this.width = width;
        this.height = height;
        body = new Rectangle((int)posX, (int)posY, width, height);
        this.body.setFill(color);
        pane.getChildren().add(this.body);
    }


    @Override
    boolean checkCollision(Point2D p, int radius) {
        Point2D lu, ld, ru, rd;
        lu = new Point2D(this.pos.getX(), this.pos.getY());
        ld = new Point2D(this.pos.getX(), this.pos.getY()+this.height);
        ru = new Point2D(this.pos.getX() + this.width, this.pos.getY());
        rd = new Point2D(this.pos.getX() + this.width, this.pos.getY() + this.height);
        if (this.pos.getX() <= p.getX() && p.getX() <= this.pos.getX()+this.width && Math.abs(p.getY()-(this.pos.getY()+this.height/2.0)) < radius + this.height/2.0) {
//            System.out.println("CollisionX");
            return true;
        } else if (this.pos.getY() <= p.getY() && p.getY() <= this.pos.getY() + this.height && Math.abs(p.getX()-(this.pos.getX()+this.width/2.0)) < radius + this.width/2.0) {
//            System.out.println("CollisionY");
            return true;
        } else return p.getDistance(lu) < radius || p.getDistance(ld) < radius || p.getDistance(ru) < radius || p.getDistance(rd) < radius;
    }

    @Override
    void handleCollision(Character c, double deltaTime) {
        Point2D lu, ld, ru, rd;
        lu = new Point2D(this.pos.getX(), this.pos.getY());
        ld = new Point2D(this.pos.getX(), this.pos.getY()+this.height);
        ru = new Point2D(this.pos.getX() + this.width, this.pos.getY());
        rd = new Point2D(this.pos.getX() + this.width, this.pos.getY() + this.height);
        if (this.pos.getX() <= c.pos.getX() && c.pos.getX() <= this.pos.getX()+this.width && Math.abs(c.pos.getY()-(this.pos.getY()+this.height/2.0)) < c.radius + this.height/2.0) {
            c.v.setY(-c.v.getY());
        } else if (this.pos.getY() <= c.pos.getY() && c.pos.getY() <= this.pos.getY() + this.height && Math.abs(c.pos.getX()-(this.pos.getX()+this.width/2.0)) < c.radius + this.width/2.0) {
            c.v.setX(-c.v.getX());
        } else if (c.pos.getDistance(lu) < c.radius || c.pos.getDistance(ld) < c.radius || c.pos.getDistance(ru) < c.radius || c.pos.getDistance(rd) < c.radius) {
//            c.v.setX(-c.v.getX());
            c.v.setY(-c.v.getY());
        }

        c.pos.setX((int) (c.body.getCenterX() + c.v.getX() * deltaTime));
        c.pos.setY((int) (c.body.getCenterY() + c.v.getY() * deltaTime));
        c.body.setCenterX(c.pos.getX());
        c.body.setCenterY(c.pos.getY());
    }
}

class CutOffObstacle extends Obstacle {
    Shape body;

    CutOffObstacle(Pane pane, Shape main, Shape cut, Color color) {
        this.body = Shape.subtract(main, cut);
        this.body.setFill(color);
        this.body.setStroke(Color.BLACK);
        pane.getChildren().add(this.body);
    }

    boolean checkCollision(Point2D p, int radius) {
        return false;
    }

    void handleCollision(Character c, double deltaTime) {

    }
}