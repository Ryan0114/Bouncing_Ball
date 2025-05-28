package com.binge;

import java.util.*;

public class Level {
    int levelLength, index;
    ArrayList<Sublevel> sublevels;
    ArrayList<Checkpoint> checkpoints;

    Level(int n) {
        this.levelLength = 0;
        this.index = n;
        this.sublevels = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
    }
}