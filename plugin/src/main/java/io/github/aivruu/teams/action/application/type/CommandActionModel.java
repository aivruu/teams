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
package io.github.aivruu.teams.action.application.type;

import io.github.aivruu.teams.action.application.ActionModelContract;
import io.github.aivruu.teams.util.application.PlaceholderParser;
import io.github.aivruu.teams.util.application.component.PlainComponentParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CommandActionModel implements ActionModelContract {
  @Override
  public @NotNull String id() {
    return "COMMAND";
  }

  @Override
  public boolean trigger(final @NotNull Player player, final @NotNull String[] parameters) {
    if (parameters.length < 2) {
      return false;
    }
    final String type = parameters[0];
    // The message could include specific message or placeholders.
    // Not available MiniPlaceholders support here, by now.
    final String command = PlainComponentParser.plain(
       PlaceholderParser.parseBoth(player, parameters[1]));
    if (type.equals("PLAYER")) {
      player.performCommand(command);
    } else if (type.equals("CONSOLE")) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    } else {
      return false;
    }
    return true;
  }
}
