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
package io.github.aivruu.teams.player.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a player with a selected-tag.
 *
 * @since 0.0.1
 */
public final class PlayerModelEntity {
  private final String id;
  private @Nullable String tag;

  /**
   * Creates a new {@link PlayerModelEntity} with the provided parameters.
   *
   * @param id the player's id.
   * @param tag the selected-tag or {@code null} if there is none.
   * @since 0.0.1
   */
  public PlayerModelEntity(final @NotNull String id, final @Nullable String tag) {
    this.id = id;
    this.tag = tag;
  }

  /**
   * Returns the player's id.
   *
   * @return The player's identifier.
   * @since 0.0.1
   */
  public @NotNull String id() {
    return this.id;
  }

  /**
   * Returns the player's selected-tag.
   *
   * @return The player's selected-tag or {@code null} if there is none.
   * @since 0.0.1
   */
  public @Nullable String tag() {
    return this.tag;
  }

  /**
   * Sets the player's current selected-tag.
   *
   * @param tag the tag's id or {@code null} for unselect.
   * @since 0.0.1
   */
  void tag(final @Nullable String tag) {
    this.tag = tag;
  }
}
