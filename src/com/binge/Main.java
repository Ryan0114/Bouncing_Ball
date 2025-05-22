package com.binge;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    // Physics constants
    public static final double GRAVITY = 980;          // pixels per second squared
    private static final double MOVE_ACCELERATION = 600; // horizontal acceleration, pixels per second squared
    private static final double MAX_MOVE_SPEED = 1000;    // maximum horizontal speed
    private static final double NATURAL_SPEED_LIM = 500;
    // FRAME_DURATION is not directly used with fixed timestep in the same way, but 1e9 is nanoseconds per second.
    public static final double FRICTION = 0.6; // General friction, review its usage

    // For fixed timestep physics
    private static final double FIXED_PHYSICS_DT = 1.0 / 60.0; // Physics update rate (e.g., 60Hz)
    private double accumulator = 0.0;
    private GraphicsContext mainCanvasGc; // To allow drawLine from updateGamePhysics

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(1200, 900);
        this.mainCanvasGc = canvas.getGraphicsContext2D(); // Store the GraphicsContext

        Pane pane = new Pane(canvas);

        ArrayList<Obstacle> obstacleArrayList = new ArrayList<>();
        ArrayList<Character> characterArrayList = new ArrayList<>(); // Not currently used beyond redBall
        ArrayList<Collectible> items = new ArrayList<>();
        ArrayList<Displacer> disp = new ArrayList<>();

        Character redBall = new Character(150, 50, 20, 50);
        redBall.body.setFill(Color.RED);
        pane.getChildren().add(redBall.body);
        // characterArrayList.add(redBall); // Only if managing multiple characters this way

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

        // RectangleObstacle(Pane pane, double centerX, double centerY, double width, double height, double angleDegrees, Color color)
        RectangleObstacle obs4 = new RectangleObstacle(pane, 1000, 650, 300, 50, 15.0, Color.BLUE);
        obstacleArrayList.add(obs4);
        RectangleObstacle obs5 = new RectangleObstacle(pane, 200, 700, 250, 50, -10.0, Color.GREEN);
        obstacleArrayList.add(obs5);
        RectangleObstacle floor = new RectangleObstacle(pane, 600, 880, 1200, 40, 0.0, Color.DARKGRAY); // Example floor
        obstacleArrayList.add(floor);


        Scene scene = new Scene(pane, 1200, 900);
        stage.setScene(scene);
        stage.setTitle("Ball");
        stage.show();

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) redBall.movingLeft = true;
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) redBall.movingRight = true;
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                if (redBall.jumpCount < 2) { // Allow double jump if on ground or in air once
                    redBall.jumpCount++;
                    redBall.movingUp = true; // This will be an impulse
                }
            }
            if (event.getCode() == KeyCode.SPACE) redBall.specialTransport = true;
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) redBall.movingLeft = false;
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) redBall.movingRight = false;
            // movingUp is an impulse, so no key release needed to stop it in the same way as continuous movement
            // If W was for continuous upward thrust, then redBall.movingUp = false; would be needed.
            if (event.getCode() == KeyCode.SPACE) redBall.specialTransport = false;
        });

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdateNanos = -1;

            @Override
            public void handle(long now) {
                if (lastUpdateNanos < 0) {
                    lastUpdateNanos = now;
                    mainCanvasGc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear on first frame
                    return;
                }

                double frameDeltaTime = (now - lastUpdateNanos) / 1_000_000_000.0; // frameDeltaTime in seconds
                lastUpdateNanos = now;

                // Cap frameDeltaTime to prevent spiral of death if game lags significantly
                // Max time step for accumulation, e.g., 0.1s (equiv. to 10 FPS)
                // This prevents the physics loop from trying to catch up too much after a long pause.
                frameDeltaTime = Math.min(frameDeltaTime, 0.1);
                mainCanvasGc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                accumulator += frameDeltaTime;

                while (accumulator >= FIXED_PHYSICS_DT) {
                    updateGamePhysics(obstacleArrayList, items, disp, redBall, pane);
                    accumulator -= FIXED_PHYSICS_DT;
                }

                // Rendering part - always happens once per AnimationTimer frame



                // Game elements are mostly JavaFX shapes added to the pane,
                // their positions are updated in updateGamePhysics.
                // The GrapplePoint's line, if active, is drawn within updateGamePhysics.
                // If you had other elements to draw directly on the canvas each frame (not as Shapes in Pane),
                // you would do it here, possibly using interpolated positions for ultimate smoothness if
                // rendering rate > physics rate. For now, direct state rendering is fine.
            }
        };
        timer.start();
    }

    private void updateGamePhysics(ArrayList<Obstacle> obstacleArrayList,
                                   ArrayList<Collectible> items, ArrayList<Displacer> disp,
                                   Character redBall, Pane pane) {

        // 1. Apply forces (Gravity, Input)
        redBall.v.add(0, GRAVITY * Main.FIXED_PHYSICS_DT);

        if (redBall.movingLeft && redBall.v.getX() > -NATURAL_SPEED_LIM) {
            redBall.v.add(-MOVE_ACCELERATION * Main.FIXED_PHYSICS_DT, 0);
        }
        if (redBall.movingRight && redBall.v.getX() < NATURAL_SPEED_LIM) {
            redBall.v.add(MOVE_ACCELERATION * Main.FIXED_PHYSICS_DT, 0);
        }
        if (redBall.movingUp) { // Jump is an impulse
            redBall.v.setY(-450); // Adjusted jump velocity, tune as needed
            redBall.movingUp = false;
        }

        // 2. Clamp velocity (max speed limits)
        redBall.v.setX(Math.max(-MAX_MOVE_SPEED, Math.min(redBall.v.getX(), MAX_MOVE_SPEED)));
        redBall.v.setY(Math.max(-MAX_MOVE_SPEED, Math.min(redBall.v.getY(), MAX_MOVE_SPEED))); // MAX_MOVE_SPEED for Y might be very high

        // 3. Collision Detection and Resolution with Obstacles
        boolean characterCollidedWithObstacle = false;
        for (Obstacle obs : obstacleArrayList) {
            double displacementX = redBall.v.getX() * Main.FIXED_PHYSICS_DT;
            double displacementY = redBall.v.getY() * Main.FIXED_PHYSICS_DT;
            if (obs.checkCollision(redBall, displacementX, displacementY, Main.FIXED_PHYSICS_DT)) {
                characterCollidedWithObstacle = true;
                // The obstacle's checkCollision or its handleCollision (called internally)
                // should manage position correction and velocity response for that specific collision.
            }
        }

        // 4. Collectibles
        Iterator<Collectible> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            Collectible item = itemIterator.next();
            if (item.checkCollision(redBall)) {
                item.handleCollision(redBall);
                if (item.hitbox != null && item.hitbox.body != null) {
                    pane.getChildren().remove(item.hitbox.body);
                }
                itemIterator.remove();
            }
        }

        // 5. Displacers (e.g., GrapplePoint)
        for (Displacer d : disp) {
            if (d.checkCollision(redBall)) {
                if (d instanceof GrapplePoint gp) {
                    if (!gp.cooldown) {
                        // Draw line directly using mainCanvasGc
                        drawLine(this.mainCanvasGc, redBall.pos.x, redBall.pos.y, gp.pos.x, gp.pos.y);
                    }
                    if (redBall.specialTransport && !gp.cooldown) {
                        d.handleCollision(redBall); // GrapplePoint might need 'dt' if its effect is over time
                    }
                }
            }
        }

        // 6. Update position IF NO OBSTACLE COLLISION handled position
        // If an obstacle collision occurred, its handleCollision should have set the correct position.
        // If characterCollidedWithObstacle is true, we assume position and velocity are handled.
        // If false, apply standard Euler integration for position.
        if (!characterCollidedWithObstacle) {
            redBall.pos.add(redBall.v.getX() * Main.FIXED_PHYSICS_DT, redBall.v.getY() * Main.FIXED_PHYSICS_DT);
        }
        // Always sync visual body to logical position after all physics and collision responses.
        // Note: Obstacle handleCollision should update redBall.pos, this ensures body reflects it.
        redBall.body.setCenterX(redBall.pos.getX());
        redBall.body.setCenterY(redBall.pos.getY());


        // 7. Boundary Collisions (Pane edges)
        double restitutionBoundary = 0.4; // How much to bounce off pane boundaries
        // Ground
        if (redBall.body.getCenterY() + redBall.body.getRadius() > pane.getHeight()) {
            redBall.body.setCenterY(pane.getHeight() - redBall.body.getRadius());
            redBall.pos.setY(redBall.body.getCenterY());
            if (redBall.v.getY() > 0) redBall.v.setY(-redBall.v.getY() * restitutionBoundary);
            redBall.jumpCount = 0; // Reset jump count on ground
        }
        // Ceiling
        if (redBall.body.getCenterY() - redBall.body.getRadius() < 0) {
            redBall.body.setCenterY(redBall.body.getRadius());
            redBall.pos.setY(redBall.body.getCenterY());
            if (redBall.v.getY() < 0) redBall.v.setY(-redBall.v.getY() * restitutionBoundary);
        }
        // Left Wall
        if (redBall.body.getCenterX() - redBall.body.getRadius() < 0) {
            redBall.body.setCenterX(redBall.body.getRadius());
            redBall.pos.setX(redBall.body.getCenterX());
            if (redBall.v.getX() < 0) redBall.v.setX(-redBall.v.getX() * restitutionBoundary);
        }
        // Right Wall
        if (redBall.body.getCenterX() + redBall.body.getRadius() > pane.getWidth()) {
            redBall.body.setCenterX(pane.getWidth() - redBall.body.getRadius());
            redBall.pos.setX(redBall.body.getCenterX());
            if (redBall.v.getX() > 0) redBall.v.setX(-redBall.v.getX() * restitutionBoundary);
        }

        // Global friction/drag - apply this carefully.
        // This could be air resistance or a general damping.
        // If applied after specific collision responses, it will affect them.
        // For smoother sliding on surfaces, the surface's own friction should dominate.
        // Let's apply a very light air drag if NOT in collision with an obstacle that handled friction.
        if (!characterCollidedWithObstacle) {
            double airDragCoefficient = 0.01; // Very light drag
            redBall.v.setX(redBall.v.getX() * (1.0 - airDragCoefficient * Main.FIXED_PHYSICS_DT)); // Scale by dt for consistency
            redBall.v.setY(redBall.v.getY() * (1.0 - airDragCoefficient * Main.FIXED_PHYSICS_DT));
        }
        // The old `redBall.v = redBall.v.scale(FRICTION);` when `collide` was true is removed
        // to let obstacle-specific friction take precedence.

        // System.out.println(redBall.v.getX() + " " + redBall.v.getY() + " | Jump: " + redBall.jumpCount);
    }

    private void drawLine(GraphicsContext gc, double x0, double y0, double x1, double y1) {
        gc.save();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeLine(x0, y0, x1, y1);
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}