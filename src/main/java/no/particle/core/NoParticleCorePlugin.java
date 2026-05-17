package no.particle.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("NoParticleCore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class NoParticleCorePlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"no.particle.core.ParticleTransformer"};
    }

    @Override
    public String getModContainerClass() { return null; }

    @Nullable
    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() { return null; }
}