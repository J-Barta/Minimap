package me.Jedi.minimap.util.minecraft;

import jdk.jfr.Frequency;
import me.Jedi.minimap.util.geometry.Point2d;
import me.Jedi.minimap.util.math.MathFunctions;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.Jedi.minimap.Minimap.pl;

public class ParticleHandler {

    public static void ParticleCircle(Location center, double radius, int points, Player player, Particle particle, int particleNumber) {

        double currentAngle = 0;
        double angleDifference = 360.0/points;

        pl.getLogger().info("(" + center.getX() + ", " + center.getY() + ", " + center.getZ() + ")");

        for(int p = 1; p <= points; p++) {

            double xPoint = Math.cos(Math.toRadians(currentAngle))*radius;
            double yPoint = Math.sin(Math.toRadians(currentAngle))*radius;


            Location actualLocation = center;
            actualLocation.setX(actualLocation.getX() + xPoint);
            actualLocation.setZ(actualLocation.getZ() + yPoint);

            pl.getLogger().info("Circle point " + p + " ("  + actualLocation.getX() + ", " + actualLocation.getZ() + "). Original: (" + xPoint + ", " + yPoint + ")");

            center.getWorld().spawnParticle(particle, actualLocation.getX(), actualLocation.getY(), actualLocation.getZ(), particleNumber, 0, 0, 0, 0);

            currentAngle += angleDifference;
        }

    }

    public static void ParticleSquare(Location center, double sideLength, Particle particle, double frequency, double angle) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        double correctedAngleRadians = Math.toRadians((angle % 90) + 45);

        Point2d pointOffset1 = new Point2d(Math.cos(Math.toRadians(correctedAngleRadians))*(sideLength/2), Math.sin(Math.toRadians(correctedAngleRadians))*(sideLength/2));
        correctedAngleRadians += Math.toRadians(90);
        Point2d pointOffset2 = new Point2d(Math.cos(Math.toRadians(correctedAngleRadians))*(sideLength/2), Math.sin(Math.toRadians(correctedAngleRadians))*(sideLength/2));
        correctedAngleRadians += Math.toRadians(90);
        Point2d pointOffset3 = new Point2d(Math.cos(Math.toRadians(correctedAngleRadians))*(sideLength/2), Math.sin(Math.toRadians(correctedAngleRadians))*(sideLength/2));
        correctedAngleRadians += Math.toRadians(90);
        Point2d pointOffset4 = new Point2d(Math.cos(Math.toRadians(correctedAngleRadians))*(sideLength/2), Math.sin(Math.toRadians(correctedAngleRadians))*(sideLength/2));

        List<Location> corners = new ArrayList<>();

        corners.add(center.add(pointOffset1.getX(), 0, pointOffset1.getY()));
        corners.add(center.add(pointOffset2.getX(), 0, pointOffset2.getY()));
        corners.add(center.add(pointOffset3.getX(), 0, pointOffset3.getY()));
        corners.add(center.add(pointOffset4.getX(), 0, pointOffset4.getY()));


        for(Location corner : corners) {
            center.getWorld().spawnParticle(particle, corner, 1);
        }

        ParticleLine(corners.get(0), corners.get(1), particle, frequency);
        ParticleLine(corners.get(1), corners.get(2), particle, frequency);
        ParticleLine(corners.get(2), corners.get(3), particle, frequency);
        ParticleLine(corners.get(3), corners.get(0), particle, frequency);

    }

    public static void ParticleLine(Location location1, Location location2, Particle particle, double frequency) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String axis;

        if(location1.getX() != location2.getX()) {
            axis = "x";
        } else if(location1.getY() != location2.getY()) {
            axis = "y";
        } else if(location1.getZ() != location2.getZ()) {
            axis = "z";
        } else {
            axis = "none";
        }

        Location currentLocation = location1.clone();

        Vector addVector = new Vector(location2.getX() != location1.getX() ? (location2.getX()-location1.getX())/frequency : 0,
                location2.getY() != location1.getY() ? (location2.getY()-location1.getY())/frequency : 0,
                location2.getZ() != location1.getZ() ? (location2.getZ()-location1.getZ())/frequency : 0);


        double endLocation;
        String methodName = "";

        if(axis == "x") {
            endLocation = location2.getX();
            methodName = "getX";
        } else if (axis == "y"){
            endLocation = location2.getY();
            methodName = "getY";
        } else if( axis == "z") {
            endLocation = location2.getZ();
            methodName = "getZ";
        } else {
            pl.getLogger().info("The points can't be the same! (me.Jedi.minimap.ParticleHandler.ParticleLine");
            return;
        }

        Method method = Location.class.getDeclaredMethod(methodName);

        while((Double) method.invoke(currentLocation) != endLocation) {
            currentLocation.add(addVector);

            currentLocation.getWorld().spawnParticle(particle, currentLocation, 1);
        }

    }
}

