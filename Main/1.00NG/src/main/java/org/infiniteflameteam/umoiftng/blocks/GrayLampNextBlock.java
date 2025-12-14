package org.infiniteflameteam.umoiftng.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class GrayLampNextBlock extends AbstractColoredLampNextBlock {
    public GrayLampNextBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getColorName() {
        return "gray";
    }
}