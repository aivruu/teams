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

import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.tag.application.modification.repository.TagModificationRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerRegistryListener implements Listener {
  private final PlayerManager playerManager;
  private final TagModificationRepository tagModificationRepository;

  public PlayerRegistryListener(
    final @NotNull PlayerManager playerManager,
    final @NotNull TagModificationRepository tagModificationRepository) {
    this.playerManager = playerManager;
    this.tagModificationRepository = tagModificationRepository;
  }

  @EventHandler
  public void onAsyncPreLogin(final @NotNull AsyncPlayerPreLoginEvent event) {
    this.playerManager.loadOne(event.getUniqueId().toString());
  }

  @EventHandler
  public void onQuit(final @NotNull PlayerQuitEvent event) {
    final String id = event.getPlayer().getUniqueId().toString();
    this.playerManager.unloadOne(id);
    this.tagModificationRepository.deleteSync(id);
  }
}
