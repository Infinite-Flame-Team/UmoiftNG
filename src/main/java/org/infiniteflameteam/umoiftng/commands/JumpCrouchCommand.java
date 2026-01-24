package org.infiniteflameteam.umoiftng.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.infiniteflameteam.umoiftng.jumpcrouch.JumpCrouchRule;
import org.infiniteflameteam.umoiftng.jumpcrouch.JumpCrouchRuleManager;
import org.infiniteflameteam.umoiftng.localization.LanguageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JumpCrouchCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(JumpCrouchCommand.class);

    // 区域自动补齐提供器
    private static final SuggestionProvider<CommandSourceStack> AREA_SUGGESTIONS =
            new SuggestionProvider<CommandSourceStack>() {
                @Override
                public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context,
                                                                     SuggestionsBuilder builder) {
                    List<JumpCrouchRule> rules = JumpCrouchRuleManager.getInstance().getAllRules();

                    for (JumpCrouchRule rule : rules) {
                        BlockPos from = rule.getFrom();
                        BlockPos to = rule.getTo();

                        if (from != null && to != null) {
                            String suggestion = String.format("%d %d %d %d %d %d",
                                    from.getX(), from.getY(), from.getZ(),
                                    to.getX(), to.getY(), to.getZ());
                            builder.suggest(suggestion);
                        }
                    }

                    return builder.buildFuture();
                }
            };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("jumpingcrouchcanjumpmorehigh")
                .requires(source -> source.hasPermission(2))

                // /jumpingcrouchcanjumpmorehigh add <target> [from] [to]
                .then(Commands.literal("add")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .executes(context -> executeAdd(
                                        context,
                                        StringArgumentType.getString(context, "target"),
                                        null,
                                        null
                                ))
                                .then(Commands.argument("from", BlockPosArgument.blockPos())
                                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                .executes(context -> executeAdd(
                                                        context,
                                                        StringArgumentType.getString(context, "target"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "from"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "to")
                                                ))
                                        )
                                )
                        )
                )

                // /jumpingcrouchcanjumpmorehigh list
                .then(Commands.literal("list")
                        .executes(context -> executeList(context))
                )

                // /jumpingcrouchcanjumpmorehigh remove <from> <to>
                .then(Commands.literal("remove")
                        .then(Commands.argument("from", BlockPosArgument.blockPos())
                                .then(Commands.argument("to", BlockPosArgument.blockPos())
                                        .suggests(AREA_SUGGESTIONS)
                                        .executes(context -> executeRemove(
                                                context,
                                                BlockPosArgument.getLoadedBlockPos(context, "from"),
                                                BlockPosArgument.getLoadedBlockPos(context, "to")
                                        ))
                                )
                        )
                )

                // 帮助信息
                .then(Commands.literal("help")
                        .executes(context -> executeHelp(context))
                )
        );
    }

    private static int executeAdd(CommandContext<CommandSourceStack> context,
                                  String targetSelector, BlockPos from, BlockPos to) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            if (player == null) {
                context.getSource().sendFailure(
                        LanguageManager.literalComponent("umoiftng.command.jumpcrouch.error.only_player")
                );
                return 0;
            }

            // 验证选择器
            if (!isValidSelector(targetSelector)) {
                context.getSource().sendFailure(
                        LanguageManager.literalComponent("umoiftng.command.jumpcrouch.error.invalid_selector")
                );
                return 0;
            }

            // 添加规则
            JumpCrouchRule rule = JumpCrouchRuleManager.getInstance().addRule(
                    targetSelector, from, to, player.getUUID()
            );

            if (from != null && to != null) {
                final BlockPos finalFrom = from;
                final BlockPos finalTo = to;
                final String finalTargetSelector = targetSelector;

                context.getSource().sendSuccess(() -> LanguageManager.literalComponent(
                        "umoiftng.command.jumpcrouch.add_area",
                        finalTargetSelector,
                        finalFrom.getX(), finalFrom.getY(), finalFrom.getZ(),
                        finalTo.getX(), finalTo.getY(), finalTo.getZ()
                ), true);
            } else {
                final String finalTargetSelector = targetSelector;
                context.getSource().sendSuccess(() -> LanguageManager.literalComponent(
                        "umoiftng.command.jumpcrouch.add_global",
                        finalTargetSelector
                ), true);
            }

            return 1;

        } catch (Exception e) {
            LOGGER.error("添加跳跃蹲下规则失败", e);
            context.getSource().sendFailure(
                    LanguageManager.literalComponent("umoiftng.command.jumpcrouch.error.add_fail", e.getMessage())
            );
            return 0;
        }
    }

    private static int executeList(CommandContext<CommandSourceStack> context) {
        try {
            List<JumpCrouchRule> rules = JumpCrouchRuleManager.getInstance().getAllRules();

            if (rules.isEmpty()) {
                context.getSource().sendSuccess(() ->
                                LanguageManager.literalComponent("umoiftng.command.jumpcrouch.list_empty"),
                        false);
                return 0;
            }

            final int ruleCount = rules.size();

            context.getSource().sendSuccess(() -> LanguageManager.literalComponent(
                    "umoiftng.command.jumpcrouch.list_title", ruleCount
            ), false);

            // 使用for循环并创建final副本
            for (int i = 0; i < rules.size(); i++) {
                final int index = i + 1; // 创建final副本
                final JumpCrouchRule rule = rules.get(i); // 创建final副本

                context.getSource().sendSuccess(() -> LanguageManager.literalComponent(
                        "umoiftng.command.jumpcrouch.list_item",
                        index, rule.toString()
                ), false);
            }

            return rules.size();

        } catch (Exception e) {
            LOGGER.error("列出跳跃蹲下规则失败", e);
            context.getSource().sendFailure(
                    LanguageManager.literalComponent("umoiftng.command.jumpcrouch.error.list_fail", e.getMessage())
            );
            return 0;
        }
    }

    private static int executeRemove(CommandContext<CommandSourceStack> context, BlockPos from, BlockPos to) {
        try {
            boolean removed = JumpCrouchRuleManager.getInstance().removeRuleByArea(from, to);

            if (removed) {
                final BlockPos finalFrom = from;
                final BlockPos finalTo = to;

                context.getSource().sendSuccess(() -> LanguageManager.literalComponent(
                        "umoiftng.command.jumpcrouch.remove_success",
                        finalFrom.getX(), finalFrom.getY(), finalFrom.getZ(),
                        finalTo.getX(), finalTo.getY(), finalTo.getZ()
                ), true);
                return 1;
            } else {
                context.getSource().sendFailure(
                        LanguageManager.literalComponent("umoiftng.command.jumpcrouch.remove_fail",
                                from.getX(), from.getY(), from.getZ(),
                                to.getX(), to.getY(), to.getZ())
                );
                return 0;
            }

        } catch (Exception e) {
            LOGGER.error("删除跳跃蹲下规则失败", e);
            context.getSource().sendFailure(
                    LanguageManager.literalComponent("umoiftng.command.jumpcrouch.error.remove_fail", e.getMessage())
            );
            return 0;
        }
    }

    private static int executeHelp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                        LanguageManager.literalComponent("umoiftng.command.jumpcrouch.help"),
                false);
        return 1;
    }

    private static boolean isValidSelector(String selector) {
        if (selector.startsWith("@")) {
            return selector.equals("@a") || selector.equals("@p") ||
                    selector.equals("@r") || selector.equals("@s");
        }
        // 玩家名 - 简单的长度检查
        return selector.length() >= 3 && selector.length() <= 16;
    }
}