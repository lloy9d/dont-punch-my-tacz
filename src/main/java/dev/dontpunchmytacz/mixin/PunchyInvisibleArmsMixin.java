package dev.dontpunchmytacz.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "punchy.client.render.PunchyArmRenderer", remap = false)
public class PunchyInvisibleArmsMixin {

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void dontpunchmytacz$hideInvisibleSkinArm(CallbackInfo ci) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null && minecraft.player != null && minecraft.player.isInvisible()) {
                ci.cancel();
            }
        } catch (Throwable ignored) {
        }
    }
}
