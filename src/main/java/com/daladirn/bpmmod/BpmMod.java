package com.daladirn.bpmmod;

import com.daladirn.bpmmod.bpm.BpmTracker;
import com.daladirn.bpmmod.command.BpmCommand;
import com.daladirn.bpmmod.hud.BpmTrackerHud;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = BpmMod.MODID, version = BpmMod.VERSION, name = BpmMod.NAME,
        clientSideOnly = true)
public class BpmMod {
    public static final BpmTracker bpmTracker = new BpmTracker();
    public static final BpmTrackerHud hud = new BpmTrackerHud();
    public static final String MODID = "bpmmod";
    public static final String VERSION = "0.0.2-1.8.9";
    public static final String NAME = "BPM Mod";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new BpmCommand(this));
    }

    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(bpmTracker);
        MinecraftForge.EVENT_BUS.register(hud);
    }

    public void unregisterHandlers() {
        MinecraftForge.EVENT_BUS.unregister(bpmTracker);
        MinecraftForge.EVENT_BUS.unregister(hud);
        bpmTracker.reset();
    }
}
