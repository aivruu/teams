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
package io.github.aivruu.teams.util.application.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used to proportionate to {@link Component} text-parsing with {@link MiniMessage}.
 *
 * @since 4.0.0
 */
public final class MiniMessageParser {
  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private MiniMessageParser() {
    throw new UnsupportedOperationException(
       "This class is for utility and cannot be instantiated.");
  }

  /**
   * Parses the given string to a {@link Component} and applies the given {@link TagResolver}s.
   *
   * @param message   the text to parse.
   * @param resolvers the text's placeholders.
   * @return A {@link Component}.
   * @since 4.0.0
   */
  public static @NotNull Component text(
     final @NotNull String message,
     final @NotNull TagResolver... resolvers) {
    return message.isEmpty() ? Component.empty() : MINI_MESSAGE.deserialize(message, resolvers);
  }

  /**
   * Parses the given array's content to {@link Component}s.
   *
   * @param content the array to parse.
   * @return A {@link Component}.
   * @since 4.0.0
   */
  public static @NotNull Component list(final @NotNull String[] content) {
    if (content.length == 0) {
      return Component.empty();
    }
    if (content.length == 1) {
      // Deserialize it single line directly instead of create a new builder.
      return MINI_MESSAGE.deserialize(content[0]);
    }
    final TextComponent.Builder componentBuilder = Component.text();
    for (final String line : content) {
      componentBuilder.append(MINI_MESSAGE.deserialize(line)).appendNewline();
    }
    return componentBuilder.build();
  }

  /**
   * Parses the given array's content to {@link Component}s and applies the given
   * {@link TagResolver}s.
   *
   * @param content   the array to parse.
   * @param resolvers the text's placeholders.
   * @return A {@link Component} array.
   * @since 4.0.0
   */
  public static @NotNull Component[] array(
     final @NotNull String[] content,
     final @NotNull TagResolver... resolvers) {
    final Component[] components = new Component[content.length];
    for (byte i = 0; i < components.length; i++) {
      components[i] = MINI_MESSAGE.deserialize(content[i], resolvers)
         .decoration(TextDecoration.ITALIC, false);
    }
    return components;
  }

  /**
   * Parses the given {@link Component} to a string.
   *
   * @param component the component to parse.
   * @return A string.
   * @since 4.0.0
   */
  public static @NotNull String string(final @NotNull Component component) {
    return MINI_MESSAGE.serialize(component);
  }
}
