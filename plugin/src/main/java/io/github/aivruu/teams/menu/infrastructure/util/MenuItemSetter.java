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
package io.github.aivruu.teams.menu.infrastructure.util;

import io.github.aivruu.teams.config.infrastructure.object.item.MenuItemSection;
import io.github.aivruu.teams.util.application.Debugger;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class MenuItemSetter {
  private MenuItemSetter() {
    throw new UnsupportedOperationException("This class is for utility and cannot be instantiated.");
  }

  public static void placeItem(
     final @NotNull Inventory inventory,
     final @NotNull MenuItemSection menuItem) {
    if (menuItem.slots.length == 0) {
      Debugger.write("Skipping item placing due to no-defined slots.");
      return;
    }
    // We check if this item will be placed on multiple-slots in the menu (such as decoration with
    // glass-pane items), so we can reuse its reference for the following slots' items, otherwise,
    // just create a new one and put it there.
    if (menuItem.slots.length == 1) {
      inventory.setItem(menuItem.slots[0], MenuItemCreator.prepareFrom(menuItem));
      return;
    }
    // Just a single object that will be used for multiple slots.
    final ItemStack item = MenuItemCreator.prepareFrom(menuItem);
    // [!] this log is temporal.
    Debugger.write("One item has been created.");
    for (final byte slot : menuItem.slots) {
      inventory.setItem(slot, item);
    }
  }
}
