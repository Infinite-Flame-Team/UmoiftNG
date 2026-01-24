package org.infiniteflameteam.umoiftng.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class CrouchKeyMapping {
    // 定义蹲下按键绑定，默认按键是C
    public static final KeyMapping CROUCH_KEY = new KeyMapping(
            "key.umoiftng.crouch",          // 按键的本地化键名
            KeyConflictContext.IN_GAME,     // 只在游戏中使用
            InputConstants.Type.KEYSYM,     // 键盘按键类型
            GLFW.GLFW_KEY_C,                // 默认按键：C键
            "category.umoiftng.binds"       // 按键分类
    );
}