package org.kingpixel.flancross.database;

import io.github.flemmli97.flan.player.PlayerClaimData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.kingpixel.flancross.model.PlayerDataFlan;

import java.util.UUID;

/**
 * @author Carlos Varas Alonso - 29/09/2025 2:04
 */
public abstract class DataBaseClient {

  public abstract void connect();

  public abstract void disconnect();

  public abstract PlayerDataFlan getPlayerData(UUID playerUUID);

  public abstract void loadPlayerData(ServerPlayerEntity player);

  public abstract void saveOrUpdate(PlayerClaimData data, ServerPlayerEntity player);
}
