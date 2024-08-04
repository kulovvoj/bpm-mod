package com.daladirn.bpmmod.mixins.transformers;

import com.daladirn.bpmmod.events.PacketSentEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetHandlerPlayClient.class, priority = 1001)
public abstract class MixinNetHandlerPlayClient implements INetHandlerPlayClient {
    @Inject(method = "addToSendQueue", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        PacketSentEvent event = new PacketSentEvent(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled() && callbackInfo.isCancellable()) {
            callbackInfo.cancel();
        }
    }
}
