package org.infiniteflameteam.umoiftng.claim;

import org.infiniteflameteam.umoiftng.localization.LanguageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ClaimMovementHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimMovementHandler.class);
    private static final Map<UUID, String> playerCurrentClaims = new HashMap<>();

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

        if (player.tickCount % 10 != 0) {
            return;
        }

        BlockPos pos = player.blockPosition();
        ClaimData currentClaim = ClaimManager.getInstance().getClaimAt(player.level(), pos);
        String currentClaimName = currentClaim != null ? currentClaim.getClaimName() : null;
        String previousClaimName = playerCurrentClaims.get(player.getUUID());

        if (currentClaimName != null && !currentClaimName.equals(previousClaimName)) {
            player.displayClientMessage(LanguageManager.literalComponent(
                    "umoiftng.claim.enter",
                    currentClaim.getOwnerName(), currentClaimName
            ), false);

            playerCurrentClaims.put(player.getUUID(), currentClaimName);
            LOGGER.debug("玩家 {} 进入了领地 {}", player.getScoreboardName(), currentClaimName);
        } else if (currentClaimName == null && previousClaimName != null) {
            ClaimData previousClaim = ClaimManager.getInstance().getClaim(previousClaimName);
            if (previousClaim != null) {
                player.displayClientMessage(LanguageManager.literalComponent(
                        "umoiftng.claim.leave",
                        previousClaim.getOwnerName(), previousClaimName
                ), false);
            }

            playerCurrentClaims.remove(player.getUUID());
            LOGGER.debug("玩家 {} 离开了领地 {}", player.getScoreboardName(), previousClaimName);
        } else if (currentClaimName != null) {
            playerCurrentClaims.put(player.getUUID(), currentClaimName);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            playerCurrentClaims.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            playerCurrentClaims.remove(player.getUUID());
        }
    }
}