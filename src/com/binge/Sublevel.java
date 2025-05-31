package com.binge;

import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class Sublevel {
    int num;
    ArrayList<Obstacle> obstacles;
    ArrayList<Collectible> items;
    ArrayList<Displacer> displacers;
    Checkpoint checkpoint;
    Pane pane;

    Sublevel(int n) {
        this.num = n;
        this.pane = new Pane();
        this.obstacles = new ArrayList<>();
        this.items = new ArrayList<>();
        this.displacers = new ArrayList<>();
    }
}
