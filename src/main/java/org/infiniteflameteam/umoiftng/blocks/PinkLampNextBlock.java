package org.infiniteflameteam.umoiftng.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class PinkLampNextBlock extends AbstractColoredLampNextBlock {
    public PinkLampNextBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getColorName() {
        return "pink";
    }
}