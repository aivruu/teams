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
import org.jetbrains.annotations.NotNull;

/**
 * Represents a custom-menu model.
 *
 * @since 0.0.1
 */
public interface MenuModelContract {
  /** The {@link NamespacedKey} used for identify any menu-model implementation's items. */
  NamespacedKey MENU_ITEM_NBT_KEY = NamespacedKey.minecraft("teams_menu_item");

  /**
   * Returns the identifier of this menu-model.
   *
   * @return The menu's id.
   * @since 0.0.1
   */
  @NotNull String id();

  /**
   * Builds the menu's GUI-model and configure the required items.
   *
   * @since 0.0.1
   */
  void build();

  /**
   * Opens the menu for the given player.
   *
   * @param player the player to open the menu for.
   * @since 0.0.1
   */
  void open(final @NotNull Player player);

  /**
   * Closes the menu for the given player.
   *
   * @param player the player to close the menu for.
   * @since 0.0.1
   */
  void close(final @NotNull Player player);
}
