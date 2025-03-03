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
package io.github.aivruu.teams.hook.application.tab.listener;

import io.github.aivruu.teams.component.application.LegacyComponentHelper;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.player.domain.PlayerModelEntity;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;
import me.neznamy.tab.api.nametag.NameTagManager;
import me.neznamy.tab.api.tablist.TabListFormatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TABEventListener implements Listener {
  private final NameTagManager nameTagManager;
  private final TabListFormatManager tabListFormatManager;
  private final TagManager tagManager;
  private final PlayerManager playerManager;

  public TABEventListener(
    final @NotNull NameTagManager nameTagManager,
    final @NotNull TabListFormatManager tabListFormatManager,
    final @NotNull TagManager tagManager,
    final @NotNull PlayerManager playerManager) {
    this.nameTagManager = nameTagManager;
    this.tabListFormatManager = tabListFormatManager;
    this.tagManager = tagManager;
    this.playerManager = playerManager;
  }

  @EventHandler
  public void onPlayerLoad(final @NotNull PlayerLoadEvent event) {
    final TabPlayer tabPlayer = event.getPlayer();
    // To this time, as the player is loaded by TAB, also should be loaded in-cache by our plugin.
    final PlayerModelEntity playerModel = this.playerManager.playerAggregateRootOf(tabPlayer.getUniqueId().toString())
      .playerModel();
    if (playerModel.tag() == null) {
      return;
    }
    final TagAggregateRoot tagAggregateRoot = this.tagManager.tagAggregateRootOf(playerModel.tag());
    // May tag was deleted while the player still have it selected?
    if (tagAggregateRoot == null) {
      return;
    }
    final TagPropertiesValueObject properties = tagAggregateRoot.tagModel().tagComponentProperties();
    final String prefix = LegacyComponentHelper.legacy(properties.prefix());
    final String suffix = LegacyComponentHelper.legacy(properties.suffix());
    this.nameTagManager.setPrefix(tabPlayer, prefix);
    this.nameTagManager.setSuffix(tabPlayer, suffix);
    this.tabListFormatManager.setPrefix(tabPlayer, prefix);
    this.tabListFormatManager.setSuffix(tabPlayer, suffix);
  }
}
