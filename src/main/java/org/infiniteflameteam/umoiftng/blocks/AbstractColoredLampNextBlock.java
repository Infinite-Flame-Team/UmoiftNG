package org.infiniteflameteam.umoiftng.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractColoredLampNextBlock extends Block {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty BY_HAND_LIT = BooleanProperty.create("by_hand_lit");
    public static final BooleanProperty BY_REDSTONE_LIT = BooleanProperty.create("by_redstone_lit");

    public AbstractColoredLampNextBlock(Properties properties) {
        super(properties);
        // 默认状态：灯亮，byHandLit=true，byRedstoneLit=false
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, true)
                .setValue(BY_HAND_LIT, true)
                .setValue(BY_REDSTONE_LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, BY_HAND_LIT, BY_REDSTONE_LIT);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player.getItemInHand(hand).isEmpty()) {
            // 获取当前状态
            boolean currentByHandLit = state.getValue(BY_HAND_LIT);
            boolean currentByRedstoneLit = state.getValue(BY_REDSTONE_LIT);

            // 切换手动状态
            boolean newByHandLit = !currentByHandLit;

            // 计算新的 LIT 状态（红石优先级更高）
            boolean newLit = currentByRedstoneLit || newByHandLit;

            // 创建新状态
            BlockState newState = state
                    .setValue(BY_HAND_LIT, newByHandLit)
                    .setValue(LIT, newLit);

            // 更新方块
            level.setBlock(pos, newState, 3);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean hasRedstoneSignal = level.hasNeighborSignal(pos);
            boolean currentByRedstoneLit = state.getValue(BY_REDSTONE_LIT);

            // 只有当红石信号状态发生变化时才更新
            if (hasRedstoneSignal != currentByRedstoneLit) {
                boolean currentByHandLit = state.getValue(BY_HAND_LIT);

                // 计算新的 LIT 状态（红石优先级更高）
                boolean newLit = hasRedstoneSignal || currentByHandLit;

                BlockState newState = state
                        .setValue(BY_REDSTONE_LIT, hasRedstoneSignal)
                        .setValue(LIT, newLit);

                level.setBlock(pos, newState, 3);
            }
        }
    }

    // 抽象方法，子类返回对应的颜色名称
    public abstract String getColorName();
}