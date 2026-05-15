package no.particle.mixin;

import no.particle.ParticleConfig;
import no.particle.CommandParticleReload;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    private static boolean commandRegistered = false;
    private static final Map<String, String> PARTICLE_NAME_MAP = new HashMap<>();

    static {
        try {
            File configDir = new File(Launch.minecraftHome, "config");
            File configFile = new File(configDir, "particleinterceptor.cfg");
            ParticleConfig.init(configFile);
            System.out.println("[no particle] Config initialized");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PARTICLE_NAME_MAP.put("net.minecraft.client.particle.ParticleFlame", "minecraft:flame");
        PARTICLE_NAME_MAP.put("net.minecraft.client.particle.ParticleSmokeNormal", "minecraft:smoke");
        PARTICLE_NAME_MAP.put("net.minecraft.client.particle.ParticleSmokeLarge", "minecraft:smoke");
        PARTICLE_NAME_MAP.put("net.minecraft.client.particle.ParticleBubble", "minecraft:bubble");
    }

    @Inject(method = "func_78873_a", at = @At("HEAD"), cancellable = true, remap = true)
    private void onAddEffect(Particle particle, CallbackInfo ci) {
        // 注册命令（仅第一次）
        if (!commandRegistered) {
            try {
                ClientCommandHandler.instance.registerCommand(new CommandParticleReload());
                System.out.println("[no particle] Command /again registered from Mixin");
                commandRegistered = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 粒子拦截逻辑
        String className = particle.getClass().getName();
        String particleId = PARTICLE_NAME_MAP.getOrDefault(className, className);
        if (!ParticleConfig.isWhitelisted(particleId)) {
            ci.cancel();
        }
    }
}