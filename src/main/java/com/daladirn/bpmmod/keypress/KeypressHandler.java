package com.daladirn.bpmmod.keypress;

import com.daladirn.bpmmod.BpmMod;
import com.daladirn.bpmmod.bpm.BpmMouseLock;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class KeypressHandler {
	private final BpmMouseLock bpmMouseLock;
	private static final KeyBinding MOUSE_LOCK_KEY = new KeyBinding("Mouse lock", Keyboard.KEY_L, "${mod_id}");
	static {
		ClientRegistry.registerKeyBinding(MOUSE_LOCK_KEY);
	}

	public KeypressHandler(BpmMouseLock bpmMouseLock) {
		this.bpmMouseLock = bpmMouseLock;
	}

	@SubscribeEvent
	public void onKeyEvent(KeyInputEvent event) {
		if (!BpmMod.isEnabled) return;
		if (MOUSE_LOCK_KEY.isPressed()) bpmMouseLock.toggle();
	}
}
