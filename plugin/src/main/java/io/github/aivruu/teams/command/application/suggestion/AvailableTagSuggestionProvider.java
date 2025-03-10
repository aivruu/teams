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
package io.github.aivruu.teams.command.application.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.aivruu.teams.permission.application.Permissions;
import io.github.aivruu.teams.tag.application.TagManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public final class AvailableTagSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
  private final TagManager tagManager;

  public AvailableTagSuggestionProvider(final @NotNull TagManager tagManager) {
    this.tagManager = tagManager;
  }

  @Override
  public @NotNull CompletableFuture<@NotNull Suggestions> getSuggestions(
    final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder
  ) {
    final CommandSender sender = context.getSource().getSender();
    // This suggestion-provider is supposed to be used for 'edit' and 'delete' subcommands, so we should
    // check by these permissions for the sender before suggesting.
    if (!sender.hasPermission(Permissions.MODIFY.node()) || !sender.hasPermission(Permissions.DELETE.node())) {
      return builder.buildFuture();
    }
    for (final String tag : this.tagManager.findAllTagIds()) {
      builder.suggest(tag);
    }
    return builder.buildFuture();
  }
}
