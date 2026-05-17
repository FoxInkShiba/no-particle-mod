package no.particle.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import no.particle.ParticleFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldServer.class)
public class WorldServerMixin {

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

    // 带 EntityPlayerMP 的重载（void 返回）
    @Inject(method = "func_175736_a", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpawnParticleWithPlayerEnum(EntityPlayerMP player, EnumParticleTypes particleType,
                                               double x, double y, double z, int count,
                                               double xOffset, double yOffset, double zOffset,
                                               double speed, int[] args,
                                               CallbackInfo ci) {
        if (ParticleFilter.isBlocked(particleType)) {
            ci.cancel();
        }
    }

    @Inject(method = "func_175736_a", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpawnParticleWithPlayerResource(EntityPlayerMP player, ResourceLocation particleId,
                                                   double x, double y, double z, int count,
                                                   double xOffset, double yOffset, double zOffset,
                                                   double speed, int[] args,
                                                   CallbackInfo ci) {
        if (ParticleFilter.isBlocked(particleId)) {
            ci.cancel();
        }
    }
}