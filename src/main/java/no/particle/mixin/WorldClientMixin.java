package no.particle.mixin;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import no.particle.ParticleFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldClient.class)
public class WorldClientMixin {

    @Inject(method = "func_175736_a", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpawnParticleEnum(EnumParticleTypes particleType,
                                     double x, double y, double z,
                                     int count, double xOffset, double yOffset, double zOffset,
                                     double speed, int[] args,
                                     CallbackInfoReturnable<Boolean> cir) {
        if (ParticleFilter.isBlocked(particleType)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "func_175736_a", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpawnParticleResource(ResourceLocation particleId,
                                         double x, double y, double z,
                                         int count, double xOffset, double yOffset, double zOffset,
                                         double speed, int[] args,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (ParticleFilter.isBlocked(particleId)) {
            cir.setReturnValue(false);
        }
    }
}