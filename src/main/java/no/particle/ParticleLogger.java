package no.particle;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleLogger {
    private static final Set<String> particles = ConcurrentHashMap.newKeySet();

    public static void record(String particleId) {
        particles.add(particleId);
    }

    public static Set<String> getParticles() {
        return particles;
    }
}