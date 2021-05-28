package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.scheduler.AuroraTask;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.scheduler.TrackLocationTask;
import com.zenya.aurora.scheduler.TrackTPSTask;
import com.zenya.aurora.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TaskManager {
    private static final String UUID = "%%__USER__%%";
    private static final List<String> BLACKLIST = new ArrayList<String>() {{
        add("126812");
    }};
    public static final TaskManager INSTANCE = new TaskManager();
    private HashMap<TaskKey, AuroraTask> taskMap = new HashMap<>();

    public TaskManager() {
        if(UUID.startsWith("%")) {
            Logger.logInfo("Thank you for helping to beta test Aurora :)");
        }
        if(BLACKLIST.contains(UUID)) {
            Logger.logInfo("You are currently using a leaked version of Aurora :(");
            Logger.logInfo("This plugin took me a whole lot of time, effort and energy to make <3");
            Logger.logInfo("If you like my work, consider purchasing a legitimate copy instead at");
            Logger.logInfo("https://www.spigotmc.org/resources/%E2%98%84%EF%B8%8Faurora%E2%98%84%EF%B8%8F-ambient-particle-display-customisable-per-biome.89399/");
            Logger.logError("Shame on Spigot user ID " + UUID + " for pirating my work D:");
            Bukkit.getServer().getPluginManager().disablePlugin(Aurora.getInstance());
            Bukkit.getServer().shutdown();
            return;
        }
        registerTask(TaskKey.TRACK_TPS_TASK, TrackTPSTask.INSTANCE);
        registerTask(TaskKey.TRACK_LOCATION_TASK, TrackLocationTask.INSTANCE);
    }

    public AuroraTask getTask(TaskKey key) {
        return taskMap.get(key);
    }

    public void registerTask(TaskKey key, AuroraTask task) {
        taskMap.put(key, task);
    }

    public void unregisterTasks() {
        for (Iterator<AuroraTask> iterator = taskMap.values().iterator(); iterator.hasNext(); ) {
            AuroraTask task = iterator.next();
            for(BukkitTask t : task.getTasks()) {
                t.cancel();
            }
            iterator.remove();
        }
    }
}


