package com.daladirn.bpmmod.bpm;

import com.daladirn.bpmmod.BpmMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class BpmMouseLock {
    private final float lockedMouseSensitivity = -1F / 3F;
    public boolean isMouseLocked = false;
    private float userMouseSensitivity;

    public void toggle() {
        isMouseLocked = !isMouseLocked;

        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        if (gameSettings == null) return;

        if (isMouseLocked) {
            this.userMouseSensitivity = gameSettings.mouseSensitivity;
            gameSettings.mouseSensitivity = lockedMouseSensitivity;
            BpmMod.bpmChat.chat("Mouse has been locked");
        } else {
            gameSettings.mouseSensitivity = userMouseSensitivity;
            BpmMod.bpmChat.chat("Mouse has been unlocked");
        }
    }
}
