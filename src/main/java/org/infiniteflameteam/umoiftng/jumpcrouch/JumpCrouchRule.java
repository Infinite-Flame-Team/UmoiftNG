package org.infiniteflameteam.umoiftng.jumpcrouch;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.UUID;

public class JumpCrouchRule {
    private final String ruleId;
    private final String selector;
    private final BlockPos from;
    private final BlockPos to;
    private final UUID creatorId;
    private final long createdTime;

    public JumpCrouchRule(String selector, BlockPos from, BlockPos to, UUID creatorId) {
        this.ruleId = UUID.randomUUID().toString().substring(0, 8);
        this.selector = selector != null ? selector : "@a";
        this.from = from;
        this.to = to;
        this.creatorId = creatorId;
        this.createdTime = System.currentTimeMillis();
    }

    public String getRuleId() { return ruleId; }
    public String getSelector() { return selector; }
    public BlockPos getFrom() { return from; }
    public BlockPos getTo() { return to; }
    public UUID getCreatorId() { return creatorId; }
    public long getCreatedTime() { return createdTime; }

    // 检查玩家是否匹配此规则
    public boolean matches(Player player) {
        // 检查选择器
        if (!matchesSelector(player)) return false;

        // 如果没有指定区域，则为全局规则
        if (from == null && to == null) return true;

        // 检查是否在区域内
        if (from == null || to == null) return false;

        BlockPos pos = player.blockPosition();
        return pos.getX() >= Math.min(from.getX(), to.getX()) &&
                pos.getX() <= Math.max(from.getX(), to.getX()) &&
                pos.getY() >= Math.min(from.getY(), to.getY()) &&
                pos.getY() <= Math.max(from.getY(), to.getY()) &&
                pos.getZ() >= Math.min(from.getZ(), to.getZ()) &&
                pos.getZ() <= Math.max(from.getZ(), to.getZ());
    }

    // 检查选择器
    private boolean matchesSelector(Player player) {
        if (selector.startsWith("@")) {
            return switch (selector) {
                case "@a", "@p", "@r", "@s" -> true; // 所有玩家选择器都接受
                default -> false;
            };
        } else {
            // 玩家名匹配
            return player.getScoreboardName().equalsIgnoreCase(selector);
        }
    }

    // 获取区域边界框
    public AABB getBounds() {
        if (from == null || to == null) return null;
        return new AABB(
                Math.min(from.getX(), to.getX()),
                Math.min(from.getY(), to.getY()),
                Math.min(from.getZ(), to.getZ()),
                Math.max(from.getX(), to.getX()) + 1,
                Math.max(from.getY(), to.getY()) + 1,
                Math.max(from.getZ(), to.getZ()) + 1
        );
    }

    // 转换为JSON
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("ruleId", ruleId);
        json.addProperty("selector", selector);
        json.addProperty("creatorId", creatorId.toString());
        json.addProperty("createdTime", createdTime);

        if (from != null && to != null) {
            JsonArray fromArray = new JsonArray();
            fromArray.add(from.getX());
            fromArray.add(from.getY());
            fromArray.add(from.getZ());
            json.add("from", fromArray);

            JsonArray toArray = new JsonArray();
            toArray.add(to.getX());
            toArray.add(to.getY());
            toArray.add(to.getZ());
            json.add("to", toArray);
        }

        return json;
    }

    // 从JSON创建
    public static JumpCrouchRule fromJson(JsonObject json) {
        String selector = json.get("selector").getAsString();
        UUID creatorId = UUID.fromString(json.get("creatorId").getAsString());

        BlockPos from = null;
        BlockPos to = null;

        if (json.has("from") && json.has("to")) {
            JsonArray fromArray = json.getAsJsonArray("from");
            JsonArray toArray = json.getAsJsonArray("to");

            from = new BlockPos(
                    fromArray.get(0).getAsInt(),
                    fromArray.get(1).getAsInt(),
                    fromArray.get(2).getAsInt()
            );

            to = new BlockPos(
                    toArray.get(0).getAsInt(),
                    toArray.get(1).getAsInt(),
                    toArray.get(2).getAsInt()
            );
        }

        return new JumpCrouchRule(selector, from, to, creatorId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("规则ID: ").append(ruleId).append(" | ");
        sb.append("选择器: ").append(selector).append(" | ");

        if (from != null && to != null) {
            sb.append("区域: (").append(from.getX()).append(",").append(from.getY()).append(",").append(from.getZ())
                    .append(") → (").append(to.getX()).append(",").append(to.getY()).append(",").append(to.getZ()).append(")");
        } else {
            sb.append("区域: 全局");
        }

        return sb.toString();
    }
}