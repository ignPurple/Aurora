package com.zenya.aurora.particle;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.api.ParticleFactory;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.util.ext.ZParticleDisplay;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.util.ext.LightAPI;
import com.zenya.aurora.util.RandomNumber;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.beykerykt.minecraft.lightapi.common.api.engine.LightFlag;

public abstract class ParticleTask {
    private final Aurora plugin;

    public BukkitTask runnables[];
    public int locIndex = 0;

    public Player player;
    public Location locs[];
    public Particle particle;
    public ZParticleDisplay display;
    public int maxCount;
    public boolean lighting;
    public RandomNumber<Double> rate;
    public RandomNumber<Integer> update;
    public RandomNumber<Long> duration;
    public RandomNumber<Double> rotationAngle;
    public char rotationAxis;

    private final LightAPI lightAPI;

    public ParticleTask(Player player, Location[] locs, ParticleFile particleFile) {
        this.plugin = Aurora.getPlugin(Aurora.class);
        this.player = player;
        this.locs = locs;
        this.particle = Particle.valueOf(particleFile.getParticle().getParticleName());
        this.display = ParticleFactory.toDisplay(particle, player);
        this.maxCount = particleFile.getParticle().getMaxCount();
        this.lighting = plugin.getStorageFileManager().getConfig().getBool("enable-lighting") && particleFile.getParticle().isEnableLighting();
        this.rate = particleFile.getProperties().getRate();
        this.update = particleFile.getProperties().getUpdate();
        this.duration = particleFile.getProperties().getDuration();
        this.rotationAngle = particleFile.getProperties().getRotationAngle();
        this.rotationAxis = particleFile.getProperties().getRotationAxis();

        this.lightAPI = plugin.getLightAPI();
    }

    public abstract TaskKey getKey();

    public abstract BukkitTask generate();

    public Aurora getPlugin() {
        return this.plugin;
    }

    public Player getPlayer() {
        return player;
    }

    public BukkitTask[] getTasks() {
        return runnables;
    }

    public LightAPI getLightAPI() {
        return this.lightAPI;
    }

    public abstract void runTasks();

    public void killTasks(boolean isShutdown) {
        for (BukkitTask t : runnables) {
            t.cancel();
        }

        if (!lighting) {
            return;
        }

        for (Location loc : locs) {
            this.lightAPI.clearLight(loc, LightFlag.BLOCK_LIGHTING, true, isShutdown);
        }
    }
}
