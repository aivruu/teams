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
package io.github.aivruu.teams.placeholder.application;

import io.github.aivruu.teams.component.application.LegacyComponentHelper;
import io.github.aivruu.teams.minimessage.application.MiniMessageHelper;
import io.github.miniplaceholders.api.MiniPlaceholders;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderHelper {
  private static final TagResolver MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS;
  private static final boolean legacyPlaceholdersHooked;
  private static final boolean modernPlaceholdersHooked;
  private static final TagResolver.Builder MODERN_PLACEHOLDERS_BUILDER;

  static {
    final PluginManager pluginManager = Bukkit.getPluginManager();
    legacyPlaceholdersHooked = (pluginManager.getPlugin("PlaceholderAPI") != null && pluginManager.isPluginEnabled("PlaceholderAPI"));
    modernPlaceholdersHooked = (pluginManager.getPlugin("MiniPlaceholders") != null && pluginManager.isPluginEnabled("MiniPlaceholders"));
    // Initialize until this moment MiniPlaceholders its global-placeholders.
    if (modernPlaceholdersHooked) {
      MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS = MiniPlaceholders.getGlobalPlaceholders();
    } else {
      MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS = TagResolver.empty();
    }
    MODERN_PLACEHOLDERS_BUILDER = TagResolver.builder().resolver(MINIPLACEHOLDERS_GLOBAL_PLACEHOLDERS);
  }

  public static @NotNull Component parseBoth(final @Nullable Player player, final @NotNull String text) {
    // Firstly we parse it to [Component] for modern-placeholders processing, then we parse it to the
    // legacy-format for PlaceholderAPI placeholders correct-parsing, and finally we convert it into
    // a [Component].
    final Component modern = MiniMessageHelper.text(text, parseModern(player));
    return LegacyComponentHelper.modern(parseLegacy(player, LegacyComponentHelper.legacy(modern)));
  }

  public static @NotNull Component[] parseBoth(final @Nullable Player player, final @NotNull String[] text) {
    final Component[] components = new Component[text.length];
    for (byte i = 0; i < text.length; i++) {
      components[i] = parseBoth(player, text[i]);
    }
    return components;
  }

  public static @NotNull TagResolver parseModern(final @Nullable Player player) {
    if (!modernPlaceholdersHooked) {
      return TagResolver.empty();
    }
    // Should for audience specific-placeholders be included?
    if (player != null) {
      MODERN_PLACEHOLDERS_BUILDER.resolver(MiniPlaceholders.getAudiencePlaceholders(player));
    }
    return MODERN_PLACEHOLDERS_BUILDER.build(); // Experimental
  }

  public static @NotNull String parseLegacy(final @Nullable Player player, final @NotNull String text) {
    if (!legacyPlaceholdersHooked) {
      return text;
    }
    return PlaceholderAPI.setPlaceholders(player, text);
  }
}
