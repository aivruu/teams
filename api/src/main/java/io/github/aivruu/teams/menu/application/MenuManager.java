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

import io.github.aivruu.teams.menu.application.repository.MenuRepository;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to proportionate {@link AbstractMenuModel}s management.
 *
 * @since 0.0.1
 */
public final class MenuManager {
  private final MenuRepository menuRepository;

  public MenuManager(final @NotNull MenuRepository menuRepository) {
    this.menuRepository = menuRepository;
  }

  /**
   * Returns a {@link AbstractMenuModel} based on the given identifier.
   *
   * @param id the identifier of the menu.
   * @return The {@link AbstractMenuModel} or {@code null} if not exist.
   * @since 0.0.1
   */
  public @Nullable AbstractMenuModel menuModelOf(final @NotNull String id) {
    return this.menuRepository.findSync(id);
  }

  /**
   * Registers a new {@link AbstractMenuModel} to the manager and builds it.
   *
   * @param menu the menu to be registered.
   * @since 0.0.1
   */
  public void register(final @NotNull AbstractMenuModel menu) {
    this.menuRepository.saveSync(menu.id(), menu);
    // Prepare menu's [GUI] and configure its items and actions.
    menu.build();
  }

  /**
   * Unregisters an {@link AbstractMenuModel} from the manager.
   *
   * @param id the menu's id.
   * @return Whether the menu was successfully unregistered (if it exists).
   * @since 0.0.1
   */
  public boolean unregister(final @NotNull String id) {
    return this.menuRepository.deleteSync(id) != null;
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
    final AbstractMenuModel menuModel = this.menuRepository.findSync(menu);
    if (menuModel != null) {
      menuModel.open(player);
    }
    return menuModel != null;
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
    final AbstractMenuModel menuModel = this.menuRepository.findSync(menu);
    if (menuModel != null) {
      menuModel.close(player);
    }
    return menuModel != null;
  }

  /**
   * Unregisters all menus.
   *
   * @since 0.0.1
   */
  public void unregisterAll() {
    this.menuRepository.clearSync();
  }
}
