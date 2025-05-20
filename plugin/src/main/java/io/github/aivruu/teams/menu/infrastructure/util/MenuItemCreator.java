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
import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.util.PlaceholderParser;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class MenuItemCreator {
  private static final ItemStack AIR_ITEM_STACK = new ItemStack(Material.AIR);

  private MenuItemCreator() {
    throw new UnsupportedOperationException("This class is for utility and cannot be instantiated.");
  }

  public static @NotNull ItemStack prepareFrom(final @NotNull MenuItemSection itemSection) {
    // Air material-type for an item in the menu shouldn't have any custom-information.
    if (itemSection.material == Material.AIR) {
      return AIR_ITEM_STACK;
    }
    final ItemStack customItem = new ItemStack(itemSection.material);
    customItem.editMeta(meta -> {
      // Provide support for support only for legacy and modern global-placeholders.
      meta.itemName(PlaceholderParser.parseBoth(null, itemSection.displayName));
      meta.lore(Arrays.asList(PlaceholderParser.parseBoth(null, itemSection.lore)));
      if (itemSection.data > 0) {
        meta.setCustomModelData(itemSection.data);
      }
      if (itemSection.glow) {
        meta.addEnchant(Enchantment.LURE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      }
      meta.getPersistentDataContainer().set(AbstractMenuModel.MENU_ITEM_NBT_KEY,
         PersistentDataType.STRING, itemSection.id);
    });
    return customItem;
  }
}
