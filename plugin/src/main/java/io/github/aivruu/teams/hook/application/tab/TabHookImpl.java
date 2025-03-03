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
package io.github.aivruu.teams.hook.application.tab;

import io.github.aivruu.teams.TeamsPlugin;
import io.github.aivruu.teams.hook.application.HookContract;
import io.github.aivruu.teams.hook.application.tab.listener.TABEventListener;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.tag.application.TagManager;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.nametag.NameTagManager;
import me.neznamy.tab.api.tablist.TabListFormatManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public final class TabHookImpl implements HookContract, Listener {
  private final TagManager tagManager;
  private final PlayerManager playerManager;

  public TabHookImpl(final @NotNull TagManager tagManager, final @NotNull PlayerManager playerManager) {
    this.tagManager = tagManager;
    this.playerManager = playerManager;
  }

  @Override
  public @NotNull String hookName() {
    return "TAB";
  }

  @Override
  public boolean register() {
    final PluginManager pluginManager = Bukkit.getPluginManager();
    if (pluginManager.getPlugin("TAB") == null || !pluginManager.isPluginEnabled("TAB")) {
      return false;
    }
    final TabAPI tabAPI = TabAPI.getInstance();
    final NameTagManager nameTagManager = tabAPI.getNameTagManager();
    if (nameTagManager == null) {
      return false;
    }
    final TabListFormatManager tabListFormatManager = tabAPI.getTabListFormatManager();
    if (tabListFormatManager == null) {
      return false;
    }
    Bukkit.getPluginManager().registerEvents(new TABEventListener(
      nameTagManager, tabListFormatManager,
      this.tagManager, this.playerManager
    ), TeamsPlugin.getPlugin(TeamsPlugin.class));
    return true;
  }
}
