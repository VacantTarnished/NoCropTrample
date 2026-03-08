package com.murqin.nocroptrample.mixin;

import com.murqin.nocroptrample.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {

    /**
     * Injects at HEAD of setToDirt - the actual method that converts farmland to dirt.
     * This is version-agnostic as setToDirt signature is stable.
     */
    @Inject(method = "setToDirt", at = @At("HEAD"), cancellable = true)
    private static void onSetToDirt(Entity entity, BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        if (entity == null) {
            // Allow natural conversion (dehydration etc.)
            return;
        }
        
        boolean isPlayer = entity instanceof PlayerEntity;
        boolean isEmpty = !(
            (world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof CropBlock)
            || (world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof StemBlock)
        );

        if (isEmpty && !ModConfig.preventEmptyTrampling) {
            return;
        }

        if (isPlayer && ModConfig.preventPlayerTrampling) {
            ci.cancel();
            return;
        }

        if (!isPlayer && ModConfig.preventMobTrampling) {
            ci.cancel();
        }
    }
}
