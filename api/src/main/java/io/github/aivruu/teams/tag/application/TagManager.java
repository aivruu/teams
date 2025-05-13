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
package io.github.aivruu.teams.tag.application;

import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagModelEntity;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.tag.domain.event.TagCreateEvent;
import io.github.aivruu.teams.tag.domain.event.TagDeleteEvent;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is used as global registry and information-handler for the tag.
 *
 * @since 0.0.1
 */
public final class TagManager {
  private final TagAggregateRootRegistry tagAggregateRootRegistry;
  private final PacketAdaptationContract packetAdaptation;

  /**
   * Creates a new {@link TagManager} with the provided parameters.
   *
   * @param tagAggregateRootRegistry the {@link TagAggregateRootRegistry}.
   * @param packetAdaptation         the {@link PacketAdaptationContract}.
   * @since 0.0.1
   */
  public TagManager(
     final @NotNull TagAggregateRootRegistry tagAggregateRootRegistry,
     final @NotNull PacketAdaptationContract packetAdaptation) {
    this.tagAggregateRootRegistry = tagAggregateRootRegistry;
    this.packetAdaptation = packetAdaptation;
  }

  /**
   * Returns a {@link TagAggregateRoot} for the specified id.
   *
   * @param id the tag's id.
   * @return The {@link TagAggregateRoot} or {@code null} if tag doesn't exist.
   * @see TagAggregateRootRegistry#findInBoth(String)
   * @since 0.0.1
   */
  public @Nullable TagAggregateRoot tagAggregateRootOf(final @NotNull String id) {
    return this.tagAggregateRootRegistry.findInBoth(id);
  }

  /**
   * Returns a {@link List} with all in-cache currently tags' ids.
   *
   * @return A {@link List} with ids or an empty and immutable-list if there are no loaded tags.
   * @see TagAggregateRootRegistry#findAllInCache()
   * @since 3.5.1
   */
  public @NotNull List<@NotNull String> findAllLoadedTagIds() {
    final Collection<TagAggregateRoot> tagAggregateRoots =
       this.tagAggregateRootRegistry.findAllInCache();
    if (tagAggregateRoots.isEmpty()) {
      return List.of();
    }
    final List<String> ids = new ArrayList<>(tagAggregateRoots.size());
    for (final TagAggregateRoot tagAggregateRoot : tagAggregateRoots) {
      ids.add(tagAggregateRoot.id());
    }
    return ids;
  }

  /**
   * Checks whether the specified {@link TagAggregateRoot}'s information exists in the
   * infrastructure.
   *
   * @param id the tag's id.
   * @return Whether the {@link TagAggregateRoot} exists.
   * @see TagAggregateRootRegistry#existsInInfrastructure(String)
   * @since 2.3.1
   */
  public boolean exists(final @NotNull String id) {
    return this.tagAggregateRootRegistry.existsInInfrastructure(id);
  }

  /**
   * Creates a new tag (and scoreboard-team) with the specified parameters.
   *
   * @param player the player who creates the tag.
   * @param id     the tag's id.
   * @param prefix the tag's prefix, {@code null} for unset.
   * @param suffix the tag's suffix, {@code null} for unset.
   * @return Whether the tag was created.
   * @see TagAggregateRootRegistry#existsInInfrastructure(String)
   * @since 0.0.1
   */
  public boolean createTag(
     final @NotNull Player player,
     final @NotNull String id,
     final @Nullable Component prefix,
     final @Nullable Component suffix,
     final @NotNull NamedTextColor color) {
    if (this.tagAggregateRootRegistry.existsInInfrastructure(id)) {
      return false;
    }
    final TagPropertiesValueObject properties = new TagPropertiesValueObject(prefix, suffix, color);
    final TagAggregateRoot tagAggregateRoot = new TagAggregateRoot(id, new TagModelEntity(id,
       properties));
    this.tagAggregateRootRegistry.register(tagAggregateRoot);
    this.packetAdaptation.createTeam(id, properties);
    this.handleTagAggregateRootSave(tagAggregateRoot);
    Bukkit.getPluginManager().callEvent(new TagCreateEvent(player, id));
    return true;
  }

  /**
   * Handles the tag's aggregate-root saving process.
   *
   * @param tagAggregateRoot the {@link TagAggregateRoot} to save.
   * @since 0.0.1
   */
  public void handleTagAggregateRootSave(final @NotNull TagAggregateRoot tagAggregateRoot) {
    this.tagAggregateRootRegistry.save(tagAggregateRoot)
       .exceptionally(exception -> {
         Debugger.write("Unexpected exception during tag-aggregate-root saving with id '{}'.",
            exception);
         return false;
       })
       .thenAccept(saved -> {
         if (!saved) {
           Debugger.write("The tag's aggregate-root information couldn't be saved.");
           // Avoid have unnecessary information in-cache.
           this.tagAggregateRootRegistry.unregister(tagAggregateRoot.id());
         }
       });
  }

  /**
   * Deletes the tag's information (and scoreboard-team) for the specified id.
   *
   * @param id the tag's id.
   * @see TagAggregateRootRegistry#existsInInfrastructure(String)
   * @since 0.0.1
   */
  public boolean deleteTag(final @NotNull String id) {
    this.tagAggregateRootRegistry.unregister(id); // Delete from cache if necessary.
    this.packetAdaptation.deleteTeam(id); // Delete internal scoreboard-item.
    if (!this.tagAggregateRootRegistry.existsInInfrastructure(id)) {
      return false;
    }
    Bukkit.getPluginManager().callEvent(new TagDeleteEvent(id));
    // Process from-infrastructure tag deletion.
    this.tagAggregateRootRegistry.delete(id)
       .exceptionally(exception -> {
         Debugger.write("Unexpected exception during tag deleting with id '{}'.", exception);
         return false;
       })
       .thenAccept(deleted -> Debugger.write(deleted
          ? "Tag '{}' information has been deleted."
          : "The tag's information couldn't be deleted.", id));
    return true;
  }
}
