package dev.dontpunchmytacz.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import punchy.client.animation.PunchyAnimationManager;
import punchy.client.animation.data.AnimationClip;

@Mixin(targets = "punchy.client.state.CrossbowStateMachine", remap = false)
public class PunchyCrossbowMixin {

    @Redirect(method = "playCrossbow", at = @At(value = "INVOKE", target = "Lpunchy/client/animation/PunchyAnimationManager;clipSpeedMultiplier(Lpunchy/client/animation/data/AnimationClip;)F"), require = 0, remap = false)
    private static float dontpunchmytacz$speedUpWithQuickCharge(AnimationClip clip) {
        float speed = PunchyAnimationManager.clipSpeedMultiplier(clip);
        try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null || minecraft.player == null) {
                return speed;
            }
            ItemStack stack = minecraft.player.getUseItem();
            if (stack.getItem() instanceof CrossbowItem) {
                int duration = CrossbowItem.getChargeDuration(stack, minecraft.player);
                if (duration > 0 && duration < 25) {
                    speed *= 25.0F / (float) duration;
                }
            }
        } catch (Throwable ignored) {
        }
        return speed;
    }
}
