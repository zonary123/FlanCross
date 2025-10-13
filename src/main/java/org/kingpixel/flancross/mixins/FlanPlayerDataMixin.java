package org.kingpixel.flancross.mixins;

import io.github.flemmli97.flan.player.PlayerClaimData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;


/**
 * @author Carlos
 */
@Mixin(PlayerClaimData.class)
public interface FlanPlayerDataMixin {

  // Claim Blocks
  @Accessor("claimBlocks")
  int flanCross$getClaimBlocks();

  @Accessor("claimBlocks")
  void flanCross$setClaimBlocks(int value);

  // Additional Claim Blocks
  @Accessor("additionalClaimBlocks")
  int flanCross$getAdditionalClaimBlocks();

  @Accessor("additionalClaimBlocks")
  void flanCross$setAdditionalClaimBlocks(int value);

  // Used Blocks
  @Accessor("usedBlocks")
  int flanCross$getUsedBlocks();

  @Accessor("usedBlocks")
  void flanCross$setUsedBlocks(int value);
}


