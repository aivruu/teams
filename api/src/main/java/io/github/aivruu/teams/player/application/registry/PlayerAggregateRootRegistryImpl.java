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
package io.github.aivruu.teams.player.application.registry;

import io.github.aivruu.teams.aggregate.domain.repository.AsyncAggregateRootRepository;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.registry.PlayerAggregateRootRegistry;
import io.github.aivruu.teams.player.domain.repository.PlayerAggregateRootRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public final class PlayerAggregateRootRegistryImpl implements PlayerAggregateRootRegistry {
  private final PlayerAggregateRootRepository playerAggregateRootRepository;
  private final AsyncAggregateRootRepository<PlayerAggregateRoot> playerAsyncAggregateRootRepository;

  public PlayerAggregateRootRegistryImpl(
    final @NotNull PlayerAggregateRootRepository playerAggregateRootRepository,
    final @NotNull AsyncAggregateRootRepository<PlayerAggregateRoot> playerAsyncAggregateRootRepository) {
    this.playerAggregateRootRepository = playerAggregateRootRepository;
    this.playerAsyncAggregateRootRepository = playerAsyncAggregateRootRepository;
  }

  @Override
  public @Nullable PlayerAggregateRoot findInCache(final @NotNull String id) {
    return this.playerAggregateRootRepository.findInCacheSync(id);
  }

  @Override
  public @NotNull CompletableFuture<PlayerAggregateRoot> findInInfrastructure(final @NotNull String id) {
    return this.playerAsyncAggregateRootRepository.findInPersistenceAsync(id);
  }

  @Override
  public @NotNull Collection<PlayerAggregateRoot> findAllInCache() {
    return this.playerAggregateRootRepository.findAllInCacheSync();
  }

  @Override
  public boolean existsGlobally(final @NotNull String id) {
    return this.playerAggregateRootRepository.existsInCacheSync(id)
      || this.playerAsyncAggregateRootRepository.existsAsync(id).join();
  }

  @Override
  public boolean existsInCache(final @NotNull String id) {
    return this.playerAggregateRootRepository.existsInCacheSync(id);
  }

  @Override
  public boolean existsInInfrastructure(final @NotNull String id) {
    return this.playerAsyncAggregateRootRepository.existsAsync(id).join();
  }

  @Override
  public void register(final @NotNull PlayerAggregateRoot aggregateRoot) {
    this.playerAggregateRootRepository.saveSync(aggregateRoot);
  }

  @Override
  public @Nullable PlayerAggregateRoot unregister(final @NotNull String id) {
    return this.playerAggregateRootRepository.deleteSync(id);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> delete(final @NotNull String id) {
    return this.playerAsyncAggregateRootRepository.deleteAsync(id);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> save(final @NotNull PlayerAggregateRoot aggregateRoot) {
    return this.playerAsyncAggregateRootRepository.saveAsync(aggregateRoot);
  }
}
