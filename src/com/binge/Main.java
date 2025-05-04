package com.binge;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

    // Physics constants
    private static final double GRAVITY = 980;          // pixels per second squared
    private static final double MOVE_ACCELERATION = 600; // horizontal acceleration, pixels per second squared
    private static final double MAX_MOVE_SPEED = 300;    // maximum horizontal speed
    private static final double FRAME_DURATION = 1e9;   // 1 second in nanoseconds

    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();

//        Circle redBall = new Circle(150, 50, 20);
        Character redBall = new Character(150, 50, 20);
        redBall.body.setFill(Color.RED);
        pane.getChildren().add(redBall.body);

        CircleObstacle obs1 = new CircleObstacle(155, 200, 20);
        obs1.body.setFill(Color.GRAY);
        pane.getChildren().add(obs1.body);

        CircleObstacle obs2 = new CircleObstacle(40, 150, 30);
        obs2.body.setFill(Color.GRAY);
        pane.getChildren().add(obs2.body);

        Scene scene = new Scene(pane, 300, 300);

        stage.setScene(scene);
        stage.setTitle("Ball");
        stage.show();

        // Key press states
        final boolean[] movingLeft = {false};
        final boolean[] movingRight = {false};
        final boolean[] movingUp = {false};

        // Set up key pressed
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                movingLeft[0] = true;
            }
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                movingRight[0] = true;
            }
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                movingUp[0] = true;
            }
        });

        // Set up key released
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                movingLeft[0] = false;
            }
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                movingRight[0] = false;
            }
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                movingUp[0] = false;
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
                if (movingLeft[0]) {
                    redBall.vX -= MOVE_ACCELERATION * deltaTime;
                }
                if (movingRight[0]) {
                    redBall.vX += MOVE_ACCELERATION * deltaTime;
                }
                if (movingUp[0]) {
                    redBall.vY -= MOVE_ACCELERATION * deltaTime;
                }

                // Limit the maximum horizontal speed
                if (redBall.vX > MAX_MOVE_SPEED) {
                    redBall.vX = MAX_MOVE_SPEED;
                }
                if (redBall.vX < -MAX_MOVE_SPEED) {
                    redBall.vX = -MAX_MOVE_SPEED;
                }

                if (obs1.checkCollision(redBall)) {
                    obs1.handleCollision(redBall);
                }
                if (obs2.checkCollision(redBall)) {
                    obs2.handleCollision(redBall);
                }

//                System.out.println(deltaTime);

                // Update position
                redBall.posX = (int)(redBall.body.getCenterX() + redBall.vX * deltaTime);
                redBall.posY = (int)(redBall.body.getCenterY() + redBall.vY * deltaTime);
//                redBall.body.setCenterY(redBall.body.getCenterY() + redBall.vY * deltaTime);
//                redBall.body.setCenterX(redBall.body.getCenterX() + redBall.vX * deltaTime);
                redBall.body.setCenterX(redBall.posX);
                redBall.body.setCenterY(redBall.posY);


                // Ground collision
                if (redBall.body.getCenterY() + redBall.body.getRadius() > pane.getHeight()) {
                    redBall.body.setCenterY(pane.getHeight() - redBall.body.getRadius());
                    redBall.vY = -(redBall.vY-100);
                    System.out.println("vY" + redBall.vY);
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

    Character(int posX, int posY, int radius) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
        this.vX = 0;
        this.vY = 0;
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

    abstract boolean checkCollision(Character c);
    abstract void handleCollision(Character c);
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
    boolean checkCollision(Character c) {
//        if (getLength(c.vX, c.vY) < 0.01) return false;
//        System.out.println(getLength(c.posX-this.posX, c.posY-this.posY));
        int slopeX = c.posX-this.posX;
        int slopeY = c.posY-this.posY;
        return (slopeX*slopeX + slopeY*slopeY < (c.radius + this.radius) * (c.radius + this.radius));
//        return (Math.sqrt((c.posX-this.posX)*(c.posX-this.posX) + (c.posY-this.posY)*(c.posY-this.posY)) < c.radius + this.radius);
    }

    @Override
    void handleCollision(Character c) {
        int slopeX = c.posX-this.posX;
        int slopeY = c.posY-this.posY;

        double cosineTheta = (slopeX*c.vX + slopeY*c.vY) / (getLength(slopeX, slopeY)*getLength(c.vX, c.vY));
        double distance = getLength(c.vX, c.vY) * cosineTheta;

        int projectionX = (int)(slopeX / getLength(slopeX, slopeY) * distance);
        int projectionY = (int)(slopeY / getLength(slopeX, slopeY) * distance);

        System.out.println("Projection: " + projectionX + " " + projectionY);
        System.out.println("V: " + c.vX + " " + c.vY);
        System.out.println("X: " + c.posX + " " + c.posY);
        c.vX -= 2*projectionX;
        c.vY -= 2*projectionY;
        c.vX *= 0.8;
        c.vY *= 0.8;

        c.posX = (int)(c.body.getCenterX() + c.vX * 0.03);
        c.posY = (int)(c.body.getCenterY() + c.vY * 0.03);
        c.body.setCenterX(c.posX);
        c.body.setCenterY(c.posY);
    }
}
