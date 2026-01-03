package org.kingpixel.flancross;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.flemmli97.flan.player.PlayerClaimData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.kingpixel.flancross.config.Config;
import org.kingpixel.flancross.database.DataBaseFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Flancross implements ModInitializer {
  public static Config config = new Config();
  public static MinecraftServer server;
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
    .setDaemon(true)
    .setNameFormat("flancross-executor-%d")
    .build());

  @Override public void onInitialize() {
    config.init();
    ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
      Flancross.server = server;
      DataBaseFactory.init();
    });

    ServerPlayerEvents.JOIN.register((player) -> runAsync(() -> {
      DataBaseFactory.INSTANCE.loadPlayerData(player);
    }));

    ServerPlayerEvents.LEAVE.register((player) -> runAsync(() -> {
      DataBaseFactory.INSTANCE.saveOrUpdate(PlayerClaimData.get(player), player);
    }));

    ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
      DataBaseFactory.INSTANCE.disconnect();
    });
  }

  public static void runAsync(Runnable runnable) {
    if (EXECUTOR_SERVICE.isShutdown() || EXECUTOR_SERVICE.isTerminated()) {
      runnable.run();
      return;
    }
    CompletableFuture.runAsync(runnable, EXECUTOR_SERVICE)
      .orTimeout(30, TimeUnit.SECONDS)
      .exceptionallyAsync(e -> {
        e.printStackTrace();
        return null;
      });
  }
}
