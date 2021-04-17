package com.zenya.aurora.worldguard;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.RegistryFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.util.Logger;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AmbientParticlesFlag {
    public static AmbientParticlesFlag INSTANCE = new AmbientParticlesFlag();
    @Getter private SetFlag<String> flag;

    public AmbientParticlesFlag() {
        try {
            flag = WGManager.INSTANCE.registerFlag(new SetFlag<>("ambient-particles", new StringFlag(null)));
        } catch (FlagConflictException exc) {
            Logger.logError("Unable to register WorldGuard flag \"ambient-particles\"");
            exc.printStackTrace();
        }
    }

    public List<ParticleFile> getParticles(Player player) {
        return getParticles(player.getLocation());
    }

    public List<ParticleFile> getParticles(Location loc) {
        ProtectedRegion global = WGManager.INSTANCE.getRegionManager(loc.getWorld()).getRegion("__global__");
        return getParticles(WGManager.INSTANCE.getApplicableRegionSet(loc), global);
    }

    private List<ParticleFile> getParticles(ApplicableRegionSet regions, ProtectedRegion global) {
        List<ParticleFile> enabledParticles = new ArrayList<>();
        //Add __global__ region
        if(global != null) regions.getRegions().add(global);

        for(ProtectedRegion region : regions.getRegions()) {
            if(region.getFlag(flag) != null && region.getFlag(flag).size() != 0) {
                for(String particleName : region.getFlag(flag)) {
                    ParticleFile particleFile = ParticleFileManager.INSTANCE.getParticleByName(particleName);
                    if (particleFile != null && !enabledParticles.contains(particleFile)) {
                        enabledParticles.add(particleFile);
                    }
                }
            }
        }
        return enabledParticles;
    }
}
