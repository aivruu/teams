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
package io.github.aivruu.teams.command.infrastructure;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.aivruu.teams.command.application.RegistrableCommandContract;
import io.github.aivruu.teams.command.application.suggestion.AvailableTagSuggestionProvider;
import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.permission.application.Permissions;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class TagFetchCommand implements RegistrableCommandContract {
  private final TagManager tagManager;
  private final ConfigurationManager configurationManager;
  private final AvailableTagSuggestionProvider availableTagSuggestionProvider;

  public TagFetchCommand(
     final @NotNull TagManager tagManager,
     final @NotNull ConfigurationManager configurationManager,
     final @NotNull AvailableTagSuggestionProvider availableTagSuggestionProvider) {
    this.tagManager = tagManager;
    this.configurationManager = configurationManager;
    this.availableTagSuggestionProvider = availableTagSuggestionProvider;
  }

  @Override
  public @NotNull String id() {
    return "tagfetch";
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public @NotNull LiteralCommandNode<CommandSourceStack> register() {
    return Commands.literal("tagfetch")
       .requires(src -> src.getSender().hasPermission(Permissions.FETCH.node()))
       .then(Commands.argument("tag-id", StringArgumentType.word())
          .suggests(this.availableTagSuggestionProvider)
          .executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            final TagAggregateRoot tagAggregateRoot = this.tagManager.tagAggregateRootOf(
               ctx.getArgument("tag-id", String.class));
            if (tagAggregateRoot == null) {
              sender.sendMessage(MiniMessageParser.text(this.configurationManager.messages().unknownTag));
            } else {
              final TagPropertiesValueObject properties = tagAggregateRoot.tagModel().tagComponentProperties();
              for (final Component line : MiniMessageParser.array(
                 this.configurationManager.messages().fetchedTagInformation,
                 Placeholder.parsed("id", tagAggregateRoot.id()),
                 Placeholder.component("prefix", (properties.prefix() != null)
                    ? properties.prefix() : Component.empty()),
                 Placeholder.component("suffix", (properties.suffix() != null)
                    ? properties.suffix() : Component.empty()),
                 Placeholder.styling("color", properties.color())
              )) {
                sender.sendMessage(line);
              }
            }
            return Command.SINGLE_SUCCESS;
          })
       )
       .build();
  }
}
