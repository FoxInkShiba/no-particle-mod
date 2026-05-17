package no.particle.mixin;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketParticles;
import no.particle.ParticleFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public class NetHandlerPlayServerMixin {

    @Inject(method = "func_147359_a", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof SPacketParticles) {
            SPacketParticles particles = (SPacketParticles) packet;
            if (ParticleFilter.isBlocked(particles)) {
                ci.cancel();
            }
        }
    }
}