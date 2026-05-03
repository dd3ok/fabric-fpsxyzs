package com.dd3ok.fpsxyzs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FPSXYZsMod implements ClientModInitializer {
    public static final String VERSION = "1.0.6";
    private static final Logger LOGGER = LoggerFactory.getLogger("fpsxyzs");
    private static ModConfig config;
    private static InfoDisplay infoDisplay;
    private static boolean loggedFirstHudRender;
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();
        infoDisplay = new InfoDisplay(client, config);
        ModKeybinds.register();
        LOGGER.info("FPSXYZs {} initialized. enabled={}, fps={}, coords={}, position={}",
                VERSION,
                config.isEnabled(),
                config.isShowFps(),
                config.isShowCoords(),
                config.getPosition());

        HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath("fpsxyzs", "info_display"), FPSXYZsMod::renderHud);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeybinds.toggleHud.consumeClick()) {
                config.setEnabled(!config.isEnabled());
                infoDisplay.reset();
                LOGGER.info("FPSXYZs HUD toggled: {}", config.isEnabled() ? "ON" : "OFF");
                client.gui.setOverlayMessage(Component.literal("FPSXYZs HUD: " + (config.isEnabled() ? "ON" : "OFF")), false);
            }
        });
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static void resetInfoDisplay() {
        if (infoDisplay != null) {
            infoDisplay.reset();
        }
    }

    public static void showStatusMessage(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.gui != null) {
            client.gui.setOverlayMessage(Component.literal(message), false);
        }
    }

    public static void renderHud(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker deltaTracker) {
        if (!loggedFirstHudRender) {
            loggedFirstHudRender = true;
            LOGGER.info("FPSXYZs HUD registry hook reached. gui={}x{}, configReady={}, enabled={}",
                    graphics.guiWidth(),
                    graphics.guiHeight(),
                    config != null,
                    config != null && config.isEnabled());
        }

        if (config == null || infoDisplay == null || !config.isEnabled()) return;
        infoDisplay.update();
        infoDisplay.render(graphics);
    }
}
