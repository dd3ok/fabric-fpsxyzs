package com.dd3ok.fpsxyzs;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class ModKeybinds {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("fpsxyzs", "general")
    );

    public static KeyMapping toggleHud;

    public static void register() {
        toggleHud = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.fpsxyzs.toggle", // 키 식별자
            InputConstants.Type.KEYSYM, // 키보드 입력 타입
            InputConstants.KEY_F8, // 기본 키 (F8)
            CATEGORY // 설정 메뉴에서의 카테고리
        ));
    }
}
