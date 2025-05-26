package com.binge;

import java.io.*;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StageLoader {

    public static void loadMainPage(Pane pane, Canvas canvas) {
        pane.getChildren().clear();

        Text title = new Text("Ball");
        title.setLayoutX(300);
        title.setLayoutY(300);
        title.setFont(new Font(80));
        pane.getChildren().add(title);

        Button selectLevels = new Button("Play");
        selectLevels.setLayoutX(400);
        selectLevels.setLayoutY(400);
        pane.getChildren().add(selectLevels);

        selectLevels.setOnAction(event1 -> {
            pane.getChildren().removeAll();
            loadSelectStage(pane, canvas);
        });

        Button customLevel = new Button("Custom level");
        customLevel.setLayoutX(380);
        customLevel.setLayoutY(440);
        pane.getChildren().add(customLevel);

//        customLevel.setOnAction(event2 -> {
//            pane.getChildren().removeAll();
//            loadCustomStage(pane, canvas);
//        });

    }

    public static void loadSelectStage(Pane pane, Canvas canvas) {
        pane.getChildren().clear();

        Button mainPage = new Button("main page");
        mainPage.setLayoutX(40);
        mainPage.setLayoutY(40);
        pane.getChildren().add(mainPage);

        mainPage.setOnAction(actionEvent -> {
            loadMainPage(pane, canvas);
        });

        Button stage = new Button("Stage 1");
        stage.setLayoutX(450);
        stage.setLayoutY(400);
        pane.getChildren().add(stage);

        stage.setOnAction(e -> {
            Main.character.currentStage = 1;
            Main.character.currentSubstage = 1;
            loadStageFromFile(pane, canvas, "src/com/binge/Stages/stage1/1.in");
        });
    }

    public static void loadStageFromFile(Pane pane, Canvas canvas, String filename) {
        Main.obstacles.clear();
        Main.items.clear();
        Main.displacers.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            pane.getChildren().clear();

            pane.getChildren().add(canvas);

            String line;
            String section = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("stage")) {
                    continue;
                } else if (line.equals("initial position") ||line.equals("CircleObstacle") ||
                        line.equals("RectangleObstacle") || line.equals("Coin") ||
                        line.equals("SizeShifter") || line.equals("GrapplePoint")) {
                    section = line;
                } else {
                    String[] tokens = line.split("\\s+");
                    switch (section) {
                        case "initial position":
                            if (tokens.length >= 2) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                pane.getChildren().remove(Main.character.body);
                                Main.character.pos.setX(x);
                                Main.character.pos.setY(y);
                                pane.getChildren().add(Main.character.body);
                            }
                            break;
                        case "CircleObstacle":
                            if (tokens.length >= 3) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                CircleObstacle co = new CircleObstacle(pane, x, y, radius, Color.GRAY);
                                Main.obstacles.add(co);
                            }
                            break;
                        case "RectangleObstacle":
                            if (tokens.length >= 5) {
                                double cx = Double.parseDouble(tokens[0]);
                                double cy = Double.parseDouble(tokens[1]);
                                double width = Double.parseDouble(tokens[2]);
                                double height = Double.parseDouble(tokens[3]);
                                double angle = Double.parseDouble(tokens[4]);
                                RectangleObstacle ro = new RectangleObstacle(pane, cx, cy, width, height, angle, Color.BLUE);
                                Main.obstacles.add(ro);
                            }
                            break;
                        case "Coin":
                            if (tokens.length >= 4) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                int value = Integer.parseInt(tokens[3]);
                                Coin coin = new Coin(pane, x, y, radius, value);
                                Main.items.add(coin);
                            }
                            break;
                        case "SizeShifter":
                            if (tokens.length >= 4) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                int increment = Integer.parseInt(tokens[3]);
                                SizeShifter ss = new SizeShifter(pane, x, y, radius, increment);
                                Main.items.add(ss);
                            }
                            break;
                        case "GrapplePoint":
                            if (tokens.length >= 3) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                GrapplePoint gp = new GrapplePoint(pane, x, y, radius);
                                Main.displacers.add(gp);
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
        }
    }

    public static void loadCustomStage(Pane pane, Canvas canvas) {

    }
}
