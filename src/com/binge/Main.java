package com.binge;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
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
        ArrayList<Character> characterArrayList = new ArrayList<Character>();

        Character redBall = new Character(150, 50, 20, 50);
        redBall.body.setFill(Color.RED);
        pane.getChildren().add(redBall.body);
        characterArrayList.add(redBall);

        CircleObstacle obs1 = new CircleObstacle(pane, 145, 200, 20, Color.GRAY);
        obstacleArrayList.add(obs1);

        CircleObstacle obs2 = new CircleObstacle(pane, 40, 150, 30, Color.GRAY);
        obstacleArrayList.add(obs2);

        CircleObstacle obs3 = new CircleObstacle(pane, 250, 170, 40, Color.GRAY);
        obstacleArrayList.add(obs3);

//        RectangleObstacle obs4 = new RectangleObstacle(100, 150, 100, 100);
//        obs4.body.setFill(Color.YELLOW);
//        pane.getChildren().add(obs4.body);
//        obstacleArrayList.add(obs4);

        Rectangle rectangle = new Rectangle(250, 300, 200, 150);
        Circle circle = new Circle(350, 425, 50);

        Obstacle cutoff = new CutOffObstacle(pane, rectangle, circle, Color.LIGHTBLUE);
        obstacleArrayList.add(cutoff);

        Scene scene = new Scene(pane, 600, 600);

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

//                double deltaTime = (now - lastUpdate) / FRAME_DURATION; // seconds
                double deltaTime = 1 / 60.0;
                lastUpdate = now;

                // Update vertical velocity (gravity)
                redBall.v.add(0, GRAVITY * deltaTime);

                // Update horizontal velocity based on input
                if (redBall.movingLeft) {
                    redBall.v.add(-MOVE_ACCELERATION * deltaTime, 0);
                }
                if (redBall.movingRight) {
                    redBall.v.add(MOVE_ACCELERATION * deltaTime, 0);
                }
                if (redBall.movingUp) {
                    redBall.v.setY(-300);
                    redBall.movingUp = false;
                }

                // Limit the maximum horizontal speed
                if (redBall.v.getX() > MAX_MOVE_SPEED) {
                    redBall.v.setX(MAX_MOVE_SPEED);
                }
                if (redBall.v.getX() < -MAX_MOVE_SPEED) {
                    redBall.v.setY(-MAX_MOVE_SPEED);
                }

                boolean collision = false;
                for (Obstacle obs : obstacleArrayList) {
                    int newPosX = (int) (redBall.pos.getX() + redBall.v.getX() * deltaTime);
                    int newPosY = (int) (redBall.pos.getY() + redBall.v.getY() * deltaTime);
                    Point2D newPos = new Point2D(newPosX, newPosY);
                    if (obs.checkCollision(newPos, redBall.radius)) {
                        collision = true;
                        obs.handleCollision(redBall, deltaTime);
                    }
                }

                // Update position
                if (!collision) {
                    redBall.pos.setX(redBall.body.getCenterX() + redBall.v.getX() * deltaTime);
                    redBall.pos.setY(redBall.body.getCenterY() + redBall.v.getY() * deltaTime);
                    redBall.body.setCenterX((int)redBall.pos.getX());
                    redBall.body.setCenterY((int)redBall.pos.getY());
                }

                // Ground collision
                if (redBall.body.getCenterY() + redBall.body.getRadius() > pane.getHeight()) {
                    redBall.body.setCenterY(pane.getHeight() - redBall.body.getRadius());
                    redBall.v.setY(-(redBall.v.getY() - 100));
                }
                if (redBall.body.getCenterY() - redBall.body.getRadius() < 0) {
                    redBall.body.setCenterY(redBall.body.getRadius());
                    redBall.v.setY(-redBall.v.getY());
                }

                // Wall collisions (left and right bounds)
                if (redBall.body.getCenterX() - redBall.body.getRadius() < 0) {
                    redBall.body.setCenterX(redBall.body.getRadius());
                    redBall.v.setX(-redBall.v.getX()*0.8);
                }
                if (redBall.body.getCenterX() + redBall.body.getRadius() > pane.getWidth()) {
                    redBall.body.setCenterX(pane.getWidth() - redBall.body.getRadius());
                    redBall.v.setX(-redBall.v.getX()*0.8);
                }
                //                System.out.println(c.pos.getX() + " " + c.pos.getY());
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}

class Character {
    Point2D pos, v;
    int radius;
    double mass;
    Circle body;
    boolean movingLeft, movingRight, movingUp;

    Character(double posX, double posY, int radius, double mass) {
        this.pos = new Point2D(posX, posY);
        this.v = new Point2D(0, 0);
        this.radius = radius;
        this.mass = mass;
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