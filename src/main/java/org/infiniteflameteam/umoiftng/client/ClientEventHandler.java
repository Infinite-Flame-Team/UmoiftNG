package org.infiniteflameteam.umoiftng.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.infiniteflameteam.umoiftng.network.CrouchNetworkHandler;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {
    private static boolean wasCrouching = false;

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(CrouchKeyMapping.CROUCH_KEY);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.screen != null) {
                return;
            }

            boolean isCrouching = CrouchKeyMapping.CROUCH_KEY.isDown();

            if (isCrouching != wasCrouching) {
                wasCrouching = isCrouching;
                CrouchNetworkHandler.sendCrouching(isCrouching);
            }
        }
    }
}