package org.kingpixel.flancross.model;

import lombok.Builder;
import lombok.Data;
import org.bson.Document;

import java.util.UUID;

/**
 * @author Carlos Varas Alonso - 29/09/2025 2:11
 */
@Data
@Builder
public class PlayerDataFlan {
  private UUID playerUUID;
  private String playerName;
  private int claimBlocks;
  private int additionalClaimBlocks;
  private int usedBlocks;

  public PlayerDataFlan(){
    this.claimBlocks = 0;
    this.additionalClaimBlocks = 0;
    this.usedBlocks = 0;
  }

  public PlayerDataFlan(UUID playerUUID, String playerName, int claimBlocks, int additionalClaimBlocks, int usedBlocks) {
    this.playerUUID = playerUUID;
    this.playerName = playerName;
    this.claimBlocks = claimBlocks;
    this.additionalClaimBlocks = additionalClaimBlocks;
    this.usedBlocks = usedBlocks;
  }

  public Document toDocument() {
    Document doc = new Document();
    doc.append("playerUUID", this.playerUUID.toString());
    doc.append("playerName", this.playerName);
    doc.append("claimBlocks", this.claimBlocks);
    doc.append("additionalClaimBlocks", this.additionalClaimBlocks);
    doc.append("usedBlocks", this.usedBlocks);
    return doc;
  }

  public static PlayerDataFlan fromDocument(Document doc) {
    if (doc == null) return null;
    String uuidStr = doc.getString("playerUUID");
    if (uuidStr == null) return null;
    UUID playerUUID = UUID.fromString(uuidStr);
    String playerName = doc.getString("playerName");
    int claimBlocks = doc.getInteger("claimBlocks", 0);
    int additionalClaimBlocks = doc.getInteger("additionalClaimBlocks", 0);
    int usedBlocks = doc.getInteger("usedBlocks", 0);
    return new PlayerDataFlan(playerUUID, playerName, claimBlocks, additionalClaimBlocks, usedBlocks);
  }

  public void fix() {
    if (this.claimBlocks < 0) this.claimBlocks = 0;
    if (this.additionalClaimBlocks < 0) this.additionalClaimBlocks = 0;
    if (this.usedBlocks < 0) this.usedBlocks = 0;
    var total = this.claimBlocks + this.additionalClaimBlocks;
    if (this.usedBlocks > total) this.usedBlocks = total;
  }
}
