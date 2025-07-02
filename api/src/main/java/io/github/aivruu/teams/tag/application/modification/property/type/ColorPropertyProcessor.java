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
package io.github.aivruu.teams.tag.application.modification.property.type;

import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.property.PropertyProcessorContract;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ColorPropertyProcessor implements PropertyProcessorContract<NamedTextColor> {
  INSTANCE;

  @Override
  public @NotNull ModificationContext context() {
    return ModificationContext.COLOR;
  }

  @Override
  public @Nullable TagPropertiesValueObject handle(
     final @NotNull String input,
     final @NotNull TagPropertiesValueObject properties,
     final @Nullable NamedTextColor oldValue) {
    final NamedTextColor newColor = NamedTextColor.NAMES.value(input.toLowerCase(Locale.ROOT));
    return (newColor != null)
       ? new TagPropertiesValueObject(properties.prefix(), properties.suffix(), newColor)
       : null;
  }
}
