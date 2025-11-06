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
import io.github.aivruu.teams.util.application.PlaceholderParser;
import io.github.miniplaceholders.api.Expansion;
import io.github.miniplaceholders.api.utils.TagsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;
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
    if (!PlaceholderParser.MODERN_PLACEHOLDERS_HOOKED) return false;

    final Expansion expansion = Expansion.builder("aldrteams")
       .author("aivruu")
       .version(Constants.VERSION)
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
    expansion.register();
    return true;
  }

  private @Nullable Tag validateTagPlaceholder(final @NotNull String tagId, final @NotNull String type) {
    return switch (type) {
      case "prefix" -> {
        final Component prefix = this.packetAdaptation.extractProperty(tagId, PacketAdaptationContract.PropertyType.PREFIX);
        yield (prefix == null) ? null : Tag.selfClosingInserting(prefix);
      }
      case "suffix" -> {
        final Component suffix = this.packetAdaptation.extractProperty(tagId, PacketAdaptationContract.PropertyType.SUFFIX);
        yield (suffix == null) ? null : Tag.selfClosingInserting(suffix);
      }
      case "color" -> {
        final NamedTextColor color = this.packetAdaptation.extractProperty(tagId, PacketAdaptationContract.PropertyType.COLOR);
        // function's result can be null if the tag doesn't exist, otherwise, white is returned by default.
        yield (color == null) ? null : Tag.styling(color);
      }
      default -> null;
    };
  }
}
