package me.Jedi.minimap.util.geometry;

import me.Jedi.minimap.util.math.MathFunctions;

import java.awt.*;

import static me.Jedi.minimap.util.math.MathFunctions.*;

public class Rectangle {
    Point2d corner1;
    Point2d corner2;

    public Rectangle(Point2d corner1, Point2d corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public boolean insideRectangle(Point2d point) {
        Line AB = new Line(corner1, new Point2d(corner1.getX(), corner2.getY()));
        Line BC = new Line(new Point2d(corner1.getX(), corner2.getY()), corner2);
        Line CD = new Line(corner2, new Point2d(corner2.getX(), corner1.getY()));
        Line DA = new Line(new Point2d(corner1.getX(), corner2.getY()), corner1);

        //TODO: Finish this logic (I'm stumped for now)

        return false;

    }

    public Point2d getCorner1() {
        return corner1;
    }

    public void setCorner1(Point2d corner1) {
        this.corner1 = corner1;
    }

    public Point2d getCorner2() {
        return corner2;
    }

    public void setCorner2(Point2d corner2) {
        this.corner2 = corner2;
    }
}
