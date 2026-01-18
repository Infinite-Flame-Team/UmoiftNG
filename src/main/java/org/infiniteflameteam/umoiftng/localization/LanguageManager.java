package org.infiniteflameteam.umoiftng.localization;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageManager.class);

    // 默认中文翻译（简体中文）- 作为服务器端回退
    private static final Map<String, String> DEFAULT_TRANSLATIONS = new HashMap<>();

    static {
        initDefaultTranslations();
    }

    private static void initDefaultTranslations() {
        // 领地系统
        DEFAULT_TRANSLATIONS.put("umoiftng.claim.no_permission.break", "§c你没有权限破坏这个方块");
        DEFAULT_TRANSLATIONS.put("umoiftng.claim.no_permission.build", "§c你没有权限放置方块");
        DEFAULT_TRANSLATIONS.put("umoiftng.claim.no_permission.interact", "§c你没有权限交互这个方块");
        DEFAULT_TRANSLATIONS.put("umoiftng.claim.no_permission.interact_entity", "§c你没有权限交互这个实体");
        DEFAULT_TRANSLATIONS.put("umoiftng.claim.no_permission.use", "§c你没有权限使用物品");
        DEFAULT_TRANSLATIONS.put("umoiftng.claim.enter", "§e进入了 §f%s §e的 §f%s");
        DEFAULT_TRANSLATIONS.put("umoiftng.claim.leave", "§e离开了 §f%s §e的 §f%s");

        // 对话框系统
        DEFAULT_TRANSLATIONS.put("umoiftng.dialog.button.ok", "确定");
        DEFAULT_TRANSLATIONS.put("umoiftng.dialog.button.yes", "是");
        DEFAULT_TRANSLATIONS.put("umoiftng.dialog.button.no", "否");
        DEFAULT_TRANSLATIONS.put("umoiftng.dialog.button.cancel", "取消");
        DEFAULT_TRANSLATIONS.put("umoiftng.dialog.title.default", "UMOIFT 模组");
        DEFAULT_TRANSLATIONS.put("umoiftng.dialog.welcome.message", "欢迎使用无限火队通用模组！");

        // 命令反馈
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.created", "§a成功创建领地: §e%s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.created_fail", "§c创建领地失败: 领地名已存在或位置重叠");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.deleted", "§a成功删除领地: §e%s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.deleted_fail", "§c删除领地失败: 领地不存在或你没有权限");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.modified", "§a成功修改领地: §e%s §a-> §e%s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.modified_fail", "§c修改领地失败: 领地不存在、新领地名已存在、位置重叠或你没有权限");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.permission_set", "§a成功设置玩家 §e%s §a的权限 §e%s §a为: §f%s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.permission_owner", "§c不能修改领地主人的权限");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.op_added", "§a成功添加全局OP: §e%s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.op_removed", "§a成功移除全局OP: §e%s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.op_already", "§c该玩家已经是全局OP");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.claim.not_op", "§c该玩家不是全局OP");

        DEFAULT_TRANSLATIONS.put("umoiftng.command.dialog.show_success", "§a向 §e%d §a名玩家显示对话框");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.dialog.show_fail", "§c显示对话框失败: %s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.dialog.clear_success", "§a已为 §e%d §a名玩家清除对话框");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.dialog.clear_fail", "§c清除对话框失败: %s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.dialog.list", "可用对话框: %s");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.dialog.reload_success", "§a对话框重载完成");
        DEFAULT_TRANSLATIONS.put("umoiftng.command.dialog.reload_fail", "§c重载失败: %s");

        // 通用错误
        DEFAULT_TRANSLATIONS.put("umoiftng.error.only_player", "只有玩家可以执行此命令");
        DEFAULT_TRANSLATIONS.put("umoiftng.error.no_targets", "未选中任何玩家");
        DEFAULT_TRANSLATIONS.put("umoiftng.error.generic", "错误: %s");
        DEFAULT_TRANSLATIONS.put("umoiftng.error.snbt_parse", "SNBT解析错误: %s");

        // 帮助信息
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.header", "§6=== 领地命令帮助 ===");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.addclaim", "§e/claim addclaim <领地名> <from> <to> §7- 创建领地");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.listclaim", "§e/claim listclaim §7- 列出所有领地");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.queryclaim", "§e/claim queryclaim <领地名> §7- 查询领地详细信息");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.removeclaim", "§e/claim removeclaim <领地名> §7- 删除领地（需权限）");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.changeclaim", "§e/claim changeclaim <旧领地名> <新领地名> <新from> <新to> <旧主人> <新主人> §7- 修改领地（需权限）");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.setpermission", "§e/claim setpermissionstoclaim <领地名> <玩家> <权限名> <true|false> §7- 设置玩家权限（需权限）");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.op", "§e/claim op <玩家> §7- 添加全局OP（需管理员权限）");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.deop", "§e/claim deop <玩家> §7- 移除全局OP（需管理员权限）");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.help", "§e/claim help §7- 显示帮助信息");
        DEFAULT_TRANSLATIONS.put("umoiftng.help.claim.permissions", "§6可用权限名: §7build, break, interact, use, pvp, explosion, fire_spread, mob_griefing");

        // GUI相关
        DEFAULT_TRANSLATIONS.put("umoiftng.gui.dialog.confirm", "确定吗？");
        DEFAULT_TRANSLATIONS.put("umoiftng.gui.dialog.select", "请选择");
        DEFAULT_TRANSLATIONS.put("umoiftng.gui.dialog.close", "关闭");
        DEFAULT_TRANSLATIONS.put("umoiftng.gui.dialog.next", "下一页");
        DEFAULT_TRANSLATIONS.put("umoiftng.gui.dialog.previous", "上一页");
        DEFAULT_TRANSLATIONS.put("umoiftng.gui.dialog.page", "第%s页");
        DEFAULT_TRANSLATIONS.put("umoiftng.gui.dialog.loading", "加载中...");
        DEFAULT_TRANSLATIONS.put("umoiftng.gui.dialog.error", "错误");
    }

    /**
     * 获取翻译文本（服务器端使用默认中文，客户端使用Minecraft的I18n）
     */
    public static String translate(String key, Object... args) {
        if (FMLEnvironment.dist.isClient() && Minecraft.getInstance() != null) {
            // 客户端使用Minecraft的翻译系统
            if (I18n.exists(key)) {
                if (args.length > 0) {
                    try {
                        return I18n.get(key, args);
                    } catch (Exception e) {
                        LOGGER.warn("Failed to format client translation for key: {}", key, e);
                        return I18n.get(key);
                    }
                }
                return I18n.get(key);
            }
        }

        // 服务器端或客户端没有翻译时使用默认翻译
        String translation = DEFAULT_TRANSLATIONS.get(key);
        if (translation == null) {
            LOGGER.warn("Missing translation for key: {}", key);
            return key; // 返回key本身作为回退
        }

        if (args.length > 0) {
            try {
                return String.format(translation, args);
            } catch (Exception e) {
                LOGGER.error("Failed to format translation for key: {}", key, e);
                return translation;
            }
        }
        return translation;
    }

    /**
     * 获取翻译组件（使用Minecraft的翻译系统）
     */
    public static MutableComponent translateComponent(String key, Object... args) {
        return Component.translatable(key, args);
    }

    /**
     * 获取文字组件（使用自定义翻译，适合服务器端）
     */
    public static MutableComponent literalComponent(String key, Object... args) {
        return Component.literal(translate(key, args));
    }

    /**
     * 检查是否有翻译
     */
    public static boolean hasTranslation(String key) {
        if (FMLEnvironment.dist.isClient() && Minecraft.getInstance() != null) {
            return I18n.exists(key);
        }
        return DEFAULT_TRANSLATIONS.containsKey(key);
    }

    /**
     * 客户端专用：获取客户端翻译（仅限客户端代码使用）
     */
    @OnlyIn(Dist.CLIENT)
    public static String getClientTranslation(String key, Object... args) {
        if (args.length > 0) {
            try {
                return I18n.get(key, args);
            } catch (Exception e) {
                LOGGER.warn("Failed to format client translation for key: {}", key, e);
                return I18n.get(key);
            }
        }
        return I18n.get(key);
    }

    /**
     * 客户端专用：获取客户端翻译组件
     */
    @OnlyIn(Dist.CLIENT)
    public static MutableComponent getClientComponent(String key, Object... args) {
        return Component.translatable(key, args);
    }

    /**
     * 服务器端专用：获取服务器端翻译（纯服务器环境）
     */
    public static String getServerTranslation(String key, Object... args) {
        return translate(key, args);
    }
}