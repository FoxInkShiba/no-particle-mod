package no.particle;

import net.minecraftforge.common.config.Configuration;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ParticleConfig {
    private static Configuration config;
    private static Set<String> whitelist = new HashSet<>();

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    private static void syncConfig() {
        String[] defaultWhitelist = new String[0];
        String[] array = config.get("particle", "whitelist", defaultWhitelist,
                "List of particle IDs (e.g., minecraft:flame)").getStringList();
        whitelist.clear();
        for (String s : array) if (!s.isEmpty()) whitelist.add(s);
        if (config.hasChanged()) config.save();
    }

    public static void reload() {
        if (config != null) {
            config.load();
            syncConfig();
        }
    }

    public static boolean isWhitelisted(String id) {
        return whitelist.contains(id);
    }
}