package com.daladirn.bpmmod.hud;

import com.daladirn.bpmmod.BpmMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class BpmTrackerHud {

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        // this method is called multiple times per frame, you want to filter it
        // by checking the event type to only render your HUD once per frame
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            this.drawHUD(event.resolution);
        }
    }

    private void drawHUD(ScaledResolution resolution) {

        // when drawing a HUD, the coordinates (x, y) represent a point on your screen
        // coordinates (0, 0) is top left of your screen,
        // coordinates (screenWidth, screenHeight) is bottom right of your screen
        final int top = 0;
        final int left = 0;
        final int bottom = resolution.getScaledHeight();
        final int right = resolution.getScaledWidth();
        String bpsText;
        String timeElapsedText = null;

        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

        // for example here we draw text on the screen
        DecimalFormat df = new DecimalFormat("#0.00");
        Double bps = BpmMod.bpmTracker.getBlockPerSecond();
        if (bps != null) {
            bpsText = df.format(BpmMod.bpmTracker.getBlockPerSecond()) + " BPS";
        } else {
            bpsText = "Hit a crop to start";
        }

        Long timeElapsed = BpmMod.bpmTracker.getTimeElapsed();
        if (timeElapsed != null) {
            timeElapsedText = String.format("%02dh %02dm %02ds",
                    TimeUnit.MILLISECONDS.toHours(timeElapsed),
                    TimeUnit.MILLISECONDS.toMinutes(timeElapsed) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeElapsed)),
                    TimeUnit.MILLISECONDS.toSeconds(timeElapsed) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed))
            );
        }

        fr.drawStringWithShadow(bpsText, 9.5f * right / 10f - fr.getStringWidth(bpsText), 9 * bottom / 10f, 0xFFFFFF);
        if (timeElapsedText != null)
            fr.drawStringWithShadow(timeElapsedText, 9.5f * right / 10f  - fr.getStringWidth(timeElapsedText), (9 * bottom + 100) / 10f, 0xFFFFFF);
    }

}