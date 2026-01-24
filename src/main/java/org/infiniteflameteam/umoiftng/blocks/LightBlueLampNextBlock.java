package org.infiniteflameteam.umoiftng.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class LightBlueLampNextBlock extends AbstractColoredLampNextBlock {
    public LightBlueLampNextBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getColorName() {
        return "light_blue";
    }
}