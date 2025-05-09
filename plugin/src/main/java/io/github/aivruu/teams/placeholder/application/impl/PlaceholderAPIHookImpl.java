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
package io.github.aivruu.teams.placeholder.application.impl;

import io.github.aivruu.teams.Constants;
import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.placeholder.application.PlaceholderHookContract;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.util.PlaceholderParser;
import io.github.aivruu.teams.util.component.LegacyComponentParser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderAPIHookImpl extends PlaceholderExpansion implements PlaceholderHookContract {
  private final PlayerManager playerManager;
  private final PacketAdaptationContract packetAdaptation;

  public PlaceholderAPIHookImpl(final @NotNull PlayerManager playerManager, final @NotNull PacketAdaptationContract packetAdaptation) {
    this.playerManager = playerManager;
    this.packetAdaptation = packetAdaptation;
  }

  @Override
  public @NotNull String hookName() {
    return "PlaceholderAPI";
  }

  @Override
  public boolean hook() {
    return PlaceholderParser.LEGACY_PLACEHOLDERS_HOOKED && super.register();
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
      return this.validateTagPlaceholder(args[0], args[1]);
    }
    if (player == null) {
      return null;
    }
    // At this point the player's information should be loaded into the cache, so the model won't be null.
    final String tagId = this.playerManager.playerAggregateRootOf(player.getUniqueId().toString())
      .playerModel()
      .tag();
    if (params.equals("tag")) {
      return (tagId == null) ? "" : tagId;
    }
    return (tagId == null) ? "" : this.validateTagPlaceholder(tagId, params);
  }

  private @Nullable String validateTagPlaceholder(final @NotNull String tagId, final @NotNull String params) {
    final String prefix = LegacyComponentParser.legacy(this.packetAdaptation.teamPrefix(tagId));
    final String suffix = LegacyComponentParser.legacy(this.packetAdaptation.teamSuffix(tagId));
    return switch (params) {
      case "prefix" -> (prefix == null) ? "" : prefix;
      case "suffix" -> (suffix == null) ? "" : suffix;
      case "color" -> LegacyComponentParser.legacy(Component.text()
        .style(builder -> builder.color(this.packetAdaptation.teamColor(tagId)))
        .build());
      default -> null;
    };
  }
}
