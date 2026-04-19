// This file is part of teams, licensed under the GNU License.
//
// Copyright (c) 2024-2026 aivruu
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
package io.github.aivruu.teams.util.application;

import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import io.github.miniplaceholders.api.MiniPlaceholders;
import me.clip.placeholderapi.PAPIComponents;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderParser {
  private static final TagResolver MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS;
  public static final boolean LEGACY_PLACEHOLDERS_HOOKED;
  public static final boolean MODERN_PLACEHOLDERS_HOOKED;

  static {
    final PluginManager pluginManager = Bukkit.getPluginManager();
    LEGACY_PLACEHOLDERS_HOOKED = pluginManager.isPluginEnabled("PlaceholderAPI");
    MODERN_PLACEHOLDERS_HOOKED = pluginManager.isPluginEnabled("MiniPlaceholders");
    // Initialize until this moment MiniPlaceholders its global-placeholders.
    if (MODERN_PLACEHOLDERS_HOOKED) {
      MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS = MiniPlaceholders.globalPlaceholders();
    } else {
      MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS = TagResolver.empty();
    }
  }

  public static @NotNull Component parseBoth(final @Nullable Player player, final @NotNull String text) {
    // Now that PlaceholderAPI supports components for placeholders we can avoid us the responsibility to handle manually legacy-text for
    // those placeholders.
    return PAPIComponents.setPlaceholders(player, MiniMessageParser.text(text, PlaceholderParser.parseModern()));
  }

  public static @NotNull Component[] parseBoth(final @Nullable Player player, final @NotNull String[] text) {
    final Component[] components = new Component[text.length];
    for (byte i = 0; i < text.length; i++) {
      components[i] = PlaceholderParser.parseBoth(player, text[i]);
    }
    return components;
  }

  @Deprecated(since = "4.1.0")
  @ApiStatus.ScheduledForRemoval(inVersion = "4.2.0")
  public static @NotNull TagResolver parseModern(final @Nullable Player player) {
    if (!MODERN_PLACEHOLDERS_HOOKED) {
      return TagResolver.empty();
    }
    final TagResolver.Builder builder = TagResolver.builder().resolver(MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS);
    // Should for audience specific-placeholders be included?
    if (player != null) {
      // Deprecated due to MiniPlaceholders API changes, will be removed on the next major-release.
      // -> MiniPlaceholders#audiencePlaceholders()
      // builder.resolver(MiniPlaceholders.getAudiencePlaceholders(player));
    }
    return builder.build();
  }

  public static @NotNull TagResolver parseModern() {
    if (!MODERN_PLACEHOLDERS_HOOKED) return TagResolver.empty();

    final TagResolver.Builder builder = TagResolver.builder().resolver(MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS);
    builder.resolver(MiniPlaceholders.audiencePlaceholders());
    return builder.build();
  }

  @Deprecated(since = "4.1.0")
  @ApiStatus.ScheduledForRemoval(inVersion = "4.2.0")
  public static @NotNull String parseLegacy(final @Nullable Player player, final @NotNull String text) {
    return LEGACY_PLACEHOLDERS_HOOKED ? PlaceholderAPI.setPlaceholders(player, text) : text;
  }
}
