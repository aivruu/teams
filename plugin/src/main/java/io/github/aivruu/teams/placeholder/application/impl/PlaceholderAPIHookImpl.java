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
package io.github.aivruu.teams.placeholder.application.impl;

import io.github.aivruu.teams.Constants;
import io.github.aivruu.teams.component.application.LegacyComponentHelper;
import io.github.aivruu.teams.placeholder.application.PlaceholderHookContract;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderAPIHookImpl extends PlaceholderExpansion implements PlaceholderHookContract {
  private final PlayerManager playerManager;
  private final TagManager tagManager;

  public PlaceholderAPIHookImpl(final @NotNull PlayerManager playerManager, final @NotNull TagManager tagManager) {
    this.playerManager = playerManager;
    this.tagManager = tagManager;
  }

  @Override
  public @NotNull String hookName() {
    return "PlaceholderAPI";
  }

  @Override
  public boolean hook() {
    final PluginManager pluginManager = Bukkit.getPluginManager();
    if (pluginManager.getPlugin("PlaceholderAPI") == null || !pluginManager.isPluginEnabled("PlaceholderAPI")) {
      return false;
    }
    return this.register();
  }

  @Override
  public @NotNull String getIdentifier() {
    return "aldrteams";
  }

  @Override
  public @NotNull String getAuthor() {
    return "aivruu";
  }

  @Override
  public @NotNull String getVersion() {
    return Constants.VERSION;
  }

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {
    if (params.contains("_")) {
      final String[] args = params.split("_", 2);
      final TagAggregateRoot tagAggregateRoot = this.tagManager.tagAggregateRootOf(args[0]);
      // Shouldn't be null if the player has it selected, but the tag could have been deleted prior
      // to placeholder-request.
      return (tagAggregateRoot == null)
        ? "" : this.validateTagPlaceholder(tagAggregateRoot.tagModel().tagComponentProperties(), args[1]);
    }
    if (player == null) {
      return "";
    }
    // At this point the player's information should be loaded into the cache, so the model won't be null.
    final String tagId = this.playerManager.playerAggregateRootOf(player.getUniqueId().toString())
      .playerModel()
      .tag();
    if (params.equals("tag")) {
      return tagId;
    }
    if (tagId == null) {
      return "";
    }
    final TagAggregateRoot tagAggregateRoot = this.tagManager.tagAggregateRootOf(tagId);
    // Shouldn't be null if the player has it selected, but the tag could have been deleted prior
    // to placeholder-request.
    return (tagAggregateRoot == null)
      ? "" : this.validateTagPlaceholder(tagAggregateRoot.tagModel().tagComponentProperties(), params);
  }

  private @NotNull String validateTagPlaceholder(final @NotNull TagPropertiesValueObject properties, final @NotNull String params) {
    return switch (params) {
      case "prefix" -> LegacyComponentHelper.legacy(properties.prefix());
      case "suffix" -> LegacyComponentHelper.legacy(properties.suffix());
      case "color" -> LegacyComponentHelper.legacy(Component.text()
        .style(builder -> builder.color(properties.color()))
        .build());
      default -> "";
    };
  }
}
