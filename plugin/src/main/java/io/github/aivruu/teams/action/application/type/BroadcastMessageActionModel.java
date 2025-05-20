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
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BroadcastMessageActionModel implements ActionModelContract {
  @Override
  public @NotNull String id() {
    return "BROADCAST";
  }

  @Override
  public boolean trigger(final @NotNull Player player, final @NotNull String[] parameters) {
    if (parameters.length < 2) {
      return false;
    }
    // Properly specific-to-player placeholders shouldn't be used for this kind of message.
    final Component message = PlaceholderParser.parseBoth(player, parameters[1]);
    final String type = parameters[0];
    if (type.equals("GLOBAL")) {
      for (final Player serverPlayer : player.getServer().getOnlinePlayers()) {
        serverPlayer.sendMessage(message);
      }
      return true;
    }
    if (type.equals("LOCAL")) {
      for (final Player worldPlayer : player.getWorld().getPlayers()) {
        worldPlayer.sendMessage(message);
      }
      return true;
    }
    return false;
  }
}
