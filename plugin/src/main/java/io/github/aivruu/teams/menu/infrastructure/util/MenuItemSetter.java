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
import io.github.aivruu.teams.menu.infrastructure.MenuItemContract;
import io.github.aivruu.teams.util.application.Debugger;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class MenuItemSetter {
  private MenuItemSetter() {
    throw new UnsupportedOperationException("This class is for utility and cannot be instantiated.");
  }

  public static void placeItems(
     final @NotNull Inventory inventory,
     final @NotNull MenuItemContract @NotNull [] items) {
    ItemStack item;
    MenuItemSection itemInformation;
    for (final MenuItemContract menuItem : items) {
      itemInformation = menuItem.itemInformation();
      // We check if this item will be "duplicated" in the menu (such as decoration with glass-pane
      // items), otherwise, we reuse its [ItemStack] object to avoid create multiple objects with
      // the same information.
      if (itemInformation.slots.length == 1) {
        inventory.setItem(itemInformation.slots[0], MenuItemCreator.prepareFrom(itemInformation));
        continue;
      }
      // Just a single object that will be used for multiple slots.
      item = MenuItemCreator.prepareFrom(itemInformation);
      // [!] this log is temporal.
      Debugger.write("One item has been created.");
      for (final byte slot : itemInformation.slots) {
        inventory.setItem(slot, item);
      }
    }
  }
}
