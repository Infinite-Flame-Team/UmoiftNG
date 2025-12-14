package org.infiniteflameteam.umoiftng.claim;

import org.infiniteflameteam.umoiftng.localization.LanguageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber
public class ClaimProtectionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimProtectionHandler.class);

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        BlockPos pos = event.getPos();
        LevelAccessor level = event.getLevel();

        if (!ClaimManager.getInstance().hasPermission(level, pos, player, "break")) {
            event.setCanceled(true);
            player.displayClientMessage(LanguageManager.translateComponent("umoiftng.claim.no_permission.break"), true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BlockPos pos = event.getPos();
            LevelAccessor level = event.getLevel();

            if (!ClaimManager.getInstance().hasPermission(level, pos, player, "build")) {
                event.setCanceled(true);
                player.displayClientMessage(LanguageManager.translateComponent("umoiftng.claim.no_permission.build"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getLevel().isClientSide()) {
            return;
        }

        BlockPos pos = event.getPos();
        LevelAccessor level = event.getLevel();

        if (!ClaimManager.getInstance().hasPermission(level, pos, player, "interact")) {
            event.setCanceled(true);
            player.displayClientMessage(LanguageManager.translateComponent("umoiftng.claim.no_permission.interact"), true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getLevel().isClientSide()) {
            return;
        }

        BlockPos pos = event.getTarget().blockPosition();
        LevelAccessor level = event.getLevel();

        if (!ClaimManager.getInstance().hasPermission(level, pos, player, "interact")) {
            event.setCanceled(true);
            player.displayClientMessage(LanguageManager.translateComponent("umoiftng.claim.no_permission.interact_entity"), true);
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getLevel().isClientSide()) {
            return;
        }

        BlockPos pos = event.getEntity().blockPosition();
        LevelAccessor level = event.getLevel();

        if (!ClaimManager.getInstance().hasPermission(level, pos, player, "use")) {
            event.setCanceled(true);
            player.displayClientMessage(LanguageManager.translateComponent("umoiftng.claim.no_permission.use"), true);
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        LevelAccessor level = event.getLevel();
        Explosion explosion = event.getExplosion();

        event.getAffectedBlocks().removeIf(pos -> {
            ClaimData claim = ClaimManager.getInstance().getClaimAt(level, pos);
            if (claim != null) {
                return false; // 暂时允许所有爆炸
            }
            return false;
        });

        event.getAffectedEntities().removeIf(entity -> {
            if (entity instanceof ServerPlayer player) {
                ClaimData claim = ClaimManager.getInstance().getClaimAt(level, entity.blockPosition());
                if (claim != null) {
                    return !ClaimManager.getInstance().hasPermission(level, entity.blockPosition(), player, "pvp");
                }
            }
            return false;
        });
    }
}