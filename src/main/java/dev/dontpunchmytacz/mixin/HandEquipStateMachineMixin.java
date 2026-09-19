package dev.dontpunchmytacz.mixin;

import dev.dontpunchmytacz.compat.CarryOnCompat;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "punchy.client.state.HandEquipStateMachine", remap = false)
public class HandEquipStateMachineMixin {

    @Inject(method = "shouldAllowVanillaHandOut", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void dontpunchmytacz$hideHandWhileCarrying(InteractionHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (CarryOnCompat.shouldDeferToVanilla()) {
            cir.setReturnValue(true);
        }
    }
}
