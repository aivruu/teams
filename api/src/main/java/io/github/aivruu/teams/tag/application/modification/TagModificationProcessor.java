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
package io.github.aivruu.teams.tag.application.modification;

import io.github.aivruu.teams.tag.application.modification.context.ProcessedContextResultValueObject;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.event.TagPropertyChangeEvent;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used as base-class to proportionate basic and main logic for the modification-input
 * processing based on the given context.
 *
 * @since 2.3.1
 */
public abstract class TagModificationProcessor {
  private final JavaPlugin plugin; // Used for sync event-firing.
  private final TagAggregateRootRegistry tagAggregateRootRegistry;

  /**
   * Creates a new {@link TagModificationProcessor} with the provided parameters.
   *
   * @param plugin                   a {@link JavaPlugin} instance.
   * @param tagAggregateRootRegistry the {@link TagAggregateRootRegistry}.
   * @since 2.3.1
   */
  protected TagModificationProcessor(
     final @NotNull JavaPlugin plugin,
     final @NotNull TagAggregateRootRegistry tagAggregateRootRegistry) {
    this.plugin = plugin;
    this.tagAggregateRootRegistry = tagAggregateRootRegistry;
  }

  /**
   * Processes the given input (as string) by multiple-checks before delegate it as "pending".
   * <p>
   * Any of these checks can return a different status through a {@link ProcessedContextResultValueObject},
   * as well, the reference to the tag's aggregate-root (can be null).
   *
   * @param player       who's modifying the tag.
   * @param modification the {@link ModificationInProgressValueObject} for the tag's modification.
   * @param input        the edit-mode's input for modification.
   * @return A {@link ProcessedContextResultValueObject}, which can be.
   * <ul>
   * <li>{@link ProcessedContextResultValueObject#asPending(TagAggregateRoot)}
   * if the tag exists, and tag-event was not cancelled.</li>
   * <li>{@link ProcessedContextResultValueObject#asFailed()} if the event was cancelled.</li>
   * <li>{@link ProcessedContextResultValueObject#asInvalid()} if the tag does not exist.</li>
   * <li>{@link ProcessedContextResultValueObject#asCancelled()} if the process was cancelled (not event).</li>
   * </ul>
   * @see TagAggregateRootRegistry#findInBoth(String)
   * @since 4.0.0
   */
  public @NotNull ProcessedContextResultValueObject process(
     final @NotNull Player player,
     final @NotNull ModificationInProgressValueObject modification,
     final @NotNull String input) {
    if (input.equals("cancel")) {
      return ProcessedContextResultValueObject.asCancelled();
    }
    final TagAggregateRoot tagAggregateRoot = this.tagAggregateRootRegistry.findInBoth(
       modification.tag());
    if (tagAggregateRoot == null) {
      return ProcessedContextResultValueObject.asInvalid();
    }
    final TagPropertyChangeEvent tagPropertyChangeEvent = new TagPropertyChangeEvent(
       modification.tag(), modification.context());
    // Avoid IllegalStateException due to asynchronous event-firing.
    Bukkit.getScheduler().runTask(this.plugin, () ->
       Bukkit.getPluginManager().callEvent(tagPropertyChangeEvent));
    return tagPropertyChangeEvent.isCancelled()
       ? ProcessedContextResultValueObject.asFailed()
       : ProcessedContextResultValueObject.asPending(tagAggregateRoot);
  }
}
