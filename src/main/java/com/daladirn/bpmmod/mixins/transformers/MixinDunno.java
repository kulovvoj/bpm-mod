package com.daladirn.bpmmod.mixins.transformers;

import com.daladirn.bpmmod.BpmMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = BlockRedstoneTorch.class, priority = 1001)
public abstract class MixinDunno extends BlockTorch {
    @Inject(method = "onNeighborBlockChange", at = @At("HEAD"))
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock, CallbackInfo callbackInfo) {
        BpmMod.bpmChat.chat("onNeighborBlockChange");
    }
    @Inject(method = "isBurnedOut", at = @At("RETURN"))
    public void isBurnedOut(World worldIn, BlockPos pos, boolean turnOff, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        BpmMod.bpmChat.chat("isBurnedOut:" + (callbackInfoReturnable.getReturnValue() ? "true" : "false"));

    }
    @Inject(method = "shouldBeOff", at = @At("RETURN"))
    public void shouldBeOff(World worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        BpmMod.bpmChat.chat("shouldBeOff:" + (callbackInfoReturnable.getReturnValue() ? "true" : "false"));

    }
}

