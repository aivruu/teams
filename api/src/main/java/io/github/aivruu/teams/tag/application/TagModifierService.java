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

import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.result.domain.ValueObjectMutationResult;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used as service for modifications for the tags.
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
    final Component currentPrefix = properties.prefix();
    if ((prefix != null && currentPrefix != null) && prefix.equals(currentPrefix)) {
      return ValueObjectMutationResult.unchanged();
    }
    this.packetAdaptation.updateTeamPrefix(tagAggregateRoot.id(), prefix);
    return ValueObjectMutationResult.mutated(new TagPropertiesValueObject(prefix, properties.suffix(), properties.color()));
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
    final Component currentSuffix = properties.suffix();
    if ((suffix != null && currentSuffix != null) && suffix.equals(currentSuffix)) {
      return ValueObjectMutationResult.unchanged();
    }
    this.packetAdaptation.updateTeamSuffix(tagAggregateRoot.id(), suffix);
    return ValueObjectMutationResult.mutated(new TagPropertiesValueObject(properties.prefix(), suffix, properties.color()));
  }

  /**
   * Updates the color-property for the tag's team.
   *
   * @param tagAggregateRoot the {@link TagAggregateRoot} to update.
   * @param color the new color for the tag.
   * @return A {@link ValueObjectMutationResult} with the status-code which can be:
   * <ul>
   * <li>
   *   {@link ValueObjectMutationResult#mutated(Object)} if the color was updated.
   *   {@link ValueObjectMutationResult#result()} isn't {@code null}.
   * </li>
   * <li>
   *   {@link ValueObjectMutationResult#unchanged()} if the color is the same as before.
   *   {@link ValueObjectMutationResult#result()} is {@code null}.
   * </li>
   * </ul>
   * @since 2.3.1
   */
  public @NotNull ValueObjectMutationResult<@Nullable TagPropertiesValueObject> updateColor(
    final @NotNull TagAggregateRoot tagAggregateRoot,
    final @NotNull NamedTextColor color
  ) {
    final TagPropertiesValueObject properties = tagAggregateRoot.tagModel().tagComponentProperties();
    if (color == properties.color()) {
      return ValueObjectMutationResult.unchanged();
    }
    this.packetAdaptation.updateTeamColor(tagAggregateRoot.id(), color);
    return ValueObjectMutationResult.mutated(new TagPropertiesValueObject(properties.prefix(), properties.suffix(), color));
  }
}
