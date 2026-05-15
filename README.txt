一个轻量级 Minecraft Forge 模组，默认禁用所有粒子，仅允许用户指定的白名单粒子显示，并支持游戏内热重载配置。

🎯 主要功能
默认拦截所有粒子：进入游戏后，任何粒子效果（火焰、烟雾、爆炸等）都不会显示。

白名单机制：通过配置文件 particleinterceptor.cfg 自定义允许显示的粒子 ID（例如 minecraft:flame、minecraft:explosion）。

热重载命令：输入 /again 即可重新加载白名单，无需重启游戏。

性能优化：大幅减少不必要的粒子渲染，提升帧率，尤其适用于低配机器或大型整合包。

⚙️ 技术特点
基于 Mixin：在 ParticleManager.addEffect 方法前注入，直接取消非白名单粒子的添加，效率极高。

客户端专用：仅影响客户端渲染，服务端无需安装。

兼容 Forge 1.12.2：使用稳定映射和标准 API，兼容大多数主流模组。

📦 使用方式
将模组 JAR 放入 .minecraft/mods 文件夹。

启动游戏，模组自动生成配置文件（.minecraft/config/particleinterceptor.cfg）。

默认白名单为空，所有粒子被拦截。

编辑配置文件，按行添加粒子 ID（如 minecraft:flame）。

在游戏中输入 /again 使新配置生效。

🧪 示例配置
properties
particle {
    S:whitelist <
        minecraft:flame
        minecraft:smoke
        minecraft:explosion
     >
}
🔗 开源许可
采用 MIT License，源码托管于 GitHub，欢迎学习、修改和分发。

📌 适用场景
不喜欢大量粒子特效的玩家。

需要提升帧率、减少视觉干扰的整合包。

测试模组时临时禁用特定粒子。

一句话总结：用白名单精准控制粒子显示，让游戏更清爽、更流畅。

