package org.infiniteflameteam.umoiftng.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.infiniteflameteam.umoiftng.Main;
import org.infiniteflameteam.umoiftng.crouch.CrouchHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class CrouchNetworkHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrouchNetworkHandler.class);
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MODID, "crouch"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(packetId++,
                ServerboundCrouchPacket.class,
                ServerboundCrouchPacket::encode,
                ServerboundCrouchPacket::decode,
                ServerboundCrouchPacket::handle);
    }

    public static class ServerboundCrouchPacket {
        private final boolean crouching;

        public ServerboundCrouchPacket(boolean crouching) {
            this.crouching = crouching;
        }

        public static void encode(ServerboundCrouchPacket msg, FriendlyByteBuf buf) {
            buf.writeBoolean(msg.crouching);
        }

        public static ServerboundCrouchPacket decode(FriendlyByteBuf buf) {
            return new ServerboundCrouchPacket(buf.readBoolean());
        }

        public static void handle(ServerboundCrouchPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    CrouchHandler.setCrouching(player, msg.crouching);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static void sendCrouching(boolean crouching) {
        try {
            ServerboundCrouchPacket packet = new ServerboundCrouchPacket(crouching);
            INSTANCE.sendToServer(packet);
        } catch (Exception e) {
            LOGGER.error("发送蹲下状态失败", e);
        }
    }
}