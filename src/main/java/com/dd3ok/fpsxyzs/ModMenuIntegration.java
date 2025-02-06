package com.dd3ok.fpsxyzs;

import com.dd3ok.fpsxyzs.gui.ModMainMenuScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ModMainMenuScreen(parent, FPSXYZsMod.getConfig());
    }
}