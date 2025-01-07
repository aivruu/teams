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

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to proportionate {@link MenuModelContract}s management.
 *
 * @since 0.0.1
 */
public final class MenuManagerService {
  private final Object2ObjectMap<String, MenuModelContract> menus = new Object2ObjectOpenHashMap<>();

  /**
   * Returns a {@link MenuModelContract} based on the given identifier.
   *
   * @param id the identifier of the menu.
   * @return The {@link MenuModelContract} or {@code null} if not exist.
   * @since 0.0.1
   */
  public @Nullable MenuModelContract menuModelOf(final @NotNull String id) {
    return this.menus.get(id);
  }

  /**
   * Registers a new {@link MenuModelContract} to the manager and builds it.
   *
   * @param menu the menu to be registered.
   * @since 0.0.1
   */
  public void register(final @NotNull MenuModelContract menu) {
    this.menus.put(menu.id(), menu);
    // Prepare menu's [GUI] and configure its items and actions.
    menu.build();
  }

  /**
   * Unregisters a {@link MenuModelContract} from the manager.
   *
   * @param id the menu's id.
   * @return Whether the menu was successfully unregistered (if it exists).
   * @since 0.0.1
   */
  public boolean unregister(final @NotNull String id) {
    return this.menus.remove(id) != null;
  }

  /**
   * Opens a menu for a player.
   *
   * @param player the player to open the menu.
   * @param menu the id of the menu to be opened.
   * @return Whether the menu exists.
   * @since 0.0.1
   */
  public boolean openMenu(final @NotNull Player player, final @NotNull String menu) {
    final MenuModelContract menuModel = this.menus.get(menu);
    if (menuModel == null) {
      return false;
    }
    menuModel.open(player);
    return true;
  }

  /**
   * Closes a menu for a player.
   *
   * @param player the player to close the menu.
   * @param menu the id of the menu to be closed.
   * @return Whether the menu exists.
   * @since 0.0.1
   */
  public boolean closeMenu(final @NotNull Player player, final @NotNull String menu) {
    final MenuModelContract menuModel = this.menus.get(menu);
    if (menuModel == null) {
      return false;
    }
    menuModel.close(player);
    return true;
  }

  /**
   * Unregisters all menus.
   *
   * @since 0.0.1
   */
  public void unregisterAll() {
    this.menus.clear();
  }
}
