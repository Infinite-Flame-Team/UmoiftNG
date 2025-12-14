//package com.infiniteflameteam.umoiftng.commands;
//
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.arguments.StringArgumentType;
//import com.mojang.brigadier.context.CommandContext;
//import com.mojang.brigadier.suggestion.SuggestionProvider;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//import net.minecraft.commands.arguments.EntityArgument;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Collection;
//import java.util.List;
//
//public class DialogCommand {
//    private static final Logger LOGGER = LoggerFactory.getLogger(DialogCommand.class);
//
//    // 提供官方对话框ID的自动补全
//    private static final SuggestionProvider<CommandSourceStack> OFFICIAL_DIALOG_SUGGESTIONS =
//            (context, builder) -> {
//                com.infiniteflameteam.umoiftng.dialog.OfficialDialogManager.getAvailableOfficialDialogs()
//                        .forEach(builder::suggest);
//                return builder.buildFuture();
//            };
//
//    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//        dispatcher.register(Commands.literal("dialog")
//                .requires(source -> source.hasPermission(2))
//
//                // dialog show <targets> <dialog_id> - 官方格式
//                .then(Commands.literal("show")
//                        .then(Commands.argument("targets", EntityArgument.players())
//                                .then(Commands.argument("dialog_id", StringArgumentType.string())
//                                        .suggests(OFFICIAL_DIALOG_SUGGESTIONS)
//                                        .executes(context -> executeShowOfficial(
//                                                context,
//                                                EntityArgument.getPlayers(context, "targets"),
//                                                StringArgumentType.getString(context, "dialog_id")
//                                        ))
//                                )
//                        )
//                )
//
//                // dialog list - 列出可用官方对话框
//                .then(Commands.literal("list")
//                        .executes(context -> executeListOfficial(context))
//                )
//
//                // dialog reload - 重载官方对话框
//                .then(Commands.literal("reload")
//                        .executes(context -> executeReloadOfficial(context))
//                )
//
//                // dialog clear - 清除当前对话框
//                .then(Commands.literal("clear")
//                        .executes(context -> executeClearDialog(context))
//                        .then(Commands.argument("targets", EntityArgument.players())
//                                .executes(context -> executeClearDialogForPlayers(
//                                        context,
//                                        EntityArgument.getPlayers(context, "targets")
//                                ))
//                        )
//                )
//        );
//    }
//
//    private static int executeShowOfficial(CommandContext<CommandSourceStack> context,
//                                           Collection<ServerPlayer> targets,
//                                           String dialogId) {
//        try {
//            int successCount = com.infiniteflameteam.umoiftng.dialog.OfficialDialogManager.showOfficialDialog(targets, dialogId);
//
//            if (successCount > 0) {
//                int finalSuccessCount = successCount; // 创建 final 变量
//                String finalDialogId = dialogId; // 创建 final 变量
//                context.getSource().sendSuccess(() ->
//                        Component.literal(
//                                String.format("向 %d 名玩家显示对话框: %s", finalSuccessCount, finalDialogId)
//                        ), true);
//            } else {
//                context.getSource().sendFailure(
//                        Component.literal("显示对话框失败: " + dialogId)
//                );
//            }
//            return successCount;
//        } catch (Exception e) {
//            LOGGER.error("显示对话框时出错", e);
//            context.getSource().sendFailure(
//                    Component.literal("错误: " + e.getMessage())
//            );
//            return 0;
//        }
//    }
//
//    private static int executeListOfficial(CommandContext<CommandSourceStack> context) {
//        List<String> dialogs = com.infiniteflameteam.umoiftng.dialog.OfficialDialogManager.getAvailableOfficialDialogs();
//        int dialogCount = dialogs.size(); // 创建 final 变量
//        context.getSource().sendSuccess(() ->
//                Component.literal("可用对话框: " + String.join(", ", dialogs)), false);
//        return dialogCount;
//    }
//
//    private static int executeReloadOfficial(CommandContext<CommandSourceStack> context) {
//        try {
//            // 这里需要重新加载资源管理器
//            context.getSource().sendSuccess(() ->
//                    Component.literal("对话框重载功能需要重启服务器"), true);
//            return 1;
//        } catch (Exception e) {
//            context.getSource().sendFailure(
//                    Component.literal("重载失败: " + e.getMessage())
//            );
//            return 0;
//        }
//    }
//
//    private static int executeClearDialog(CommandContext<CommandSourceStack> context) {
//        try {
//            if (context.getSource().getPlayer() != null) {
//                // 向当前玩家发送清除对话框的数据包
//                ServerPlayer player = context.getSource().getPlayer();
//                // 这里需要实现清除对话框的网络包
//                context.getSource().sendSuccess(() ->
//                        Component.literal("已清除当前对话框"), true);
//                return 1;
//            } else {
//                context.getSource().sendFailure(Component.literal("只有玩家可以执行此命令"));
//                return 0;
//            }
//        } catch (Exception e) {
//            context.getSource().sendFailure(Component.literal("清除对话框失败: " + e.getMessage()));
//            return 0;
//        }
//    }
//
//    private static int executeClearDialogForPlayers(CommandContext<CommandSourceStack> context,
//                                                    Collection<ServerPlayer> targets) {
//        try {
//            int clearedCount = 0;
//            for (ServerPlayer player : targets) {
//                // 这里需要实现清除对话框的网络包
//                clearedCount++;
//            }
//
//            // 创建 final 变量用于 lambda 表达式
//            int finalClearedCount = clearedCount;
//            context.getSource().sendSuccess(() ->
//                    Component.literal("已为 " + finalClearedCount + " 名玩家清除对话框"), true);
//            return finalClearedCount;
//        } catch (Exception e) {
//            context.getSource().sendFailure(Component.literal("清除对话框失败: " + e.getMessage()));
//            return 0;
//        }
//    }
//}
// 以上是旧版代码

