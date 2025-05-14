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
package io.github.aivruu.teams.menu.application;

import io.github.aivruu.teams.action.application.ActionManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
  protected final String id;
  protected final ActionManager actionManager;
  protected Inventory inventory;

  /**
   * Creates a new {@link AbstractMenuModel} with the given id.
   *
   * @param id            this inventory's id.
   * @param actionManager the {@link ActionManager} reference.
   * @since 3.4.1
   */
  protected AbstractMenuModel(
     final @NotNull String id,
     final @NotNull ActionManager actionManager) {
    this.id = id;
    this.actionManager = actionManager;
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
   * @param player    the player who clicked the item.
   * @param clicked   the item clicked, {@code null} if none.
   * @param clickType the {@link ClickType} of the interaction.
   * @return A {@link ProcessedMenuItemValueObject} with the item's id and meta-data, or
   * {@code null} if the click was out of the inventory, or the item was air.
   * @since 4.0.0
   */
  public @Nullable ProcessedMenuItemValueObject handleClickLogic(
     final @NotNull Player player,
     final @Nullable ItemStack clicked,
     final @NotNull ClickType clickType) {
    if (clicked == null || clicked.getType() == Material.AIR) {
      return null;
    }
    final ItemMeta meta = clicked.getItemMeta();
    final String id = meta.getPersistentDataContainer()
       .get(MENU_ITEM_NBT_KEY, PersistentDataType.STRING);
    return (id == null) ? null : new ProcessedMenuItemValueObject(id, clicked.getItemMeta());
  }

  /**
   * Executes the menu's clicked-item item's actions depending on the click-type involved in the
   * event.
   *
   * @param player            the player who executed the actions.
   * @param clickType         the event's {@link ClickType}.
   * @param leftClickActions  the item's left-click actions.
   * @param rightClickActions the item's right-click actions.
   * @since 3.4.1
   */
  public final void processItemActions(
     final @NotNull Player player,
     final @NotNull ClickType clickType,
     final @NotNull String[] leftClickActions,
     final @NotNull String[] rightClickActions) {
    for (final String action : (clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT)
       ? leftClickActions : rightClickActions) {
      this.actionManager.execute(player, action);
    }
  }

  /**
   * Opens this inventory to the player, if the inventory is not built yet, it will be before open
   * it to the player.
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
   * {@inheritDoc}
   * <p>
   * It will build the inventory if necessary before provide it.
   */
  @Override
  public @NotNull Inventory getInventory() {
    if (this.inventory == null) {
      this.build();
    }
    return this.inventory;
  }
}
