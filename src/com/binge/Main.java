package com.binge;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Main extends Application {

    // Physics constants
    public static final double GRAVITY = 980;          // pixels per second squared
    private static final double MOVE_ACCELERATION = 600; // horizontal acceleration, pixels per second squared
    private static final double MAX_MOVE_SPEED = 1000;    // maximum horizontal speed
    private static final double NATURAL_SPEED_LIM = 500;
    private static final double FRAME_DURATION = 1e9;   // 1 second in nanoseconds
    private static final double FRICTION = 0.6;

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(1200, 900);

        Pane pane = new Pane(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        ArrayList<Obstacle> obstacleArrayList = new ArrayList<>();
        ArrayList<Character> characterArrayList = new ArrayList<>();
        ArrayList<Collectible> items = new ArrayList<>();
        ArrayList<Displacer> disp = new ArrayList<>();

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

        Coin coin1 = new Coin(pane, 300, 450, 10, 10);
        items.add(coin1);

        Coin coin2 = new Coin(pane, 300, 420, 10, 10);
        items.add(coin2);

        Coin coin3 = new Coin(pane, 300, 390, 10, 10);
        items.add(coin3);

        Coin coin4 = new Coin(pane, 300, 360, 10, 10);
        items.add(coin4);

        SizeShifter mag1 = new SizeShifter(pane, 400, 400, 10, 10);
        items.add(mag1);

        SizeShifter mag2 = new SizeShifter(pane, 480, 400, 10, -10);
        items.add(mag2);

        GrapplePoint grap1 = new GrapplePoint(pane, 300, 500, 500);
        disp.add(grap1);

        RectangleObstacle obs4 = new RectangleObstacle(pane, 900, 400, 200, 100, Color.GRAY);
        obstacleArrayList.add(obs4);

//        Rectangle rectangle = new Rectangle(250, 300, 200, 150);
//        Circle circle = new Circle(350, 425, 50);
//
//        Obstacle cutoff = new CutOffObstacle(pane, rectangle, circle, Color.LIGHTBLUE);
//        obstacleArrayList.add(cutoff);

        Scene scene = new Scene(pane, 1200, 900);

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
//                System.out.println(redBall.jumpCount);
                if (redBall.jumpCount <= 2) {
                    redBall.jumpCount++;
                    redBall.movingUp = true;
                }
            }
            if (event.getCode() == KeyCode.SPACE) {
                redBall.specialTransport = true;
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
            if (event.getCode() == KeyCode.SPACE) {
                redBall.specialTransport = false;
            }
        });

        // Animation timer
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = -1;

            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                if (lastUpdate < 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / FRAME_DURATION; // seconds
//                double deltaTime = 1 / 60.0;
                lastUpdate = now;
                
                // Update vertical velocity (gravity)
                redBall.v.add(0, GRAVITY * deltaTime);

                // Update horizontal velocity based on input
                if (redBall.movingLeft && redBall.v.getX() > -NATURAL_SPEED_LIM) {
                    redBall.v.add(-MOVE_ACCELERATION * deltaTime, 0);
                }
                if (redBall.movingRight && redBall.v.getX() <  NATURAL_SPEED_LIM) {
                    redBall.v.add(MOVE_ACCELERATION * deltaTime, 0);
                }
                if (redBall.movingUp) {
                    redBall.v.setY(-300);
                    redBall.movingUp = false;
                }

//                 Limit the maximum horizontal speed
                if (redBall.v.getX() > MAX_MOVE_SPEED) {
                    redBall.v.setX(MAX_MOVE_SPEED);
                }
                if (redBall.v.getX() < -MAX_MOVE_SPEED) {
                    redBall.v.setX(-MAX_MOVE_SPEED);
                }
                if (redBall.v.getY() > MAX_MOVE_SPEED) {
                    redBall.v.setY(MAX_MOVE_SPEED);
                }
                if (redBall.v.getY() < -MAX_MOVE_SPEED) {
                    redBall.v.setY(-MAX_MOVE_SPEED);
                }

                boolean collide = false;
                for (Obstacle obs : obstacleArrayList) {
                    // predict the next position based on current velocity
                    double displacementX = redBall.v.getX() * deltaTime;
                    double displacementY = redBall.v.getY() * deltaTime;
                    // use the predicted position to check collision to avoid multiple collisions at once
                    collide = collide || obs.checkCollision(redBall, displacementX, displacementY, deltaTime);
                }

                Iterator<Collectible> iterator = items.iterator();
                while (iterator.hasNext()) {
                    Collectible item = iterator.next();
                    if (item.checkCollision(redBall)) {
                        item.handleCollision(redBall);
                        iterator.remove();
                    }
                }

                for (Displacer d : disp) {
                    if (d.checkCollision(redBall)) {
                        if (d instanceof GrapplePoint gp) {
                            if (!gp.cooldown) drawLine(gc, redBall.pos.x, redBall.pos.y, gp.pos.x, gp.pos.y);
//                            if (!((GrapplePoint) d).activate && redBall.specialTransport) {
                            if (redBall.specialTransport && !gp.cooldown) {
                                d.handleCollision(redBall);
//                                redBall.v.add(0, -GRAVITY * deltaTime);
                            }
                        }
                    }
                }


                // Update position
                if (!collide) {
                    redBall.pos.setX(redBall.body.getCenterX() + redBall.v.getX() * deltaTime);
                    redBall.pos.setY(redBall.body.getCenterY() + redBall.v.getY() * deltaTime);
                    redBall.body.setCenterX((int) redBall.pos.getX());
                    redBall.body.setCenterY((int) redBall.pos.getY());
                } else {
                    System.out.println(redBall.v.getY());
                    redBall.v = redBall.v.scale(FRICTION);
                }

                // Ground collision
                if (redBall.body.getCenterY() + redBall.body.getRadius() > pane.getHeight()) {
                    redBall.body.setCenterY(pane.getHeight() - redBall.body.getRadius());
                    redBall.v.setY(-(redBall.v.getY() - 150));
                    redBall.jumpCount = 0;
                }
                if (redBall.body.getCenterY() - redBall.body.getRadius() < 0) {
                    redBall.body.setCenterY(redBall.body.getRadius());
                    redBall.v.setY(-redBall.v.getY());
                }

                // Wall collisions (left and right bounds)
                if (redBall.body.getCenterX() - redBall.body.getRadius() < 0) {
                    redBall.body.setCenterX(redBall.body.getRadius());
                    redBall.v.setX(-redBall.v.getX()*FRICTION);
                }
                if (redBall.body.getCenterX() + redBall.body.getRadius() > pane.getWidth()) {
                    redBall.body.setCenterX(pane.getWidth() - redBall.body.getRadius());
                    redBall.v.setX(-redBall.v.getX()*FRICTION);
                }
                //                System.out.println(c.pos.getX() + " " + c.pos.getY());
            }
        };
        timer.start();
    }

    private void drawLine(GraphicsContext gc, double x0, double y0, double x1, double y1) {
        gc.setStroke(Color.RED);  // Set line color
        gc.setLineWidth(2);       // Set line thickness
        gc.strokeLine(x0, y0, x1, y1);  // Draw line
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}

abstract class Terrain {
    String color;

    abstract boolean checkCollision(Character c);
}