package org.infiniteflameteam.umoiftng.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.infiniteflameteam.umoiftng.dialog.OfficialDialogManager;
import org.infiniteflameteam.umoiftng.localization.LanguageManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.infiniteflameteam.umoiftng.network.DialogNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class DialogCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(DialogCommand.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 提供官方对话框ID的自动补全
    private static final SuggestionProvider<CommandSourceStack> OFFICIAL_DIALOG_SUGGESTIONS =
            (context, builder) -> {
                List<String> dialogs = OfficialDialogManager.getAvailableOfficialDialogs();
                for (String dialog : dialogs) {
                    builder.suggest(dialog);
                }

                // 同时提供一些常用的SNBT示例
                builder.suggest("{type:\"notice\",title:\"标题\",body:[{type:\"plain_message\",contents:\"内容\"}]}");
                builder.suggest("{type:\"confirmation\",title:\"确认\",body:[{type:\"plain_message\",contents:\"你确定吗？\"}]}");

                return builder.buildFuture();
            };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dialog")
                .requires(source -> source.hasPermission(2))

                // dialog clear <targets> - 清除对话框
                .then(Commands.literal("clear")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> executeClearDialog(
                                        context,
                                        EntityArgument.getPlayers(context, "targets")
                                ))
                        )
                )

                // dialog show <targets> <dialog> - 展示对话框
                .then(Commands.literal("show")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("dialog", StringArgumentType.greedyString())
                                        .suggests(OFFICIAL_DIALOG_SUGGESTIONS)
                                        .executes(context -> executeShowDialog(
                                                context,
                                                EntityArgument.getPlayers(context, "targets"),
                                                StringArgumentType.getString(context, "dialog")
                                        ))
                                )
                        )
                )

                // 保留原有的 list 和 reload 命令作为管理命令
                .then(Commands.literal("list")
                        .executes(context -> executeListOfficial(context))
                )
                .then(Commands.literal("reload")
                        .executes(context -> executeReloadOfficial(context))
                )
        );
    }

    private static int executeShowDialog(CommandContext<CommandSourceStack> context,
                                         Collection<ServerPlayer> targets,
                                         String dialogInput) {
        try {
            // 检查是否选中了玩家
            if (targets.isEmpty()) {
                context.getSource().sendFailure(LanguageManager.literalComponent("umoiftng.error.no_targets"));
                return 0;
            }

            int successCount = 0;

            // 判断输入是命名空间ID还是SNBT
            if (dialogInput.startsWith("{")) {
                // SNBT格式 - 解析并显示自定义对话框
                successCount = showCustomDialog(targets, dialogInput);
            } else {
                // 命名空间ID格式 - 显示预定义的对话框
                String dialogId = parseDialogId(dialogInput);
                successCount = OfficialDialogManager.showOfficialDialog(targets, dialogId);
            }

            // 修复：创建final变量用于lambda表达式
            final int finalSuccessCount = successCount;

            if (successCount > 0) {
                context.getSource().sendSuccess(() ->
                                LanguageManager.literalComponent("umoiftng.command.dialog.show_success", finalSuccessCount),
                        true);
                return successCount;
            } else {
                context.getSource().sendFailure(
                        LanguageManager.literalComponent("umoiftng.command.dialog.show_fail", dialogInput)
                );
                return 0;
            }
        } catch (Exception e) {
            LOGGER.error("显示对话框时出错", e);
            context.getSource().sendFailure(
                    LanguageManager.literalComponent("umoiftng.error.generic", e.getMessage())
            );
            return 0;
        }
    }

    private static String parseDialogId(String input) {
        // 处理命名空间ID，如 "custom:example/test" -> "example/test"
        if (input.contains(":")) {
            String[] parts = input.split(":", 2);
            return parts[1]; // 返回路径部分
        }
        return input; // 直接返回ID
    }

    private static int showCustomDialog(Collection<ServerPlayer> targets, String snbtData) {
        try {
            LOGGER.info("解析SNBT对话框数据: {}", snbtData);

            // 将SNBT转换为JSON对象
            JsonObject dialogJson = parseSnbtToJson(snbtData);
            if (dialogJson == null) {
                LOGGER.error("SNBT解析失败");
                return 0;
            }

            int successCount = 0;
            for (ServerPlayer player : targets) {
                if (showCustomDialogToPlayer(player, dialogJson)) {
                    successCount++;
                }
            }
            return successCount;
        } catch (Exception e) {
            LOGGER.error("显示自定义对话框失败", e);
            return 0;
        }
    }

    private static JsonObject parseSnbtToJson(String snbt) {
        try {
            // 更健壮的SNBT到JSON转换
            String jsonString = snbt.trim();

            // 确保以 { 开头和 } 结尾
            if (!jsonString.startsWith("{") || !jsonString.endsWith("}")) {
                throw new IllegalArgumentException(LanguageManager.translate("umoiftng.error.snbt_parse", "无效的SNBT格式"));
            }

            // 处理常见的SNBT与JSON差异
            jsonString = jsonString
                    .replace("'", "\"") // 单引号转双引号
                    .replace("\\\"", "'") // 保留原有的转义双引号
                    .replace("minecraft:", "") // 移除minecraft命名空间
                    .replace("\\'", "\""); // 处理转义的单引号

            // 尝试解析为JSON
            return JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (Exception e) {
            LOGGER.error("SNBT解析失败: {}", snbt, e);
            throw new IllegalArgumentException(LanguageManager.translate("umoiftng.error.snbt_parse", e.getMessage()));
        }
    }

    private static boolean showCustomDialogToPlayer(ServerPlayer player, JsonObject dialog) {
        try {
            LOGGER.info("向玩家 {} 显示自定义对话框", player.getScoreboardName());

            // 使用临时ID发送自定义对话框
            String tempId = "custom_" + System.currentTimeMillis();
            DialogNetworkHandler.sendOfficialDialogOpen(player, tempId, dialog);

            return true;
        } catch (Exception e) {
            LOGGER.error("向玩家显示自定义对话框失败: {}", player.getScoreboardName(), e);
            return false;
        }
    }

    private static int executeClearDialog(CommandContext<CommandSourceStack> context,
                                          Collection<ServerPlayer> targets) {
        try {
            // 检查是否选中了玩家
            if (targets.isEmpty()) {
                context.getSource().sendFailure(LanguageManager.literalComponent("umoiftng.error.no_targets"));
                return 0;
            }

            int clearedCount = clearDialogsForPlayers(targets);

            // 修复：创建final变量用于lambda表达式
            final int finalClearedCount = clearedCount;

            context.getSource().sendSuccess(() ->
                            LanguageManager.literalComponent("umoiftng.command.dialog.clear_success", finalClearedCount),
                    true);
            return clearedCount;
        } catch (Exception e) {
            context.getSource().sendFailure(LanguageManager.literalComponent("umoiftng.command.dialog.clear_fail", e.getMessage()));
            return 0;
        }
    }

    private static int clearDialogsForPlayers(Collection<ServerPlayer> targets) {
        int clearedCount = 0;
        for (ServerPlayer player : targets) {
            try {
                // 发送清除对话框的网络包
                DialogNetworkHandler.sendClearDialog(player);
                clearedCount++;
            } catch (Exception e) {
                LOGGER.error("清除玩家对话框失败: {}", player.getScoreboardName(), e);
            }
        }
        return clearedCount;
    }

    // 保留原有的管理命令方法
    private static int executeListOfficial(CommandContext<CommandSourceStack> context) {
        List<String> dialogs = OfficialDialogManager.getAvailableOfficialDialogs();
        // 修复：创建final变量用于lambda表达式
        final String dialogsList = String.join(", ", dialogs);
        final int dialogCount = dialogs.size();

        context.getSource().sendSuccess(() ->
                        LanguageManager.literalComponent("umoiftng.command.dialog.list", dialogsList),
                false);
        return dialogCount;
    }

    private static int executeReloadOfficial(CommandContext<CommandSourceStack> context) {
        try {
            // 重新加载对话框
            OfficialDialogManager.loadDialogs(
                    context.getSource().getServer().getResourceManager()
            );
            context.getSource().sendSuccess(() ->
                            LanguageManager.literalComponent("umoiftng.command.dialog.reload_success"),
                    true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(
                    LanguageManager.literalComponent("umoiftng.command.dialog.reload_fail", e.getMessage())
            );
            return 0;
        }
    }
}