package org.infiniteflameteam.umoiftng.crouch;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CrouchHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrouchHandler.class);

    private static final Map<UUID, Boolean> CROUCHING_PLAYERS = new HashMap<>();

    public static void setCrouching(ServerPlayer player, boolean crouching) {
        UUID playerId = player.getUUID();
        Boolean wasCrouching = CROUCHING_PLAYERS.get(playerId);

        if (wasCrouching != null && wasCrouching == crouching) {
            return;
        }

        CROUCHING_PLAYERS.put(playerId, crouching);

        if (crouching) {
            player.setPose(Pose.SWIMMING);
            LOGGER.debug("玩家 {} 开始蹲下（游泳姿势）", player.getScoreboardName());
        } else {
            player.setPose(Pose.STANDING);
            LOGGER.debug("玩家 {} 停止蹲下", player.getScoreboardName());
        }
    }

    public static boolean isCrouching(ServerPlayer player) {
        return CROUCHING_PLAYERS.getOrDefault(player.getUUID(), false);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        boolean isCrouching = isCrouching(player);

        if (isCrouching && player.getPose() != Pose.SWIMMING) {
            player.setPose(Pose.SWIMMING);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CROUCHING_PLAYERS.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CROUCHING_PLAYERS.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CROUCHING_PLAYERS.remove(player.getUUID());
        }
    }
}