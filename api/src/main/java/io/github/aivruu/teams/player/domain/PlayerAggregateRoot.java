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

import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link AggregateRoot} implementation for {@link PlayerModelEntity}.
 *
 * @since 0.0.1
 */
public final class PlayerAggregateRoot extends AggregateRoot {
  /** The tag specified is already selected. */
  public static final byte TAG_IS_ALREADY_SELECTED = 1;
  /** The current-tag has been cleared. */
  public static final byte TAG_HAS_BEEN_CLEARED = 2;
  /** The current-tag has been changed by other one. */
  public static final byte TAG_HAS_BEEN_CHANGED = 3;
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
   * Sets and handles the tag selection/deselection for this {@link PlayerAggregateRoot}.
   *
   * @param tag the tag's id or {@code null} for unselect.
   * @return A status-code which can be:
   * <ul>
   * <li>{@link #TAG_HAS_BEEN_CHANGED} if the tag has been changed for this aggregate-root.</li>
   * <li>{@link #TAG_HAS_BEEN_CLEARED} if the current-tag has been cleared.</li>
   * <li>{@link #TAG_IS_ALREADY_SELECTED} if the specified-tag is already selected.</li>
   * </ul>
   * @since 2.2.1
   */
  public byte tagWithStatus(final @Nullable String tag) {
    final String currentTag = this.playerModel.tag();
    if (currentTag != null && currentTag.equals(tag)) {
      return TAG_IS_ALREADY_SELECTED;
    }
    this.playerModel.tag(tag);
    return (tag == null) ? TAG_HAS_BEEN_CLEARED : TAG_HAS_BEEN_CHANGED;
  }
}
