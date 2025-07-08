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
package io.github.aivruu.teams.repository.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface DomainRepository<T> {
  /** Cached-exception for implementations that does not support all interface's methods. */
  UnsupportedOperationException NOT_IMPLEMENTED_EXCEPTION =
     new UnsupportedOperationException("This method is not implemented and is not supported.");

  /**
   * Returns the object requested if found in the repository.
   *
   * @param id the object's id.
   * @return The object or {@code null} if isn't in the repository.
   * @since 4.0.0
   */
  @Nullable T findSync(final @NotNull String id);

  /**
   * Checks if the specified object is stored by the repository.
   *
   * @param id the id for the object to store.
   * @return true if the object is stored, false otherwise.
   * @since 2.3.1
   */
  boolean existsSync(final @NotNull String id);

  /**
   * Returns all objects stored by this repository until this moment.
   * <p>
   * This method returns a {@link Collection} with all the objects that this repository contains
   * until the moment of the call, but also, it reflects when an object is added, updated or
   * removed as well, but it does not make changes to the original collection
   * (returned by {@link Map#values()}).
   *
   * @return A "viewer" {@link Collection} with the objects that this repository has.
   * @since 4.0.0
   */
  @NotNull Collection<T> findAllSync();

  /**
   * Saves the given object into the repository.
   *
   * @param id the object's id.
   * @param object the object to save.
   * @since 4.0.0
   */
  void saveSync(final @NotNull String id, final @NotNull T object);

  /**
   * Updates the mapping for the cache-entry with the specified id.
   *
   * @param id the entry's id.
   * @param value the new-value for the entry.
   * @param <V> a generic-type for the value.
   */
  <V> void updateSync(final @NotNull String id, final @NotNull V value);

  /**
   * Removes the object for the specified-id from the repository, and returns its reference.
   *
   * @param id the aggregate-root's id.
   * @return The removed object's reference or {@code null} if was not found.
   * @since 4.0.0
   */
  @Nullable T deleteSync(final @NotNull String id);

  /**
   * Deletes all the objects stored in this repository.
   *
   * @since 4.0.0
   */
  void clearSync();
}
