package me.Jedi.minimap.util.geometry;

public class SlopeIntLine {
    double slope;
    double yIntercept;

    public SlopeIntLine(double slope, double yIntercept) {
        this.slope = slope;
        this.yIntercept = yIntercept;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public double getYIntercept() {
        return yIntercept;
    }

    public void setyIntercept(double yIntercept) {
        this.yIntercept = yIntercept;
    }

    public static SlopeIntLine fromTwoPoints(Point2d point1, Point2d point2) {
        double m = (point2.getX()-point1.getX())/(point2.getY()-point1.getX());
        double b = point1.getY()/(m*point1.getX());
        return new SlopeIntLine(m, b);
    }

    public static SlopeIntLine fromPointSlope(Point2d point, double slope) {
        //y=mx+b
        //y/(mx) = b
        return new SlopeIntLine(slope, point.getY()/(slope*point.getX()));
    }
}
