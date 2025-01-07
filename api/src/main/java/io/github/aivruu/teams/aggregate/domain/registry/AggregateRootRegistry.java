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
package io.github.aivruu.teams.aggregate.domain.registry;

import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * This interface defines the set of methods that subclasses must implement to perform
 * any operation with {@link AggregateRoot}'s registries related.
 *
 * @param <A> an aggregate-root type.
 * @since 0.0.1
 */
public interface AggregateRootRegistry<A extends AggregateRoot> {
  /**
   * Returns the {@link AggregateRoot} specified from the cache if found.
   *
   * @param id the aggregate-root's identifier.
   * @return The {@link AggregateRoot} or {@code null} if not found in cache.
   * @since 0.0.1
   */
  @Nullable A findInCache(final @NotNull String id);

  /**
   * Returns the {@link AggregateRoot} specified from the cache-repository if found,
   * otherwise it will search at infrastructure-repository by aggregate-root's information
   * and will return it if found.
   *
   * @param id the aggregate-root's identifier.
   * @return The {@link AggregateRoot} or {@code null} if not exists.
   * @since 0.0.1
   */
  @Nullable A findInBoth(final @NotNull String id);

  /**
   * Returns the {@link AggregateRoot} specified from the infrastructure-repository if found.
   *
   * @param id the aggregate-root's identifier.
   * @return A {@link CompletableFuture} with the {@link AggregateRoot} or {@code null} if not found in
   * infrastructure.
   * @since 0.0.1
   */
  @NotNull CompletableFuture<@Nullable A> findInInfrastructure(final @NotNull String id);

  /**
   * Returns a {@link Collection} with the registry's currently cached {@link AggregateRoot}s.
   *
   * @return A {@link Collection} with the cached {@link AggregateRoot}s.
   * @since 0.0.1
   */
  @NotNull Collection<A> findAllInCache();

  /**
   * Checks if the aggregate-root specified is cached or saved at the infrastructure.
   *
   * @param id the aggregate-root's identifier.
   * @return Whether the aggregate-root is cached or at the infrastructure.
   * @since 0.0.1
   */
  boolean existsGlobally(final @NotNull String id);

  /**
   * Checks if the aggregate-root specified is cached.
   *
   * @param id the aggregate-root's identifier.
   * @return Whether the aggregate-root is cached.
   * @since 0.0.1
   */
  boolean existsInCache(final @NotNull String id);

  /**
   * Checks if the aggregate-root specified exists in the infrastructure.
   *
   * @param id the aggregate-root's identifier.
   * @return Whether the aggregate-root is in the infrastructure.
   * @since 0.0.1
   */
  boolean existsInInfrastructure(final @NotNull String id);

  /**
   * Stores the given {@link AggregateRoot} into cache.
   *
   * @param aggregateRoot the {@link AggregateRoot} to store.
   * @since 0.0.1
   */
  void register(final @NotNull A aggregateRoot);

  /**
   * Removes the {@link AggregateRoot} mapping from cache-repository and return
   * its reference.
   *
   * @param id the aggregate-root's identifier.
   * @return The {@link AggregateRoot} or {@code null} if no found.
   * @since 0.0.1
   */
  @Nullable A unregister(final @NotNull String id);

  /**
   * Deletes the {@link AggregateRoot}'s information from infrastructure.
   *
   * @param id the aggregate-root's identifier.
   * @return A {@link CompletableFuture} with the operation's result.
   * @since 0.0.1
   */
  @NotNull CompletableFuture<Boolean> delete(final @NotNull String id);

  /**
   * Saves the given {@link AggregateRoot} into the infrastructure.
   *
   * @param aggregateRoot the {@link AggregateRoot} to save.
   * @return A {@link CompletableFuture} with the operation's result.
   * @since 0.0.1
   */
  @NotNull CompletableFuture<Boolean> save(final @NotNull A aggregateRoot);
}
