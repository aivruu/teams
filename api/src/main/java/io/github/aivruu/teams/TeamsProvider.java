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
package io.github.aivruu.teams;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility used for {@link Teams} instances providing.
 *
 * @since 0.0.1
 */
public final class TeamsProvider {
  private static @Nullable Teams instance;

  private TeamsProvider() {
    throw new UnsupportedOperationException("This class is for utility.");
  }

  /**
   * Returns the {@link Teams} instance, will throw an {@link IllegalStateException} if the
   * instance hasn't been initialized yet.
   *
   * @return the {@link Teams} instance
   * @since 0.0.1
   */
  public static @NotNull Teams get() {
    if (instance == null) {
      throw new IllegalStateException("Plugin's API instance hasn't been initialized yet.");
    }
    return instance;
  }

  /**
   * Sets a new {@link Teams} instance for instance-field, will throw an {@link IllegalStateException}
   * if the instance it's already initialized.
   *
   * @param impl a {@link Teams} implementation.
   * @since 0.0.1
   */
  public static void set(final @NotNull Teams impl) {
    if (instance != null) {
      throw new IllegalStateException("Plugin's API instance has been already initialized.");
    }
    instance = impl;
  }
}
