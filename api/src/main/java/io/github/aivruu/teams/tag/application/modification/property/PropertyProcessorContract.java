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
package io.github.aivruu.teams.tag.application.modification.property;

import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface-contract that defines the methods required for tag-properties processing.
 *
 * @param <T> the property's type.
 * @since 4.1.0
 */
public interface PropertyProcessorContract<T> {
  /**
   * Returns the context of the modification for this property.
   *
   * @return The {@link ModificationContext} for the property.
   * @since 4.1.0
   */
  @NotNull ModificationContext context();

  /**
   * Handles the processing-logic for the specified tag-property with the given input.
   *
   * @param input      the input for the property.
   * @param properties the current properties of the tag.
   * @param oldValue   the old-value for the property.
   * @return A new {@link TagPropertiesValueObject} or {@code null} if the property was not
   * modified, or is the same as the old value.
   * @since 4.1.0
   */
  @Nullable TagPropertiesValueObject handle(
     final @NotNull String input,
     final @NotNull TagPropertiesValueObject properties,
     final @Nullable T oldValue);
}
