// This file is part of teams, licensed under the GNU License.
//
// Copyright (c) 2024-2025 aivruu
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
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
