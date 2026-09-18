package dev.dontpunchmytacz.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(targets = "punchy.client.render.PunchyArmRenderer", remap = false)
public class PunchyArmTrimMixin {

    private static final String TRIM_CALL = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/UvMapping;II)V";

    private static ItemStack dontpunchmytacz$stack;
    private static EquipmentClientInfo.LayerType dontpunchmytacz$layer;
    private static ResourceKey<?> dontpunchmytacz$assetKey;

    private static boolean dontpunchmytacz$prepared;
    private static Object dontpunchmytacz$assets;
    private static Method dontpunchmytacz$assetsGet;
    private static Object dontpunchmytacz$lookup;
    private static Method dontpunchmytacz$lookupApply;
    private static Constructor<?> dontpunchmytacz$trimKey;
    private static Method dontpunchmytacz$armorTrim;
    private static Method dontpunchmytacz$texture;

    @Inject(method = "renderArmorLayersOnPart", at = @At("HEAD"), require = 0, remap = false)
    private static void dontpunchmytacz$capture(ModelPart modelPart, EquipmentClientInfo.LayerType layerType, ResourceKey<?> assetKey, ItemStack stack, Identifier texture, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, CallbackInfo ci) {
        dontpunchmytacz$stack = stack;
        dontpunchmytacz$layer = layerType;
        dontpunchmytacz$assetKey = assetKey;
    }

    @ModifyArgs(method = "renderArmorLayersOnPart", at = @At(value = "INVOKE", target = TRIM_CALL, ordinal = 2), require = 0, remap = false)
    private static void dontpunchmytacz$fixTrim(Args args) {
        try {
            if (args.get(5) != null) {
                return;
            }
            ItemStack stack = dontpunchmytacz$stack;
            EquipmentClientInfo.LayerType layerType = dontpunchmytacz$layer;
            if (stack == null || layerType == null || !dontpunchmytacz$prepare()) {
                return;
            }
            ArmorTrim trim = stack.get(DataComponents.TRIM);
            if (trim == null) {
                return;
            }
            Object info = dontpunchmytacz$assetsGet.invoke(dontpunchmytacz$assets, dontpunchmytacz$assetKey);
            Object key;
            try {
                key = dontpunchmytacz$trimKey.newInstance(trim, layerType, info);
            } catch (Throwable ignored) {
                dontpunchmytacz$trimKey = null;
                return;
            }
            Object handle = dontpunchmytacz$lookupApply.invoke(dontpunchmytacz$lookup, key);
            if (handle == null) {
                return;
            }
            Identifier texture = dontpunchmytacz$resolveTexture(handle);
            if (texture == null) {
                return;
            }
            Object renderType = dontpunchmytacz$armorTrim.invoke(null, texture, trim.pattern().value().decal());
            if (renderType == null) {
                return;
            }
            args.set(2, renderType);
            args.set(5, handle);
        } catch (Throwable ignored) {
        }
    }

    private static Identifier dontpunchmytacz$resolveTexture(Object handle) {
        if (dontpunchmytacz$texture == null) {
            try {
                Method method = handle.getClass().getMethod("textureLocation");
                method.setAccessible(true);
                dontpunchmytacz$texture = method;
            } catch (Throwable ignored) {
                dontpunchmytacz$lookupApply = null;
                return null;
            }
        }
        try {
            return (Identifier) dontpunchmytacz$texture.invoke(handle);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean dontpunchmytacz$prepare() {
        if (dontpunchmytacz$prepared) {
            return dontpunchmytacz$lookupApply != null && dontpunchmytacz$armorTrim != null;
        }
        dontpunchmytacz$prepared = true;
        try {
            Class<?> punchy = Class.forName("punchy.client.render.PunchyArmRenderer");
            dontpunchmytacz$assets = dontpunchmytacz$staticField(punchy, "ARMOR_EQUIPMENT_ASSETS", "EQUIPMENT_ASSETS");
            Object renderer = dontpunchmytacz$staticField(punchy, "ARMOR_EQUIPMENT_RENDERER", "EQUIPMENT_RENDERER");
            if (dontpunchmytacz$assets == null || renderer == null) {
                return false;
            }
            dontpunchmytacz$assetsGet = dontpunchmytacz$assets.getClass().getMethod("get", ResourceKey.class);
            Field field = renderer.getClass().getDeclaredField("trimTextureLookup");
            field.setAccessible(true);
            dontpunchmytacz$lookup = field.get(renderer);
            dontpunchmytacz$lookupApply = dontpunchmytacz$applyMethod(field.getType());
            for (Constructor<?> ctor : Class.forName("net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer$TrimTextureKey").getDeclaredConstructors()) {
                if (ctor.getParameterCount() == 3) {
                    ctor.setAccessible(true);
                    dontpunchmytacz$trimKey = ctor;
                    break;
                }
            }
            dontpunchmytacz$armorTrim = Class.forName("net.minecraft.client.renderer.rendertype.RenderTypes").getMethod("armorTrim", Identifier.class, boolean.class);
        } catch (Throwable ignored) {
        }
        return dontpunchmytacz$lookupApply != null && dontpunchmytacz$armorTrim != null && dontpunchmytacz$trimKey != null && dontpunchmytacz$assetsGet != null;
    }

    private static Object dontpunchmytacz$staticField(Class<?> owner, String... names) {
        for (String name : names) {
            try {
                Field field = owner.getDeclaredField(name);
                field.setAccessible(true);
                Object value = field.get(null);
                if (value != null) {
                    return value;
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static Method dontpunchmytacz$applyMethod(Class<?> owner) {
        Method fallback = null;
        for (Method method : owner.getMethods()) {
            if (method.getParameterCount() != 1) {
                continue;
            }
            if (method.getName().equals("apply")) {
                return method;
            }
            if (fallback == null && method.getName().equals("get")) {
                fallback = method;
            }
        }
        return fallback;
    }
}
