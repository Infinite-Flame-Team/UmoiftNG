package org.infiniteflameteam.umoiftng.claim;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimData {
    private final String claimName;
    private final UUID ownerId;
    private final String ownerName;
    private final BlockPos from;
    private final BlockPos to;
    private final String dimension;
    private final long createdTime;

    private final Map<UUID, Map<String, Boolean>> playerPermissions = new ConcurrentHashMap<>();
    private final Set<UUID> opPlayers = new HashSet<>();

    public ClaimData(String claimName, ServerPlayer owner, BlockPos from, BlockPos to) {
        this.claimName = claimName;
        this.ownerId = owner.getUUID();
        this.ownerName = owner.getScoreboardName();
        this.from = new BlockPos(
                Math.min(from.getX(), to.getX()),
                Math.min(from.getY(), to.getY()),
                Math.min(from.getZ(), to.getZ())
        );
        this.to = new BlockPos(
                Math.max(from.getX(), to.getX()),
                Math.max(from.getY(), to.getY()),
                Math.max(from.getZ(), to.getZ())
        );
        this.dimension = owner.level().dimension().location().toString();
        this.createdTime = System.currentTimeMillis();

        setDefaultPermissions(ownerId);
    }

    private void setDefaultPermissions(UUID playerId) {
        Map<String, Boolean> permissions = new ConcurrentHashMap<>();
        permissions.put("build", true);
        permissions.put("break", true);
        permissions.put("interact", true);
        permissions.put("use", true);
        permissions.put("pvp", false);
        permissions.put("explosion", false);
        permissions.put("fire_spread", false);
        permissions.put("mob_griefing", true);

        playerPermissions.put(playerId, permissions);
    }

    public String getClaimName() { return claimName; }
    public UUID getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public BlockPos getFrom() { return from; }
    public BlockPos getTo() { return to; }
    public String getDimension() { return dimension; }
    public long getCreatedTime() { return createdTime; }
    public Set<UUID> getOpPlayers() { return Collections.unmodifiableSet(opPlayers); }

    // 检查位置是否在领地内（使用维度字符串）
    public boolean contains(String dimension, BlockPos pos) {
        if (!this.dimension.equals(dimension)) {
            return false;
        }

        return pos.getX() >= from.getX() && pos.getX() <= to.getX() &&
                pos.getY() >= from.getY() && pos.getY() <= to.getY() &&
                pos.getZ() >= from.getZ() && pos.getZ() <= to.getZ();
    }

    // 权限管理
    public boolean setPermission(UUID playerId, String permission, boolean value) {
        if (playerId.equals(ownerId)) {
            return false;
        }

        Map<String, Boolean> permissions = playerPermissions.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        permissions.put(permission, value);
        return true;
    }

    public boolean getPermission(UUID playerId, String permission) {
        if (playerId.equals(ownerId)) {
            return true;
        }

        if (opPlayers.contains(playerId)) {
            return true;
        }

        Map<String, Boolean> permissions = playerPermissions.get(playerId);
        if (permissions == null) {
            return false;
        }

        Boolean value = permissions.get(permission);
        return value != null ? value : false;
    }

    public Map<String, Boolean> getAllPermissions(UUID playerId) {
        if (playerId.equals(ownerId)) {
            Map<String, Boolean> allTrue = new ConcurrentHashMap<>();
            allTrue.put("build", true);
            allTrue.put("break", true);
            allTrue.put("interact", true);
            allTrue.put("use", true);
            allTrue.put("pvp", true);
            allTrue.put("explosion", true);
            allTrue.put("fire_spread", true);
            allTrue.put("mob_griefing", true);
            return allTrue;
        }

        return Collections.unmodifiableMap(playerPermissions.getOrDefault(playerId, new ConcurrentHashMap<>()));
    }

    public Set<UUID> getPlayersWithPermissions() {
        Set<UUID> players = new HashSet<>(playerPermissions.keySet());
        players.add(ownerId);
        return players;
    }

    // OP管理
    public boolean addOpPlayer(UUID playerId) {
        return opPlayers.add(playerId);
    }

    public boolean removeOpPlayer(UUID playerId) {
        return opPlayers.remove(playerId);
    }

    public boolean isOpPlayer(UUID playerId) {
        return opPlayers.contains(playerId);
    }

    public boolean canModify(UUID playerId) {
        return playerId.equals(ownerId) || opPlayers.contains(playerId);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("claimName", claimName);
        json.addProperty("ownerId", ownerId.toString());
        json.addProperty("ownerName", ownerName);
        json.addProperty("fromX", from.getX());
        json.addProperty("fromY", from.getY());
        json.addProperty("fromZ", from.getZ());
        json.addProperty("toX", to.getX());
        json.addProperty("toY", to.getY());
        json.addProperty("toZ", to.getZ());
        json.addProperty("dimension", dimension);
        json.addProperty("createdTime", createdTime);

        JsonObject permissionsJson = new JsonObject();
        for (Map.Entry<UUID, Map<String, Boolean>> entry : playerPermissions.entrySet()) {
            JsonObject playerPerms = new JsonObject();
            for (Map.Entry<String, Boolean> permEntry : entry.getValue().entrySet()) {
                playerPerms.addProperty(permEntry.getKey(), permEntry.getValue());
            }
            permissionsJson.add(entry.getKey().toString(), playerPerms);
        }
        json.add("permissions", permissionsJson);

        JsonArray opArray = new JsonArray();
        for (UUID opId : opPlayers) {
            opArray.add(opId.toString());
        }
        json.add("opPlayers", opArray);

        return json;
    }

    public String getInfoString() {
        return String.format("领地: %s\n主人: %s\n位置: (%d,%d,%d) 到 (%d,%d,%d)\n维度: %s\n创建时间: %s",
                claimName, ownerName,
                from.getX(), from.getY(), from.getZ(),
                to.getX(), to.getY(), to.getZ(),
                dimension, new Date(createdTime).toString()
        );
    }
}