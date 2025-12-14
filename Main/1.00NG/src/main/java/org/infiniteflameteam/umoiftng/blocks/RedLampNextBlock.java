package org.infiniteflameteam.umoiftng.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class RedLampNextBlock extends AbstractColoredLampNextBlock {
    public RedLampNextBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getColorName() {
        return "red";
    }
}