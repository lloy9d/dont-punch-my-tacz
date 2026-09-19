package dev.dontpunchmytacz;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(DontPunchMyTacz.MOD_ID)
public final class DontPunchMyTacz {
    public static final String MOD_ID = "dontpunchmytacz";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public DontPunchMyTacz(FMLJavaModLoadingContext context) {
        BlacklistForce.apply();
        context.getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(BlacklistForce::apply);
    }
}
