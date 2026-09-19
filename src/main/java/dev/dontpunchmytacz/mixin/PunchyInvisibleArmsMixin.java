package dev.dontpunchmytacz.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "punchy.client.render.PunchyArmRenderer", remap = false)
public class PunchyInvisibleArmsMixin {

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void dontpunchmytacz$hideInvisibleArm(PlayerModel model, AbstractClientPlayer player, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light, Identifier texture, boolean slim, float partialTick, CallbackInfo ci) {
        if (player != null && player.isInvisible()) {
            ci.cancel();
        }
    }
}
