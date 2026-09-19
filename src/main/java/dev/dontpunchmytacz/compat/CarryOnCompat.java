package dev.dontpunchmytacz.compat;

import dev.dontpunchmytacz.DontPunchMyTacz;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public final class CarryOnCompat {

    private static final Logger LOGGER = LoggerFactory.getLogger(DontPunchMyTacz.MOD_ID);

    private static final String CARRY_ON_DATA_MANAGER = "tschipp.carryon.common.carry.CarryOnDataManager";

    private static Method getCarryData;
    private static Method isCarryingMethod;
    private static boolean unavailable;
    private static boolean announced;

    private CarryOnCompat() {
    }

    public static boolean shouldDeferToVanilla() {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("firstperson") || loader.isModLoaded("firstpersonmod")) {
            return false;
        }

        return isCarrying();
    }

    private static boolean isCarrying() {
        if (unavailable) {
            return false;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }

        try {
            if (getCarryData == null) {
                Class<?> manager = Class.forName(CARRY_ON_DATA_MANAGER);
                getCarryData = manager.getMethod("getCarryData", Player.class);
            }

            Object data = getCarryData.invoke(null, player);
            if (data == null) {
                return false;
            }

            if (isCarryingMethod == null) {
                isCarryingMethod = data.getClass().getMethod("isCarrying");
            }

            boolean carrying = Boolean.TRUE.equals(isCarryingMethod.invoke(data));
            if (carrying && !announced) {
                announced = true;
                LOGGER.info("Carry On found, hiding Punchy's hand while carrying");
            }
            return carrying;
        } catch (Throwable t) {
            unavailable = true;
            LOGGER.warn("Carry On integration disabled: {}", t.toString());
            return false;
        }
    }
}
