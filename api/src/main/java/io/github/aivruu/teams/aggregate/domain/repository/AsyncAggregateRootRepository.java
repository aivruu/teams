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

import java.util.concurrent.CompletableFuture;

/**
 * An interface-contract with defined methods required for {@link AggregateRoot}s information-handling
 * in infrastructure-side.
 *
 * @param <A> an aggregate-root type.
 * @since 0.0.1
 */
public interface AsyncAggregateRootRepository<A extends AggregateRoot> {
  /**
   * Returns the {@link AggregateRoot} for the id specified.
   *
   * @param id the aggregate-root's id.
   * @return A {@link CompletableFuture} with the {@link AggregateRoot} if found, otherwise {@code null}.
   * @since 0.0.1
   */
  @NotNull CompletableFuture<@Nullable A> findInPersistenceAsync(final @NotNull String id);

  /**
   * Checks if the {@link AggregateRoot} specified exists in repository.
   *
   * @param id the aggregate-root's id.
   * @return A {@link CompletableFuture} with a {@code boolean} value for aggregate-root existing.
   * @since 0.0.1
   */
  @NotNull CompletableFuture<Boolean> existsAsync(final @NotNull String id);

  /**
   * Saves the given {@link AggregateRoot} into the repository.
   *
   * @param aggregateRoot the aggregate-root to save.
   * @return A {@link CompletableFuture} with a {@code boolean} value for successful saving.
   * @since 0.0.1
   */
  @NotNull CompletableFuture<Boolean> saveAsync(final @NotNull A aggregateRoot);

  /**
   * Deletes the {@link AggregateRoot} specified from repository.
   *
   * @param id the aggregate-root's id.
   * @return A {@link CompletableFuture} with a {@code boolean} value for successful deletion.
   * @since 0.0.1
   */
  @NotNull CompletableFuture<Boolean> deleteAsync(final @NotNull String id);
}
