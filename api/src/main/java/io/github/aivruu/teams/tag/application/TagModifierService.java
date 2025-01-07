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
package io.github.aivruu.teams.tag.application;

import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.result.domain.ValueObjectMutationResult;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.tag.domain.event.TagPropertyChangeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used as service for modifcations for the tags.
 *
 * @since 0.0.1
 */
public final class TagModifierService {
  private final PacketAdaptationContract packetAdaptation;

  /**
   * Creates a new {@link TagModifierService} with the provided parameters.
   *
   * @param packetAdaptation the {@link PacketAdaptationContract} for teams update.
   * @since 0.0.1
   */
  public TagModifierService(final @NotNull PacketAdaptationContract packetAdaptation) {
    this.packetAdaptation = packetAdaptation;
  }

  /**
   * Updates the prefix-property for the tag's team.
   *
   * @param tagAggregateRoot the {@link TagAggregateRoot} to update.
   * @param prefix the new prefix for the tag.
   * @return A {@link ValueObjectMutationResult} with the status-code which can be:
   * <ul>
   * <li>
   *   {@link ValueObjectMutationResult#mutated(Object)} if the prefix was updated.
   *   {@link ValueObjectMutationResult#result()} isn't {@code null}.
   * </li>
   * <li>
   *   {@link ValueObjectMutationResult#error()} if the event was cancelled.
   *   {@link ValueObjectMutationResult#result()} is {@code null}.
   * </li>
   * <li>
   *   {@link ValueObjectMutationResult#unchanged()} if the prefix is the same as before.
   *   {@link ValueObjectMutationResult#result()} is {@code null}.
   * </li>
   * </ul>
   * @since 0.0.1
   */
  public @NotNull ValueObjectMutationResult<@Nullable TagPropertiesValueObject> updatePrefix(
    final @NotNull TagAggregateRoot tagAggregateRoot,
    final @Nullable Component prefix
  ) {
    final TagPropertiesValueObject properties = tagAggregateRoot.tagModel().tagComponentProperties();
    if (prefix != null && prefix.equals(properties.prefix())) {
      return ValueObjectMutationResult.unchanged();
    }
    final TagPropertiesValueObject propertiesWithNewPrefix = new TagPropertiesValueObject(prefix, properties.suffix());
    final TagPropertyChangeEvent tagPropertyChangeEvent = new TagPropertyChangeEvent(
      properties, propertiesWithNewPrefix, tagAggregateRoot.id());
    Bukkit.getPluginManager().callEvent(tagPropertyChangeEvent);
    if (tagPropertyChangeEvent.isCancelled()) {
      return ValueObjectMutationResult.error();
    }
    this.packetAdaptation.updateTeamPrefix(tagAggregateRoot.id(), prefix);
    return ValueObjectMutationResult.mutated(propertiesWithNewPrefix);
  }

  /**
   * Updates the suffix-property for the tag's team.
   *
   * @param tagAggregateRoot the {@link TagAggregateRoot} to update.
   * @param suffix the new suffix for the tag.
   * @return A {@link ValueObjectMutationResult} with the status-code which can be:
   * <ul>
   * <li>
   *   {@link ValueObjectMutationResult#mutated(Object)} if the suffix was updated.
   *   {@link ValueObjectMutationResult#result()} isn't {@code null}.
   * </li>
   * <li>
   *   {@link ValueObjectMutationResult#error()} if the event was cancelled.
   *   {@link ValueObjectMutationResult#result()} is {@code null}.
   * </li>
   * <li>
   *   {@link ValueObjectMutationResult#unchanged()} if the suffix is the same as before.
   *   {@link ValueObjectMutationResult#result()} is {@code null}.
   * </li>
   * </ul>
   * @since 0.0.1
   */
  public @NotNull ValueObjectMutationResult<@Nullable TagPropertiesValueObject> updateSuffix(
    final @NotNull TagAggregateRoot tagAggregateRoot,
    final @Nullable Component suffix
  ) {
    final TagPropertiesValueObject properties = tagAggregateRoot.tagModel().tagComponentProperties();
    if (suffix != null && suffix.equals(properties.suffix())) {
      return ValueObjectMutationResult.unchanged();
    }
    final TagPropertiesValueObject propertiesWithNewSuffix = new TagPropertiesValueObject(properties.prefix(), suffix);
    final TagPropertyChangeEvent tagPropertyChangeEvent = new TagPropertyChangeEvent(
      properties, propertiesWithNewSuffix, tagAggregateRoot.id());
    Bukkit.getPluginManager().callEvent(tagPropertyChangeEvent);
    if (tagPropertyChangeEvent.isCancelled()) {
      return ValueObjectMutationResult.error();
    }
    this.packetAdaptation.updateTeamSuffix(tagAggregateRoot.id(), suffix);
    return ValueObjectMutationResult.mutated(properties);
  }
}
