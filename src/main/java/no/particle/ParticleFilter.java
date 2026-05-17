package no.particle;

import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleFilter {
    private static final Field PARTICLE_NAME_FIELD;
    private static final Field PARTICLE_TYPE_FIELD;

    // 缓存 EnumParticleTypes 的拦截结果（避免重复调用 getParticleName()）
    private static final Map<EnumParticleTypes, Boolean> ENUM_CACHE = new ConcurrentHashMap<>();
    // 缓存 ResourceLocation 的拦截结果（避免重复调用 toString()）
    private static final Map<ResourceLocation, Boolean> RL_CACHE = new ConcurrentHashMap<>();
    // 缓存字符串名称的结果（由 ParticleConfig 内部已有，此处不再重复，直接调用其缓存）
    // 注意：ParticleConfig 内部已有 ConcurrentHashMap 缓存，所以 isBlocked(SPacketParticles) 无需额外缓存

    static {
        try {
            PARTICLE_NAME_FIELD = SPacketParticles.class.getDeclaredField("particleName");
            PARTICLE_NAME_FIELD.setAccessible(true);
            PARTICLE_TYPE_FIELD = SPacketParticles.class.getDeclaredField("particleType");
            PARTICLE_TYPE_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to initialize ParticleFilter", e);
        }
    }

    /**
     * 从网络包拦截（反射读取粒子名称，然后委托给 ParticleConfig 缓存）
     */
    public static boolean isBlocked(SPacketParticles packet) {
        if (packet == null) return false;
        String particleName = null;
        try {
            ResourceLocation rl = (ResourceLocation) PARTICLE_NAME_FIELD.get(packet);
            if (rl != null) {
                particleName = rl.toString();
            } else {
                EnumParticleTypes type = (EnumParticleTypes) PARTICLE_TYPE_FIELD.get(packet);
                if (type != null) {
                    particleName = type.getParticleName();
                }
            }
        } catch (IllegalAccessException e) {
            return false;
        }
        return particleName != null && ParticleConfig.shouldBlockParticleName(particleName);
    }

    /**
     * 从 EnumParticleTypes 拦截（用于源头拦截，带缓存）
     */
    public static boolean isBlocked(EnumParticleTypes type) {
        if (type == null) return false;
        return ENUM_CACHE.computeIfAbsent(type, t ->
                ParticleConfig.shouldBlockParticleName(t.getParticleName())
        );
    }

    /**
     * 从 ResourceLocation 拦截（用于源头拦截，带缓存）
     */
    public static boolean isBlocked(ResourceLocation rl) {
        if (rl == null) return false;
        return RL_CACHE.computeIfAbsent(rl, r ->
                ParticleConfig.shouldBlockParticleName(r.toString())
        );
    }
}