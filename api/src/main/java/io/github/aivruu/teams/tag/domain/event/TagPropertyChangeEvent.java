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

import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player is about to modify a property of a tag, fired only after all necessary
 * checking.
 * <br><br>
 * It should be said that this event will be fired before modification-processing take
 * place, so the event could have been fired, but modifications could were cancelled due to
 * equality between requested property and modification given.
 *
 * @since 0.0.1
 */
public final class TagPropertyChangeEvent extends Event implements Cancellable {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final String tag;
  private final ModificationContext context;
  private boolean cancelled;

  /**
   * Creates a new {@link TagPropertyChangeEvent} with the provided parameters.
   *
   * @param tag the modified tag's id.
   * @param context the modification's context.
   * @since 0.0.1
   */
  public TagPropertyChangeEvent(
     final @NotNull String tag,
     final @NotNull ModificationContext context) {
    this.tag = tag;
    this.context = context;
  }

  /**
   * Returns the modified tag's id.
   *
   * @return The id of the tag.
   * @since 0.0.1
   */
  public @NotNull String tag() {
    return this.tag;
  }

  /**
   * Returns the modification's context.
   *
   * @return The involved {@link ModificationContext}.
   * @since 2.3.1
   */
  public @NotNull ModificationContext context() {
    return this.context;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(final boolean cancel) {
    this.cancelled = cancel;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static @NotNull HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
