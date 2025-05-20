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
package io.github.aivruu.teams.tag.domain.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player deselects their current tag.
 *
 * @since 0.0.1
 */
public final class TagUnselectEvent extends Event {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private final String tag;

  /**
   * Creates a new {@link TagUnselectEvent} with the provided parameters.
   *
   * @param player the player who deselected the tag.
   * @param tag the player's old tag's id.
   * @since 0.0.1
   */
  public TagUnselectEvent(final @NotNull Player player, final @NotNull String tag) {
    this.player = player;
    this.tag = tag;
  }

  /**
   * Returns the player who deselected the tag.
   *
   * @return The {@link Player} involved in this event.
   * @since 0.0.1
   */
  public @NotNull Player player() {
    return this.player;
  }

  /**
   * Returns the player's old tag's id.
   *
   * @return The player's old tag's id.
   * @since 0.0.1
   */
  public @NotNull String tag() {
    return this.tag;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static @NotNull HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
