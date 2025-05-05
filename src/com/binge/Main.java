package com.binge;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.util.*;

public class Main extends Application {

    // Physics constants
    private static final double GRAVITY = 980;          // pixels per second squared
    private static final double MOVE_ACCELERATION = 600; // horizontal acceleration, pixels per second squared
    private static final double MAX_MOVE_SPEED = 300;    // maximum horizontal speed
    private static final double FRAME_DURATION = 1e9;   // 1 second in nanoseconds

    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();

        ArrayList<Obstacle> obstacleArrayList = new ArrayList<Obstacle>();

        Character redBall = new Character(150, 50, 20);
        redBall.body.setFill(Color.RED);
        pane.getChildren().add(redBall.body);

        CircleObstacle obs1 = new CircleObstacle(155, 200, 20);
        obs1.body.setFill(Color.GRAY);
        pane.getChildren().add(obs1.body);
        obstacleArrayList.add(obs1);

        CircleObstacle obs2 = new CircleObstacle(40, 150, 30);
        obs2.body.setFill(Color.GRAY);
        pane.getChildren().add(obs2.body);
        obstacleArrayList.add(obs2);

        CircleObstacle obs3 = new CircleObstacle(250, 170, 40);
        obs3.body.setFill(Color.GRAY);
        pane.getChildren().add(obs3.body);
        obstacleArrayList.add(obs3);

        Scene scene = new Scene(pane, 300, 300);

        stage.setScene(scene);
        stage.setTitle("Ball");
        stage.show();


        // Set up key pressed
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                redBall.movingLeft = true;
            }
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                redBall.movingRight = true;
            }
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                redBall.movingUp = true;
            }
        });

        // Set up key released
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                redBall.movingLeft = false;
            }
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                redBall.movingRight = false;
            }
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                redBall.movingUp = false;
            }
        });

        // Animation timer
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = -1;

            @Override
            public void handle(long now) {
                if (lastUpdate < 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / FRAME_DURATION; // seconds
                lastUpdate = now;

                // Update vertical velocity (gravity)
                redBall.vY += GRAVITY * deltaTime;

                // Update horizontal velocity based on input
                if (redBall.movingLeft) {
                    redBall.vX -= MOVE_ACCELERATION * deltaTime;
                }
                if (redBall.movingRight) {
                    redBall.vX += MOVE_ACCELERATION * deltaTime;
                }
                if (redBall.movingUp) {
                    redBall.vY = -300;
                    redBall.movingUp = false;
                }

                // Limit the maximum horizontal speed
                if (redBall.vX > MAX_MOVE_SPEED) {
                    redBall.vX = MAX_MOVE_SPEED;
                }
                if (redBall.vX < -MAX_MOVE_SPEED) {
                    redBall.vX = -MAX_MOVE_SPEED;
                }

                for (Obstacle obs: obstacleArrayList) {
                    int newPosX = (int)(redBall.posX + redBall.vX * deltaTime);
                    int newPosY = (int)(redBall.posY + redBall.vY * deltaTime);
                    if (obs.checkCollision(newPosX, newPosY, redBall.radius)) {
                        obs.handleCollision(redBall, deltaTime);
                    }
                }

//                System.out.println(deltaTime);

                // Update position
                redBall.posX = (int)(redBall.body.getCenterX() + redBall.vX * deltaTime);
                redBall.posY = (int)(redBall.body.getCenterY() + redBall.vY * deltaTime);
                redBall.body.setCenterX(redBall.posX);
                redBall.body.setCenterY(redBall.posY);


                // Ground collision
                if (redBall.body.getCenterY() + redBall.body.getRadius() > pane.getHeight()) {
                    redBall.body.setCenterY(pane.getHeight() - redBall.body.getRadius());
                    redBall.vY = -(redBall.vY-100);
                }
                if (redBall.body.getCenterY() - redBall.body.getRadius() < 0) {
                    redBall.body.setCenterY(redBall.body.getRadius());
                    redBall.vY = -(redBall.vY+100);
                }

                // Wall collisions (left and right bounds)
                if (redBall.body.getCenterX() - redBall.body.getRadius() < 0) {
                    redBall.body.setCenterX(redBall.body.getRadius());
                    redBall.vX = -redBall.vX*0.8;
                }
                if (redBall.body.getCenterX() + redBall.body.getRadius() > pane.getWidth()) {
                    redBall.body.setCenterX(pane.getWidth() - redBall.body.getRadius());
                    redBall.vX = -redBall.vX*0.8;
                }
//                System.out.println(redBall.posX + " " + redBall.posY);
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}

class Character {
    int posX, posY, radius;
    double vX, vY;
    Circle body;
    boolean movingLeft, movingRight, movingUp;

    Character(int posX, int posY, int radius) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
        this.vX = 0;
        this.vY = 0;
        this.movingLeft = false;
        this.movingRight = false;
        this.movingUp = false;
        body = new Circle(posX, posY, radius);
    }
}

abstract class Terrain {
    String color;

    abstract boolean checkCollision(Character c);
}

abstract class Obstacle {
    int posX, posY;
    private String color;

    abstract boolean checkCollision(int x, int y, int radius);
    abstract void handleCollision(Character c, double deltaTime);
}

class CircleObstacle extends Obstacle {
    private int radius;
    Circle body;

    CircleObstacle(int posX, int posY, int radius) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
        body = new Circle(posX, posY, radius);
    }

    double getLength(double x, double y) {
        return Math.sqrt(x*x+y*y);
    }

    @Override
    boolean checkCollision(int x, int y, int radius) {
        int slopeX = x-this.posX;
        int slopeY = y-this.posY;
        return (slopeX*slopeX + slopeY*slopeY < (radius + this.radius) * (radius + this.radius));
//        return (Math.sqrt((c.posX-this.posX)*(c.posX-this.posX) + (c.posY-this.posY)*(c.posY-this.posY)) < c.radius + this.radius);
    }

    @Override
    void handleCollision(Character c, double deltaTime) {
        int slopeX = c.posX-this.posX;
        int slopeY = c.posY-this.posY;

        double collisionPointX = this.posX + slopeX / getLength(slopeX, slopeY) * (this.radius + c.radius);
        double collisionPointY = this.posY + slopeY / getLength(slopeX, slopeY) * (this.radius + c.radius);
        c.posX = (int)(collisionPointX);
        c.posY = (int)(collisionPointY);
        c.body.setCenterX(c.posX);
        c.body.setCenterY(c.posY);

        double cosineTheta = (slopeX*c.vX + slopeY*c.vY) / (getLength(slopeX, slopeY)*getLength(c.vX, c.vY));
        double distance = getLength(c.vX, c.vY) * cosineTheta;

        int projectionX = (int)(slopeX / getLength(slopeX, slopeY) * distance);
        int projectionY = (int)(slopeY / getLength(slopeX, slopeY) * distance);

        System.out.println("Projection: " + projectionX + " " + projectionY);
        System.out.println("V: " + c.vX + " " + c.vY);
        System.out.println("X: " + c.posX + " " + c.posY);
        c.vX -= 2*projectionX;
        c.vY -= 2*projectionY;

//        c.posX = (int)(c.body.getCenterX() + c.vX * 0.04);
//        c.posY = (int)(c.body.getCenterY() + c.vY * 0.04);
//        c.body.setCenterX(c.posX);
//        c.body.setCenterY(c.posY);
    }
}
