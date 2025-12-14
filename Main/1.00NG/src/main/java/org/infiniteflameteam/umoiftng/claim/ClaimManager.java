package org.infiniteflameteam.umoiftng.claim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ClaimManager instance;

    private final Map<String, ClaimData> claims = new ConcurrentHashMap<>();
    private final Map<String, Map<BlockPos, String>> dimensionClaims = new ConcurrentHashMap<>();
    private final Set<UUID> globalOpPlayers = new HashSet<>();

    private ClaimManager() {
        loadClaims();
        loadGlobalOps();
    }

    public static ClaimManager getInstance() {
        if (instance == null) {
            instance = new ClaimManager();
        }
        return instance;
    }

    public boolean createClaim(String claimName, ServerPlayer owner, BlockPos from, BlockPos to) {
        if (claims.containsKey(claimName)) {
            return false;
        }

        if (isOverlapping(owner.level(), from, to, null)) {
            return false;
        }

        ClaimData claim = new ClaimData(claimName, owner, from, to);
        claims.put(claimName, claim);
        updateDimensionIndex(claim);

        saveClaims();
        LOGGER.info("玩家 {} 创建了领地: {}", owner.getScoreboardName(), claimName);
        return true;
    }

    private boolean isOverlapping(Level level, BlockPos from, BlockPos to, String excludeClaimName) {
        String dimension = level.dimension().location().toString();
        Map<BlockPos, String> dimClaims = dimensionClaims.get(dimension);

        if (dimClaims == null) {
            return false;
        }

        BlockPos min = new BlockPos(
                Math.min(from.getX(), to.getX()),
                Math.min(from.getY(), to.getY()),
                Math.min(from.getZ(), to.getZ())
        );

        BlockPos max = new BlockPos(
                Math.max(from.getX(), to.getX()),
                Math.max(from.getY(), to.getY()),
                Math.max(from.getZ(), to.getZ())
        );

        for (BlockPos pos : dimClaims.keySet()) {
            String existingClaimName = dimClaims.get(pos);
            if (excludeClaimName != null && excludeClaimName.equals(existingClaimName)) {
                continue;
            }

            ClaimData existingClaim = claims.get(existingClaimName);
            if (existingClaim != null) {
                if (pos.getX() >= min.getX() && pos.getX() <= max.getX() &&
                        pos.getY() >= min.getY() && pos.getY() <= max.getY() &&
                        pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void updateDimensionIndex(ClaimData claim) {
        String dimension = claim.getDimension();
        Map<BlockPos, String> dimClaims = dimensionClaims.computeIfAbsent(dimension, k -> new ConcurrentHashMap<>());

        BlockPos from = claim.getFrom();
        BlockPos to = claim.getTo();

        dimClaims.put(from, claim.getClaimName());
        dimClaims.put(to, claim.getClaimName());
        dimClaims.put(new BlockPos(from.getX(), from.getY(), to.getZ()), claim.getClaimName());
        dimClaims.put(new BlockPos(to.getX(), to.getY(), from.getZ()), claim.getClaimName());
    }

    public boolean removeClaim(String claimName, ServerPlayer requester) {
        ClaimData claim = claims.get(claimName);
        if (claim == null) {
            return false;
        }

        if (!hasModifyPermission(claim, requester)) {
            return false;
        }

        String dimension = claim.getDimension();
        Map<BlockPos, String> dimClaims = dimensionClaims.get(dimension);
        if (dimClaims != null) {
            dimClaims.values().removeIf(name -> name.equals(claimName));
        }

        claims.remove(claimName);
        saveClaims();
        LOGGER.info("玩家 {} 删除了领地: {}", requester.getScoreboardName(), claimName);
        return true;
    }

    public boolean modifyClaim(String oldName, String newName, BlockPos newFrom, BlockPos newTo,
                               ServerPlayer newOwner, ServerPlayer requester) {
        ClaimData oldClaim = claims.get(oldName);
        if (oldClaim == null) {
            return false;
        }

        if (!hasModifyPermission(oldClaim, requester)) {
            return false;
        }

        if (!oldName.equals(newName) && claims.containsKey(newName)) {
            return false;
        }

        if (isOverlapping(requester.level(), newFrom, newTo, oldName)) {
            return false;
        }

        String dimension = oldClaim.getDimension();
        Map<BlockPos, String> dimClaims = dimensionClaims.get(dimension);
        if (dimClaims != null) {
            dimClaims.values().removeIf(name -> name.equals(oldName));
        }

        ClaimData newClaim = new ClaimData(newName, newOwner, newFrom, newTo);

        for (UUID playerId : oldClaim.getPlayersWithPermissions()) {
            if (!playerId.equals(oldClaim.getOwnerId())) {
                Map<String, Boolean> perms = oldClaim.getAllPermissions(playerId);
                for (Map.Entry<String, Boolean> entry : perms.entrySet()) {
                    newClaim.setPermission(playerId, entry.getKey(), entry.getValue());
                }
            }
        }

        for (UUID opId : oldClaim.getOpPlayers()) {
            newClaim.addOpPlayer(opId);
        }

        claims.remove(oldName);
        claims.put(newName, newClaim);
        updateDimensionIndex(newClaim);

        saveClaims();
        LOGGER.info("玩家 {} 修改了领地 {} 为 {}", requester.getScoreboardName(), oldName, newName);
        return true;
    }

    public ClaimData getClaim(String claimName) {
        return claims.get(claimName);
    }

    public List<String> getAllClaimNames() {
        return new ArrayList<>(claims.keySet());
    }

    public List<ClaimData> getPlayerClaims(ServerPlayer player) {
        List<ClaimData> result = new ArrayList<>();
        UUID playerId = player.getUUID();

        for (ClaimData claim : claims.values()) {
            if (claim.getOwnerId().equals(playerId)) {
                result.add(claim);
            }
        }

        return result;
    }

    // 原有方法，支持 Level
    public ClaimData getClaimAt(Level level, BlockPos pos) {
        return getClaimAt(level.dimension().location().toString(), pos);
    }

    // 新方法，支持 LevelAccessor
    public ClaimData getClaimAt(LevelAccessor level, BlockPos pos) {
        if (level instanceof Level) {
            return getClaimAt((Level) level, pos);
        }

        // 对于非 Level 的 LevelAccessor，尝试获取维度信息
        // 注意：LevelAccessor 可能没有直接的维度信息获取方法
        // 这里简化为尝试从可能的上下文中获取
        return null;
    }

    // 内部方法，使用维度字符串
    private ClaimData getClaimAt(String dimension, BlockPos pos) {
        Map<BlockPos, String> dimClaims = dimensionClaims.get(dimension);

        if (dimClaims == null) {
            return null;
        }

        for (BlockPos indexPos : dimClaims.keySet()) {
            if (Math.abs(indexPos.getX() - pos.getX()) < 100 &&
                    Math.abs(indexPos.getZ() - pos.getZ()) < 100) {
                String claimName = dimClaims.get(indexPos);
                ClaimData claim = claims.get(claimName);
                if (claim != null && claim.contains(dimension, pos)) {
                    return claim;
                }
            }
        }

        return null;
    }

    // 原有方法，支持 Level
    public boolean hasPermission(Level level, BlockPos pos, ServerPlayer player, String permission) {
        return hasPermission(level.dimension().location().toString(), pos, player, permission);
    }

    // 新方法，支持 LevelAccessor
    public boolean hasPermission(LevelAccessor level, BlockPos pos, ServerPlayer player, String permission) {
        if (level instanceof Level) {
            return hasPermission((Level) level, pos, player, permission);
        }

        // 对于非 Level 的 LevelAccessor，默认允许
        return true;
    }

    // 内部方法，使用维度字符串
    private boolean hasPermission(String dimension, BlockPos pos, ServerPlayer player, String permission) {
        ClaimData claim = getClaimAt(dimension, pos);
        if (claim == null) {
            return true; // 不在领地内，允许所有操作
        }

        return claim.getPermission(player.getUUID(), permission);
    }

    // 检查修改权限
    private boolean hasModifyPermission(ClaimData claim, ServerPlayer player) {
        UUID playerId = player.getUUID();

        // 服务器OP
        if (player.hasPermissions(2)) {
            return true;
        }

        // 全局OP
        if (globalOpPlayers.contains(playerId)) {
            return true;
        }

        // 领地主人或领地OP
        return claim.canModify(playerId);
    }

    // 全局OP管理
    public boolean addGlobalOp(ServerPlayer player) {
        boolean added = globalOpPlayers.add(player.getUUID());
        if (added) {
            saveGlobalOps();
            LOGGER.info("添加全局OP: {}", player.getScoreboardName());
        }
        return added;
    }

    public boolean removeGlobalOp(ServerPlayer player) {
        boolean removed = globalOpPlayers.remove(player.getUUID());
        if (removed) {
            saveGlobalOps();
            LOGGER.info("移除全局OP: {}", player.getScoreboardName());
        }
        return removed;
    }

    public boolean isGlobalOp(ServerPlayer player) {
        return globalOpPlayers.contains(player.getUUID());
    }

    public boolean canUseClaimCommand(ServerPlayer player) {
        return player.hasPermissions(2) || globalOpPlayers.contains(player.getUUID());
    }

    // 保存领地数据
    public void saveClaims() {
        try {
            Path claimsDir = getDataDirectory();
            Files.createDirectories(claimsDir);

            JsonObject allClaims = new JsonObject();
            for (Map.Entry<String, ClaimData> entry : claims.entrySet()) {
                allClaims.add(entry.getKey(), entry.getValue().toJson());
            }

            Path claimsFile = claimsDir.resolve("claims.json");
            Files.writeString(claimsFile, GSON.toJson(allClaims));
        } catch (IOException e) {
            LOGGER.error("保存领地数据失败", e);
        }
    }

    // 加载领地数据
    private void loadClaims() {
        try {
            Path claimsFile = getDataDirectory().resolve("claims.json");
            if (!Files.exists(claimsFile)) {
                return;
            }

            String jsonString = Files.readString(claimsFile);
            JsonObject allClaims = GSON.fromJson(jsonString, JsonObject.class);

            claims.clear();
            dimensionClaims.clear();

            for (String claimName : allClaims.keySet()) {
                try {
                    JsonObject claimJson = allClaims.getAsJsonObject(claimName);
                } catch (Exception e) {
                    LOGGER.error("加载领地失败: {}", claimName, e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("加载领地数据失败", e);
        }
    }

    // 保存全局OP
    private void saveGlobalOps() {
        try {
            Path opsFile = getDataDirectory().resolve("global_ops.json");
            JsonArray opArray = new JsonArray();
            for (UUID opId : globalOpPlayers) {
                opArray.add(opId.toString());
            }

            JsonObject root = new JsonObject();
            root.add("ops", opArray);

            Files.writeString(opsFile, GSON.toJson(root));
        } catch (IOException e) {
            LOGGER.error("保存全局OP失败", e);
        }
    }

    // 加载全局OP
    private void loadGlobalOps() {
        try {
            Path opsFile = getDataDirectory().resolve("global_ops.json");
            if (!Files.exists(opsFile)) {
                return;
            }

            String jsonString = Files.readString(opsFile);
            JsonObject root = GSON.fromJson(jsonString, JsonObject.class);
            JsonArray opArray = root.getAsJsonArray("ops");

            globalOpPlayers.clear();
            for (int i = 0; i < opArray.size(); i++) {
                try {
                    UUID opId = UUID.fromString(opArray.get(i).getAsString());
                    globalOpPlayers.add(opId);
                } catch (Exception e) {
                    LOGGER.error("解析全局OP ID失败", e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("加载全局OP失败", e);
        }
    }

    private Path getDataDirectory() {
        return Paths.get("config/umoiftng/claims");
    }
}