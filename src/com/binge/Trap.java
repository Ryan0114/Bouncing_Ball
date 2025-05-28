package com.binge;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public interface Trap {}

class CircleTrap extends CircleObstacle implements Trap {
    CircleTrap(Pane pane, double posX, double posY, int radius, Color color) {
        super(pane, posX, posY, radius, color);
    }

    @Override
    void handleCollision(Character c, Point2D normal, double penetration, double deltaTime) {

    }
}
