package org.kingpixel.flancross.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import io.github.flemmli97.flan.player.PlayerClaimData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bson.Document;
import org.kingpixel.flancross.Flancross;
import org.kingpixel.flancross.mixins.FlanPlayerDataMixin;
import org.kingpixel.flancross.model.PlayerDataFlan;

import java.util.UUID;

/**
 * @author Carlos
 */
public class MongoDBClient extends DataBaseClient {

  private MongoCollection<Document> playerDataFlan;

  @Override
  public void connect() {
    MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
      .applyConnectionString(new ConnectionString(Flancross.config.getMongoURL()))
        .applicationName("FlanCross")
      .build());
    MongoDatabase database = mongoClient.getDatabase("flancross");
    playerDataFlan = database.getCollection("playerDataFlan");
    System.out.println("[MongoDB] Conectado correctamente a flancross.playerDataFlan");
  }

  @Override
  public void disconnect() {
    Flancross.server.getPlayerManager().getPlayerList().forEach(player -> {
      saveOrUpdate(PlayerClaimData.get(player), player);
    });
    System.out.println("[MongoDB] Desconectado correctamente de la base de datos");
  }

  @Override
  public PlayerDataFlan getPlayerData(UUID playerUUID) {
    Document query = new Document("playerUUID", playerUUID.toString());
    Document playerData = playerDataFlan.find(query).first();

    if (playerData != null) {
      // Extraemos datos de forma segura con defaults
      String uuidStr = playerData.getString("playerUUID");
      String playerName = playerData.getString("playerName");
      int claimBlocks = playerData.getInteger("claimBlocks", 0);
      int additionalClaimBlocks = playerData.getInteger("additionalClaimBlocks", 0);
      int usedBlocks = playerData.getInteger("usedBlocks", 0);

      if (uuidStr == null) {
        System.out.println("[MongoDB] Documento inválido sin UUID: " + playerData.toJson());
        return null;
      }

      UUID uuid = UUID.fromString(uuidStr);

      return PlayerDataFlan.builder()
        .playerUUID(uuid)
        .playerName(playerName != null ? playerName : "Desconocido")
        .claimBlocks(claimBlocks)
        .additionalClaimBlocks(additionalClaimBlocks)
        .usedBlocks(usedBlocks)
        .build();
    } else {
      // No existe documento en Mongo → construimos a partir de Flan
      ServerPlayerEntity player = Flancross.server.getPlayerManager().getPlayer(playerUUID);
      if (player == null) {
        System.out.println("[MongoDB] No se encontró jugador con UUID: " + playerUUID);
        return null;
      }
      PlayerClaimData data = PlayerClaimData.get(player);

      return PlayerDataFlan.builder()
        .playerUUID(playerUUID)
        .playerName(player.getGameProfile().getName())
        .claimBlocks(data.getClaimBlocks())
        .additionalClaimBlocks(data.getAdditionalClaims())
        .usedBlocks(data.usedClaimBlocks())
        .build();
    }
  }

  @Override
  public void loadPlayerData(ServerPlayerEntity player) {
    PlayerDataFlan playerData = getPlayerData(player.getUuid());
    if (playerData == null) {
      System.out.println("[MongoDB] No se encontró data para " + player.getGameProfile().getName());
      return;
    }

    PlayerClaimData flanData = PlayerClaimData.get(player);
    FlanPlayerDataMixin accessor = (FlanPlayerDataMixin) flanData;

    playerData.fix();

    accessor.flanCross$setClaimBlocks(playerData.getClaimBlocks());
    accessor.flanCross$setAdditionalClaimBlocks(playerData.getAdditionalClaimBlocks());
    accessor.flanCross$setUsedBlocks(playerData.getUsedBlocks());
  }

  @Override
  public void saveOrUpdate(PlayerClaimData data, ServerPlayerEntity player) {
    FlanPlayerDataMixin accessor = (FlanPlayerDataMixin) data;

    Document query = new Document("playerUUID", player.getUuid().toString());
    Document update = new Document("$set", new Document()
      .append("playerUUID", player.getUuid().toString())
      .append("playerName", player.getGameProfile().getName())
      .append("claimBlocks", accessor.flanCross$getClaimBlocks())
      .append("additionalClaimBlocks", accessor.flanCross$getAdditionalClaimBlocks())
      .append("usedBlocks", accessor.flanCross$getUsedBlocks())
    );

    playerDataFlan.updateOne(query, update, new UpdateOptions().upsert(true));
    System.out.println("[MongoDB] Guardada data para " + player.getGameProfile().getName());
  }
}
