package org.infiniteflameteam.umoiftng.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class MagentaLampNextBlock extends AbstractColoredLampNextBlock {
    public MagentaLampNextBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getColorName() {
        return "magenta";
    }
}