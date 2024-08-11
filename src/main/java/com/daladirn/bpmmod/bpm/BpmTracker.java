package com.daladirn.bpmmod.bpm;

import com.daladirn.bpmmod.BpmMod;
import com.daladirn.bpmmod.events.PacketSentEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.properties.PropertyInteger;
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
import java.util.Timer;
import java.util.TimerTask;

public class BpmTracker {
    private final Map<Block, PropertyInteger> MEASURED_BLOCKS = new HashMap<Block, PropertyInteger>();
    private final Map<BlockPos, Timer> farmedBlocks = new HashMap<BlockPos, Timer>();
    private long blocksBroken = 0;
    private Instant startTime;
    private Instant lastBrokenTime;

    public BpmTracker() {
        MEASURED_BLOCKS.put(Blocks.wheat, BlockCrops.AGE);
        MEASURED_BLOCKS.put(Blocks.carrots, BlockCrops.AGE);
        MEASURED_BLOCKS.put(Blocks.potatoes, BlockCrops.AGE);
        MEASURED_BLOCKS.put(Blocks.nether_wart, BlockNetherWart.AGE);
        MEASURED_BLOCKS.put(Blocks.cocoa, BlockCocoa.AGE);
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
        if (farmedBlocks.containsKey(position)) return;
        if (!isBlockFarmable(position)) return;

        if (startTime == null) {
            startTime = Instant.now();
        }
        lastBrokenTime = Instant.now();
        blocksBroken++;

        Timer timer = new Timer();
        timer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    farmedBlocks.remove(position);
                }
            },
            500
        );
        farmedBlocks.put(position, timer);
    }

    public boolean isBlockFarmable(BlockPos position) {
        IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(position);
        if (!MEASURED_BLOCKS.containsKey(blockState.getBlock())) return false;
        if (MEASURED_BLOCKS.get(blockState.getBlock()) == null) return true;
        Integer ripeAge = MEASURED_BLOCKS.get(blockState.getBlock()).getAllowedValues().stream().reduce(null, (acc, age) -> acc == null || age > acc ? age : acc);
        Comparable<?> cropAge = blockState.getProperties().get(MEASURED_BLOCKS.get(blockState.getBlock()));
        return cropAge.equals(ripeAge);
    }

    public void reset() {
        farmedBlocks.values().forEach(Timer::cancel);
        farmedBlocks.clear();
        blocksBroken = 0;
        startTime = null;
        lastBrokenTime = null;
        BpmMod.bpmChat.chat("The tracker has been reset");
    }

    public Double getBlockPerSecond() {
        if (startTime == null || lastBrokenTime == null) return null;
        return (double) (blocksBroken * 1000) / Duration.between(startTime, lastBrokenTime).toMillis();
    }

    public Long getTimeElapsed() {
        if (startTime == null || lastBrokenTime == null) return null;
        return Duration.between(startTime, lastBrokenTime).toMillis();
    }
}
