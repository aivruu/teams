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
package io.github.aivruu.teams.player.domain;

import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link AggregateRoot} implementation for {@link PlayerModelEntity}.
 *
 * @since 0.0.1
 */
public final class PlayerAggregateRoot extends AggregateRoot {
  private final PlayerModelEntity playerModel;

  /**
   * Creates a new {@link PlayerAggregateRoot} with the provided parameters.
   *
   * @param id the aggregate-root's id.
   * @param playerModel this aggregate-root's {@link PlayerModelEntity} object.
   * @since 0.0.1
   */
  public PlayerAggregateRoot(final @NotNull String id, final @NotNull PlayerModelEntity playerModel) {
    super(id);
    this.playerModel = playerModel;
  }

  /**
   * Returns the {@link PlayerModelEntity}.
   *
   * @return This aggregate-root's {@link PlayerModelEntity}.
   * @since 0.0.1
   */
  public @NotNull PlayerModelEntity playerModel() {
    return this.playerModel;
  }

  /**
   * Sets a new tag-selection for the player.
   *
   * @param tag the tag's id or {@code null} for unselect.
   * @since 0.0.1
   */
  public void tag(final @Nullable String tag) {
    this.playerModel.tag(tag);
  }
}
