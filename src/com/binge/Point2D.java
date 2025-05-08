package com.binge;

public class Point2D {
    double x, y;

    Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getX() {
        return this.x;
    }

    void setX(double x) {
        this.x = x;
    }

    double getY() {
        return this.y;
    }

    void setY(double y) {
        this.y = y;
    }

    void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    double getDistance(Point2D p) {
        Point2D dis = new Point2D(this.x-p.x, this.y-p.y);
        return dis.magnitude();
    }

    double dot(Point2D p) {
        return this.x * p.x + this.y * p.y;
    }
}