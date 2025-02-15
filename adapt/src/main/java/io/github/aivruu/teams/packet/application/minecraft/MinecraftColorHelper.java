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
package io.github.aivruu.teams.packet.application.minecraft;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * A utility-class used as "wrapper" for color and format conversion from {@link Color}
 * to {@link ChatFormatting} and vice versa.
 *
 * @since 2.3.1
 */
public final class MinecraftColorHelper {
  private MinecraftColorHelper() {
    throw new UnsupportedOperationException("This class is for utility.");
  }

  /**
   * Returns a Minecraft {@link ChatFormatting} using the given color's name.
   *
   * @param colorName the color's name to validate.
   * @return A {@link ChatFormatting} or {@code null} if the color-name is not valid.
   * @see ChatFormatting#getByName(String)
   * @since 2.3.1
   */
  public static @Nullable ChatFormatting minecraft(final @NotNull String colorName) {
    return ChatFormatting.getByName(colorName.toUpperCase());
  }

  /**
   * Returns a Minecraft {@link ChatFormatting} using the given {@link NamedTextColor}.
   *
   * @param namedTextColor the {@link NamedTextColor} to process.
   * @return A {@link ChatFormatting} or {@code null}.
   * @see ChatFormatting#getByHexValue(int)
   * @since 2.3.1
   */
  public static @NotNull ChatFormatting minecraft(final @NotNull NamedTextColor namedTextColor) {
    // If its color is provided by a [NamedTextColor] instance, it shouldn't be null never.
    return ChatFormatting.getByHexValue(namedTextColor.value());
  }

  /**
   * Returns a {@link NamedTextColor} that represents the color provided by the Minecraft's
   * {@link ChatFormatting}.
   *
   * @param chatFormatting the {@link ChatFormatting} to process.
   * @return A {@link NamedTextColor} or {@code null} if color-value isn't available or is
   * not valid.
   * @see ChatFormatting#getColor()
   * @see NamedTextColor#namedColor(int)
   * @since 2.3.1
   */
  public static @Nullable NamedTextColor modern(final @NotNull ChatFormatting chatFormatting) {
    // Check if the [ChatFormatting] provided color-value is valid to get a [NamedTextColor] object.
    final Integer colorValue = chatFormatting.getColor();
    return (colorValue != null) ? NamedTextColor.namedColor(colorValue) : NamedTextColor.WHITE;
  }

  /**
   * Returns a {@link Color} with the given {@link TextColor}'s {@link ChatFormatting}'s
   * colors.
   *
   * @param textColor the {@link TextColor} to process.
   * @return The {@link Color} with the applied color-value, or {@link Color#WHITE}. Could
   * return {@code null}.
   * @see #modern(ChatFormatting)
   * @since 2.3.1
   */
  public static @Nullable NamedTextColor modern(final @NotNull TextColor textColor) {
    return (textColor.format == null) ? NamedTextColor.WHITE : modern(textColor.format);
  }
}
