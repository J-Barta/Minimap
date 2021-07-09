package me.Jedi.minimap.util.geometry;

import me.Jedi.minimap.util.exception.InvalidValueException;

import java.awt.*;
import java.util.IllegalFormatException;

public class Line {
    Point2d point1;
    Point2d point2;
    double slope;

    public Line(Point2d point1, Point2d point2) {
        this.point1 = point1;
        this.point2 = point2;
        this.slope = (point2.y-point1.y) / (point2.x-point1.x);
    }

    public Line(Point2d point1, double slope) throws InvalidValueException {
        if(point1.getX() == 0) {
            throw new InvalidValueException("You can't pass a y-intercept to me.Jedi.geometry.line Line(POint2d point1, double slope)");
        }
        this.point1 = point1;
        this.slope = slope;

        //We have point slope and need to get two points
        //y-y1 = m(x-x1)
        //y-y1 = m(0-x1)
        //y = m(-x1)+y1
        this.point2 = new Point2d(0, slope*(-point1.getX())+point1.getY());
    }

    public Point2d getPoint1() {
        return point1;
    }

    public void setPoint1(Point2d point1) {
        this.point1 = point1;
    }

    public Point2d getPoint2() {
        return point2;
    }

    public void setPoint2(Point2d point2) {
        this.point2 = point2;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }
}

