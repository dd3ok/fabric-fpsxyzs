package com.dd3ok.fpsxyzs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class FPSXYZsMod implements ClientModInitializer {
    private static ModConfig config;
    private static InfoDisplay infoDisplay;
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();
        infoDisplay = new InfoDisplay(client, config);
        ModKeybinds.register();

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("fpsxyzs", "info_display"), (graphics, tickDelta) -> {
            if (!config.isEnabled()) return; // early return으로 모든 처리 스킵
            infoDisplay.update();
            infoDisplay.render(graphics);
        });

        // 키 입력 이벤트는 유지 (모드 활성화/비활성화 용도)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeybinds.toggleHud.consumeClick()) {
                config.setEnabled(!config.isEnabled());
            }
        });
    }

    public static ModConfig getConfig() {
        return config;
    }
}
