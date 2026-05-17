package no.particle.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ParticleTransformer implements IClassTransformer {

    private static final Set<String> TARGET_CLASSES = new HashSet<>();
    private static boolean disableTConstruct = true; // 默认禁用

    static {
        TARGET_CLASSES.add("slimeknights.tconstruct.common.CommonProxy");
        TARGET_CLASSES.add("slimeknights.tconstruct.client.ClientProxy");
        loadConfig();
    }

    private static void loadConfig() {
        try {
            File configFile = new File("config/no_particle.cfg");
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                Properties defaultProps = new Properties();
                defaultProps.setProperty("disable_tconstruct_particles", "true");
                try (FileOutputStream out = new FileOutputStream(configFile)) {
                    defaultProps.store(out, "NoParticle Config - restart required after change");
                }
            }
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
            }
            disableTConstruct = Boolean.parseBoolean(props.getProperty("disable_tconstruct_particles", "true"));
            System.out.println("[NoParticle] TConstruct particle override: " + (disableTConstruct ? "DISABLED" : "ENABLED"));
        } catch (IOException e) {
            System.err.println("[NoParticle] Failed to load config: " + e);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (!TARGET_CLASSES.contains(transformedName)) return basicClass;
        if (!disableTConstruct) {
            System.out.println("[NoParticle] TConstruct particle override is disabled, skipping transformation for " + transformedName);
            return basicClass;
        }

        System.out.println("[NoParticle] Transforming: " + transformedName);
        try {
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String methodName, String desc,
                                                 String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, methodName, desc, signature, exceptions);
                    boolean isTarget = (transformedName.contains("CommonProxy") && "spawnEffectParticle".equals(methodName))
                            || (transformedName.contains("ClientProxy") && "spawnParticle".equals(methodName));
                    if (isTarget) {
                        System.out.println("[NoParticle] Clearing method: " + methodName);
                        // 替换方法体为 return;
                        return new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitCode() {
                                mv.visitInsn(Opcodes.RETURN);
                                mv.visitMaxs(0, 0);
                                // 禁止访问原始代码
                                super.visitCode();
                            }
                            @Override public void visitInsn(int opcode) {}
                            @Override public void visitVarInsn(int opcode, int var) {}
                            @Override public void visitFieldInsn(int opcode, String owner, String name, String desc) {}
                            @Override public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {}
                            @Override public void visitJumpInsn(int opcode, Label label) {}
                            @Override public void visitLabel(Label label) {}
                            @Override public void visitLdcInsn(Object value) {}
                            @Override public void visitIincInsn(int var, int increment) {}
                            @Override public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {}
                            @Override public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {}
                            @Override public void visitMultiANewArrayInsn(String desc, int dims) {}
                            @Override public void visitTypeInsn(int opcode, String type) {}
                            @Override public void visitIntInsn(int opcode, int operand) {}
                        };
                    }
                    return mv;
                }
            };
            cr.accept(cv, 0);
            return cw.toByteArray();
        } catch (Exception e) {
            System.err.println("[NoParticle] Error transforming " + transformedName + ": " + e);
            return basicClass;
        }
    }
}