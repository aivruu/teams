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
package io.github.aivruu.teams.action.application;

import io.github.aivruu.teams.logger.application.DebugLoggerHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * This class is used to proportionate {@link ActionModelContract}s storing and execution-handling.
 *
 * @since 0.0.1
 */
public final class ActionManager {
  private final Object2ObjectMap<String, ActionModelContract> actions = new Object2ObjectOpenHashMap<>();

  /**
   * Returns a {@link ActionModelContract} depart the given identifier.
   *
   * @param id the action's id.
   * @return The {@link ActionModelContract} or {@code null} if not found.
   * @since 0.0.1
   */
  public @Nullable ActionModelContract actionModelOf(final @NotNull String id) {
    return this.actions.get(id);
  }

  /**
   * Registers the given {@link ActionModelContract} into the action-manager's registry.
   *
   * @param action the action to register.
   * @since 0.0.1
   */
  public void register(final @NotNull ActionModelContract action) {
    this.actions.put(action.id(), action);
  }

  /**
   * Executes the action specified by the given action-container.
   *
   * @param player the player who triggered the action.
   * @param action the action's input.
   * @since 0.0.1
   */
  public void execute(final @NotNull Player player, final @NotNull String action) {
    if (action.isEmpty()) {
      return;
    }
    final ActionModelContract actionModel = this.actions.get(
      StringUtils.substringBetween(action, "[", "]").toUpperCase(Locale.ROOT));
    if (actionModel == null) {
      DebugLoggerHelper.write("Unknown action-type specified, skipping execution.");
      return;
    }
    // We give the method the action's arguments without the identifier, and we check if it was executed
    // successfully.
    if (!actionModel.trigger(player, action.substring(actionModel.id().length() + 3).split(";"))) {
      DebugLoggerHelper.write("The action {}'s execution has failed.", actionModel.id());
    }
  }

  /**
   * Unregisters the specified {@link ActionModelContract}.
   *
   * @param id the action's id.
   * @return Whether the action was registered prior deletion.
   * @since 0.0.1
   */
  public boolean unregister(final @NotNull String id) {
    return this.actions.remove(id) != null;
  }

  /**
   * Unregisters all the registered {@link ActionModelContract}s.
   *
   * @since 0.0.1
   */
  public void unregisterAll() {
    this.actions.clear();
  }
}
