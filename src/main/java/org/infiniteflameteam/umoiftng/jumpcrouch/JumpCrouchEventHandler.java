package org.infiniteflameteam.umoiftng.jumpcrouch;

import org.infiniteflameteam.umoiftng.crouch.CrouchHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class JumpCrouchEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JumpCrouchEventHandler.class);

    // 跟踪玩家的跳跃状态
    private static final Map<UUID, Boolean> PLAYER_JUMPING = new HashMap<>();
    private static final Map<UUID, Long> LAST_TELEPORT_TIME = new HashMap<>();
    private static final long TELEPORT_COOLDOWN = 500; // 500毫秒冷却

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

        // 检查玩家是否在跳跃（通过垂直速度检测）
        boolean isJumping = player.getDeltaMovement().y > 0.1;
        boolean wasJumping = PLAYER_JUMPING.getOrDefault(player.getUUID(), false);

        // 检查玩家是否在蹲下
        boolean isCrouching = CrouchHandler.isCrouching(player);

        // 玩家开始跳跃并蹲下
        if (isJumping && !wasJumping && isCrouching) {
            // 检查是否有适用的规则
            JumpCrouchRule rule = JumpCrouchRuleManager.getInstance().getRuleForPlayer(player);
            if (rule != null) {
                // 检查冷却时间
                long currentTime = System.currentTimeMillis();
                long lastTeleport = LAST_TELEPORT_TIME.getOrDefault(player.getUUID(), 0L);

                if (currentTime - lastTeleport >= TELEPORT_COOLDOWN) {
                    // 将玩家向上传送1格
                    Vec3 currentPos = player.position();
                    Vec3 newPos = new Vec3(currentPos.x, currentPos.y + 1.0, currentPos.z);

                    player.teleportTo(newPos.x, newPos.y, newPos.z);

                    LAST_TELEPORT_TIME.put(player.getUUID(), currentTime);
                    LOGGER.debug("玩家 {} 跳跃蹲下传送 +1格", player.getScoreboardName());
                }
            }
        }

        // 更新跳跃状态
        PLAYER_JUMPING.put(player.getUUID(), isJumping);
    }
}