package com.binge;

import java.io.*;
import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class StageLoader {

    public static void loadMainPage(Pane pane) {
        pane.getChildren().removeAll();
        Text title = new Text("Ball");
        title.setLayoutX(300);
        title.setLayoutY(300);
        pane.getChildren().add(title);
    }

    public static void loadStageFromFile(String filename, Pane pane,
                                         ArrayList<Obstacle> obstacles, ArrayList<Collectible> items, ArrayList<Displacer> displacers) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String section = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("stage")) {
                    continue;
                } else if (line.equals("CircleObstacle") || line.equals("RectangleObstacle") || line.equals("Coin") ||
                        line.equals("SizeShifter") || line.equals("GrapplePoint")) {
                    section = line;
                } else {
                    String[] tokens = line.split("\\s+");
                    switch (section) {
                        case "CircleObstacle":
                            if (tokens.length >= 3) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                CircleObstacle co = new CircleObstacle(pane, x, y, radius, Color.GRAY);
                                obstacles.add(co);
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
                                obstacles.add(ro);
                            }
                            break;
                        case "Coin":
                            if (tokens.length >= 4) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                int value = Integer.parseInt(tokens[3]);
                                Coin coin = new Coin(pane, x, y, radius, value);
                                items.add(coin);
                            }
                            break;
                        case "SizeShifter":
                            if (tokens.length >= 4) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                int increment = Integer.parseInt(tokens[3]);
                                SizeShifter ss = new SizeShifter(pane, x, y, radius, increment);
                                items.add(ss);
                            }
                            break;
                        case "GrapplePoint":
                            if (tokens.length >= 3) {
                                double x = Double.parseDouble(tokens[0]);
                                double y = Double.parseDouble(tokens[1]);
                                int radius = Integer.parseInt(tokens[2]);
                                GrapplePoint gp = new GrapplePoint(pane, x, y, radius);
                                displacers.add(gp);
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
        }
    }
}
