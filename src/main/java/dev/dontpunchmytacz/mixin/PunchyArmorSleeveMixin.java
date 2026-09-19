package dev.dontpunchmytacz.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import punchy.client.access.TransformablePart;
import punchy.mixin.client.accessor.HumanoidArmorLayerAccessor;
import punchy.mixin.client.accessor.LivingEntityRendererAccessor;

@Mixin(targets = "punchy.client.render.PunchyArmRenderer", remap = false)
public class PunchyArmorSleeveMixin {

    private static EntityRenderer<?> dontpunchmytacz$armorLayerOwner;
    private static HumanoidArmorLayer<?, ?, ?> dontpunchmytacz$armorLayer;

    @Inject(method = "renderArmArmor", at = @At("RETURN"), require = 0, remap = false)
    private static void dontpunchmytacz$releaseArmorSleeveMatrix(CallbackInfo ci) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null || minecraft.player == null) {
                return;
            }
            HumanoidArmorLayer<?, ?, ?> layer = dontpunchmytacz$resolveArmorLayer();
            if (layer == null) {
                return;
            }
            HumanoidModel<?> outer = ((HumanoidArmorLayerAccessor) layer).punchy$getOuterModel();
            if (outer != null) {
                dontpunchmytacz$clear(outer.rightArm);
                dontpunchmytacz$clear(outer.leftArm);
            }
            HumanoidModel<?> inner = ((HumanoidArmorLayerAccessor) layer).punchy$getInnerModel();
            if (inner != null) {
                dontpunchmytacz$clear(inner.rightArm);
                dontpunchmytacz$clear(inner.leftArm);
            }
        } catch (Throwable ignored) {
        }
    }

    private static HumanoidArmorLayer<?, ?, ?> dontpunchmytacz$resolveArmorLayer() {
        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderer<?> owner = minecraft.getEntityRenderDispatcher().getRenderer(minecraft.player);
        if (owner == dontpunchmytacz$armorLayerOwner && dontpunchmytacz$armorLayer != null) {
            return dontpunchmytacz$armorLayer;
        }
        HumanoidArmorLayer<?, ?, ?> found = null;
        if (owner instanceof LivingEntityRenderer<?, ?> living) {
            for (Object candidate : ((LivingEntityRendererAccessor) living).punchy$getLayers()) {
                if (candidate instanceof HumanoidArmorLayer<?, ?, ?> armorLayer) {
                    found = armorLayer;
                    break;
                }
            }
        }
        dontpunchmytacz$armorLayerOwner = owner;
        dontpunchmytacz$armorLayer = found;
        return found;
    }

    private static void dontpunchmytacz$clear(ModelPart part) {
        Object candidate = part;
        if (candidate instanceof TransformablePart transformable && transformable.punchy$getExplicitTransform() != null) {
            transformable.punchy$setExplicitTransform(null);
        }
    }
}
