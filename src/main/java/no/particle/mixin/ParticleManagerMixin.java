package no.particle.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import no.particle.CommandParticleReload;
import no.particle.CommandTxt;
import no.particle.ParticleConfig;
import no.particle.ParticleLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.File;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    private static boolean commandRegistered = false;

    static {
        try {
            File configDir = new File(Launch.minecraftHome, "config");
            File configFile = new File(configDir, "particleinterceptor.cfg");
            ParticleConfig.init(configFile);
            System.out.println("[NoParticle] Config initialized");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "func_78873_a", at = @At("HEAD"), cancellable = true, remap = false)
    private void onAddEffect(Particle particle, CallbackInfo ci) {
        if (!commandRegistered) {
            try {
                ClientCommandHandler.instance.registerCommand(new CommandParticleReload());
                ClientCommandHandler.instance.registerCommand(new CommandTxt());
                System.out.println("[NoParticle] Commands /again and /txt registered");
                commandRegistered = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String className = particle.getClass().getName();
        ParticleLogger.record(className);
        if (ParticleConfig.shouldBlock(className)) {
            ci.cancel();
        }
    }
}