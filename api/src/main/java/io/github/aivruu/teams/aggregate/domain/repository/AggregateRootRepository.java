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
package io.github.aivruu.teams.aggregate.domain.repository;

import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This interface-contract defines the methods that implementations must follow to
 * proportionate information-handling in-cache for {@link AggregateRoot}s.
 *
 * @param <A> an aggregate-root type.
 * @since 0.0.1
 */
public interface AggregateRootRepository<A extends AggregateRoot> {
  /**
   * Returns the {@link AggregateRoot} requested if found.
   *
   * @param id the aggregate-root's id.
   * @return A {@link AggregateRoot} or {@code null} if isn't cached.
   * @since 0.0.1
   */
  @Nullable A findInCacheSync(final @NotNull String id);

  /**
   * Returns all the current {@link AggregateRoot} cached in this repository.
   *
   * @return A {@link Collection} of {@link AggregateRoot}s.
   * @since 0.0.1
   */
  @NotNull Collection<A> findAllInCacheSync();

  /**
   * Saves the given {@link AggregateRoot} into the repository.
   *
   * @param aggregateRoot the aggregate-root to save.
   * @since 0.0.1
   */
  void saveSync(final @NotNull A aggregateRoot);

  /**
   * Removes the {@link AggregateRoot} specified from repository.
   *
   * @param id the aggregate-root's id.
   * @return The removed {@link AggregateRoot} or {@code null} if isn't cached.
   * @since 0.0.1
   */
  @Nullable A deleteSync(final @NotNull String id);

  /**
   * Clears the repository from all {@link AggregateRoot}s.
   *
   * @since 0.0.1
   */
  void clearAllSync();
}
