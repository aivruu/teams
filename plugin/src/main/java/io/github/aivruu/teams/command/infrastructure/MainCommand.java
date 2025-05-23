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
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.aivruu.teams.TeamsPlugin;
import io.github.aivruu.teams.command.application.RegistrableCommandContract;
import io.github.aivruu.teams.permission.application.Permissions;
import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MainCommand implements RegistrableCommandContract {
  private final TeamsPlugin plugin;
  private final ConfigurationManager configurationManager;

  public MainCommand(
     final @NotNull TeamsPlugin plugin,
     final @NotNull ConfigurationManager configurationManager) {
    this.plugin = plugin;
    this.configurationManager = configurationManager;
  }

  @Override
  public @NotNull String id() {
    return "aldrteams";
  }

  @SuppressWarnings("UnstableApiUsage")
  @Override
  public @NotNull LiteralCommandNode<CommandSourceStack> register() {
    return Commands.literal("aldrteams")
      .executes(ctx -> Command.SINGLE_SUCCESS)
      .then(Commands.literal("help")
        .requires(src -> src.getSender().hasPermission(Permissions.HELP.node()))
        .executes(ctx -> {
          ctx.getSource().getSender().sendMessage(MiniMessageParser.list(
             this.configurationManager.messages().help));
          return Command.SINGLE_SUCCESS;
        })
      )
      .then(Commands.literal("reload")
        .requires(src -> src.getSender().hasPermission(Permissions.RELOAD.node()))
        .executes(ctx -> {
          final CommandSender sender = ctx.getSource().getSender();
          if (this.plugin.reload()) {
            sender.sendMessage(MiniMessageParser.text(this.configurationManager.messages().reloadSuccess));
          } else {
            sender.sendMessage(MiniMessageParser.text(this.configurationManager.messages().reloadError));
          }
          return Command.SINGLE_SUCCESS;
        })
      )
      .build();
  }
}
