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
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.menu.application.MenuManager;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import io.github.aivruu.teams.permission.application.Permissions;
import io.github.aivruu.teams.player.application.PlayerTagSelectorManager;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.application.modification.repository.TagModificationRepository;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class TagsCommand implements RegistrableCommandContract {
  private final ConfigurationManager configurationManager;
  private final TagManager tagManager;
  private final MenuManager menuManager;
  private final PlayerTagSelectorManager playerTagSelectorManager;
  private final TagModificationRepository tagModificationRepository;
  private final AvailableTagSuggestionProvider tagsSuggestionProvider;

  public TagsCommand(
     final @NotNull ConfigurationManager configurationManager,
     final @NotNull TagManager tagManager,
     final @NotNull MenuManager menuManager,
     final @NotNull PlayerTagSelectorManager playerTagSelectorManager,
     final @NotNull TagModificationRepository tagModificationRepository,
     final AvailableTagSuggestionProvider tagsSuggestionProvider) {
    this.configurationManager = configurationManager;
    this.tagManager = tagManager;
    this.menuManager = menuManager;
    this.playerTagSelectorManager = playerTagSelectorManager;
    this.tagModificationRepository = tagModificationRepository;
    this.tagsSuggestionProvider = tagsSuggestionProvider;
  }

  @Override
  public @NotNull String id() {
    return "tags";
  }

  @Override
  public @NotNull LiteralCommandNode<CommandSourceStack> register() {
    return Commands.literal("tags")
       .requires(src -> src.getSender() instanceof Player)
       .then(Commands.literal("selector")
          .requires(src -> src.getSender().hasPermission(Permissions.SELECT.node()))
          .executes(ctx -> {
            final Player player = (Player) ctx.getSource().getSender();
            // We ignore the boolean-result due that this menu always exists for the menu-manager.
            this.menuManager.open(player, MenuConstants.TAGS_MENU_ID);
            player.sendMessage(MiniMessageParser.text(this.configurationManager.messages().openedMenu));
            return Command.SINGLE_SUCCESS;
          })
       )
       .then(Commands.literal("unselect")
          .executes(ctx -> {
            final Player player = (Player) ctx.getSource().getSender();
            final MessagesConfigurationModel messages = this.configurationManager.messages();
            switch (this.playerTagSelectorManager.unselect(player)) {
              case PlayerTagSelectorManager.PLAYER_IS_NOT_ONLINE ->
                 player.sendMessage(MiniMessageParser.text(messages.playerUnknownInfo));
              case PlayerTagSelectorManager.THERE_IS_NO_TAG_SELECTED ->
                 player.sendMessage(MiniMessageParser.text(messages.noSelectedTag));
              case PlayerAggregateRoot.TAG_HAS_BEEN_CLEARED ->
                 player.sendMessage(MiniMessageParser.text(messages.unselected));
              default ->
                 throw new UnsupportedOperationException("Unexpected status-code for tag-unselection.");
            }
            return Command.SINGLE_SUCCESS;
          })
       )
       .then(Commands.literal("create")
          .requires(src -> src.getSender().hasPermission(Permissions.CREATE.node()))
          .executes(ctx -> {
            ctx.getSource().getSender().sendMessage(MiniMessageParser.text(
               this.configurationManager.messages().modifyUsage));
            return Command.SINGLE_SUCCESS;
          })
          .then(Commands.argument("id", StringArgumentType.word())
             .executes(ctx -> {
               ctx.getSource().getSender().sendMessage(MiniMessageParser.text(
                  this.configurationManager.messages().modifyUsage));
               return Command.SINGLE_SUCCESS;
             })
             .then(Commands.argument("prefix", StringArgumentType.string())
                .executes(ctx -> {
                  ctx.getSource().getSender().sendMessage(MiniMessageParser.text(
                     this.configurationManager.messages().modifyUsage));
                  return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("suffix", StringArgumentType.string()).executes(ctx -> {
                  final Player player = (Player) ctx.getSource().getSender();
                  final String id = ctx.getArgument("id", String.class);
                  final String prefix = ctx.getArgument("prefix", String.class);
                  final String suffix = ctx.getArgument("suffix", String.class);
                  final MessagesConfigurationModel messages = this.configurationManager.messages();
                  final boolean wasCreated = this.tagManager.createTag(player, id,
                     prefix.isEmpty() ? null : MiniMessageParser.text(prefix),
                     suffix.isEmpty() ? null : MiniMessageParser.text(suffix), NamedTextColor.WHITE);
                  if (wasCreated) {
                    player.sendMessage(MiniMessageParser.text(messages.created,
                       Placeholder.parsed("tag-id", id)));
                  } else {
                    player.sendMessage(MiniMessageParser.text(messages.alreadyExists));
                  }
                  return Command.SINGLE_SUCCESS;
                }))
             )
          )
       )
       .then(Commands.literal("edit")
          .requires(src -> src.getSender().hasPermission(Permissions.MODIFY.node()))
          .then(Commands.argument("id", StringArgumentType.word())
             .suggests(this.tagsSuggestionProvider)
             .executes(ctx -> {
               final MessagesConfigurationModel messages = this.configurationManager.messages();
               final Player player = (Player) ctx.getSource().getSender();
               final String tag = ctx.getArgument("id", String.class);
               if (!this.tagManager.exists(tag)) {
                 player.sendMessage(MiniMessageParser.text(messages.unknownTag));
                 return Command.SINGLE_SUCCESS;
               }
               final String playerId = player.getUniqueId().toString();
               if (this.tagModificationRepository.existsSync(playerId)) {
                 player.sendMessage(MiniMessageParser.text(messages.alreadyInModification));
                 return Command.SINGLE_SUCCESS;
               }
               this.tagModificationRepository.saveSync(playerId,
                  new ModificationInProgressValueObject(tag, ModificationContext.NONE));
               // We ignore the boolean-result due that this menu always exists for the menu-manager.
               this.menuManager.open(player, MenuConstants.TAGS_EDITOR_ID);
               player.sendMessage(MiniMessageParser.text(messages.openedMenu));
               return Command.SINGLE_SUCCESS;
             })
          )
       )
       .then(Commands.literal("delete")
          .requires(src -> src.getSender().hasPermission(Permissions.DELETE.node()))
          .then(Commands.argument("id", StringArgumentType.word())
             .suggests(this.tagsSuggestionProvider)
             .executes(ctx -> {
               final MessagesConfigurationModel messages = this.configurationManager.messages();
               final Player player = (Player) ctx.getSource().getSender();
               final String id = ctx.getArgument("id", String.class);
               if (this.tagManager.deleteTag(id)) {
                 player.sendMessage(MiniMessageParser.text(messages.deleted,
                    Placeholder.parsed("tag-id", id)));
               } else {
                 player.sendMessage(MiniMessageParser.text(messages.unknownTag));
               }
               return Command.SINGLE_SUCCESS;
             }))
       )
       .build();
  }
}
