package org.infiniteflameteam.umoiftng.mixin;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.server.commands.data.EntityDataAccessor.class)
public class MixinTargetEntityDataAccessor {
    @org.spongepowered.asm.mixin.Shadow
    private net.minecraft.world.entity.Entity entity;

    @org.spongepowered.asm.mixin.Overwrite
    public void setData(net.minecraft.nbt.CompoundTag p_139519_) {
        java.util.UUID $$1 = this.entity.getUUID();
        this.entity.load(p_139519_);
        this.entity.setUUID($$1);
    }
}
