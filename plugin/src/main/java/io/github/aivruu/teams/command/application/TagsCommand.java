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
package io.github.aivruu.teams.command.application;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.aivruu.teams.config.infrastructure.ConfigurationContainer;
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.menu.application.MenuManagerService;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.minimessage.application.MiniMessageHelper;
import io.github.aivruu.teams.permission.application.Permissions;
import io.github.aivruu.teams.player.application.PlayerTagSelectorManager;
import io.github.aivruu.teams.result.domain.ValueObjectMutationResult;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.application.TagModifierService;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class TagsCommand implements RegistrableCommandContract {
  private static final RequiredArgumentBuilder<CommandSourceStack, String> INPUT_ARGUMENT = Commands.argument("input", StringArgumentType.string());
  private final ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;
  private final TagManager tagManager;
  private final MenuManagerService menuManagerService;
  private final PlayerTagSelectorManager playerTagSelectorManager;
  private final TagModifierService tagModifierService;

  public TagsCommand(
    final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer,
    final @NotNull TagManager tagManager,
    final @NotNull MenuManagerService menuManagerService,
    final @NotNull PlayerTagSelectorManager playerTagSelectorManager,
    final @NotNull TagModifierService tagModifierService) {
    this.messagesModelContainer = messagesModelContainer;
    this.tagManager = tagManager;
    this.menuManagerService = menuManagerService;
    this.playerTagSelectorManager = playerTagSelectorManager;
    this.tagModifierService = tagModifierService;
  }

  @Override
  public @NotNull String id() {
    return "tags";
  }

  @Override
  public @NotNull LiteralCommandNode<CommandSourceStack> register() {
    return Commands.literal("tags")
      .requires(src -> src.getSender() instanceof Player player && player.hasPermission(Permissions.SELECT.node()))
      .executes(ctx -> {
        final Player player = (Player) ctx.getSource().getSender();
        // We ignore the boolean-result due that this menu always exists for the menu-manager.
        this.menuManagerService.openMenu(player, MenuConstants.TAGS_MENU_ID);
        player.sendMessage(MiniMessageHelper.text(this.messagesModelContainer.model().openedMenu));
        return Command.SINGLE_SUCCESS;
      })
      .then(Commands.literal("unselect")
        .executes(ctx -> {
          final Player player = (Player) ctx.getSource().getSender();
          final MessagesConfigurationModel messages = this.messagesModelContainer.model();
          switch (this.playerTagSelectorManager.unselect(player)) {
            case PlayerTagSelectorManager.PLAYER_IS_NOT_ONLINE ->
              player.sendMessage(MiniMessageHelper.text(messages.playerUnknownInfo));
            case PlayerTagSelectorManager.THERE_IS_NO_TAG_SELECTED ->
              player.sendMessage(MiniMessageHelper.text(messages.noSelectedTag));
            case PlayerTagSelectorManager.TAG_UNSELECTED_CORRECTLY ->
              player.sendMessage(MiniMessageHelper.text(messages.unselected));
            default -> throw new UnsupportedOperationException("Unexpected status-code for tag-unselection.");
          }
          return Command.SINGLE_SUCCESS;
        })
      )
      .then(Commands.literal("create")
        .requires(src -> src.getSender().hasPermission(Permissions.CREATE.node()))
        .executes(ctx -> {
          ctx.getSource().getSender().sendMessage(MiniMessageHelper.text(this.messagesModelContainer.model().modifyUsage));
          return Command.SINGLE_SUCCESS;
        })
        .then(Commands.argument("id", StringArgumentType.word())
          .executes(ctx -> {
            ctx.getSource().getSender().sendMessage(MiniMessageHelper.text(this.messagesModelContainer.model().modifyUsage));
            return Command.SINGLE_SUCCESS;
          })
          .then(Commands.argument("prefix", StringArgumentType.string())
            .executes(ctx -> {
              ctx.getSource().getSender().sendMessage(MiniMessageHelper.text(this.messagesModelContainer.model().modifyUsage));
              return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("suffix", StringArgumentType.string()).executes(ctx -> {
              final Player player = (Player) ctx.getSource().getSender();
              final String id = ctx.getArgument("id", String.class);
              final String prefix = ctx.getArgument("prefix", String.class);
              final String suffix = ctx.getArgument("suffix", String.class);
              final MessagesConfigurationModel messages = this.messagesModelContainer.model();
              if (!this.tagManager.createTag(player, id,
                prefix.isEmpty() ? null : MiniMessageHelper.text(prefix),
                suffix.isEmpty() ? null : MiniMessageHelper.text(suffix))
              ) {
                player.sendMessage(MiniMessageHelper.text(messages.alreadyExists));
              } else {
                player.sendMessage(MiniMessageHelper.text(messages.created, Placeholder.parsed("tag-id", id)));
              }
              return Command.SINGLE_SUCCESS;
            }))
          )
        )
      )
      .then(Commands.literal("modify")
        .requires(src -> src.getSender().hasPermission(Permissions.MODIFY.node()))
        .then(Commands.argument("id", StringArgumentType.word())
          .executes(ctx -> {
            ctx.getSource().getSender().sendMessage(MiniMessageHelper.text(this.messagesModelContainer.model().modifyUsage));
            return Command.SINGLE_SUCCESS;
          })
          .then(Commands.literal("prefix")
            .executes(ctx -> {
              ctx.getSource().getSender().sendMessage(MiniMessageHelper.text(this.messagesModelContainer.model().modifyUsage));
              return Command.SINGLE_SUCCESS;
            })
            .then(INPUT_ARGUMENT.executes(ctx -> {
              final Player player = (Player) ctx.getSource().getSender();
              final MessagesConfigurationModel messages = this.messagesModelContainer.model();
              final TagAggregateRoot tagAggregateRoot = this.tagManager.tagAggregateRootOf(ctx.getArgument("id", String.class));
              if (tagAggregateRoot == null) {
                player.sendMessage(MiniMessageHelper.text(messages.unknownTag));
                return Command.SINGLE_SUCCESS;
              }
              this.processPrefixModification(player, ctx.getArgument("input", String.class), tagAggregateRoot, messages);
              return Command.SINGLE_SUCCESS;
            }))
          )
          .then(Commands.literal("suffix")
            .executes(ctx -> {
              ctx.getSource().getSender().sendMessage(MiniMessageHelper.text(this.messagesModelContainer.model().modifyUsage));
              return Command.SINGLE_SUCCESS;
            })
            .then(INPUT_ARGUMENT.executes(ctx -> {
              final Player player = (Player) ctx.getSource().getSender();
              final MessagesConfigurationModel messages = this.messagesModelContainer.model();
              final TagAggregateRoot tagAggregateRoot = this.tagManager.tagAggregateRootOf(ctx.getArgument("id", String.class));
              if (tagAggregateRoot == null) {
                player.sendMessage(MiniMessageHelper.text(messages.unknownTag));
                return Command.SINGLE_SUCCESS;
              }
              this.processSuffixModification(player, ctx.getArgument("input", String.class), tagAggregateRoot, messages);
              return Command.SINGLE_SUCCESS;
            }))
          )
        )
      )
      .then(Commands.literal("delete")
        .requires(src -> src.getSender().hasPermission(Permissions.DELETE.node()))
        .then(Commands.argument("id", StringArgumentType.word()).executes(ctx -> {
          final Player player = (Player) ctx.getSource().getSender();
          final MessagesConfigurationModel messages = this.messagesModelContainer.model();
          final String id = ctx.getArgument("id", String.class);
          if (!this.tagManager.deleteTag(id)) {
            player.sendMessage(MiniMessageHelper.text(messages.unknownTag));
            return Command.SINGLE_SUCCESS;
          }
          player.sendMessage(MiniMessageHelper.text(messages.deleted, Placeholder.parsed("tag-id", id)));
          return Command.SINGLE_SUCCESS;
        }))
      )
      .build();
  }

  private void processPrefixModification(
    final @NotNull Player player,
    final @NotNull String input,
    final @NotNull TagAggregateRoot tagAggregateRoot,
    final @NotNull MessagesConfigurationModel messages
  ) {
    final ValueObjectMutationResult<TagPropertiesValueObject> result = this.tagModifierService.updatePrefix(
      tagAggregateRoot, input.isEmpty() ? null : MiniMessageHelper.text(input));
    if (result.wasUnchanged()) {
      player.sendMessage(MiniMessageHelper.text(messages.tagModifyError));
      return;
    }
    if (result.wasError()) {
      player.sendMessage(MiniMessageHelper.text(messages.tagModifyEventIssue));
      return;
    }
    tagAggregateRoot.tagComponentProperties(result.result());
    player.sendMessage(MiniMessageHelper.text(messages.modifiedTagPrefix));
  }

  private void processSuffixModification(
    final @NotNull Player player,
    final @NotNull String input,
    final @NotNull TagAggregateRoot tagAggregateRoot,
    final @NotNull MessagesConfigurationModel messages
  ) {
    final ValueObjectMutationResult<TagPropertiesValueObject> result = this.tagModifierService.updateSuffix(
      tagAggregateRoot, input.isEmpty() ? null : MiniMessageHelper.text(input));
    if (result.wasUnchanged()) {
      player.sendMessage(MiniMessageHelper.text(messages.tagModifyError));
      return;
    }
    if (result.wasError()) {
      player.sendMessage(MiniMessageHelper.text(messages.tagModifyEventIssue));
      return;
    }
    tagAggregateRoot.tagComponentProperties(result.result());
    player.sendMessage(MiniMessageHelper.text(messages.modifiedTagSuffix));
  }
}
