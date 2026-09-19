package dev.dontpunchmytacz.mixin;

import dev.dontpunchmytacz.DontPunchMyTacz;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import punchy.client.animation.data.AnimationClip;

@Mixin(targets = "punchy.client.animation.PunchyAnimationManager", remap = false)
public class PunchyCrossbowMixin {

    private static final float dontpunchmytacz$BASE_CHARGE_TICKS = 25.0F;

    private static final float dontpunchmytacz$MAX_FACTOR = 5.0F;

    private static boolean dontpunchmytacz$loggedCurrentCharge;

    @Inject(method = "clipSpeedMultiplier", at = @At("RETURN"), cancellable = true, require = 1, remap = false)
    private static void dontpunchmytacz$speedUpQuickCharge(AnimationClip clip, CallbackInfoReturnable<Float> cir) {
        try {
            String name = clip == null ? null : clip.getName();
            if (name == null || !dontpunchmytacz$startsWithIgnoreCase(name, "use_crossbow")) {
                return;
            }
            if (dontpunchmytacz$containsIgnoreCase(name, "shot")) {
                dontpunchmytacz$loggedCurrentCharge = false;
                return;
            }

            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft == null ? null : minecraft.player;
            if (player == null || !player.isUsingItem()) {
                dontpunchmytacz$loggedCurrentCharge = false;
                return;
            }
            ItemStack stack = player.getUseItem();
            if (!(stack.getItem() instanceof CrossbowItem)) {
                return;
            }

            int duration = CrossbowItem.getChargeDuration(stack);
            if (duration <= 0 || duration >= (int) dontpunchmytacz$BASE_CHARGE_TICKS) {
                return;
            }

            float speed = cir.getReturnValueF();
            if (!Float.isFinite(speed) || speed <= 0.0F) {
                return;
            }

            float factor = Math.min(dontpunchmytacz$BASE_CHARGE_TICKS / (float) duration, dontpunchmytacz$MAX_FACTOR);
            cir.setReturnValue(speed * factor);

            if (!dontpunchmytacz$loggedCurrentCharge && DontPunchMyTacz.LOGGER.isDebugEnabled()) {
                dontpunchmytacz$loggedCurrentCharge = true;
                DontPunchMyTacz.LOGGER.debug("[dont punch my tacz] crossbow charge animation x{} (clip '{}', charge {} ticks)",
                        factor, name, duration);
            }
        } catch (Throwable ignored) {
        }
    }

    private static boolean dontpunchmytacz$startsWithIgnoreCase(String value, String prefix) {
        return value.length() >= prefix.length() && value.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    private static boolean dontpunchmytacz$containsIgnoreCase(String value, String needle) {
        int last = value.length() - needle.length();
        for (int i = 0; i <= last; i++) {
            if (value.regionMatches(true, i, needle, 0, needle.length())) {
                return true;
            }
        }
        return false;
    }
}
