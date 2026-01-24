package org.infiniteflameteam.umoiftng.jumpcrouch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class JumpCrouchRuleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JumpCrouchRuleManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static JumpCrouchRuleManager instance;

    private final List<JumpCrouchRule> rules = new CopyOnWriteArrayList<>();
    private final Map<String, JumpCrouchRule> ruleById = new ConcurrentHashMap<>();

    private JumpCrouchRuleManager() {
        loadRules();
    }

    public static JumpCrouchRuleManager getInstance() {
        if (instance == null) {
            instance = new JumpCrouchRuleManager();
        }
        return instance;
    }

    // 添加规则
    public JumpCrouchRule addRule(String selector, BlockPos from, BlockPos to, UUID creatorId) {
        JumpCrouchRule rule = new JumpCrouchRule(selector, from, to, creatorId);
        rules.add(rule);
        ruleById.put(rule.getRuleId(), rule);

        saveRules();
        LOGGER.info("添加跳跃蹲下规则: {}", rule.getRuleId());
        return rule;
    }

    // 删除规则
    public boolean removeRule(String ruleId) {
        JumpCrouchRule rule = ruleById.remove(ruleId);
        if (rule != null) {
            rules.remove(rule);
            saveRules();
            LOGGER.info("删除跳跃蹲下规则: {}", ruleId);
            return true;
        }
        return false;
    }

    // 删除区域规则
    public boolean removeRuleByArea(BlockPos from, BlockPos to) {
        Iterator<JumpCrouchRule> iterator = rules.iterator();
        while (iterator.hasNext()) {
            JumpCrouchRule rule = iterator.next();
            BlockPos ruleFrom = rule.getFrom();
            BlockPos ruleTo = rule.getTo();

            if (ruleFrom != null && ruleTo != null &&
                    ruleFrom.equals(from) && ruleTo.equals(to)) {
                iterator.remove();
                ruleById.remove(rule.getRuleId());
                saveRules();
                LOGGER.info("删除区域规则: {} 到 {}", from, to);
                return true;
            }
        }
        return false;
    }

    // 列出所有规则
    public List<JumpCrouchRule> getAllRules() {
        return new ArrayList<>(rules);
    }

    // 获取玩家适用的规则
    public JumpCrouchRule getRuleForPlayer(ServerPlayer player) {
        // 首先检查区域规则
        for (JumpCrouchRule rule : rules) {
            if (rule.matches(player) && rule.getFrom() != null && rule.getTo() != null) {
                return rule;
            }
        }

        // 然后检查全局规则
        for (JumpCrouchRule rule : rules) {
            if (rule.matches(player) && rule.getFrom() == null && rule.getTo() == null) {
                return rule;
            }
        }

        return null;
    }

    // 检查玩家是否应该被传送
    public boolean shouldTeleport(ServerPlayer player) {
        JumpCrouchRule rule = getRuleForPlayer(player);
        return rule != null; // 规则存在就意味着启用
    }

    // 保存规则
    public void saveRules() {
        try {
            Path rulesDir = getDataDirectory();
            Files.createDirectories(rulesDir);

            JsonArray rulesArray = new JsonArray();
            for (JumpCrouchRule rule : rules) {
                rulesArray.add(rule.toJson());
            }

            Path rulesFile = rulesDir.resolve("jump_crouch_rules.json");
            Files.writeString(rulesFile, GSON.toJson(rulesArray));
        } catch (IOException e) {
            LOGGER.error("保存跳跃蹲下规则失败", e);
        }
    }

    // 加载规则
    private void loadRules() {
        try {
            Path rulesFile = getDataDirectory().resolve("jump_crouch_rules.json");
            if (!Files.exists(rulesFile)) {
                return;
            }

            String jsonString = Files.readString(rulesFile);
            JsonArray rulesArray = GSON.fromJson(jsonString, JsonArray.class);

            rules.clear();
            ruleById.clear();

            for (int i = 0; i < rulesArray.size(); i++) {
                try {
                    JsonObject ruleJson = rulesArray.get(i).getAsJsonObject();
                    JumpCrouchRule rule = JumpCrouchRule.fromJson(ruleJson);
                    rules.add(rule);
                    ruleById.put(rule.getRuleId(), rule);
                } catch (Exception e) {
                    LOGGER.error("加载规则失败: 索引 {}", i, e);
                }
            }

            LOGGER.info("已加载 {} 个跳跃蹲下规则", rules.size());
        } catch (IOException e) {
            LOGGER.error("加载跳跃蹲下规则失败", e);
        }
    }

    private Path getDataDirectory() {
        return Paths.get("config/umoiftng/jump_crouch");
    }
}