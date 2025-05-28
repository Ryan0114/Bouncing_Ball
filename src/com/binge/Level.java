package com.binge;

import java.util.*;

import static com.binge.Main.character;
import static com.binge.Main.pane;

public class Level {
    int levelLength, index;
    ArrayList<Sublevel> sublevels;
    ArrayList<Checkpoint> checkpoints;
    Checkpoint lastCheckpoint;

    Level(int n) {
        this.levelLength = 0;
        this.index = n;
        this.sublevels = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
    }

    void revive(Character c) {
        if (lastCheckpoint==null) {
            terminate(c);
        } else {
            character.sublevelNum = this.lastCheckpoint.substageNum;
            pane = this.sublevels.get(character.sublevelNum).pane;
            pane.getChildren().add(Main.canvas);
            Main.scene.setRoot(pane);

            Main.currentSublevel = this.sublevels.get(character.sublevelNum);
        }
    }

    void terminate(Character c) {

    }
}