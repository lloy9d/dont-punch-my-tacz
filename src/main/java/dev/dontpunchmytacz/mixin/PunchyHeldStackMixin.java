package dev.dontpunchmytacz.mixin;

import dev.dontpunchmytacz.DontPunchMyTacz;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "punchy.client.animation.PunchyAnimationManager", remap = false)
public class PunchyHeldStackMixin {

    private static boolean dontpunchmytacz$reported = false;

    @Inject(method = "resolveRenderStack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/HumanoidArm;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
    private static void dontpunchmytacz$keepLiveHeldStack(Player player, HumanoidArm arm, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        try {
            ItemStack rendered = cir.getReturnValue();
            if (player == null || arm == null || rendered == null || rendered.isEmpty()) {
                return;
            }
            ItemStack live = player.getItemInHand(arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
            if (live == rendered || live.isEmpty() || !ItemStack.isSameItem(live, rendered) || live.getCount() != rendered.getCount()) {
                return;
            }
            cir.setReturnValue(live);
            cir.cancel();
            if (!dontpunchmytacz$reported) {
                dontpunchmytacz$reported = true;
                DontPunchMyTacz.LOGGER.info("Kept the held stack live for Punchy");
            }
        } catch (Throwable ignored) {
        }
    }
}
