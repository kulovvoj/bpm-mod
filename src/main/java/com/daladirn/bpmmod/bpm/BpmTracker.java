package com.daladirn.bpmmod.bpm;

import com.daladirn.bpmmod.events.PacketSentEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BpmTracker {
    private final Map<Block, Integer> MEASURED_BLOCKS = new HashMap<Block, Integer>();
    private long BlocksBroken = 0;
    private Instant startTime;
    private Instant lastBrokenTime;

    public BpmTracker() {
        MEASURED_BLOCKS.put(Blocks.wheat, 7);
        MEASURED_BLOCKS.put(Blocks.carrots, 7);
        MEASURED_BLOCKS.put(Blocks.potatoes, 7);
        MEASURED_BLOCKS.put(Blocks.nether_wart, 3);
        MEASURED_BLOCKS.put(Blocks.cocoa, 2);
        MEASURED_BLOCKS.put(Blocks.melon_block, null);
        MEASURED_BLOCKS.put(Blocks.pumpkin, null);
        MEASURED_BLOCKS.put(Blocks.brown_mushroom, null);
        MEASURED_BLOCKS.put(Blocks.red_mushroom, null);
        MEASURED_BLOCKS.put(Blocks.cactus, null);
        MEASURED_BLOCKS.put(Blocks.reeds, null);
    }


    @SubscribeEvent
    public void PacketSentEvent(PacketSentEvent packetEvent) {
        Packet<?> packet = packetEvent.packet;
        if (!(packet instanceof C07PacketPlayerDigging) || ((C07PacketPlayerDigging) packet).getStatus() != C07PacketPlayerDigging.Action.START_DESTROY_BLOCK)
            return;

        BlockPos position = ((C07PacketPlayerDigging) packet).getPosition();
        IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(position);
        if (!MEASURED_BLOCKS.containsKey(blockState.getBlock())) return;
        if (MEASURED_BLOCKS.get(blockState.getBlock()) != null && MEASURED_BLOCKS.get(blockState.getBlock()) != blockState.getProperties().get(BlockCrops.AGE)) return;

        if (startTime == null) {
            startTime = Instant.now();
        }
        lastBrokenTime = Instant.now();
        BlocksBroken++;
    }

    public void reset() {
        BlocksBroken = 0;
        startTime = null;
        lastBrokenTime = null;
    }

    public Double getBlockPerSecond() {
        if (startTime == null || lastBrokenTime == null) return null;
        return (double) (BlocksBroken * 1000) / Duration.between(startTime, lastBrokenTime).toMillis();
    }

    public Long getTimeElapsed() {
        if (startTime == null || lastBrokenTime == null) return null;
        return Duration.between(startTime, lastBrokenTime).toMillis();
    }
}
