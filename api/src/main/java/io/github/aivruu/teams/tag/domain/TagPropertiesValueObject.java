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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the properties that a tag can have.
 *
 * @param prefix the prefix {@link Component} or {@code null} if no have.
 * @param suffix the suffix {@link Component} or {@code null} if no have.
 * @param color  the tag's color.
 * @since 0.0.1
 */
public record TagPropertiesValueObject(
   @Nullable Component prefix,
   @Nullable Component suffix,
   @NotNull NamedTextColor color) {
  /** Represents an "empty" (or non-configured) properties-container for a tag. */
  public static final TagPropertiesValueObject EMPTY =
     new TagPropertiesValueObject(null, null, NamedTextColor.WHITE);
}
