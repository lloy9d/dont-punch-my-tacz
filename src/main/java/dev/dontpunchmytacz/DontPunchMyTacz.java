package dev.dontpunchmytacz;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DontPunchMyTacz implements ClientModInitializer {
    public static final String MOD_ID = "dontpunchmytacz";
    static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        BlacklistForce.apply();
    }
}
