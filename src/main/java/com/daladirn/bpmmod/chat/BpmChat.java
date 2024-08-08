package com.daladirn.bpmmod.chat;

import com.daladirn.bpmmod.BpmMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;

public class BpmChat {
    public void chat(String message) {
        if (!BpmMod.isEnabled) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) return;
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null) return;
        player.addChatMessage(new ChatComponentText("\u00a76[BPM] \u00a7f" + message));
    }
}
