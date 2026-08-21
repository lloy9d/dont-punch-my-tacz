package dev.dontpunchmytacz;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = DontPunchMyTacz.MOD_ID, dist = Dist.CLIENT)
public final class DontPunchMyTacz {
    public static final String MOD_ID = "dontpunchmytacz";
    static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public DontPunchMyTacz(IEventBus modBus) {
        // We load before Punchy, so the json is ready when Punchy opens it.
        BlacklistForce.apply();
        modBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(BlacklistForce::apply);
    }
}
