package no.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CommandTxt extends CommandBase {

    @Override
    public String getName() {
        return "txt";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/txt";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.effectRenderer == null) {
                sender.sendMessage(new TextComponentString("§cParticleManager is null."));
                return;
            }

            Object particleManager = mc.effectRenderer;

            // 1. 数字注册表 (Integer -> IParticleFactory) -> 原版粒子
            Map<Integer, IParticleFactory> intRegistry = findIntegerRegistry(particleManager);
            // 2. 名称注册表 (ResourceLocation -> IParticleFactory) -> 标准模组粒子
            Map<ResourceLocation, IParticleFactory> nameRegistry = findNameRegistry(particleManager);
            // 3. 运行时动态粒子 (通过 Mixin 记录)
            Set<String> runtimeParticles = ParticleLogger.getParticles();

            File configDir = new File(mc.mcDataDir, "config");
            if (!configDir.exists()) configDir.mkdirs();
            File outFile = new File(configDir, "particle_list.txt");

            try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
                writer.println("=== Forge 1.12.2 All Particles ===");
                writer.println();

                // 输出数字注册表（原版粒子）
                if (intRegistry != null && !intRegistry.isEmpty()) {
                    writer.println("## Particles from Integer Registry (mostly vanilla) ##");
                    writer.println("Format: ID -> WhitelistIdentifier -> FactoryClass");
                    writer.println("Note: Use the second column (WhitelistIdentifier) in your whitelist.");
                    writer.println();
                    Map<Integer, ResourceLocation> idToName = buildIdToNameMap(intRegistry, nameRegistry);
                    for (Map.Entry<Integer, IParticleFactory> entry : intRegistry.entrySet()) {
                        Integer id = entry.getKey();
                        IParticleFactory factory = entry.getValue();
                        String identifier = getWhitelistIdentifier(id, idToName);
                        writer.println(id + " -> " + identifier + " -> " + (factory == null ? "null" : factory.getClass().getName()));
                    }
                    writer.println();
                }

                // 输出名称注册表（标准模组粒子）
                if (nameRegistry != null && !nameRegistry.isEmpty()) {
                    writer.println("## Particles from Name Registry (standard mod particles) ##");
                    writer.println("Format: ResourceLocation -> FactoryClass");
                    writer.println("Use the first column (e.g., 'mymod:custom') in your whitelist.");
                    writer.println();
                    for (Map.Entry<ResourceLocation, IParticleFactory> entry : nameRegistry.entrySet()) {
                        writer.println(entry.getKey() + " -> " + (entry.getValue() == null ? "null" : entry.getValue().getClass().getName()));
                    }
                    writer.println();
                }

                // 输出运行时动态粒子
                if (runtimeParticles != null && !runtimeParticles.isEmpty()) {
                    writer.println("## Runtime Dynamic Particles (captured by Mixin) ##");
                    writer.println("Format: ClassName (use this directly in whitelist)");
                    writer.println();
                    for (String className : runtimeParticles) {
                        writer.println(className);
                    }
                    writer.println();
                }

                int total = (intRegistry == null ? 0 : intRegistry.size())
                        + (nameRegistry == null ? 0 : nameRegistry.size())
                        + (runtimeParticles == null ? 0 : runtimeParticles.size());
                writer.println("Total unique particles: " + total);
            }

            sender.sendMessage(new TextComponentString("§aParticle registry exported."));
            sender.sendMessage(new TextComponentString("§f" + outFile.getAbsolutePath()));

        } catch (Exception e) {
            sender.sendMessage(new TextComponentString("§cError: " + e.getClass().getSimpleName() + ": " + e.getMessage()));
            e.printStackTrace();
        }
    }

    // ================= 以下为辅助方法 =================

    private Map<Integer, ResourceLocation> buildIdToNameMap(Map<Integer, IParticleFactory> intReg,
                                                            Map<ResourceLocation, IParticleFactory> nameReg) {
        Map<Integer, ResourceLocation> map = new HashMap<>();
        if (nameReg == null) return map;
        for (Map.Entry<ResourceLocation, IParticleFactory> nameEntry : nameReg.entrySet()) {
            IParticleFactory factory = nameEntry.getValue();
            for (Map.Entry<Integer, IParticleFactory> intEntry : intReg.entrySet()) {
                if (intEntry.getValue() == factory) {
                    map.put(intEntry.getKey(), nameEntry.getKey());
                    break;
                }
            }
        }
        return map;
    }

    private String getWhitelistIdentifier(int id, Map<Integer, ResourceLocation> idToName) {
        if (idToName.containsKey(id)) {
            return idToName.get(id).toString();
        }
        try {
            EnumParticleTypes particle = EnumParticleTypes.getParticleFromId(id);
            if (particle != null) {
                return "minecraft:" + particle.getParticleName();
            }
        } catch (Exception ignored) {}
        return String.valueOf(id);
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, IParticleFactory> findIntegerRegistry(Object particleManager) {
        Class<?> clazz = particleManager.getClass();
        String[] knownFields = {"field_178932_g", "particleTypes", "particleRegistry"};
        for (String fieldName : knownFields) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(particleManager);
                if (value instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) value;
                    if (isValidIntegerRegistry(map)) {
                        System.out.println("[NoParticle] Found integer registry via known field: " + fieldName);
                        return new LinkedHashMap<>((Map<Integer, IParticleFactory>) map);
                    }
                }
            } catch (Exception ignored) {}
        }
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(particleManager);
                if (value instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) value;
                    if (isValidIntegerRegistry(map)) {
                        System.out.println("[NoParticle] Found integer registry via field scan: " + field.getName());
                        return new LinkedHashMap<>((Map<Integer, IParticleFactory>) map);
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<ResourceLocation, IParticleFactory> findNameRegistry(Object particleManager) {
        Class<?> clazz = particleManager.getClass();
        String[] knownFields = {"field_78876_c", "particleRegistry", "particleTypes"};
        for (String fieldName : knownFields) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(particleManager);
                if (value instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) value;
                    if (isValidNameRegistry(map)) {
                        System.out.println("[NoParticle] Found name registry via known field: " + fieldName);
                        return new LinkedHashMap<>((Map<ResourceLocation, IParticleFactory>) map);
                    }
                }
            } catch (Exception ignored) {}
        }
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(particleManager);
                if (value instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) value;
                    if (isValidNameRegistry(map)) {
                        System.out.println("[NoParticle] Found name registry via field scan: " + field.getName());
                        return new LinkedHashMap<>((Map<ResourceLocation, IParticleFactory>) map);
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private boolean isValidIntegerRegistry(Map<?, ?> map) {
        if (map == null || map.isEmpty()) return false;
        int valid = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof Integer)) continue;
            Object value = entry.getValue();
            if (value == null) continue;
            if (IParticleFactory.class.isAssignableFrom(value.getClass())) {
                valid++;
                if (valid >= 3) return true;
            }
        }
        return false;
    }

    private boolean isValidNameRegistry(Map<?, ?> map) {
        if (map == null || map.isEmpty()) return false;
        int valid = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof ResourceLocation)) continue;
            Object value = entry.getValue();
            if (value == null) continue;
            if (IParticleFactory.class.isAssignableFrom(value.getClass())) {
                valid++;
                if (valid >= 3) return true;
            }
        }
        return false;
    }
}