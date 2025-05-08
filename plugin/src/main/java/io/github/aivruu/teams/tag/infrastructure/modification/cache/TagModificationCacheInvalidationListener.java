package io.github.aivruu.teams.tag.infrastructure.modification.cache;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class TagModificationCacheInvalidationListener
   implements RemovalListener<String, ModificationInProgressValueObject> {
  // Pending PR merging for this feature.
//  private final ConfigurationManager configurationManager;

  @Override
  public void onRemoval(
     final @Nullable String id,
     final @Nullable ModificationInProgressValueObject modification,
     final @NotNull RemovalCause cause) {
    if (id == null) {
      return;
    }
    final Player player = Bukkit.getPlayer(UUID.fromString(id));
    if (player == null) {
      return;
    }
    Debugger.write("Invalidating expired modification-entry for player: {}", id);
    // ...
  }
}
