package org.infiniteflameteam.umoiftng.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class LightGrayLampNextBlock extends AbstractColoredLampNextBlock {
    public LightGrayLampNextBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getColorName() {
        return "light_gray";
    }
}