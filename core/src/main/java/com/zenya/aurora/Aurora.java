package com.zenya.aurora;

import com.zenya.aurora.api.LightAPI;
import com.zenya.aurora.command.AuroraCommand;
import com.zenya.aurora.event.Listeners;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.storage.YAMLFileManager;
import com.zenya.aurora.storage.TaskManager;
import com.zenya.aurora.storage.ParticleManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Aurora extends JavaPlugin {
    @Getter private static Aurora instance;
    private LightAPI lightAPI;
    private TaskManager taskManager;
    private YAMLFileManager yamlFileManager;
    private ParticleFileManager particleFileManager;
    private ParticleFileCache particleFileCache;

    @Override
    public void onEnable() {
        instance = this;

        //Init LightAPI
        lightAPI = LightAPI.INSTANCE;

        //Register all runnables
        taskManager = TaskManager.INSTANCE;

        //Init all configs and particle files
        yamlFileManager = YAMLFileManager.INSTANCE;
        particleFileManager = ParticleFileManager.INSTANCE;
        particleFileCache = ParticleFileCache.INSTANCE;

        //Register events
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);

        //Register commands
        this.getCommand("aurora").setExecutor(new AuroraCommand());
    }

    @Override
    public void onDisable() {
        ParticleManager pm = ParticleManager.INSTANCE;
        for(Player player : pm.getPlayers()) {
            pm.unregisterTasks(player);
        }
        lightAPI.disable();
    }
}