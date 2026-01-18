//package com.infiniteflameteam.umoift.client;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.infiniteflameteam.umoift.client.gui.OfficialDialogScreen;
//import com.infiniteflameteam.umoift.network.DialogNetworkHandler;
//import net.minecraft.client.Minecraft;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@OnlyIn(Dist.CLIENT)
//public class ClientDialogHandler {
//    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDialogHandler.class);
//    private static final Gson GSON = new GsonBuilder().create();
//
//    public static void handleOfficialDialogOpen(String dialogId, String dialogData) {
//        Minecraft minecraft = Minecraft.getInstance();
//
//        try {
//            LOGGER.info("收到对话框打开请求: {}", dialogId);
//
//            // 解析对话框数据
//            JsonObject dialog = GSON.fromJson(dialogData, JsonObject.class);
//
//            // 在客户端线程中显示对话框
//            minecraft.execute(() -> {
//                LOGGER.info("创建对话框界面");
//                minecraft.setScreen(new OfficialDialogScreen(dialogId, dialog));
//            });
//        } catch (Exception e) {
//            LOGGER.error("处理对话框打开失败", e);
//        }
//    }
//
//    public static void sendOfficialDialogAction(String dialogId, String actionType, JsonObject actionData) {
//        LOGGER.info("发送对话框动作: {} - {} - {}", dialogId, actionType, actionData);
//        DialogNetworkHandler.ServerboundOfficialDialogActionPacket packet =
//                new DialogNetworkHandler.ServerboundOfficialDialogActionPacket(dialogId, actionType, actionData);
//        DialogNetworkHandler.INSTANCE.sendToServer(packet);
//    }
//}
// 以上是旧版代码

package org.infiniteflameteam.umoiftng.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.infiniteflameteam.umoiftng.client.gui.OfficialDialogScreen;
import org.infiniteflameteam.umoiftng.network.DialogNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OnlyIn(Dist.CLIENT)
public class ClientDialogHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDialogHandler.class);
    private static final Gson GSON = new GsonBuilder().create();

    public static void handleOfficialDialogOpen(String dialogId, String dialogData) {
        Minecraft minecraft = Minecraft.getInstance();

        try {
            LOGGER.info("收到对话框打开请求: {}", dialogId);

            // 解析对话框数据
            JsonObject dialog = GSON.fromJson(dialogData, JsonObject.class);

            // 在客户端线程中显示对话框
            minecraft.execute(() -> {
                LOGGER.info("创建对话框界面");
                minecraft.setScreen(new OfficialDialogScreen(dialogId, dialog));
            });
        } catch (Exception e) {
            LOGGER.error("处理对话框打开失败", e);
        }
    }

    public static void sendOfficialDialogAction(String dialogId, String actionType, JsonObject actionData) {
        LOGGER.info("发送对话框动作: {} - {} - {}", dialogId, actionType, actionData);
        DialogNetworkHandler.ServerboundOfficialDialogActionPacket packet =
                new DialogNetworkHandler.ServerboundOfficialDialogActionPacket(dialogId, actionType, actionData);
        DialogNetworkHandler.INSTANCE.sendToServer(packet);
    }

    public static void handleClearDialog() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            // 如果当前屏幕是对话框，则关闭它
            if (minecraft.screen instanceof OfficialDialogScreen) {
                minecraft.setScreen(null);
                LOGGER.info("已清除对话框");
            }
        });
    }
}