package org.infiniteflameteam.umoiftng;

import org.infiniteflameteam.umoiftng.blocks.*;
import org.infiniteflameteam.umoiftng.commands.DialogCommand;
import org.infiniteflameteam.umoiftng.commands.ClaimCommand;
import org.infiniteflameteam.umoiftng.dialog.OfficialDialogManager;
import org.infiniteflameteam.umoiftng.network.DialogNetworkHandler;
import org.infiniteflameteam.umoiftng.claim.ClaimManager;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "umoiftng";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // 添加创造模式标签页的注册
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 颜色数组 - 移到前面以避免前向引用
    private static final String[] COLORS = {
            "white", "orange", "magenta", "light_blue",
            "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    };

    // 存储彩色灯的映射
    public static final Map<String, RegistryObject<Block>> COLORED_LAMPS = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<Item>> COLORED_LAMP_ITEMS = new LinkedHashMap<>();

    // 注册一个特殊的图标物品
    public static final RegistryObject<Item> MOD_LOGO_ITEM = ITEMS.register("umoiftng_logo",
            () -> new Item(new Item.Properties()) {
                // 这是一个特殊的物品，仅在标签页图标中使用
                // 我们不希望玩家能够获取或使用它
            });

    // 注册UMOIFTNG标签页
    public static final RegistryObject<CreativeModeTab> UMOIFTNG_TAB = CREATIVE_MODE_TABS.register("umoiftng_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.umoiftng"))
                    .icon(() -> {
                        // 使用我们的特殊图标物品作为标签页图标
                        return new ItemStack(MOD_LOGO_ITEM.get());
                    })
                    .displayItems((parameters, output) -> {
                        // 只添加彩色灯方块，不添加图标物品
                        for (String color : COLORS) {
                            RegistryObject<Item> item = COLORED_LAMP_ITEMS.get(color);
                            if (item != null && item.isPresent()) {
                                output.accept(item.get());
                            }
                        }
                    })
                    .build());

    // 初始化彩色灯的方法 - 在构造函数中调用
    private static void initColoredLamps() {
        for (String color : COLORS) {
            String blockId = color + "_lamp_next";

            RegistryObject<Block> block = createColoredLampBlock(color, blockId);
            RegistryObject<Item> item = ITEMS.register(blockId,
                    () -> new BlockItem(block.get(), new Item.Properties()));

            COLORED_LAMPS.put(color, block);
            COLORED_LAMP_ITEMS.put(color, item);
        }
    }

    private static RegistryObject<Block> createColoredLampBlock(String color, String blockId) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .strength(0.3f)
                .lightLevel(state -> state.getValue(AbstractColoredLampNextBlock.LIT) ? 15 : 0);

        return switch (color) {
            case "white" -> BLOCKS.register(blockId, () -> new WhiteLampNextBlock(properties));
            case "orange" -> BLOCKS.register(blockId, () -> new OrangeLampNextBlock(properties));
            case "magenta" -> BLOCKS.register(blockId, () -> new MagentaLampNextBlock(properties));
            case "light_blue" -> BLOCKS.register(blockId, () -> new LightBlueLampNextBlock(properties));
            case "yellow" -> BLOCKS.register(blockId, () -> new YellowLampNextBlock(properties));
            case "lime" -> BLOCKS.register(blockId, () -> new LimeLampNextBlock(properties));
            case "pink" -> BLOCKS.register(blockId, () -> new PinkLampNextBlock(properties));
            case "gray" -> BLOCKS.register(blockId, () -> new GrayLampNextBlock(properties));
            case "light_gray" -> BLOCKS.register(blockId, () -> new LightGrayLampNextBlock(properties));
            case "cyan" -> BLOCKS.register(blockId, () -> new CyanLampNextBlock(properties));
            case "purple" -> BLOCKS.register(blockId, () -> new PurpleLampNextBlock(properties));
            case "blue" -> BLOCKS.register(blockId, () -> new BlueLampNextBlock(properties));
            case "brown" -> BLOCKS.register(blockId, () -> new BrownLampNextBlock(properties));
            case "green" -> BLOCKS.register(blockId, () -> new GreenLampNextBlock(properties));
            case "red" -> BLOCKS.register(blockId, () -> new RedLampNextBlock(properties));
            case "black" -> BLOCKS.register(blockId, () -> new BlackLampNextBlock(properties));
            default -> BLOCKS.register(blockId, () -> new WhiteLampNextBlock(properties));
        };
    }

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 初始化彩色灯
        initColoredLamps();

        // 注册方块和物品
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        // 注册创造模式标签页
        CREATIVE_MODE_TABS.register(modEventBus);

        // 注册 Mod 事件总线处理器
        modEventBus.register(new ModEventHandler());

        // 注册 Forge 事件总线处理器
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        // 初始化领地管理器（会自动加载数据）
        ClaimManager.getInstance();

        // 注册网络信道
        DialogNetworkHandler.register();

        LOGGER.info("Universal Mod of Infinite Flame Team Next Generation初始化完成");
    }

    // 专门处理 Mod 事件总线的类
    private static class ModEventHandler {
        @SubscribeEvent
        public void addCreative(BuildCreativeModeTabContentsEvent event) {
            // 我们不将模组物品添加到原版标签页，因为我们有自己的独立标签页
            // 特别是不添加图标物品
        }
    }

    // 专门处理 Forge 事件总线的类
    private static class ForgeEventHandler {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            DialogCommand.register(event.getDispatcher());
            ClaimCommand.register(event.getDispatcher());
            LOGGER.info("UMOIFTNG命令注册完成");
        }

        @SubscribeEvent
        public void onAddReloadListener(AddReloadListenerEvent event) {
            event.addListener(new DialogReloadListener());
        }

        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
            // 服务器启动时初始化对话框系统
            OfficialDialogManager.loadDialogs(event.getServer().getResourceManager());

            // 确保领地系统初始化
            ClaimManager.getInstance();
            LOGGER.info("领地系统初始化完成");
        }

        @SubscribeEvent
        public void onServerStopping(net.minecraftforge.event.server.ServerStoppingEvent event) {
            // 服务器关闭时保存领地数据
            ClaimManager.getInstance().saveClaims();
            LOGGER.info("领地数据已保存");
        }
    }

    // 修复后的 DialogReloadListener
    private static class DialogReloadListener implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparationBarrier barrier,
                                              ResourceManager resourceManager,
                                              ProfilerFiller preparationsProfiler,
                                              ProfilerFiller reloadProfiler,
                                              Executor backgroundExecutor,
                                              Executor gameExecutor) {
            return CompletableFuture.runAsync(() -> {
                // 在后台线程中重新加载对话框
                try {
                    OfficialDialogManager.loadDialogs(resourceManager);
                    LOGGER.info("对话框配置重载完成");
                } catch (Exception e) {
                    LOGGER.error("重载对话框配置时发生错误", e);
                }
            }, backgroundExecutor).thenCompose(barrier::wait);
        }
    }
}