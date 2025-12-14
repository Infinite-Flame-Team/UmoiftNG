//package com.infiniteflameteam.umoift.network;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.infiniteflameteam.umoift.client.ClientDialogHandler;
//import com.infiniteflameteam.umoift.dialog.OfficialDialogManager;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraftforge.network.NetworkDirection;
//import net.minecraftforge.network.NetworkEvent;
//import net.minecraftforge.network.NetworkRegistry;
//import net.minecraftforge.network.simple.SimpleChannel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.function.Supplier;
//
//public class DialogNetworkHandler {
//    private static final Logger LOGGER = LoggerFactory.getLogger(DialogNetworkHandler.class);
//    private static final String PROTOCOL_VERSION = "1";
//    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
//            new ResourceLocation("umoift", "dialog"),
//            () -> PROTOCOL_VERSION,
//            PROTOCOL_VERSION::equals,
//            PROTOCOL_VERSION::equals
//    );
//
//    private static final Gson GSON = new GsonBuilder().create();
//    private static int packetId = 0;
//
//    public static void register() {
//        // 注册官方对话框相关数据包
//        INSTANCE.registerMessage(packetId++, ClientboundOfficialDialogOpenPacket.class,
//                ClientboundOfficialDialogOpenPacket::encode,
//                ClientboundOfficialDialogOpenPacket::decode,
//                ClientboundOfficialDialogOpenPacket::handle);
//
//        INSTANCE.registerMessage(packetId++, ServerboundOfficialDialogActionPacket.class,
//                ServerboundOfficialDialogActionPacket::encode,
//                ServerboundOfficialDialogActionPacket::decode,
//                ServerboundOfficialDialogActionPacket::handle);
//    }
//
//    // 服务器 -> 客户端：打开官方对话框
//    public static class ClientboundOfficialDialogOpenPacket {
//        public final String dialogId;
//        public final String dialogData;
//
//        public ClientboundOfficialDialogOpenPacket(String dialogId, String dialogData) {
//            this.dialogId = dialogId;
//            this.dialogData = dialogData;
//        }
//
//        public static void encode(ClientboundOfficialDialogOpenPacket msg, FriendlyByteBuf buf) {
//            buf.writeUtf(msg.dialogId);
//            buf.writeUtf(msg.dialogData);
//        }
//
//        public static ClientboundOfficialDialogOpenPacket decode(FriendlyByteBuf buf) {
//            return new ClientboundOfficialDialogOpenPacket(buf.readUtf(), buf.readUtf());
//        }
//
//        public static void handle(ClientboundOfficialDialogOpenPacket msg, Supplier<NetworkEvent.Context> ctx) {
//            ctx.get().enqueueWork(() -> {
//                ClientDialogHandler.handleOfficialDialogOpen(msg.dialogId, msg.dialogData);
//            });
//            ctx.get().setPacketHandled(true);
//        }
//    }
//
//    // 客户端 -> 服务器：官方对话框动作
//    public static class ServerboundOfficialDialogActionPacket {
//        public final String dialogId;
//        public final String actionType;
//        public final JsonObject actionData;
//
//        public ServerboundOfficialDialogActionPacket(String dialogId, String actionType, JsonObject actionData) {
//            this.dialogId = dialogId;
//            this.actionType = actionType;
//            this.actionData = actionData;
//        }
//
//        public static void encode(ServerboundOfficialDialogActionPacket msg, FriendlyByteBuf buf) {
//            buf.writeUtf(msg.dialogId);
//            buf.writeUtf(msg.actionType);
//            buf.writeUtf(GSON.toJson(msg.actionData));
//        }
//
//        public static ServerboundOfficialDialogActionPacket decode(FriendlyByteBuf buf) {
//            String dialogId = buf.readUtf();
//            String actionType = buf.readUtf();
//            String actionDataStr = buf.readUtf();
//            JsonObject actionData = GSON.fromJson(actionDataStr, JsonObject.class);
//            return new ServerboundOfficialDialogActionPacket(dialogId, actionType, actionData);
//        }
//
//        public static void handle(ServerboundOfficialDialogActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
//            ctx.get().enqueueWork(() -> {
//                ServerPlayer player = ctx.get().getSender();
//                if (player != null) {
//                    OfficialDialogManager.handleOfficialDialogAction(player, msg.dialogId, msg.actionType, msg.actionData);
//                }
//            });
//            ctx.get().setPacketHandled(true);
//        }
//    }
//
//    public static void sendOfficialDialogOpen(ServerPlayer player, String dialogId, JsonObject dialog) {
//        try {
//            String dialogData = GSON.toJson(dialog);
//            ClientboundOfficialDialogOpenPacket packet = new ClientboundOfficialDialogOpenPacket(dialogId, dialogData);
//            INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
//
//            LOGGER.info("发送对话框数据包到: {}", player.getScoreboardName());
//        } catch (Exception e) {
//            LOGGER.error("发送对话框打开数据包失败", e);
//        }
//    }
//}
// 以上是旧版代码

