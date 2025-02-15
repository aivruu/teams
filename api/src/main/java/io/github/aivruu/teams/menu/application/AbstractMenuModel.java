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
package io.github.aivruu.teams.menu.application;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract-class that represents a customizable-inventory.
 *
 * @since 3.4.1
 */
public abstract class AbstractMenuModel implements InventoryHolder {
  /** The {@link NamespacedKey} used for identify any menu-model implementation's items. */
  public static final NamespacedKey MENU_ITEM_NBT_KEY = NamespacedKey.minecraft("teams_menu_item");
  private final String id;
  protected Inventory inventory;

  /**
   * Creates a new {@link AbstractMenuModel} with the given id.
   *
   * @param id this inventory's id.
   * @since 3.4.1
   */
  protected AbstractMenuModel(final @NotNull String id) {
    this.id = id;
  }

  /**
   * Returns the menu's identifier.
   *
   * @return This inventory's id.
   * @since 3.4.1
   */
  public final @NotNull String id() {
    return this.id;
  }

  /**
   * Executes the defined logic for the inventory's creation.
   *
   * @since 3.4.1
   */
  public abstract void build();

  /**
   * Handles the basic logic for when an inventory of this type is interacted.
   *
   * @param player the player who clicked the item.
   * @param clicked the item clicked, {@code null} if none.
   * @param clickType the {@link ClickType} of the interaction.
   * @return Whether the item is valid, and have the {@link #MENU_ITEM_NBT_KEY} key.
   * @since 2.4.1
   */
  public boolean handleClickLogic(final @NotNull Player player, final @Nullable ItemStack clicked, final @NotNull ClickType clickType) {
    if (clicked == null) {
      return false;
    }
    return clicked.getItemMeta().getPersistentDataContainer().has(MENU_ITEM_NBT_KEY, PersistentDataType.STRING);
  }

  /**
   * Opens this inventory to the player, if the inventory is not built yet, it will be before open it
   * to the player.
   *
   * @param player the player to who open the inventory.
   * @since 3.4.1
   */
  public void open(final @NotNull Player player) {
    if (this.inventory == null) {
      this.build();
    }
    player.openInventory(this.inventory);
  }

  /**
   * Closes the inventory for the player with the {@link InventoryCloseEvent.Reason#PLUGIN} reason.
   *
   * @param player the player to who close the inventory.
   * @since 3.4.1
   */
  public void close(final @NotNull Player player) {
    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
  }

  /**
   * Implementation method for {@link InventoryHolder} interface-contract, it will build the inventory
   * if necessary before provide it.
   *
   * @return This {@link AbstractMenuModel}'s {@link Inventory}.
   * @since 3.4.1
   */
  @Override
  public @NotNull Inventory getInventory() {
    if (this.inventory == null) {
      this.build();
    }
    return this.inventory;
  }
}
