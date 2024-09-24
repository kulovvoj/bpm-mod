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
import java.util.*;

public class BpmTracker {
    private final Map<Block, PropertyInteger> MEASURED_BLOCKS = new HashMap<Block, PropertyInteger>();
    private final Map<BlockPos, Timer> farmedBlocks = new HashMap<BlockPos, Timer>();
    private final Map<BlockPos, Timer> connectedFarmedBlocks = new HashMap<BlockPos, Timer>();
    private long blocksBroken = 0;
    private long cropsFarmed = 0;
    private long cropsFarmedWithoutRespawns = 0;
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
        if (farmedBlocks.containsKey(position) || connectedFarmedBlocks.containsKey(position)) return;
        if (!isBlockFarmable(position)) return;

        if (startTime == null) {
            startTime = Instant.now();
        }
        lastBrokenTime = Instant.now();
        handleFarmedBlock(position);
        handleConnectedBlocks(position);
    }

    private void handleConnectedBlocks(BlockPos position) {
        IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(position);
        if (blockState.getBlock() == null) return;
        Block originalBlock = blockState.getBlock();
        if (originalBlock == Blocks.reeds || originalBlock == Blocks.cactus) {
            handleConnectedReedsAndCactus(position, originalBlock);
        }
    }

    private void handleConnectedReedsAndCactus(BlockPos position, Block originalBlock) {
        BlockPos positionAbove = position.add(0,1,0);
        if (!isBlockFarmable(positionAbove, originalBlock)) return;
        handleConnectedReedsAndCactus(positionAbove, originalBlock);

        cropsFarmed++;
        cropsFarmedWithoutRespawns++;
        Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (isBlockFarmable(positionAbove, originalBlock)) cropsFarmedWithoutRespawns--;
                        farmedBlocks.remove(positionAbove);
                    }
                },
                500
        );
        farmedBlocks.put(positionAbove, timer);
    }

    private void handleFarmedBlock(BlockPos position) {
        blocksBroken++;

        cropsFarmed++;
        cropsFarmedWithoutRespawns++;
        Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (isBlockFarmable(position)) cropsFarmedWithoutRespawns--;
                        farmedBlocks.remove(position);
                    }
                },
                500
        );
        farmedBlocks.put(position, timer);
    }

    private boolean isBlockFarmable(BlockPos position) {
        IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(position);
        if (!MEASURED_BLOCKS.containsKey(blockState.getBlock())) return false;
        if (MEASURED_BLOCKS.get(blockState.getBlock()) == null) return true;
        Integer ripeAge = MEASURED_BLOCKS.get(blockState.getBlock()).getAllowedValues().stream().reduce(null, (acc, age) -> acc == null || age > acc ? age : acc);
        Comparable<?> cropAge = blockState.getProperties().get(MEASURED_BLOCKS.get(blockState.getBlock()));
        return cropAge.equals(ripeAge);
    }

    private boolean isBlockFarmable(BlockPos position, Block cropType) {
        IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(position);
        return cropType.equals(blockState.getBlock());
    }

    public void reset() {
        farmedBlocks.values().forEach(Timer::cancel);
        farmedBlocks.clear();
        blocksBroken = 0;
        cropsFarmed = 0;
        cropsFarmedWithoutRespawns = 0;
        startTime = null;
        lastBrokenTime = null;
        BpmMod.bpmChat.chat("The tracker has been reset");
    }

    public Double getBlocksPerSecond() {
        if (startTime == null || lastBrokenTime == null) return null;
        return (double) (blocksBroken * 1000) / Duration.between(startTime, lastBrokenTime).toMillis();
    }

    public Double getCropsPerSecond() {
        if (startTime == null || lastBrokenTime == null) return null;
        return (double) (cropsFarmed * 1000) / Duration.between(startTime, lastBrokenTime).toMillis();
    }


    public Long getTimeElapsed() {
        if (startTime == null || lastBrokenTime == null) return null;
        return Duration.between(startTime, lastBrokenTime).toMillis();
    }

    public long getBlocksBroken() {
        return blocksBroken;
    }
    public long getCropsFarmed() {
        return cropsFarmed;
    }
    public long getCropsFarmedWithoutRespawns() {
        return cropsFarmedWithoutRespawns;
    }
}
