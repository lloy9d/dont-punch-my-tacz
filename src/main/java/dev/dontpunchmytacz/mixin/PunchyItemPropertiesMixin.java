package dev.dontpunchmytacz.mixin;

import dev.dontpunchmytacz.DontPunchMyTacz;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ItemProperties.class, priority = 2000, remap = false)
public class PunchyItemPropertiesMixin {

    private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> dontpunchmytacz$castProperties = new HashMap<>();

    @Inject(method = "register(Lnet/minecraft/world/item/Item;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/item/ItemPropertyFunction;)V", at = @At("HEAD"), require = 0, remap = false)
    private static void dontpunchmytacz$rememberCastProperty(Item item, ResourceLocation id, ItemPropertyFunction function, CallbackInfo ci) {
        try {
            if (item == null || id == null || function == null || item instanceof FishingRodItem || !"cast".equals(id.getPath())) {
                return;
            }
            Map<ResourceLocation, ItemPropertyFunction> properties = dontpunchmytacz$castProperties.get(item);
            if (properties == null) {
                properties = new HashMap<>();
                dontpunchmytacz$castProperties.put(item, properties);
            }
            if (properties.put(id, function) == null) {
                DontPunchMyTacz.LOGGER.info("Kept {} untouched by Punchy", id);
            }
        } catch (Throwable ignored) {
        }
    }

    @Inject(method = "getProperty(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/item/ItemPropertyFunction;", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
    private static void dontpunchmytacz$unwrapCastProperty(ItemStack stack, ResourceLocation id, CallbackInfoReturnable<ItemPropertyFunction> cir) {
        try {
            ItemPropertyFunction function = dontpunchmytacz$originalCastProperty(stack, id);
            if (function != null && cir.getReturnValue() != function) {
                cir.setReturnValue(function);
                cir.cancel();
            }
        } catch (Throwable ignored) {
        }
    }

    @Inject(method = "isUseRelatedProperty(Lnet/minecraft/resources/ResourceLocation;)Z", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void dontpunchmytacz$releaseForeignCastProperty(ResourceLocation id, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (id == null || !"cast".equals(id.getPath())) {
                return;
            }
            for (Map<ResourceLocation, ItemPropertyFunction> properties : dontpunchmytacz$castProperties.values()) {
                if (properties.containsKey(id)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static ItemPropertyFunction dontpunchmytacz$originalCastProperty(ItemStack stack, ResourceLocation id) {
        if (stack == null || id == null || dontpunchmytacz$castProperties.isEmpty()) {
            return null;
        }
        Map<ResourceLocation, ItemPropertyFunction> properties = dontpunchmytacz$castProperties.get(stack.getItem());
        return properties == null ? null : properties.get(id);
    }
}
