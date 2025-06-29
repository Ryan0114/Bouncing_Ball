package com.binge;

import com.binge.HomingLaserProjectile;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.util.*;

public class Main extends Application {
    public static Canvas canvas;
    public static Pane pane;
    public static Scene scene;
    private static Text pauseText;

    // Screen properties
    public static final int WINDOW_HEIGHT = 800;
    public static final int WINDOW_WIDTH = 1200;

    // Physics constants
    public static final double GRAVITY = 980;          // pixels per second squared
    private static final double MOVE_ACCELERATION = 600; // horizontal acceleration, pixels per second squared
    private static final double MAX_MOVE_SPEED = 1000;    // maximum horizontal speed
    private static final double NATURAL_SPEED_LIM = 500;
    public static final double FRICTION = 0.6;

    // containers
    public static Level currentLevel = new Level(0);
    public static Sublevel currentSublevel = new Sublevel(0);

    // For fixed timestep physics
    public static Timeline timeline;
    public static double FIXED_PHYSICS_DT = 1.0 / 60.0; // Physics update rate (e.g., 60Hz)
    public static GraphicsContext mainCanvasGc; // To allow drawLine from updateGamePhysics

    // Main character
    public static Character character = new Character(150, 50, 20, Color.rgb(255,241,204));

    static Text coinCounterText;

