package org.kingpixel.flancross;

import io.github.flemmli97.flan.event.PlayerEvents;
import io.github.flemmli97.flan.player.PlayerClaimData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.core.jmx.Server;
import org.kingpixel.flancross.config.Config;
import org.kingpixel.flancross.database.DataBaseClient;
import org.kingpixel.flancross.database.DataBaseFactory;

import java.io.FileNotFoundException;

public class Flancross implements ModInitializer {
  public static Config config = new Config();
  public static MinecraftServer server;

  @Override public void onInitialize() {
    config.init();
    ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
      Flancross.server = server;
      DataBaseFactory.init();
    });

    ServerPlayerEvents.JOIN.register((player) -> {
      DataBaseFactory.INSTANCE.loadPlayerData(player);
    });

    ServerPlayerEvents.LEAVE.register((player) -> {
      DataBaseFactory.INSTANCE.saveOrUpdate(PlayerClaimData.get(player), player);
    });

    ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
      DataBaseFactory.INSTANCE.disconnect();
    });
  }
}