package org.infiniteflameteam.umoiftng.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.infiniteflameteam.umoiftng.client.ClientDialogHandler;
import org.infiniteflameteam.umoiftng.dialog.OfficialDialogManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class DialogNetworkHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DialogNetworkHandler.class);
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("umoiftng", "dialog"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final Gson GSON = new GsonBuilder().create();
    private static int packetId = 0;

    public static void register() {
        // 注册官方对话框相关数据包
        INSTANCE.registerMessage(packetId++, ClientboundOfficialDialogOpenPacket.class,
                ClientboundOfficialDialogOpenPacket::encode,
                ClientboundOfficialDialogOpenPacket::decode,
                ClientboundOfficialDialogOpenPacket::handle);

        INSTANCE.registerMessage(packetId++, ServerboundOfficialDialogActionPacket.class,
                ServerboundOfficialDialogActionPacket::encode,
                ServerboundOfficialDialogActionPacket::decode,
                ServerboundOfficialDialogActionPacket::handle);

        // 注册清除对话框数据包
        INSTANCE.registerMessage(packetId++, ClientboundClearDialogPacket.class,
                ClientboundClearDialogPacket::encode,
                ClientboundClearDialogPacket::decode,
                ClientboundClearDialogPacket::handle);
    }

    // 服务器 -> 客户端：打开官方对话框
    public static class ClientboundOfficialDialogOpenPacket {
        public final String dialogId;
        public final String dialogData;

        public ClientboundOfficialDialogOpenPacket(String dialogId, String dialogData) {
            this.dialogId = dialogId;
            this.dialogData = dialogData;
        }

        public static void encode(ClientboundOfficialDialogOpenPacket msg, FriendlyByteBuf buf) {
            buf.writeUtf(msg.dialogId);
            buf.writeUtf(msg.dialogData);
        }

        public static ClientboundOfficialDialogOpenPacket decode(FriendlyByteBuf buf) {
            return new ClientboundOfficialDialogOpenPacket(buf.readUtf(), buf.readUtf());
        }

        public static void handle(ClientboundOfficialDialogOpenPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ClientDialogHandler.handleOfficialDialogOpen(msg.dialogId, msg.dialogData);
            });
            ctx.get().setPacketHandled(true);
        }
    }

    // 客户端 -> 服务器：官方对话框动作
    public static class ServerboundOfficialDialogActionPacket {
        public final String dialogId;
        public final String actionType;
        public final JsonObject actionData;

        public ServerboundOfficialDialogActionPacket(String dialogId, String actionType, JsonObject actionData) {
            this.dialogId = dialogId;
            this.actionType = actionType;
            this.actionData = actionData;
        }

        public static void encode(ServerboundOfficialDialogActionPacket msg, FriendlyByteBuf buf) {
            buf.writeUtf(msg.dialogId);
            buf.writeUtf(msg.actionType);
            buf.writeUtf(GSON.toJson(msg.actionData));
        }

        public static ServerboundOfficialDialogActionPacket decode(FriendlyByteBuf buf) {
            String dialogId = buf.readUtf();
            String actionType = buf.readUtf();
            String actionDataStr = buf.readUtf();
            JsonObject actionData = GSON.fromJson(actionDataStr, JsonObject.class);
            return new ServerboundOfficialDialogActionPacket(dialogId, actionType, actionData);
        }

        public static void handle(ServerboundOfficialDialogActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    OfficialDialogManager.handleOfficialDialogAction(player, msg.dialogId, msg.actionType, msg.actionData);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    // 服务器 -> 客户端：清除对话框
    public static class ClientboundClearDialogPacket {
        public static void encode(ClientboundClearDialogPacket msg, FriendlyByteBuf buf) {
            // 无数据需要编码
        }

        public static ClientboundClearDialogPacket decode(FriendlyByteBuf buf) {
            return new ClientboundClearDialogPacket();
        }

        public static void handle(ClientboundClearDialogPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ClientDialogHandler.handleClearDialog();
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static void sendOfficialDialogOpen(ServerPlayer player, String dialogId, JsonObject dialog) {
        try {
            String dialogData = GSON.toJson(dialog);
            ClientboundOfficialDialogOpenPacket packet = new ClientboundOfficialDialogOpenPacket(dialogId, dialogData);
            INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);

            LOGGER.info("发送对话框数据包到: {}", player.getScoreboardName());
        } catch (Exception e) {
            LOGGER.error("发送对话框打开数据包失败", e);
        }
    }

    public static void sendClearDialog(ServerPlayer player) {
        try {
            ClientboundClearDialogPacket packet = new ClientboundClearDialogPacket();
            INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        } catch (Exception e) {
            LOGGER.error("发送清除对话框数据包失败", e);
        }
    }
}