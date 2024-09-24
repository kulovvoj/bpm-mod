package com.daladirn.bpmmod.hud;

import com.daladirn.bpmmod.BpmMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        String bpsText;
        String cpsText = null;
        String timeElapsedText = null;
        String cropsFarmedText = null;
        String cropsFarmedWithoutRespawnsText = null;
        String pingText = null;

        DecimalFormat df = new DecimalFormat("#0.00");
        Double bps = BpmMod.bpmTracker.getBlocksPerSecond();
        Double cps = BpmMod.bpmTracker.getCropsPerSecond();
        Long timeElapsed = BpmMod.bpmTracker.getTimeElapsed();
        long blocksBroken = BpmMod.bpmTracker.getBlocksBroken();
        long cropsFarmed = BpmMod.bpmTracker.getCropsFarmed();
        long cropsFarmedWithoutRespawns = BpmMod.bpmTracker.getCropsFarmedWithoutRespawns();
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float yaw = rotationYaw % 360 > 180 ? rotationYaw % 360 - 360 : rotationYaw % 360;
        float pitch = Minecraft.getMinecraft().thePlayer.rotationPitch;

        if (bps != null) {
            bpsText = df.format(bps) + " BPS";
        } else {
            bpsText = "Hit a crop to start";
        }

        if (bps != null && blocksBroken != cropsFarmed) {
            cpsText = (df.format(cps) + " CPS");
        }

        if (timeElapsed != null) {
            timeElapsedText = String.format("%02dh %02dm %02ds",
                    TimeUnit.MILLISECONDS.toHours(timeElapsed),
                    TimeUnit.MILLISECONDS.toMinutes(timeElapsed) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeElapsed)),
                    TimeUnit.MILLISECONDS.toSeconds(timeElapsed) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed))
            );
        }

        if (bps != null) {
            cropsFarmedText = String.format("%01d crops", cropsFarmed);
        }
        if (bps != null) {
            cropsFarmedWithoutRespawnsText = String.format("%01d crops without respawns", cropsFarmedWithoutRespawns);
        }

        Integer ping = this.getPlayerResponseTime();
        if (ping != null) {
            pingText = String.format("Ping: %01dms", ping);
        }

        drawStrings(resolution, Arrays.asList(bpsText, cpsText, timeElapsedText, cropsFarmedText, cropsFarmedWithoutRespawnsText, pingText, "Yaw: " + df.format(yaw > 180 ? yaw - 360 : yaw), "Pitch: " + df.format(pitch)));
    }

    private Integer getPlayerResponseTime() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) return null;
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || player.sendQueue == null) return null;
        NetworkPlayerInfo networkPlayerInfo = player.sendQueue.getPlayerInfo(player.getGameProfile().getId());
        if (networkPlayerInfo == null) return null;

        return networkPlayerInfo.getResponseTime();
    }

    private void drawStrings(ScaledResolution resolution, List<String> strings) {
        // when drawing a HUD, the coordinates (x, y) represent a point on your screen
        // coordinates (0, 0) is top left of your screen,
        final int bottom = resolution.getScaledHeight();
        final int right = resolution.getScaledWidth();
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        List<String> filteredStrings = strings.stream().filter(Objects::nonNull).collect(Collectors.toList());

        IntStream.range(0, filteredStrings.size())
            .forEach(idx -> {
                fr.drawStringWithShadow(filteredStrings.get(idx), 9.5f * right / 10f  - fr.getStringWidth(filteredStrings.get(idx)), (8f * bottom) / 10f + idx * fr.FONT_HEIGHT, 0xFFFFFF);
            });
    }
}