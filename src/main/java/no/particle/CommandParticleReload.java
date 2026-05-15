package no.particle;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandParticleReload extends CommandBase {
    @Override
    public String getName() { return "again"; }
    @Override
    public String getUsage(ICommandSender sender) { return "/again"; }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ParticleConfig.reload();
        sender.sendMessage(new TextComponentString("Particle config reloaded. New particles will follow updated whitelist."));
    }
}