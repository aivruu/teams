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
package io.github.aivruu.teams.plain.application;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility used for {@link Component}s conversion to plain-text and vice versa.
 *
 * @since 0.0.1
 */
public final class PlainComponentHelper {
  private static final PlainTextComponentSerializer PLAIN_TEXT_COMPONENT_SERIALIZER = PlainTextComponentSerializer.plainText();

  private PlainComponentHelper() {
    throw new UnsupportedOperationException("This class is for utility.");
  }

  /**
   * Returns a plain-text representation for the given {@link Component}.
   *
   * @param component the component to serialize.
   * @return the plain-text representation of the {@link Component}.
   * @since 0.0.1
   */
  public static @NotNull String plain(final @NotNull Component component) {
    return PLAIN_TEXT_COMPONENT_SERIALIZER.serialize(component);
  }

  /**
   * Returns a plain-text representation for the given {@link Component} if it is available.
   *
   * @param component the component to serialize or {@code null}.
   * @return The plain-text representation or {@code null}.
   * @since 3.5.1
   */
  public static @Nullable String plainOrNull(final @Nullable Component component) {
    return (component == null) ? null : PLAIN_TEXT_COMPONENT_SERIALIZER.serialize(component);
  }

  /**
   * Returns a {@link Component} for the given plain-text representation.
   *
   * @param plain the plain-text representation.
   * @return The {@link Component}.
   * @since 0.0.1
   */
  public static @NotNull Component modern(final @NotNull String plain) {
    return PLAIN_TEXT_COMPONENT_SERIALIZER.deserialize(plain);
  }
}
