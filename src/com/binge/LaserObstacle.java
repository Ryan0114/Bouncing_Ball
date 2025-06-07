package com.binge;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape; // Required for the body property

public class LaserObstacle extends Obstacle {

    Point2D startPoint; // More general, but for horizontal, y will be same
    Point2D endPoint;   // More general, but for horizontal, y will be same
    // Or, for specifically horizontal:
    // double yPosition;
    // double startX;
    // double endX;

    boolean isOn;
    double cycleDuration = 4.0; // 2s on + 2s off
    double onDuration = 2.0;
    double timer; // Manages the blinking cycle

    Color onColor = Color.RED; // Laser color when active
    // Off color is handled by visibility, so no offColor field needed for stroke
    double thickness = 3.0;

    // Constructor for a horizontal laser
    public LaserObstacle(Pane pane, double yPos, double startX, double endX, boolean initiallyOn, double initialTimerOffset) {
        this.pos = new Point2D(startX + (endX - startX) / 2, yPos); // Midpoint, for Obstacle's pos
        this.startPoint = new Point2D(startX, yPos);
        this.endPoint = new Point2D(endX, yPos);
        
        this.fatal = true; // Lasers are fatal
        this.color = onColor; // Obstacle base color, can be the 'on' color

        this.timer = initialTimerOffset % cycleDuration;
        // Determine initial 'on' state based on timer and onDuration
        // If initialTimerOffset makes timer fall into onDuration, it's on.
        this.isOn = (this.timer < onDuration); 
        // Note: The 'initiallyOn' parameter is not directly used here if initialTimerOffset dictates state.
        // If 'initiallyOn' was meant to override timer for the very first state, logic would need adjustment.
        // For now, initialTimerOffset determines the starting point in the cycle.

        // Setup the visual representation (JavaFX Line)
        Line lineBody = new Line(startX, yPos, endX, yPos);
        lineBody.setStrokeWidth(thickness);
        lineBody.setStroke(this.onColor); // Always set to 'on' color
        lineBody.setVisible(this.isOn);   // Control visibility for blinking
        
        this.body = lineBody; // Assign to the inherited 'body' field
        pane.getChildren().add(this.body);
    }
    
    @Override
    public void update(double deltaTime) {
        timer = (timer + deltaTime) % cycleDuration;
        boolean newIsOn = (timer < onDuration);

        if (newIsOn != isOn) {
            isOn = newIsOn;
            this.body.setVisible(isOn);
        }
    }

    @Override
    boolean checkCollision(Character c, double dispX, double dispY, double deltaTime) {
        if (!isOn) {
            return false; // No collision if the laser is off
        }

        // Predicted character position (center of the circle)
        // For lasers, which are instantaneous hazards, using the current character position
        // might be sufficient, as they don't typically "push" the character.
        // If precise swept collision is needed, it's more complex. Let's start with current position.
        Point2D charPos = c.pos; // Current character center

        // Laser properties
        double laserY = this.startPoint.getY(); // y-coordinate of the horizontal laser
        double laserStartX = this.startPoint.getX();
        double laserEndX = this.endPoint.getX();

        // 1. Check for Y-axis overlap (character's circle vs. laser line's effective thickness)
        // The laser line itself has a visual thickness, but for collision,
        // we treat it as a line and see if the character's circle crosses it.
        // The character is hit if its center's Y is close enough to the laser's Y,
        // and its body (radius) crosses the laser line.
        boolean yOverlap = Math.abs(charPos.getY() - laserY) < c.radius;

        // 2. Check for X-axis overlap (character's circle vs. laser line segment)
        // The character's horizontal span is [charPos.getX() - c.radius, charPos.getX() + c.radius]
        // The laser's horizontal span is [laserStartX, laserEndX]
        boolean xOverlap = (charPos.getX() + c.radius > laserStartX) && // Char right edge past laser left edge
                           (charPos.getX() - c.radius < laserEndX);   // Char left edge before laser right edge

        if (yOverlap && xOverlap) {
            // Collision detected.
            // For lasers, the "normal" and "penetration" are less about physics response
            // and more about just detecting the hit. We can pass dummy values or null
            // if handleCollision for lasers doesn't use them.
            // The `handleCollision(Character c)` signature was a placeholder.
            // Let's assume we need to call the inherited fatal logic if it exists,
            // or directly call c.revive().
            // The Obstacle class doesn't have a generic handleCollision.
            // Our LaserObstacle has `handleCollision(Character c)`.
            handleCollision(c); // Call our specific handler
            return true;
        }

        return false;
    }

    // Placeholder for handleCollision method - will be detailed in a future step
    // It might need to match a specific signature if Obstacle's collision handling evolves.
    // For now, let's assume a simple one.
    public void handleCollision(Character c) {
        if (this.fatal && this.isOn) {
            c.revive();
        }
    }
}
