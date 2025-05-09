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
package io.github.aivruu.teams.tag.domain;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a tag-model.
 *
 * @since 0.0.1
 */
public final class TagModelEntity {
  private final String id;
  private TagPropertiesValueObject tagComponentProperties;

  /**
   * Creates a new {@link TagModelEntity} with the provided parameters.
   *
   * @param id the tag's id.
   * @param tagComponentProperties the tag's properties.
   * @since 0.0.1
   */
  public TagModelEntity(final @NotNull String id, final @NotNull TagPropertiesValueObject tagComponentProperties) {
    this.id = id;
    this.tagComponentProperties = tagComponentProperties;
  }

  /**
   * Returns the tag's id.
   *
   * @return The tag's identifier.
   * @since 0.0.1
   */
  public @NotNull String id() {
    return this.id;
  }

  /**
   * Returns the tag's properties.
   *
   * @return The tag's properties.
   * @since 0.0.1
   */
  public @NotNull TagPropertiesValueObject tagComponentProperties() {
    return this.tagComponentProperties;
  }

  /**
   * Sets a new properties for the tag.
   *
   * @param tagComponentProperties the tag's new {@link TagPropertiesValueObject}.
   * @since 0.0.1
   */
  void tagComponentProperties(final @NotNull TagPropertiesValueObject tagComponentProperties) {
    this.tagComponentProperties = tagComponentProperties;
  }
}
