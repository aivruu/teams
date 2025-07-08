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

import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import io.github.aivruu.teams.repository.domain.DomainRepository;
import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagModelEntity;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.tag.domain.event.TagCreateEvent;
import io.github.aivruu.teams.tag.domain.event.TagDeleteEvent;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
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
  private final List<String> existingTagsIds = new ArrayList<>();
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
   * Returns a viewer-collection with all the {@link TagAggregateRoot}s loaded in-cache until now.
   * <p>
   * See documentation for {@link DomainRepository#findAllSync()} method to know how it works the
   * returned objects-collection.
   *
   * @return A viewer {@link Collection} of {@link TagAggregateRoot}.
   * @since 4.1.0
   */
  public @NotNull Collection<@NotNull TagAggregateRoot> getCachedTags() {
    return this.tagAggregateRootRegistry.findAllInCache();
  }

  /**
   * Returns a {@link List} with all in-cache currently tags' ids.
   *
   * @deprecated in favour of {@link #getCachedTags()}.
   * @return A {@link List} with ids or an empty and immutable-list if there are no loaded tags.
   * @see TagAggregateRootRegistry#findAllInCache()
   * @since 3.5.1
   */
  @Deprecated(forRemoval = true)
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
   * @deprecated in favour of {@link #existsAtList(String)}.
   * @param id the tag's id.
   * @return Whether the {@link TagAggregateRoot} exists.
   * @see TagAggregateRootRegistry#existsInInfrastructure(String)
   * @since 2.3.1
   */
  @Deprecated(forRemoval = true)
  public boolean exists(final @NotNull String id) {
    return this.tagAggregateRootRegistry.existsInInfrastructure(id);
  }

  /**
   * Returns whether the tag with the specified id is contained by the {@link #existingTagsIds} list.
   *
   * @param id the tag's id to check.
   * @return {@code true} if the tag exists in the list, {@code false} otherwise.
   * @see List#contains(Object)
   * @since 1.0.0
   */
  public boolean existsAtList(final @NotNull String id) {
    return this.existingTagsIds.contains(id);
  }

  /**
   * Creates a new tag with the provided information, and registers and save it in the
   * infrastructure.
   * <p>
   * This method will unregister and delete any existing tag or scoreboard-team (of a tag) with the
   * given tag-id, as well will decide the final-values for both tag's prefix and suffix.
   * <p>
   * If some exception occurs during tag's saving-process
   * (at method {@link TagAggregateRootRegistry#save(AggregateRoot)}), the plugin will continue with
   * the execution-flow and continue to perform required operations, but the tag's information won't
   * be saved.
   * <p>
   * This method will stop execution and return false if the tag-id already exists in the
   * {@link #existingTagsIds} list.
   *
   * @param player the player who creates the tag.
   * @param id     the tag's id.
   * @param prefix the tag's prefix, empty for unset.
   * @param suffix the tag's suffix, empty for unset.
   * @return {@code true} if the tag was added successfully, {@code false} otherwise.
   * @see Collection#add(Object)
   * @since 4.1.0
   */
  @SuppressWarnings("ConstantConditions") // for method's first-condition.
  public boolean create(
     final @NotNull Player player,
     final @NotNull String id,
     final @NotNull String prefix,
     final @NotNull String suffix,
     final @Nullable NamedTextColor color) {
    if (!this.existingTagsIds.add(id)) { // tag already exists in the infrastructure?
      return false;
    }
    /*
     * Due to we provide access to the tag-repository and registry through the API, might someone
     * had deleted the tag's information from the infrastructure, but not from the memory, and may
     * that reference be still loaded, so we need to remove it.
     */
    this.tagAggregateRootRegistry.unregister(id);
    this.packetAdaptation.deleteTeam(id); // the same for the tag's sb-team.

    final Component prefixComponent = prefix.isEmpty() ? null : MiniMessageParser.text(prefix);
    final Component suffixComponent = suffix.isEmpty() ? null : MiniMessageParser.text(suffix);

    final TagPropertiesValueObject properties = new TagPropertiesValueObject(prefixComponent,
       suffixComponent, (color == null) ? NamedTextColor.WHITE : color);
    final TagAggregateRoot tagAggregateRoot = new TagAggregateRoot(id,
       new TagModelEntity(id, properties));
    this.tagAggregateRootRegistry.register(tagAggregateRoot);
    this.handleTagAggregateRootSave(tagAggregateRoot);

    this.packetAdaptation.createTeam(id, properties);
    Bukkit.getPluginManager().callEvent(new TagCreateEvent(player, id));
    return true;
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
   * @deprecated in favour of {@link #create(Player, String, String, String, NamedTextColor)}.
   */
  @Deprecated(forRemoval = true)
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
          ? "Tag '{}' information has been deleted." : "The tag's information couldn't be deleted.",
          id));
    return true;
  }
}