    @Override
    public void start(Stage stage) {

        canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainCanvasGc = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        //coins
        coinCounterText = new Text(10, 12, "Coins: 0");
        coinCounterText.setFont(new Font(20));
        coinCounterText.setFill(Color.GOLD);
        pane.getChildren().add(coinCounterText);


        Pane rootPane = new Pane();
        rootPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        rootPane.getChildren().add(coinCounterText);
        scene = new Scene(rootPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Space Ball");
        stage.show();
        PageLoader.loadMainPage();
        handleKeyEvent();
        scene.rootProperty().addListener((obs, oldRoot, newRoot) -> {
            if (newRoot instanceof Pane) {
                ensureCoinCounterDisplayed((Pane) newRoot);
            }
        });

        final Duration frameDuration = Duration.seconds(FIXED_PHYSICS_DT);

        timeline = new Timeline(new KeyFrame(frameDuration, e -> {
            if (character.inGame) {
                mainCanvasGc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                updateGamePhysics(character);
                updateGamePhysics(character.missile);
                coinCounterText.setText("Coins: " + character.coins);
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }
    public static void ensureCoinCounterDisplayed(Pane currentPane) {
        if (!currentPane.getChildren().contains(coinCounterText)) {
            currentPane.getChildren().add(coinCounterText);
        }
        coinCounterText.setLayoutX(10);
        coinCounterText.setLayoutY(12);
        coinCounterText.toFront();
    }

    private void updateGamePhysics(Character character) {

        // Apply forces (Gravity, Input)
        if (!(character instanceof Projectile)) character.v.add(0, GRAVITY * Main.FIXED_PHYSICS_DT);

        if (character.movingLeft && character.v.getX() > -NATURAL_SPEED_LIM) {
            character.v.add(-MOVE_ACCELERATION * Main.FIXED_PHYSICS_DT, 0);
        }
        if (character.movingRight && character.v.getX() < NATURAL_SPEED_LIM) {
            character.v.add(MOVE_ACCELERATION * Main.FIXED_PHYSICS_DT, 0);
        }
        if (character.movingUp) { // Jump is an impulse
            character.v.setY(-450); // Adjusted jump velocity, tune as needed
            character.movingUp = false;
        }

        // max speed limits
        character.v.setX(Math.max(-MAX_MOVE_SPEED, Math.min(character.v.getX(), MAX_MOVE_SPEED)));
        character.v.setY(Math.max(-MAX_MOVE_SPEED, Math.min(character.v.getY(), MAX_MOVE_SPEED))); // MAX_MOVE_SPEED for Y might be very high

        // Update all obstacles (e.g., for animations like blinking lasers)
        for (Obstacle obs : currentSublevel.obstacles) {
            obs.update(FIXED_PHYSICS_DT); // Use the fixed delta time
        }
        // Collision Detection with Obstacles
        boolean characterCollidedWithObstacle = false;
        List<Obstacle> toRemove = new ArrayList<>();
        double displacementX = character.v.getX() * Main.FIXED_PHYSICS_DT;
        double displacementY = character.v.getY() * Main.FIXED_PHYSICS_DT;
        for (Obstacle obs : currentSublevel.obstacles) {
            if (obs.checkCollision(character, displacementX, displacementY, Main.FIXED_PHYSICS_DT)) {
                characterCollidedWithObstacle = true;

                if (obs.destroyable && (character instanceof Projectile)) toRemove.add(obs);
            }
        }
        currentSublevel.obstacles.removeAll(toRemove);

        // Collectibles
        Iterator<Collectible> itemIterator = currentSublevel.items.iterator();
        while (itemIterator.hasNext()) {
            Collectible item = itemIterator.next();
            if (item.checkCollision(character)) {
                item.handleCollision(character);
                if (item.hitbox != null && item.hitbox.body != null) {
                    pane.getChildren().remove(item.hitbox.body);
                }
                itemIterator.remove();
            }
        }


        // Displacers (e.g., GrapplePoint)
        for (Displacer d : currentSublevel.displacers) {
            if (d.checkCollision(character)) {
                if ((d instanceof GrapplePoint gp) && !(character instanceof Projectile)) {
                    if (!gp.cooldown) {
                        // Draw line directly using mainCanvasGc
                        drawLine(mainCanvasGc, character.pos.x, character.pos.y, gp.pos.x, gp.pos.y);
                    }
                    if (character.specialTransport && !gp.cooldown) {
                        d.handleCollision(character); // GrapplePoint might need 'dt' if its effect is over time
                    }
                }
            }
        }

        // checkpoint collision
        for (Checkpoint c : currentLevel.checkpoints) {
            if (c != null && currentSublevel.num == c.substageNum) {
                c.checkCollision(character, displacementX, displacementY, Main.FIXED_PHYSICS_DT);
            }
        }

        // lock / key collision
        for (Lock l : currentSublevel.locks) {
            l.key.checkCollision(character);
            l.checkCollision(character, 0, 0, Main.FIXED_PHYSICS_DT);
        }

        if (currentSublevel.goal != null && !(character instanceof Projectile)) {
            currentSublevel.goal.checkCollision(character, 0, 0, Main.FIXED_PHYSICS_DT);
        }

        // --- Homing Laser Projectile Update and Collision ---
        Iterator<HomingLaserProjectile> projectileIterator = currentSublevel.activeProjectiles.iterator();
        while (projectileIterator.hasNext()) {
            HomingLaserProjectile projectile = projectileIterator.next();
            projectile.update(FIXED_PHYSICS_DT); // Update movement, lifespan, etc.

            // Check for collision with static obstacles first
            // If projectile hits an obstacle, its checkCollisionWithObstacles->setActive(false)
            // will also call removeFromPane().
            if (projectile.isActive()) { // Only check obstacle collision if still active after update
                projectile.checkCollisionWithObstacles(currentSublevel.obstacles);
            }

            // Then, if still active (didn't hit an obstacle), check for player collision
            // If projectile hits player, its checkCollisionWithPlayer->setActive(false)
            // will also call removeFromPane().
            if (projectile.isActive()) {
                if (projectile.checkCollisionWithPlayer(character)) {
                    character.revive(); // Player is hit
                }
            }

            // Finally, remove from list if inactive for any reason (lifespan, hit obstacle, hit player)
            if (!projectile.isActive()) {
                // projectile.removeFromPane() was already called by setActive(false)
                projectileIterator.remove();
            }
        }
        // --- End Homing Laser Projectile ---

        // 6. Update position
        if (!characterCollidedWithObstacle) {
            character.pos.add(character.v.getX() * Main.FIXED_PHYSICS_DT, character.v.getY() * Main.FIXED_PHYSICS_DT);
        }
        character.body.setCenterX(character.pos.getX());
        character.body.setCenterY(character.pos.getY());


        // 7. Boundary Collisions
        double restitutionBoundary = 0.4; // How much to bounce off pane boundaries
        // Ground collision
        if (character.body.getCenterY() + character.body.getRadius() > pane.getHeight()) {
            if (!(character instanceof Projectile)) character.revive();
        }
        // Ceiling collision
        if (character.body.getCenterY() - character.body.getRadius() < 0) {
            if (character instanceof Projectile) {
                ((Projectile) character).vanish();
            } else {
                character.body.setCenterY(character.body.getRadius());
                character.pos.setY(character.body.getCenterY());
                if (character.v.getY() < 0) character.v.setY(-character.v.getY() * restitutionBoundary);
            }
        }
        // Left Wall collision
        if (character.body.getCenterX() - character.body.getRadius() < 0) {
            if (character instanceof Projectile) {
                ((Projectile) character).vanish();
            } else {
                int lastSubstage = character.sublevelNum - 1;
                String lastStagePath = "src/com/binge/Stages/stage1/" + lastSubstage + ".in";
                File lastStageFile = new File(lastStagePath);

                if (character.sublevelNum - 1 >= 1) {
                    character.sublevelNum -= 1;
                    currentSublevel = currentLevel.sublevels.get(character.sublevelNum - 1);
                    scene.setRoot(currentSublevel.pane);
                    if (!currentSublevel.pane.getChildren().contains(canvas))
                        currentSublevel.pane.getChildren().add(canvas);
                    Point2D pos = character.pos;
                    pos.add(WINDOW_WIDTH - 2.5 * character.radius, 0);
                    character.pos = pos;
                    character.body.setCenterX(character.pos.getX());
                    character.body.setCenterY(character.pos.getY());
                    if (!currentSublevel.pane.getChildren().contains(character.body))
                        currentSublevel.pane.getChildren().add(character.body);
                } else {
                    character.body.setCenterX(character.body.getRadius());
                    character.pos.setX(character.body.getCenterX());
                    if (character.v.getX() < 0) character.v.setX(-character.v.getX() * restitutionBoundary);
                }
            }
        }
        // Right Wall collision
        if (character.body.getCenterX() + character.body.getRadius() > pane.getWidth()) {
            if (character instanceof Projectile) {
                ((Projectile) character).vanish();
            } else {
                int nextSubstage = character.sublevelNum + 1;
                String nextStagePath = "src/com/binge/Stages/stage1/" + nextSubstage + ".in";
                File nextStageFile = new File(nextStagePath);

                if (character.sublevelNum + 1 <= currentLevel.levelLength) {
                    character.sublevelNum += 1;
                    currentSublevel = currentLevel.sublevels.get(character.sublevelNum - 1);
                    scene.setRoot(currentSublevel.pane);
                    if (!currentSublevel.pane.getChildren().contains(canvas))
                        currentSublevel.pane.getChildren().add(canvas);
                    Point2D pos = character.pos;
                    pos.add(-WINDOW_WIDTH + 2.5 * character.radius, 0);
                    character.pos = pos;
                    character.body.setCenterX(character.pos.getX());
                    character.body.setCenterY(character.pos.getY());
                    if (!currentSublevel.pane.getChildren().contains(character.body))
                        currentSublevel.pane.getChildren().add(character.body);
                } else {
                    character.body.setCenterX(pane.getWidth() - character.body.getRadius());
                    character.pos.setX(character.body.getCenterX());
                    if (character.v.getX() > 0) {
                        character.v.setX(-character.v.getX() * restitutionBoundary);
                    }
                }
                coinCounterText.toFront();
            }
        }

        // air resistance
        if (!characterCollidedWithObstacle) {
            double airDragCoefficient = 0.01; // Very light drag
            character.v.setX(character.v.getX() * (1.0 - airDragCoefficient * Main.FIXED_PHYSICS_DT)); // Scale by dt for consistency
            character.v.setY(character.v.getY() * (1.0 - airDragCoefficient * Main.FIXED_PHYSICS_DT));
        }
    }

    private void handleKeyEvent() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) character.movingLeft = true;
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) character.movingRight = true;
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                if (character.jumpCount < 2) { // Allow double jump if on ground or in air once
                    character.jumpCount++;
                    character.movingUp = true; // This will be an impulse
                }
            }

            if (event.getCode() == KeyCode.P && character.inGame) {
                if (pauseText == null) {
                    pauseText = new Text(450, 350, "Pause");
                    pauseText.setFont(Font.font("Arial", FontWeight.BOLD, 100));
                    pauseText.setFill(Color.GREEN);
                    pauseText.setStroke(Color.CYAN);
                    pauseText.setStrokeWidth(3);
                }
                //unpause
                if (timeline.getStatus() == Animation.Status.PAUSED) {
                    if (pauseText != null) {
                        currentSublevel.pane.getChildren().remove(pauseText);
                    }
                    timeline.play();

                }
                //pause
                else {
                    timeline.pause();
                    if (!currentSublevel.pane.getChildren().contains(pauseText)) {
                        currentSublevel.pane.getChildren().add(pauseText);
                    }
                }
            }
            //shoot
            if (event.getCode() == KeyCode.F && character.inGame) {
                if (character.missile==null || !character.missile.activate) {
                    character.initMissile();
                }
                else {
                    character.missile.vanish();
                }
            }

            //hook
            if (event.getCode() == KeyCode.SPACE) character.specialTransport = true;
        });



        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) character.movingLeft = false;
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) character.movingRight = false;
            if (event.getCode() == KeyCode.SPACE) character.specialTransport = false;
        });
    }

    //Hook line
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