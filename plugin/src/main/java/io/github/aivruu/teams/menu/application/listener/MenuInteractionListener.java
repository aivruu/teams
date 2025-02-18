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
package io.github.aivruu.teams.menu.application.listener;

import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.jetbrains.annotations.NotNull;

public final class MenuInteractionListener implements Listener {
  @EventHandler
  public void onInventoryClick(final @NotNull InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof AbstractMenuModel menuModel)) {
      return;
    }
    if (menuModel.handleClickLogic((Player) event.getWhoClicked(), event.getCurrentItem(), event.getClick()) == null) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onInventoryDrag(final @NotNull InventoryMoveItemEvent event) {
    if (!(event.getInitiator().getHolder() instanceof AbstractMenuModel) || !(event.getDestination().getHolder() instanceof AbstractMenuModel)) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
    if (!(event.getInventory().getHolder() instanceof AbstractMenuModel)) {
      return;
    }
    event.setCancelled(true);
  }
}
