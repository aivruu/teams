// This file is part of teams, licensed under the GNU License.
//
// Copyright (c) 2024 aivruu
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
package io.github.aivruu.teams.tag.application;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import io.github.aivruu.teams.tag.application.modification.ModificationOnCurseValueObject;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * This class is used as cache-container for any {@link ModificationOnCurseValueObject} object.
 *
 * @since 2.3.1
 */
public final class TagModificationContainer {
  // We set 15 seconds as max-duration for a modification in case that a player closes
  // the menu without select an editor first, so the object is removed automatically.
  private final Cache<String, ModificationOnCurseValueObject> modificationsOnCurse = Caffeine.newBuilder()
    .expireAfterWrite(15, TimeUnit.SECONDS)
    .scheduler(Scheduler.systemScheduler())
    .build();

  /**
   * Checks if there's a modification on the curse with the specified player.
   *
   * @param playerId the player's id.
   * @return Whether the player is modifying a tag.
   * @since 2.3.1
   */
  public boolean isModifying(final @NotNull String playerId) {
    return this.modificationsOnCurse.asMap().containsKey(playerId);
  }

  /**
   * Registers a new modification for the given player over the specified tag.
   *
   * @param playerId the player's id.
   * @param tag the tag's id.
   * @return Whether the player is not modifying a tag currently.
   * @since 2.3.1
   */
  public boolean registerModification(final @NotNull String playerId, final @NotNull String tag) {
    if (this.modificationsOnCurse.asMap().containsKey(playerId)) {
      return false;
    }
    this.modificationsOnCurse.put(playerId, new ModificationOnCurseValueObject(tag, ModificationContext.NONE));
    return true;
  }

  /**
   * Updates the in-cache {@link ModificationOnCurseValueObject}'s {@link ModificationContext}.
   *
   * @param playerId the player's id.
   * @param modificationContext the new {@link ModificationContext}.
   * @since 2.3.1
   */
  public void updateModificationContext(final @NotNull String playerId, final @NotNull ModificationContext modificationContext) {
    this.modificationsOnCurse.asMap().computeIfPresent(playerId, (key, context) ->
      new ModificationOnCurseValueObject(context.tag(), modificationContext));
  }

  /**
   * Removes the {@link ModificationOnCurseValueObject} for the specified player from the cache.
   *
   * @param playerId the player's id.
   * @return The removed {@link ModificationOnCurseValueObject} or {@code null} if the player is not modifying a tag.
   * @since 2.3.1
   */
  public @Nullable ModificationOnCurseValueObject unregisterModification(final @NotNull String playerId) {
    return this.modificationsOnCurse.asMap().remove(playerId);
  }

  /**
   * Removes all the current {@link ModificationOnCurseValueObject}s from cache.
   *
   * @since 2.3.1
   */
  public void clearModifications() {
    this.modificationsOnCurse.invalidateAll();
  }
}
