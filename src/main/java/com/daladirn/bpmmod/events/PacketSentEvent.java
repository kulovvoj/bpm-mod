package com.daladirn.bpmmod.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketSentEvent extends Event {
    public Packet<?> packet;
    public PacketSentEvent(Packet<?> packet) {
        this.packet = packet;
    }

}
