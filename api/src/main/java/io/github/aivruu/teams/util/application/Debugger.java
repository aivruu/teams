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
package io.github.aivruu.teams.util.application;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A utility used for plugin debug-messages function.
 *
 * @since 4.0.0
 */
public final class Debugger {
  private static final ComponentLogger LOGGER = ComponentLogger.logger("Teams-Debugger");
  private static boolean enabled;

  private Debugger() {
    throw new UnsupportedOperationException("This class is for utility.");
  }

  /**
   * Enable or disable the debug-mode.
   *
   * @param enable whether debug-mode should be enabled or disabled.
   * @since 0.0.1
   */
  public static void enable(final boolean enable) {
    enabled = enable;
  }

  /**
   * Prints the debug-message with the given content and replace-values in the console,
   * only if debug-mode is enabled.
   *
   * @param message the message to print.
   * @param values the message's values to replace (marked with {@code {}}).
   * @since 0.0.1
   */
  public static void write(final @NotNull String message, final @NotNull Object... values) {
    if (enabled) {
      LOGGER.info(Component.text(message), values);
    }
  }
}
