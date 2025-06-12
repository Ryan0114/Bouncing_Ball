package com.binge;

import com.binge.LaserObstacle; // by Joe
import com.binge.SpiralMissileLauncherObstacle;
import com.binge.LaserObstacle.LaserOrientation;
import com.binge.SpinningLaserObstacle;
import com.binge.TrackingLaserObstacle;
import com.binge.HomingMissileLauncherObstacle;

import java.io.*;
import java.util.Random;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import static com.binge.Main.*;

public class PageLoader {

    // 背景漸變顏色
    private static final LinearGradient BG_GRADIENT = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#1a2a6c")),
            new Stop(0.5, Color.web("#b21f1f")),
            new Stop(1, Color.web("#1a2a6c"))
    );

    // 按鈕樣式
    private static final String BUTTON_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #4CAF50, #2E7D32); " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 18px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 10 20; " +
                    "-fx-background-radius: 15; " +
                    "-fx-border-radius: 15; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-color: #1B5E20; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);";

    private static final String BUTTON_HOVER_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #66BB6A, #388E3C); " +
                    "-fx-cursor: hand;";

    // 標題陰影效果
    private static final Effect TITLE_SHADOW = new DropShadow(10, Color.GOLD);

    // 漸層
    private static final LinearGradient LEVEL_BG = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#0f2027")),
            new Stop(0.5, Color.web("#203a43")),
            new Stop(1, Color.web("#2c5364"))
    );

    // 星空
    private static void createStarBackground(Pane pane) {
        // 尺寸
        double width = pane.getWidth() > 0 ? pane.getWidth() : 1200;
        double height = pane.getHeight() > 0 ? pane.getHeight() : 800;

        Canvas bgCanvas = new Canvas(width, height);
        GraphicsContext gc = bgCanvas.getGraphicsContext2D();

        // 漸層
        gc.setFill(LEVEL_BG);
        gc.fillRect(0, 0, width, height);

        // 星星
        gc.setFill(Color.WHITE);
        Random rand = new Random();
        for (int i = 0; i < 200; i++) {
            double x = rand.nextDouble() * width;
            double y = rand.nextDouble() * height;
            double size = rand.nextDouble() * 1.5 + 0.5;

            // 隨機閃爍
            double opacity = 0.5 + rand.nextDouble() * 0.5;
            gc.setFill(Color.rgb(255, 255, 255, opacity));

            gc.fillOval(x, y, size, size);
        }

        // 星系效果
        gc.setFill(new RadialGradient(
                0, 0, 0.7, 0.3, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(150, 40, 200, 0.2)),
                new Stop(1, Color.TRANSPARENT)
        ));
        gc.fillOval(width * 0.8, height * 0.2, 300, 300);

        gc.setFill(new RadialGradient(
                0, 0, 0.3, 0.7, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(40, 150, 200, 0.2)),
                new Stop(1, Color.TRANSPARENT)
        ));
        gc.fillOval(width * 0.2, height * 0.7, 400, 400);

        // 确保背景在最底層
        pane.getChildren().add(0, bgCanvas);

        // 尺寸調整
        pane.widthProperty().addListener((obs, oldVal, newVal) -> {
            bgCanvas.setWidth(newVal.doubleValue());
            drawBackground(bgCanvas);
        });

        pane.heightProperty().addListener((obs, oldVal, newVal) -> {
            bgCanvas.setHeight(newVal.doubleValue());
            drawBackground(bgCanvas);
        });
    }

    // 重绘背景
    private static void drawBackground(Canvas bgCanvas) {
        GraphicsContext gc = bgCanvas.getGraphicsContext2D();
        double width = bgCanvas.getWidth();
        double height = bgCanvas.getHeight();

        gc.clearRect(0, 0, width, height);
        gc.setFill(LEVEL_BG);
        gc.fillRect(0, 0, width, height);

        // 绘制星星
        gc.setFill(Color.WHITE);
        Random rand = new Random();
        for (int i = 0; i < 200; i++) {
            double x = rand.nextDouble() * width;
            double y = rand.nextDouble() * height;
            double size = rand.nextDouble() * 1.5 + 0.5;

            double opacity = 0.5 + rand.nextDouble() * 0.5;
            gc.setFill(Color.rgb(255, 255, 255, opacity));

            gc.fillOval(x, y, size, size);
        }

        // 添加星系效果
        gc.setFill(new RadialGradient(
                0, 0, 0.7, 0.3, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(150, 40, 200, 0.2)),
                new Stop(1, Color.TRANSPARENT)
        ));
        gc.fillOval(width * 0.8, height * 0.2, 300, 300);

        gc.setFill(new RadialGradient(
                0, 0, 0.3, 0.7, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(40, 150, 200, 0.2)),
                new Stop(1, Color.TRANSPARENT)
        ));
        gc.fillOval(width * 0.2, height * 0.7, 400, 400);
    }

    public static void loadMainPage() {
        pane.getChildren().clear();
        pane.setBackground(new Background(new BackgroundFill(BG_GRADIENT, null, null)));
        character.lastCheckpoint=null;
        // 標題設計 - 保留原有位置但美化樣式
        Text title = new Text("Ball");
        title.setLayoutX(300);
        title.setLayoutY(300);
        title.setFont(Font.font("Arial Rounded MT Bold", 80));
        title.setFill(new LinearGradient(0,0,1,1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.YELLOW),
                new Stop(0.5, Color.ORANGE),
                new Stop(1, Color.RED)
        ));
        title.setEffect(TITLE_SHADOW);
        pane.getChildren().add(title);

        // 按鈕美化
        Button selectLevels = createStyledButton("Play");
        selectLevels.setLayoutX(400);
        selectLevels.setLayoutY(400);
        pane.getChildren().add(selectLevels);

        selectLevels.setOnAction(event1 -> {
            pane.getChildren().removeAll();
            loadSelectStage();
        });

        Button customLevel = createStyledButton("Custom level");
        customLevel.setLayoutX(380);
        customLevel.setLayoutY(460);
        pane.getChildren().add(customLevel);
        customLevel.setOnAction(event2 -> {
            loadCustomStage();
        });
        if (!currentSublevel.pane.getChildren().contains(Main.coinCounterText)) {
            currentSublevel.pane.getChildren().add(Main.coinCounterText);
        }
        if (Main.scene==null) Main.scene = new Scene(pane, 1200, 800);
        else Main.scene.setRoot(pane);
        Main.ensureCoinCounterDisplayed(pane);
    }

    public static void loadSelectStage() {
        pane.getChildren().clear();
        pane.setBackground(new Background(new BackgroundFill(BG_GRADIENT, null, null)));
        character.lastCheckpoint=null;
        // 返回按鈕美化
        Button mainPage = createStyledButton("Main page");
        mainPage.setLayoutX(40);
        mainPage.setLayoutY(40);
        pane.getChildren().add(mainPage);

        mainPage.setOnAction(actionEvent -> {
            loadMainPage();
        });

        // 關卡按鈕美化
        Button stageBtn = createStyledButton("Stage 1");
        stageBtn.setLayoutX(360);
        stageBtn.setLayoutY(400);
        pane.getChildren().add(stageBtn);

        stageBtn.setOnAction(e -> {
            character.inGame = true;
            character.levelNum = 1;
            character.sublevelNum = 1;
            character.jumpCount=2;
            loadStage(1);
        });

        Button stageBtn2 = createStyledButton("Stage 2");
        stageBtn2.setLayoutX(490);
        stageBtn2.setLayoutY(400);
        pane.getChildren().add(stageBtn2);

        stageBtn2.setOnAction(e -> {
            character.inGame = true;
            character.levelNum = 2;
            character.sublevelNum = 2;
            character.jumpCount=2;
            loadStage(2);
        });

        Button stageBtn3 = createStyledButton("Stage 3");
        stageBtn3.setLayoutX(620);
        stageBtn3.setLayoutY(400);
        pane.getChildren().add(stageBtn3);

        stageBtn3.setOnAction(e -> {
            character.inGame = true;
            character.levelNum = 3;
            character.sublevelNum = 3;
            character.jumpCount=2;
            loadStage(3);
        });

        Button stageBtn4 = createStyledButton("Stage 4");
        stageBtn4.setLayoutX(750);
        stageBtn4.setLayoutY(400);
        pane.getChildren().add(stageBtn4);

        stageBtn4.setOnAction(e -> {
            character.inGame = true;
            character.levelNum = 4;
            character.sublevelNum = 4;
            character.jumpCount=2;
            loadStage(4);
        });

        Main.scene.setRoot(pane);
        Main.ensureCoinCounterDisplayed(pane);
    }


    public static void loadStage(int n) {
        Level level = new Level(n);
        String path = "src/com/binge/Stages/stage" + n + "/";
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                Sublevel sublevel = loadStageFromFile(path + child.getName(), level.levelLength+1);
                level.sublevels.add(sublevel);
                level.checkpoints.add(sublevel.checkpoint);
                if (sublevel.checkpoint != null) sublevel.checkpoint.substageNum = level.sublevels.size();
                level.levelLength += 1;
            }
        }
        character.levelNum = n;
        character.sublevelNum = 1;
        pane = level.sublevels.getFirst().pane;
        pane.getChildren().add(canvas);
        Main.scene.setRoot(pane);

        Main.currentLevel = level;
        Main.currentSublevel = level.sublevels.getFirst();

        if (canvas.getParent() != null) {
            ((Pane) canvas.getParent()).getChildren().remove(canvas);
        }
        createStarBackground(Main.currentSublevel.pane);

        pane = Main.currentSublevel.pane;
        if (!pane.getChildren().contains(canvas)) {
            pane.getChildren().add(canvas);
        }

        Main.scene.setRoot(pane);
        Main.ensureCoinCounterDisplayed(pane);
    }

    public static Sublevel loadStageFromFile(String filename, int n) {
        Sublevel sublevel = new Sublevel(n);

//        Main.obstacles.clear();
//        Main.items.clear();
//        Main.displacers.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
//            pane.getChildren().clear();
//
//            stage.getChildren().add(Main.canvas);

            String line;
            String section = "";
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("stage")) {
                    continue;
                } else if (line.equals("initial position") ||line.equals("CircleObstacle") ||
                        line.equals("RectangleObstacle") || line.equals("Coin") ||
                        line.equals("SizeShifter") || line.equals("GrapplePoint") || line.equals("Checkpoint") ||
                        line.equals("CircleTrap") || line.equals("Goal") || line.equals("Lock") ||
                        line.equals("LaserObstacle") || line.equals("VerticalLaserObstacle") || line.equals("SpinningLaserObstacle")
                        || line.equals("TrackingLaserObstacle") || line.equals("HomingMissileLauncherObstacle") ||
                        line.equals("SpiralMissileLauncherObstacle") || line.equals("Text")) {
                    section = line;
                } else {
                    String[] tokens = line.split("\\s+");
                    switch (section) {
                        case "initial position":
                            if (tokens.length >= 2) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                //character = new Character(x, y, 20, Color.rgb(255,241,204));
                                character.pos.setX(x);
                                character.pos.setY(y);
                                sublevel.pane.getChildren().add(character.body);
                            }
                            break;
                        case "CircleObstacle":
                            if (tokens.length >= 3) {
                                CircleObstacle co = getCircleObstacle(tokens, sublevel);
                                sublevel.obstacles.add(co);
                            }
                            break;
                        case "RectangleObstacle":
                            if (tokens.length >= 5) {
                                double cx = Double.parseDouble(tokens[0]);
                                double cy = Double.parseDouble(tokens[1]);
                                double width = Double.parseDouble(tokens[2]);
                                double height = Double.parseDouble(tokens[3]);
                                double angle = Double.parseDouble(tokens[4]);
                                boolean fatal = false, destroyable = false;
                                if (tokens.length >= 7) {
                                    fatal = Boolean.parseBoolean(tokens[5]);
                                    destroyable = Boolean.parseBoolean(tokens[6]);
                                }
                                RectangleObstacle ro = new RectangleObstacle(sublevel.pane, cx, cy, width, height, angle, Color.GRAY, fatal, destroyable);
                                sublevel.obstacles.add(ro);
                            }
                            break;
                        case "Checkpoint":
                            if (tokens.length >= 2) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                sublevel.checkpoint = new Checkpoint(sublevel.pane, x, y);
                            }
                            break;
                        case "Coin":
                            if (tokens.length >= 4) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                int value = Integer.parseInt(tokens[3]);
                                Coin coin = new Coin(sublevel.pane, x, y, radius, value);
                                sublevel.items.add(coin);
                            }
                            break;
                        case "Lock":
                            if (tokens.length >= 4) {
                                double lockX = Double.parseDouble(tokens[0]);
                                double lockY = Double.parseDouble(tokens[1]);
                                double keyX = Double.parseDouble(tokens[2]);
                                double keyY = Double.parseDouble(tokens[3]);
                                Random rand = new Random();
                                Color color = new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0); // 1.0 is full opacity

                                Lock lock = new Lock(sublevel.pane, lockX, lockY, 30, 1000, color,
                                        keyX, keyY);
                                sublevel.locks.add(lock);
                            }
                            break;
                        case "SizeShifter":
                            if (tokens.length >= 4) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                int increment = Integer.parseInt(tokens[3]);
                                SizeShifter ss = new SizeShifter(sublevel.pane, x, y, radius, increment);
                                sublevel.items.add(ss);
                            }
                            break;
                        case "GrapplePoint":
                            if (tokens.length >= 3) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                GrapplePoint gp = new GrapplePoint(sublevel.pane, x, y, radius);
                                sublevel.displacers.add(gp);
                            }
                            break;
                        case "Goal":
                            if (tokens.length >= 1) {
                                double x = Double.parseDouble(tokens[0]);
                                sublevel.goal = new Goal(sublevel.pane, x);
                            }
                            break;
                        case "LaserObstacle":
                            if (tokens.length >= 3) {
                                double yPos = Double.parseDouble(tokens[0]);
                                double startX = Double.parseDouble(tokens[1]);
                                double endX = Double.parseDouble(tokens[2]);
                                double initialTimerOffset = 0.0; // Default value
                                if (tokens.length >= 4) {
                                    initialTimerOffset = Double.parseDouble(tokens[3]);
                                }
                                // Calculate initiallyOn based on offset, consistent with LaserObstacle constructor
                                boolean initiallyOn = (initialTimerOffset % 4.0) < 2.0; // Assuming cycleDuration=4.0, onDuration=2.0

                                boolean isPulsing = false;
                                double minThickness = 3.0; // DEFAULT_LASER_THICKNESS
                                double maxThickness = 3.0; // DEFAULT_LASER_THICKNESS
                                double pulseDuration = 1.0;

                                // tokens[4] is isPulsing
                                if (tokens.length > 4) {
                                    isPulsing = Integer.parseInt(tokens[4]) == 1;
                                    if (isPulsing) {
                                        // tokens[5] is minThickness, tokens[6] is maxThickness, tokens[7] is pulseDuration
                                        if (tokens.length > 7) {
                                            minThickness = Double.parseDouble(tokens[5]);
                                            maxThickness = Double.parseDouble(tokens[6]);
                                            pulseDuration = Double.parseDouble(tokens[7]);
                                        } else { // isPulsing is true, but not all 3 specific params given
                                            minThickness = 1.0;
                                            maxThickness = 5.0;
                                        }
                                    }
                                }

                                LaserObstacle laser = new LaserObstacle(
                                        sublevel.pane,
                                        LaserOrientation.HORIZONTAL,
                                        yPos, startX, endX,
                                        initiallyOn, initialTimerOffset,
                                        isPulsing, minThickness, maxThickness, pulseDuration // New params
                                );
                                sublevel.obstacles.add(laser);
                            }
                            break;
                        case "VerticalLaserObstacle":
                            if (tokens.length >= 3) {
                                LaserObstacle verticalLaser = getLaserObstacle(tokens, sublevel);
                                sublevel.obstacles.add(verticalLaser);
                            }
                            break;
                        case "SpinningLaserObstacle":
                            // Expected format: pivotX pivotY length initialAngleDeg rotationSpeedDegPerSec [timerOffset] [isPulsing] [minThick] [maxThick] [pulseDur]
                            if (tokens.length >= 5) {
                                double pivotX = Double.parseDouble(tokens[0]);
                                double pivotY = Double.parseDouble(tokens[1]);
                                double length = Double.parseDouble(tokens[2]);
                                double initialAngleDegrees = Double.parseDouble(tokens[3]);
                                double rotationSpeedDegrees = Double.parseDouble(tokens[4]);
                                double initialTimerOffsetSpin = 0.0; // Default
                                if (tokens.length >= 6) {
                                    initialTimerOffsetSpin = Double.parseDouble(tokens[5]);
                                }

                                Point2D pivot = new Point2D(pivotX, pivotY);

                                boolean isPulsingSpin = false;
                                double minThicknessSpin = 3.0; // DEFAULT_LASER_THICKNESS
                                double maxThicknessSpin = 3.0; // DEFAULT_LASER_THICKNESS
                                double pulseDurationSpin = 1.0;

                                // tokens[6] is isPulsing for spinning lasers
                                if (tokens.length > 6) {
                                    isPulsingSpin = Integer.parseInt(tokens[6]) == 1;
                                    if (isPulsingSpin) {
                                        // tokens[7] is minThickness, tokens[8] is maxThickness, tokens[9] is pulseDuration
                                        if (tokens.length > 9) {
                                            minThicknessSpin = Double.parseDouble(tokens[7]);
                                            maxThicknessSpin = Double.parseDouble(tokens[8]);
                                            pulseDurationSpin = Double.parseDouble(tokens[9]);
                                        } else { // isPulsing is true, but not all 3 specific params given
                                            minThicknessSpin = 1.0;
                                            maxThicknessSpin = 5.0;
                                        }
                                    }
                                }

                                SpinningLaserObstacle spinningLaser = new SpinningLaserObstacle(
                                        sublevel.pane,
                                        pivot, length,
                                        initialAngleDegrees, rotationSpeedDegrees,
                                        initialTimerOffsetSpin,
                                        isPulsingSpin, minThicknessSpin, maxThicknessSpin, pulseDurationSpin // New params
                                );
                                sublevel.obstacles.add(spinningLaser);
                            }
                            break;
                        case "TrackingLaserObstacle":
                            // Expected format: emitterX emitterY rotationSpeedDeg detectionRange beamLength chargeSecs fireSecs cooldownSecs [initialAngleDeg]
                            if (tokens.length >= 8) {
                                double emitterX = Double.parseDouble(tokens[0]);
                                double emitterY = Double.parseDouble(tokens[1]);
                                double rotationSpeedDeg = Double.parseDouble(tokens[2]);
                                double detectionRange = Double.parseDouble(tokens[3]);
                                double beamLength = Double.parseDouble(tokens[4]);
                                double chargeSecs = Double.parseDouble(tokens[5]);
                                double fireSecs = Double.parseDouble(tokens[6]);
                                double cooldownSecs = Double.parseDouble(tokens[7]);

                                double initialAngleDeg = 0.0; // Default initial angle
                                if (tokens.length >= 9) {
                                    initialAngleDeg = Double.parseDouble(tokens[8]);
                                }

                                Point2D emitterPos = new Point2D(emitterX, emitterY);

                                TrackingLaserObstacle trackingLaser = new TrackingLaserObstacle(
                                        sublevel.pane,
                                        emitterPos,
                                        rotationSpeedDeg,
                                        detectionRange,
                                        beamLength,
                                        chargeSecs,
                                        fireSecs,
                                        cooldownSecs,
                                        initialAngleDeg
                                );
                                sublevel.obstacles.add(trackingLaser);
                            }
                            break;
                        case "HomingMissileLauncherObstacle":
                            // New Format: emitterX emitterY rotSpeedDeg detectRange lockonSecs fireInterval numProjectilesInSpread spreadAngleDeg cooldownSecs projSpeed projTurnRateDeg projLifespan [initialAngleDeg]
                            if (tokens.length >= 12) { // Now 12 mandatory parameters
                                double emitterX = Double.parseDouble(tokens[0]);
                                double emitterY = Double.parseDouble(tokens[1]);
                                double rotSpeedDeg = Double.parseDouble(tokens[2]);
                                double detectRange = Double.parseDouble(tokens[3]);
                                double lockonSecs = Double.parseDouble(tokens[4]);
                                double fireInterval = Double.parseDouble(tokens[5]); // Still parsed, though current spread logic might not use it
                                int numProjectilesInSpread = Integer.parseInt(tokens[6]); // Formerly volleySize
                                double spreadAngleDegParam = Double.parseDouble(tokens[7]); // New spread angle param
                                double cooldownSecs = Double.parseDouble(tokens[8]);      // Index shifted
                                double projSpeed = Double.parseDouble(tokens[9]);         // Index shifted
                                double projTurnRateDeg = Double.parseDouble(tokens[10]); // Index shifted
                                double projLifespan = Double.parseDouble(tokens[11]);    // Index shifted

                                double initialAngleDeg = 0.0; // Default initial angle
                                if (tokens.length >= 13) { // Index shifted
                                    initialAngleDeg = Double.parseDouble(tokens[12]);
                                }

                                Point2D emitterPos = new Point2D(emitterX, emitterY);

                                HomingMissileLauncherObstacle launcher = new HomingMissileLauncherObstacle(
                                        sublevel.pane,
                                        emitterPos,
                                        rotSpeedDeg,
                                        detectRange,
                                        lockonSecs,
                                        fireInterval,
                                        numProjectilesInSpread, // Passed as numProjectilesInSpread
                                        spreadAngleDegParam,  // New argument
                                        cooldownSecs,
                                        projSpeed,
                                        projTurnRateDeg,
                                        projLifespan,
                                        initialAngleDeg
                                );
                                sublevel.obstacles.add(launcher);
                            } else {
                                System.err.println("HomingMissileLauncherObstacle: Not enough parameters. Expected at least 12, got " + tokens.length + " for line: " + java.util.Arrays.toString(tokens));
                            }
                            break;
                        case "SpiralMissileLauncherObstacle":
                            // Expected format: emitterX emitterY initialAimRotSpeedDeg spiralRotSpeedDeg detectionRange aimTimeSecs spiralFireDurSecs fireIntervalSecs cooldownSecs projSpeed projTurnRateDeg projLifespanSecs [initialAngleDeg]
                            if (tokens.length >= 12) { // 12 mandatory parameters
                                double emitterX = Double.parseDouble(tokens[0]);
                                double emitterY = Double.parseDouble(tokens[1]);
                                double initialAimRotSpeedDeg = Double.parseDouble(tokens[2]);
                                double spiralRotSpeedDeg = Double.parseDouble(tokens[3]);
                                double detectionRange = Double.parseDouble(tokens[4]);
                                double aimTimeSecs = Double.parseDouble(tokens[5]);
                                double spiralFireDurSecs = Double.parseDouble(tokens[6]);
                                double fireIntervalSecs = Double.parseDouble(tokens[7]);
                                double cooldownSecs = Double.parseDouble(tokens[8]);
                                double projSpeed = Double.parseDouble(tokens[9]);
                                double projTurnRateDeg = Double.parseDouble(tokens[10]);
                                double projLifespanSecs = Double.parseDouble(tokens[11]);

                                double initialAngleDeg = 0.0; // Default initial angle
                                if (tokens.length >= 13) {
                                    initialAngleDeg = Double.parseDouble(tokens[12]);
                                }

                                Point2D emitterPos = new Point2D(emitterX, emitterY);

                                SpiralMissileLauncherObstacle spiralLauncher = new SpiralMissileLauncherObstacle(
                                        sublevel.pane, // Pass the sublevel's pane
                                        emitterPos,
                                        initialAimRotSpeedDeg,
                                        spiralRotSpeedDeg,
                                        detectionRange,
                                        aimTimeSecs,
                                        spiralFireDurSecs,
                                        fireIntervalSecs,
                                        cooldownSecs,
                                        projSpeed,
                                        projTurnRateDeg,
                                        projLifespanSecs,
                                        initialAngleDeg
                                );
                                sublevel.obstacles.add(spiralLauncher);
                            } else {
                                System.err.println("SpiralMissileLauncherObstacle: Not enough parameters. Expected at least 12, got " + tokens.length + " for line: " + java.util.Arrays.toString(tokens));
                            }
                            break;

                        case "Text":
                            if (tokens.length >= 3) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);

                                // 將 tokens[2] 之後的內容全部合併成一句話
                                StringBuilder sb = new StringBuilder();
                                for (int i = 2; i < tokens.length; i++) {
                                    sb.append(tokens[i]);
                                    if (i < tokens.length - 1) sb.append(" ");
                                }
                                String message = sb.toString();

                                Text text = new Text(x, y, message);
                                text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                                text.setFill(Color.GREEN);
                                text.setStroke(Color.CYAN);
                                text.setStrokeWidth(1);

                                if (!sublevel.pane.getChildren().contains(text)) {
                                    sublevel.pane.getChildren().add(text);
                                }

                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
        }
        createStarBackground(sublevel.pane);
        return sublevel;
    }

    private static LaserObstacle getLaserObstacle(String[] tokens, Sublevel sublevel) {
        double xPos = Double.parseDouble(tokens[0]);     // primaryAxisPos (x for vertical)
        double startY = Double.parseDouble(tokens[1]);  // startSecondaryAxis (startY for vertical)
        double endY = Double.parseDouble(tokens[2]);    // endSecondaryAxis (endY for vertical)
        double initialTimerOffset = 0.0;
        if (tokens.length >= 4) {
            initialTimerOffset = Double.parseDouble(tokens[3]);
        }
        boolean initiallyOn = (initialTimerOffset % 4.0) < 2.0; // Consistent calculation

        boolean isPulsing = false;
        double minThickness = 3.0; // DEFAULT_LASER_THICKNESS
        double maxThickness = 3.0; // DEFAULT_LASER_THICKNESS
        double pulseDuration = 1.0;

        // tokens[4] is isPulsing
        if (tokens.length > 4) {
            isPulsing = Integer.parseInt(tokens[4]) == 1;
            if (isPulsing) {
                // tokens[5] is minThickness, tokens[6] is maxThickness, tokens[7] is pulseDuration
                if (tokens.length > 7) {
                    minThickness = Double.parseDouble(tokens[5]);
                    maxThickness = Double.parseDouble(tokens[6]);
                    pulseDuration = Double.parseDouble(tokens[7]);
                } else { // isPulsing is true, but not all 3 specific params given
                    minThickness = 1.0;
                    maxThickness = 5.0;
                }
            }
        }

        LaserObstacle verticalLaser = new LaserObstacle(
                sublevel.pane,
                LaserOrientation.VERTICAL,
                xPos, startY, endY,
                initiallyOn, initialTimerOffset,
                isPulsing, minThickness, maxThickness, pulseDuration // New params
        );
        return verticalLaser;
    }

    private static CircleObstacle getCircleObstacle(String[] tokens, Sublevel sublevel) {
        double x = Double.parseDouble(tokens[0]);
        double y = Double.parseDouble(tokens[1]);
        int radius = Integer.parseInt(tokens[2]);
        boolean fatal = false, destroyable = false;
        if (tokens.length >= 5) {
            fatal = Boolean.parseBoolean(tokens[3]);
            destroyable = Boolean.parseBoolean(tokens[4]);
        }
        CircleObstacle co = new CircleObstacle(sublevel.pane, x, y, radius, Color.GRAY, fatal, destroyable);
        return co;
    }

    public static void loadCustomStage() {
        Pane custom = new Pane(canvas);

    }

    public static void loadFinishPage() {
        Pane finishPage = new Pane(canvas);
        finishPage.setBackground(new Background(new BackgroundFill(BG_GRADIENT, null, null)));
        Main.scene.setRoot(finishPage);
        character.lastCheckpoint=null;
        character.v.setX(0);
        character.v.setY(0);
        Text congratulation = new Text("Congratulations!");
        congratulation.setFont(Font.font("Arial", 60));
        congratulation.setFill(Color.GOLD);
        congratulation.setEffect(new DropShadow(15, Color.BLACK));
        congratulation.setLayoutX(30);
        congratulation.setLayoutY(80);
        finishPage.getChildren().add(congratulation);

        // 美化按鈕

        if(currentLevel.index!=4){
            Button nextStage = createStyledButton("Next Stage");
            nextStage.setLayoutX(600);
            nextStage.setLayoutY(440);
            nextStage.setOnAction(e -> {
            character.inGame = true;
            character.levelNum = character.levelNum+1;
            character.sublevelNum = character.sublevelNum+1;
            character.jumpCount = 2;
            loadStage(currentLevel.index+1);

            });
            finishPage.getChildren().add(nextStage);
        }
        else{
            Text Thanks = new Text("Thank you for playing!");
            Thanks.setFont(Font.font("Arial", 100));
            Thanks.setFill(Color.LIGHTGOLDENRODYELLOW);
            Thanks.setEffect(new DropShadow(15, Color.BLACK));
            Thanks.setLayoutX(100);
            Thanks.setLayoutY(300);
            Thanks.setFill(new LinearGradient(0,0,1,1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.YELLOW),
                    new Stop(0.5, Color.LIGHTGREEN),
                    new Stop(1, Color.CYAN)
            ));
            finishPage.getChildren().add(Thanks);
        }


        Button selectStage = createStyledButton("Select Stage");
        selectStage.setLayoutX(600);
        selectStage.setLayoutY(495);
        selectStage.setOnAction(event_selectStage -> loadSelectStage());
        finishPage.getChildren().add(selectStage);

        Button mainPage = createStyledButton("Main Page");
        mainPage.setLayoutX(600);
        mainPage.setLayoutY(550);
        mainPage.setOnAction(event_mainPage -> loadMainPage());
        finishPage.getChildren().add(mainPage);

        Button quit = createStyledButton("Quit");
        quit.setLayoutX(600);
        quit.setLayoutY(605);
        quit.setOnAction(event_quit -> Platform.exit());
        finishPage.getChildren().add(quit);
        Main.ensureCoinCounterDisplayed(pane);
    }

    public static void loadDeathPage() {
        Pane deathPage = new Pane();
        deathPage.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0,0,1,1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#8E0E00")),
                        new Stop(1, Color.web("#1F1C18"))
                ), null, null)));
        Main.scene.setRoot(deathPage);

        Text youDied = new Text("YOU DIED");
        youDied.setFont(Font.font("Arial", 60));
        youDied.setFill(Color.RED);
        youDied.setEffect(new Glow(0.8));
        youDied.setLayoutX(30);
        youDied.setLayoutY(80);
        deathPage.getChildren().add(youDied);

        // 美化按鈕
        Button retry = createStyledButton("Retry");
        retry.setLayoutX(600);
        retry.setLayoutY(440);
        retry.setOnAction(event_retry -> {
            character.inGame = true;
            loadStage(currentLevel.index);
        });
        deathPage.getChildren().add(retry);

        Button selectStage = createStyledButton("Select Stage");
        selectStage.setLayoutX(600);
        selectStage.setLayoutY(495);
        selectStage.setOnAction(event_selectStage -> loadSelectStage());
        deathPage.getChildren().add(selectStage);

        Button mainPage = createStyledButton("Main Page");
        mainPage.setLayoutX(600);
        mainPage.setLayoutY(550);
        mainPage.setOnAction(event_mainPage -> loadMainPage());
        deathPage.getChildren().add(mainPage);

        Button quit = createStyledButton("Quit");
        quit.setLayoutX(600);
        quit.setLayoutY(605);
        quit.setOnAction(event_quit -> Platform.exit());
        deathPage.getChildren().add(quit);
        Main.ensureCoinCounterDisplayed(pane);
    }

    private static Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BUTTON_STYLE);
        btn.setOnMouseEntered(e -> btn.setStyle(BUTTON_STYLE + BUTTON_HOVER_STYLE));
        btn.setOnMouseExited(e -> btn.setStyle(BUTTON_STYLE));
        return btn;
    }
}