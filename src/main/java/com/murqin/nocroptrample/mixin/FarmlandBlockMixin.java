package com.murqin.nocroptrample.mixin;

import com.murqin.nocroptrample.config.ModConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for FarmlandBlock to prevent trampling based on configuration.
 * <p>
 * This mixin intercepts the {@code turnToDirt} method which converts farmland
 * to dirt when an entity jumps or falls on it. By injecting at the HEAD with
 * cancellable=true, we can prevent the trampling behavior selectively based
 * on whether the entity is a player or mob and what the configuration allows.
 * </p>
 */
@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {

    /**
     * Injects at the HEAD of turnToDirt to intercept trampling attempts.
     * <p>
     * This injection runs before farmland converts to dirt. If the entity is
     * null (natural conversion like dehydration), the method proceeds normally.
     * Otherwise, it checks configuration to determine if trampling should be
     * prevented based on entity type (player vs mob).
     * </p>
     *
     * @param entity the entity causing trampling (null for natural conversion)
     * @param state  the current block state
     * @param level  the world/level
     * @param pos    the block position
     * @param ci     callback info for cancelling the method
     */
    @Inject(method = "turnToDirt", at = @At("HEAD"), cancellable = true)
    private static void onTurnToDirt(Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (entity == null) {
            // Allow natural conversion (dehydration etc.)
            return;
        }

        boolean isPlayer = entity instanceof Player;

        if (isPlayer && ModConfig.isPreventPlayerTrampling()) {
            ci.cancel();
            return;
        }

        if (!isPlayer && ModConfig.isPreventMobTrampling()) {
            ci.cancel();
        }
    }
}
