package no.particle;

import net.minecraftforge.common.config.Configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleConfig {
    private static Configuration config;
    private static Set<String> modList = new HashSet<>();
    private static Set<String> singleList = new HashSet<>();
    private static boolean invert = false;
    private static boolean disableTConstruct = true;

    private static final Map<String, String> STANDARD_TO_CLASS = new HashMap<>();
    private static final Map<String, Boolean> CACHE = new ConcurrentHashMap<>();

    public static void init(File configFile) {
        config = new Configuration(configFile);
        loadMappingFromFile();
        syncConfig();
    }

    private static void loadMappingFromFile() {
        File mappingFile = new File(config.getConfigFile().getParentFile().getParentFile(), "config/particle_list.txt");
        if (!mappingFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(mappingFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("->")) {
                    String[] parts = line.split("->");
                    if (parts.length >= 2) {
                        String className = parts[0].trim();
                        String standardId = parts[1].trim();
                        if (!className.isEmpty() && !standardId.isEmpty()) {
                            STANDARD_TO_CLASS.put(standardId, className);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ParticleConfig] Failed to load mapping: " + e.getMessage());
        }
    }

    private static void syncConfig() {
        String[] modArray = config.get("particle", "mod_list", new String[0],
                "List of mod IDs or keywords.").getStringList();
        modList.clear();
        for (String s : modArray) {
            s = s.trim();
            if (!s.isEmpty()) modList.add(s);
        }

        String[] singleArray = config.get("particle", "list", new String[0],
                "List of individual particle identifiers (supports both standard ID and class name).").getStringList();
        singleList.clear();
        for (String s : singleArray) {
            s = s.trim();
            if (!s.isEmpty()) singleList.add(s);
        }

        invert = config.getBoolean("invert_list", "particle", false,
                "false: allowlist; true: blocklist.");
        disableTConstruct = config.getBoolean("disable_tconstruct_particles", "particle", true,
                "Disable all particle spawning from Tinkers' Construct mod.");
        if (config.hasChanged()) config.save();
        CACHE.clear();
    }

    public static void reload() {
        if (config != null) {
            loadMappingFromFile();
            config.load();
            syncConfig();
            CACHE.clear();
        }
    }

    public static boolean shouldBlock(String particleId) {
        boolean inMod = false;
        if (particleId.contains(":")) {
            String modid = particleId.split(":")[0];
            inMod = modList.contains(modid);
        } else {
            for (String mod : modList) {
                if (particleId.contains(mod)) {
                    inMod = true;
                    break;
                }
            }
        }

        boolean inSingle = singleList.contains(particleId);
        if (!inSingle && particleId.contains(":")) {
            String className = STANDARD_TO_CLASS.get(particleId);
            if (className != null && singleList.contains(className)) {
                inSingle = true;
            }
        }

        boolean matched = inMod || inSingle;
        boolean allowed = invert ? !matched : matched;
        if (inMod && inSingle) allowed = !allowed;
        return !allowed;
    }

    public static boolean shouldBlockParticleName(String particleName) {
        if (particleName == null) return false;
        if (invert && modList.isEmpty() && singleList.isEmpty()) {
            return true;
        }
        return CACHE.computeIfAbsent(particleName, name -> {
            boolean inMod = false;
            if (name.contains(":")) {
                String modid = name.split(":")[0];
                inMod = modList.contains(modid);
            } else {
                for (String mod : modList) {
                    if (name.contains(mod)) {
                        inMod = true;
                        break;
                    }
                }
            }
            boolean inSingle = singleList.contains(name);
            boolean matched = inMod || inSingle;
            boolean allowed = invert ? !matched : matched;
            if (inMod && inSingle) allowed = !allowed;
            return !allowed;
        });
    }

    public static boolean shouldDisableTConstruct() {
        return disableTConstruct;
    }
}