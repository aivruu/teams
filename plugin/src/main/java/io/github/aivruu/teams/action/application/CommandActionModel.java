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
package io.github.aivruu.teams.action.application;

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
    if (parameters[0].equals("PLAYER")) {
      player.performCommand(parameters[1]);
    } else if (parameters[0].equals("CONSOLE")) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parameters[1]);
    } else {
      return false;
    }
    return true;
  }
}
