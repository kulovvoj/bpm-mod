package com.daladirn.bpmmod;

import com.daladirn.bpmmod.bpm.BpmMouseLock;
import com.daladirn.bpmmod.bpm.BpmTracker;
import com.daladirn.bpmmod.chat.BpmChat;
import com.daladirn.bpmmod.keypress.KeypressHandler;
import com.daladirn.bpmmod.command.BpmCommand;
import com.daladirn.bpmmod.hud.BpmTrackerHud;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = BpmMod.MODID, version = BpmMod.VERSION, name = BpmMod.NAME,
        clientSideOnly = true)
public class BpmMod {
    public static final BpmTracker bpmTracker = new BpmTracker();
    public static final BpmTrackerHud hud = new BpmTrackerHud();
    public static final BpmMouseLock bpmMouseLock = new BpmMouseLock();
    public static final BpmChat bpmChat = new BpmChat();
    public static final String MODID = "bpmmod";
    public static final String VERSION = "0.0.2-1.8.9";
    public static final String NAME = "BPM Mod";
    public static boolean isEnabled = false;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new BpmCommand(this));
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void load(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new KeypressHandler(bpmMouseLock));
    }

    public void enable() {
        if (BpmMod.isEnabled) return;

        BpmMod.isEnabled = true;
        this.registerHandlers();
    }

    public void disable() {
        if (!BpmMod.isEnabled) return;

        BpmMod.isEnabled = false;
        this.unregisterHandlers();
        if (BpmMod.bpmMouseLock.isMouseLocked) {
            BpmMod.bpmMouseLock.toggle();
        }
    }

    private void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(bpmTracker);
        MinecraftForge.EVENT_BUS.register(hud);
    }

    private void unregisterHandlers() {
        MinecraftForge.EVENT_BUS.unregister(bpmTracker);
        MinecraftForge.EVENT_BUS.unregister(hud);
        bpmTracker.reset();
    }
}
