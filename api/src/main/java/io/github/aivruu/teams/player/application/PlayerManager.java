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
package io.github.aivruu.teams.player.application;

import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.PlayerModelEntity;
import io.github.aivruu.teams.player.domain.registry.PlayerAggregateRootRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used as global registry and information-handler for the player.
 *
 * @since 0.0.1
 */
public final class PlayerManager {
  private final PlayerAggregateRootRegistry playerAggregateRootRegistry;

  /**
   * Creates a new {@link PlayerManager} with the provided parameters.
   *
   * @param playerAggregateRootRegistry the {@link PlayerAggregateRootRegistry}.
   * @since 0.0.1
   */
  public PlayerManager(final @NotNull PlayerAggregateRootRegistry playerAggregateRootRegistry) {
    this.playerAggregateRootRegistry = playerAggregateRootRegistry;
  }

  /**
   * Returns a {@link PlayerAggregateRoot} for the specified id.
   *
   * @param id the player's id.
   * @return The {@link PlayerAggregateRoot} or {@code null} if player is offline.
   * @see PlayerAggregateRootRegistry#findInCache(String)
   * @since 0.0.1
   */
  public @Nullable PlayerAggregateRoot playerAggregateRootOf(final @NotNull String id) {
    return this.playerAggregateRootRegistry.findInCache(id);
  }

  /**
   * Loads (or creates if necessary) the player's information into the cache.
   *
   * @param id the player's id.
   * @see PlayerAggregateRootRegistry#existsInCache(String)
   * @since 0.0.1
   */
  public void loadOne(final @NotNull String id) {
    if (this.playerAggregateRootRegistry.existsInCache(id)) {
      return;
    }
    this.playerAggregateRootRegistry.findInInfrastructure(id)
      .thenAccept(playerAggregateRoot -> {
        if (playerAggregateRoot == null) {
          playerAggregateRoot = new PlayerAggregateRoot(id, new PlayerModelEntity(id, null));
        }
        // In-cache storing and in-infrastructure save handling.
        this.playerAggregateRootRegistry.register(playerAggregateRoot);
        this.handlePlayerAggregateRootSave(playerAggregateRoot);
      })
      .whenComplete((result, exception) -> {
        if (exception != null) {
          Debugger.write("Unexpected exception during player's information search in infrastructure.", exception);
        }
      });
  }

  /**
   * Handles the player's information saving process.
   *
   * @param playerAggregateRoot the {@link PlayerAggregateRoot} to save.
   * @since 0.0.1
   */
  public void handlePlayerAggregateRootSave(final @NotNull PlayerAggregateRoot playerAggregateRoot) {
    this.playerAggregateRootRegistry.save(playerAggregateRoot)
      .thenAccept(saved -> {
        if (!saved) Debugger.write("The player's information couldn't be saved.");
      })
      .whenComplete((result, exception) -> {
        if (exception != null) Debugger.write("Unexpected exception during player's data saving.", exception);
      });
  }

  /**
   * Unloads the player's information from cache and saves it.
   *
   * @param id the player's id.
   * @see PlayerAggregateRootRegistry#unregister(String)
   * @since 0.0.1
   */
  public void unloadOne(final @NotNull String id) {
    final PlayerAggregateRoot playerAggregateRoot = this.playerAggregateRootRegistry.unregister(id);
    if (playerAggregateRoot == null) {
      return;
    }
    this.handlePlayerAggregateRootSave(playerAggregateRoot);
  }
}
