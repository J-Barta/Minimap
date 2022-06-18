package me.Jedi.minimap.util.math;


import me.Jedi.minimap.util.geometry.Line;
import me.Jedi.minimap.util.geometry.Point2d;
import me.Jedi.minimap.util.geometry.SlopeIntLine;

import static java.lang.Math.*;

public class MathFunctions {

    public static double distance(double x1, double y1, double x2, double y2) {
        return sqrt(pow(x2-x1, 2) + pow(y2-y1, 2));
    }

    public static double distance(Point2d point1, Point2d point2) {
        return sqrt(pow(point2.getX()-point1.getX(), 2) + pow(point2.getY()-point1.getY(), 2));
    }

    public static double distance(Line line) {
        return sqrt(pow(line.getPoint2().getX()-line.getPoint1().getX(), 2) + pow(line.getPoint2().getY()-line.getPoint1().getY(), 2));
    }

    public static double distanceFromLine(Line line, Point2d point) {
        //y=mx+b
        //y/(mx)=b
        SlopeIntLine interceptionLine = SlopeIntLine.fromPointSlope(point, -1/line.getSlope());
        SlopeIntLine originalLine = SlopeIntLine.fromTwoPoints(line.getPoint1(), line.getPoint2());
        //Now we have a system of equations
        Point2d intersection = intersection(interceptionLine, originalLine);

        return distance(intersection, point);
    }

    public static Point2d intersection(SlopeIntLine line1, SlopeIntLine line2) {
        //y=m1x+b1
        //y=m2x+b2
        //m1x+b1 = m2x+b2
        //m1x - m2x = b2 - b1
        //(m1-m2)x = b2 - b1
        //x = (b2-b1)/(m1-m2)

        double x = (line2.getYIntercept()-line1.getYIntercept())/(line1.getSlope()-line2.getSlope());
        double y = line1.getSlope()*x + line1.getYIntercept();

        return new Point2d(x, y);



    }

}
