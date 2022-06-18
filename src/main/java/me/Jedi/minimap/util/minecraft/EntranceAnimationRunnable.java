package me.Jedi.minimap.util.minecraft;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;

public class EntranceAnimationRunnable extends BukkitRunnable {
    Player player;
    Location location;
    double radius;
    int particleNumber;
    Particle particle;
    int particlePoints;
    double sideLength;
    double frequency;
    double angle;

    public EntranceAnimationRunnable(Player player, Location location, double radius, int particleNumber, Particle particle, int particlePoints, double sideLength, double frequency, double angle) {
        this.player = player;
        this.location = location;
        this.radius = radius;
        this.particleNumber = particleNumber;
        this.particle = particle;
        this.particlePoints = particlePoints;
        this.sideLength = sideLength;
        this.frequency = frequency;
        this.angle = angle;
    }

    @Override
    public void run() {

        ParticleHandler.ParticleCircle(location, radius, particlePoints, player, particle, particleNumber);

        try {
            ParticleHandler.ParticleSquare(location, sideLength, particle, frequency, angle);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

}
