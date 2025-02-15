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

import io.github.aivruu.teams.placeholder.application.PlaceholderHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MessageActionModel implements ActionModelContract {
  @Override
  public @NotNull String id() {
    return "MESSAGE";
  }

  @Override
  public boolean trigger(final @NotNull Player player, final @NotNull String[] parameters) {
    player.sendMessage(PlaceholderHelper.parseBoth(player, parameters[0]));
    return true;
  }
}
