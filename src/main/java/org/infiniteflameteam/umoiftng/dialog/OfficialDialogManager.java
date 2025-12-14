package org.infiniteflameteam.umoiftng.dialog;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.infiniteflameteam.umoiftng.network.DialogNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OfficialDialogManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfficialDialogManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, JsonObject> DIALOGS = new ConcurrentHashMap<>();

    public static void loadDialogs(ResourceManager resourceManager) {
        DIALOGS.clear();

        try {
            LOGGER.info("开始加载对话框...");

            loadFromNamespace(resourceManager, "umoift");
            loadFromNamespace(resourceManager, "minecraft");

            LOGGER.info("已加载 {} 个对话框定义", DIALOGS.size());

            if (DIALOGS.isEmpty()) {
                loadDefaultDialogs();
            }
        } catch (Exception e) {
            LOGGER.error("加载对话框时发生错误", e);
            loadDefaultDialogs();
        }
    }

    private static void loadFromNamespace(ResourceManager resourceManager, String namespace) {
        try {
            var resources = resourceManager.listResources("dialog",
                    location -> location.getPath().endsWith(".json") && location.getNamespace().equals(namespace)
            );

            LOGGER.info("在命名空间 {} 中找到 {} 个对话框文件", namespace, resources.size());

            for (var resource : resources.entrySet()) {
                try {
                    var resourceLocation = resource.getKey();
                    LOGGER.info("加载对话框: {}", resourceLocation);

                    var resourceOpt = resourceManager.getResource(resourceLocation);
                    if (resourceOpt.isPresent()) {
                        try (var stream = resourceOpt.get().open()) {
                            JsonObject dialogJson = GSON.fromJson(new InputStreamReader(stream), JsonObject.class);
                            String dialogId = extractDialogId(resourceLocation);
                            DIALOGS.put(dialogId, dialogJson);
                            LOGGER.info("成功加载对话框: {}", dialogId);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("加载对话框文件失败: {}", resource.getKey(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("从命名空间加载对话框失败: {}", namespace, e);
        }
    }

    private static String extractDialogId(ResourceLocation location) {
        String path = location.getPath();
        // 从路径中提取对话框ID，例如: "dialog/welcome.json" -> "welcome"
        return path.substring("dialog/".length()).replace(".json", "");
    }

    public static int showOfficialDialog(Collection<ServerPlayer> targets, String dialogId) {
        JsonObject dialog = DIALOGS.get(dialogId);
        if (dialog == null) {
            LOGGER.warn("对话框不存在: {}", dialogId);
            return 0;
        }

        int successCount = 0;
        for (ServerPlayer player : targets) {
            if (showOfficialDialogToPlayer(player, dialogId, dialog)) {
                successCount++;
            }
        }

        return successCount;
    }

    private static boolean showOfficialDialogToPlayer(ServerPlayer player, String dialogId, JsonObject dialog) {
        try {
            LOGGER.info("向玩家 {} 显示对话框: {}", player.getScoreboardName(), dialogId);

            // 发送网络数据包显示对话框
            DialogNetworkHandler.sendOfficialDialogOpen(player, dialogId, dialog);

            return true;
        } catch (Exception e) {
            LOGGER.error("向玩家显示对话框失败: {}", player.getScoreboardName(), e);
            return false;
        }
    }

    public static void handleOfficialDialogAction(ServerPlayer player, String dialogId, String actionType, JsonObject actionData) {
        LOGGER.info("处理对话框动作: {} - {} - {}", dialogId, actionType, actionData);

        // 根据动作类型执行相应操作
        switch (actionType) {
            case "run_command":
                handleRunCommandAction(player, actionData);
                break;
            case "show_dialog":
                handleShowDialogAction(player, actionData);
                break;
            case "close":
                // 关闭对话框，由客户端处理
                break;
            default:
                LOGGER.warn("未知的对话框动作类型: {}", actionType);
        }
    }

    private static void handleRunCommandAction(ServerPlayer player, JsonObject actionData) {
        if (actionData.has("command")) {
            String command = actionData.get("command").getAsString();
            LOGGER.info("执行对话框命令: {}", command);
            player.getServer().getCommands().performPrefixedCommand(
                    player.createCommandSourceStack(), command
            );
        }
    }

    private static void handleShowDialogAction(ServerPlayer player, JsonObject actionData) {
        if (actionData.has("dialog")) {
            String targetDialog = actionData.get("dialog").getAsString();
            LOGGER.info("打开对话框: {}", targetDialog);
            showOfficialDialog(Collections.singleton(player), targetDialog);
        }
    }

    public static List<String> getAvailableOfficialDialogs() {
        return new ArrayList<>(DIALOGS.keySet());
    }

    private static void loadDefaultDialogs() {
        try {
            LOGGER.info("加载默认对话框...");

            // 默认的notice对话框
            JsonObject noticeDialog = new JsonObject();
            noticeDialog.addProperty("type", "notice");

            // 修复标题 - 使用JsonObject而不是字符串
            JsonObject titleObj = new JsonObject();
            titleObj.addProperty("text", "UMOIFTNG 模组");
            titleObj.addProperty("color", "gold");
            noticeDialog.add("title", titleObj);

            JsonArray body = new JsonArray();
            JsonObject message = new JsonObject();
            message.addProperty("type", "plain_message");

            // 修复消息内容 - 使用JsonObject
            JsonObject messageContent = new JsonObject();
            messageContent.addProperty("text", "欢迎使用无限火队通用下一代模组！");
            messageContent.addProperty("color", "white");
            message.add("contents", messageContent);

            body.add(message);
            noticeDialog.add("body", body);

            JsonObject action = new JsonObject();

            // 修复按钮标签 - 使用JsonObject
            JsonObject labelObj = new JsonObject();
            labelObj.addProperty("text", "确定");
            labelObj.addProperty("color", "green");
            action.add("label", labelObj);

            JsonObject actionData = new JsonObject();
            actionData.addProperty("type", "close");
            action.add("action", actionData);

            noticeDialog.add("action", action);
            noticeDialog.addProperty("pause", true);
            noticeDialog.addProperty("can_close_with_escape", true);
            noticeDialog.addProperty("after_action", "close");

            DIALOGS.put("welcome", noticeDialog);

            LOGGER.info("默认对话框加载完成");

        } catch (Exception e) {
            LOGGER.error("加载默认对话框失败", e);
        }
    }

    public static JsonObject getDialog(String dialogId) {
        return DIALOGS.get(dialogId);
    }
}