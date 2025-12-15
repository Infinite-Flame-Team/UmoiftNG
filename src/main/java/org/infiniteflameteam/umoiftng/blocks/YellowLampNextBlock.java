package org.infiniteflameteam.umoiftng.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class YellowLampNextBlock extends AbstractColoredLampNextBlock {
    public YellowLampNextBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getColorName() {
        return "yellow";
    }
}