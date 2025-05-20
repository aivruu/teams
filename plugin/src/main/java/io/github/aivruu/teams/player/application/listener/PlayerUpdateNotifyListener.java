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
package io.github.aivruu.teams.player.application.listener;

import io.github.aivruu.teams.Constants;
import io.github.aivruu.teams.util.application.UpdateChecker;
import io.github.aivruu.teams.permission.application.Permissions;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerUpdateNotifyListener implements Listener {
  private final Component[] notifyMessages = new Component[]{
     MiniMessageParser.text("<gradient:blue:green>[AldrTeams] A new update has been published!"),
     MiniMessageParser.text("<green>Current: <aqua><current></aqua>, Latest: <aqua><latest></aqua>.",
        Placeholder.parsed("current", Constants.VERSION),
        Placeholder.parsed("latest", UpdateChecker.getLatestVersion())),
     MiniMessageParser.text("<yellow><hover:show_text:'<green>Click to check github-release information.'><click:open_url:'https://github.com/aivruu/teams/releases/latest'>Check the changelog for more information!</click></hover>")
  };

  @EventHandler
  public void onJoin(final @NotNull PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    if (!player.hasPermission(Permissions.NOTIFY.node())) {
      return;
    }
    for (final Component notifyMessage : this.notifyMessages) {
      player.sendMessage(notifyMessage);
    }
  }
}
