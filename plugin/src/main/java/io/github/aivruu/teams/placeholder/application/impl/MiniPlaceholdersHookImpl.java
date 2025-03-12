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

import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.placeholder.application.PlaceholderHookContract;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.miniplaceholders.api.Expansion;
import io.github.miniplaceholders.api.utils.TagsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MiniPlaceholdersHookImpl implements PlaceholderHookContract {
  private final PlayerManager playerManager;
  private final PacketAdaptationContract packetAdaptation;

  public MiniPlaceholdersHookImpl(final @NotNull PlayerManager playerManager, final @NotNull PacketAdaptationContract packetAdaptation) {
    this.playerManager = playerManager;
    this.packetAdaptation = packetAdaptation;
  }

  @Override
  public @NotNull String hookName() {
    return "MiniPlaceholders";
  }

  @Override
  public boolean hook() {
    final PluginManager pluginManager = Bukkit.getPluginManager();
    if (pluginManager.getPlugin("MiniPlaceholders") == null || !pluginManager.isPluginEnabled("MiniPlaceholders")) {
      return false;
    }
    final Expansion expansion = Expansion.builder("aldrteams")
      .filter(Player.class)
      .audiencePlaceholder("tag", (audience, queue, ctx) -> {
        // At this point the player's information should be loaded into the cache, so the model won't be null.
        final String tagId = this.playerManager.playerAggregateRootOf(((Player) audience).getUniqueId().toString())
          .playerModel()
          .tag();
        return (tagId == null) ? TagsUtils.EMPTY_TAG : Tag.selfClosingInserting(Component.text(tagId));
      })
      .audiencePlaceholder("prefix", (audience, queue, ctx) -> {
        // At this point the player's information should be loaded into the cache, so the model won't be null.
        final String tagId = this.playerManager.playerAggregateRootOf(((Player) audience).getUniqueId().toString())
          .playerModel()
          .tag();
        return (tagId == null) ? TagsUtils.EMPTY_TAG : this.validateTagPlaceholder(tagId, "prefix");
      })
      .audiencePlaceholder("suffix", (audience, queue, ctx) -> {
        // At this point the player's information should be loaded into the cache, so the model won't be null.
        final String tagId = this.playerManager.playerAggregateRootOf(((Player) audience).getUniqueId().toString())
          .playerModel()
          .tag();
        return (tagId == null) ? TagsUtils.EMPTY_TAG : this.validateTagPlaceholder(tagId, "suffix");
      })
      .audiencePlaceholder("color", (audience, queue, ctx) -> {
        // At this point the player's information should be loaded into the cache, so the model won't be null.
        final String tagId = this.playerManager.playerAggregateRootOf(((Player) audience).getUniqueId().toString())
          .playerModel()
          .tag();
        return (tagId == null) ? TagsUtils.EMPTY_TAG : this.validateTagPlaceholder(tagId, "color");
      })
      .build();
    // Register the expansion to MiniPlaceholders.
    expansion.register();
    return true;
  }

  private @Nullable Tag validateTagPlaceholder(final @NotNull String tagId, final @NotNull String type) {
    final Component prefix = this.packetAdaptation.teamPrefix(tagId);
    final Component suffix = this.packetAdaptation.teamSuffix(tagId);
    return switch (type) {
      case "prefix" -> (prefix == null) ? null : Tag.selfClosingInserting(prefix);
      case "suffix" -> (suffix == null) ? null : Tag.selfClosingInserting(suffix);
      case "color" -> Tag.styling(builder -> builder.color(this.packetAdaptation.teamColor(tagId)));
      default -> null;
    };
  }
}
