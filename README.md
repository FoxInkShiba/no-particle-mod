📖 模组简介（中文）
NoParticle 是一个轻量级 Minecraft Forge 模组，默认禁用所有粒子，仅允许用户指定的白名单粒子显示。V1.1 版本新增服务端发包拦截、源头生成拦截、匠魂模组专项优化、粒子列表导出等功能，更彻底地消除粒子对性能的影响。

🎯 主要功能
默认拦截所有粒子：进入游戏后，任何粒子效果（火焰、烟雾、爆炸、模组粒子等）都不会显示。

白名单机制：通过配置文件 no_particle.cfg 自定义允许显示的粒子（支持按模组ID过滤或单个粒子ID）。

双端拦截：服务端拦截网络包（SPacketParticles）、源头拦截 World.spawnParticle（避免创建粒子对象）、客户端拦截渲染，三层防护。

匠魂模组专项优化：可直接清空匠魂的粒子生成方法，彻底禁用其粒子效果（默认开启，可通过配置文件关闭，需重启游戏）。

热重载命令：输入 /again 重新加载配置，无需重启游戏。

粒子列表导出：输入 /txt 将所有已注册粒子导出到 config/particle_list.txt，方便配置白名单。

极致性能：源头拦截避免对象创建，服务端减少网络开销，客户端降低渲染压力，显著提升帧率，尤其适合低配机器或大型整合包。

⚙️ 技术特点
Mixin + ASM 混合：使用 Mixin 拦截核心方法，使用 ASM 清空匠魂特定方法，兼容性与性能兼顾。

服务端/客户端双兼容：服务端可安装以拦截网络包，客户端可单独安装以拦截渲染。

Forge 1.12.2：基于稳定映射，兼容绝大多数主流模组。

开源许可：MIT License，源码托管于 GitHub，欢迎学习、修改和分发。

📦 使用方式
将模组 JAR 放入 .minecraft/mods 文件夹。

必须安装 MixinBooter（本模组依赖外部 Mixin 环境）。

启动游戏，模组自动生成配置文件（.minecraft/config/no_particle.cfg）。

编辑配置文件：

mod_list：按模组 ID 过滤（例如 tconstruct）。

list：按单个粒子 ID 过滤（例如 minecraft:flame）。

invert_list：false = 白名单模式（仅显示列表中的粒子），true = 黑名单模式（显示列表以外的粒子）。

disable_tconstruct_particles：是否彻底禁用匠魂粒子（默认 true，需重启游戏生效）。

在游戏中输入 /again 使新配置生效（匠魂开关除外）。

（可选）输入 /txt 导出粒子列表，辅助配置。

🧪 配置示例
properties
particle {
    S:mod_list <
        tconstruct
     >
    S:list <
        minecraft:flame
        minecraft:smoke
     >
    B:invert_list=false
    B:disable_tconstruct_particles=true
}
🔗 开源许可
MIT License，源码托管于 GitHub：https://github.com/FoxInkShiba/no-particle-mod

📌 适用场景
不喜欢大量粒子特效的玩家。

需要提升帧率、减少视觉干扰的整合包。

服务器管理员想减少粒子网络包开销。

匠魂模组玩家想彻底关闭其繁多的粒子效果。

一句话总结
默认禁用所有粒子，通过白名单精准控制，三层拦截 + 匠魂专项优化，让游戏更清爽、更流畅。

📖 Mod Summary (English)
NoParticle is a lightweight Minecraft Forge mod that disables all particles by default and only allows user‑defined whitelisted ones. Version 1.1 adds server‑side packet interception, source‑level World.spawnParticle interception, Tinkers' Construct optimization, and particle list export for even better performance.

🎯 Key Features
All particles blocked by default – no particle effects (flame, smoke, explosion, mod particles) appear.

Whitelist mechanism – configure allowed particles via no_particle.cfg (by mod ID or individual particle ID).

Three‑layer interception – server‑side packet (SPacketParticles), source‑level World.spawnParticle (prevents object creation), and client‑side rendering.

Tinkers' Construct optimization – can completely clear TConstruct's particle generation methods (enabled by default, toggle via config, requires restart).

Hot reload – /again command reloads the config without restarting the game.

Export particle list – /txt command dumps all registered particles to config/particle_list.txt for easy whitelist setup.

Performance boost – source‑level interception avoids object creation, reduces network overhead, and lowers rendering load → smoother FPS, ideal for low‑end machines or large modpacks.

⚙️ Technical Highlights
Mixin + ASM – uses Mixin for core interception and ASM to clear TConstruct methods, balancing compatibility and performance.

Server & Client compatible – can be installed on server (packet interception) and/or client (rendering interception).

Forge 1.12.2 – based on stable mappings, compatible with most mainstream mods.

Open source – MIT License, hosted on GitHub.

📦 How to Use
Place the mod JAR into .minecraft/mods.

Must install MixinBooter (this mod relies on external Mixin environment).

Launch the game – config file is auto‑generated at .minecraft/config/no_particle.cfg.

Edit the config:

mod_list – filter by mod ID (e.g., tconstruct).

list – filter by individual particle ID (e.g., minecraft:flame).

invert_list – false = whitelist mode (only listed particles are shown), true = blacklist mode (show all except listed ones).

disable_tconstruct_particles – whether to completely disable TConstruct particles (default true, requires restart).

In game, run /again to apply the new config (TConstruct toggle requires restart).

(Optional) Run /txt to export the particle list for assistance in configuration.

🧪 Example Configuration
properties
particle {
    S:mod_list <
        tconstruct
     >
    S:list <
        minecraft:flame
        minecraft:smoke
     >
    B:invert_list=false
    B:disable_tconstruct_particles=true
}
🔗 Open Source License
MIT License, source code on GitHub: https://github.com/FoxInkShiba/no-particle-mod

📌 Use Cases
Players who dislike excessive particle effects.

Modpacks aiming to boost FPS and reduce visual clutter.

Server admins wanting to reduce particle packet overhead.

TConstruct players who want to completely disable its numerous particles.

One‑Sentence Summary
Blocks all particles by default, offers precise whitelist control, three‑layer interception + TConstruct optimization – for a cleaner, smoother Minecraft experience.




