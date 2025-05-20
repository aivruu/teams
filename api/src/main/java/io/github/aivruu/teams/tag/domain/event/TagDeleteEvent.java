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

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a tag is deleted.
 *
 * @since 0.0.1
 */
public final class TagDeleteEvent extends Event {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final String tag;

  /**
   * Creates a new {@link TagDeleteEvent} with the provided parameters.
   *
   * @param tag the deleted tag's id.
   * @since 0.0.1
   */
  public TagDeleteEvent(final @NotNull String tag) {
    this.tag = tag;
  }

  /**
   * Returns the deleted tag's id.
   *
   * @return The id of the tag.
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
