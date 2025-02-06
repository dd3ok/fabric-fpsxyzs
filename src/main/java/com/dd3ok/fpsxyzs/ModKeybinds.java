package com.dd3ok.fpsxyzs;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static KeyBinding toggleHud;

    public static void register() {
        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.fpsxyzs.toggle", // 키 식별자
            InputUtil.Type.KEYSYM, // 키보드 입력 타입
            GLFW.GLFW_KEY_LEFT_BRACKET, // 기본 키 ( [ 키 )
            "category.fpsxyzs.general" // 설정 메뉴에서의 카테고리
        ));
    }
}