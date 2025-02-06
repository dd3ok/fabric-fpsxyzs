package com.dd3ok.fpsxyzs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class FPSXYZsMod implements ClientModInitializer {
    private static ModConfig config;
    private static InfoDisplay infoDisplay;
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();
        infoDisplay = new InfoDisplay(client, config);
        ModKeybinds.register();

        // HUD 콜백에서 config 체크를 가장 먼저 수행
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (!config.isEnabled()) return; // early return으로 모든 처리 스킵
            infoDisplay.update();
            infoDisplay.render(context);
        });

        // 키 입력 이벤트는 유지 (모드 활성화/비활성화 용도)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeybinds.toggleHud.wasPressed()) {
                config.setEnabled(!config.isEnabled());
                config.save();
            }
        });
    }

    public static ModConfig getConfig() {
        return config;
    }
}
