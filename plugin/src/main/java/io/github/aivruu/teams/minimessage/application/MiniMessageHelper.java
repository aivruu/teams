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
package io.github.aivruu.teams.minimessage.application;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public final class MiniMessageHelper {
  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private MiniMessageHelper() {
    throw new UnsupportedOperationException("This class is for utility and cannot be instantiated.");
  }

  public static @NotNull Component text(final @NotNull String message, final @NotNull TagResolver... resolvers) {
    return message.isEmpty() ? Component.empty() : MINI_MESSAGE.deserialize(message, resolvers);
  }

  public static @NotNull Component list(final @NotNull String[] content) {
    if (content.length == 0) {
      return Component.empty();
    }
    final TextComponent.Builder componentBuilder = Component.text();
    for (final String line : content) {
      componentBuilder.append(MINI_MESSAGE.deserialize(line))
        .appendNewline();
    }
    return componentBuilder.build();
  }

  public static @NotNull Component[] array(final @NotNull String[] content, final @NotNull TagResolver... resolvers) {
    final Component[] components = new Component[content.length];
    for (byte i = 0; i < components.length; i++) {
      components[i] = MINI_MESSAGE.deserialize(content[i], resolvers)
        .decoration(TextDecoration.ITALIC, false);
    }
    return components;
  }

  public static @NotNull String string(final @NotNull Component component) {
    return MINI_MESSAGE.serialize(component);
  }
}
