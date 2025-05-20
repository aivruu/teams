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
package io.github.aivruu.teams.tag.application;

import io.github.aivruu.teams.aggregate.domain.repository.AsyncAggregateRootRepository;
import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import io.github.aivruu.teams.tag.domain.repository.TagAggregateRootRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public final class TagAggregateRootRegistryImpl implements TagAggregateRootRegistry {
  private final TagAggregateRootRepository tagAggregateRootRepository;
  private final AsyncAggregateRootRepository<TagAggregateRoot> tagAsyncAggregateRootRepository;

  public TagAggregateRootRegistryImpl(
    final @NotNull TagAggregateRootRepository tagAggregateRootRepository,
    final @NotNull AsyncAggregateRootRepository<TagAggregateRoot> tagAsyncAggregateRootRepository) {
    this.tagAggregateRootRepository = tagAggregateRootRepository;
    this.tagAsyncAggregateRootRepository = tagAsyncAggregateRootRepository;
  }

  @Override
  public @Nullable TagAggregateRoot findInCache(final @NotNull String id) {
    return this.tagAggregateRootRepository.findSync(id);
  }

  @Override
  public @Nullable TagAggregateRoot findInBoth(final @NotNull String id) {
    TagAggregateRoot tagAggregateRoot = this.tagAggregateRootRepository.findSync(id);
    if (tagAggregateRoot != null) {
      return tagAggregateRoot;
    }
    tagAggregateRoot = this.tagAsyncAggregateRootRepository.findAsync(id)
      .exceptionally(exception -> {
        Debugger.write("Unexpected exception during in-infrastructure tag fetching with id '{}'.",
           id, exception);
        return null;
      })
      .join();
    if (tagAggregateRoot != null) {
      this.tagAggregateRootRepository.saveSync(tagAggregateRoot.id(), tagAggregateRoot);
    }
    return tagAggregateRoot;
  }

  @Override
  public @NotNull CompletableFuture<@Nullable TagAggregateRoot> findInInfrastructure(
     final @NotNull String id) {
    return this.tagAsyncAggregateRootRepository.findAsync(id);
  }

  @Override
  public @NotNull Collection<TagAggregateRoot> findAllInCache() {
    return this.tagAggregateRootRepository.findAllSync();
  }

  @Override
  public boolean existsGlobally(final @NotNull String id) {
    return this.tagAggregateRootRepository.existsSync(id)
       || this.tagAsyncAggregateRootRepository.existsAsync(id).join();
  }

  @Override
  public boolean existsInCache(final @NotNull String id) {
    return this.tagAggregateRootRepository.existsSync(id);
  }

  @Override
  public boolean existsInInfrastructure(final @NotNull String id) {
    // We need to await for async-operation to complete for correct-result getting.
    return this.tagAsyncAggregateRootRepository.existsAsync(id).join();
  }

  @Override
  public void register(final @NotNull TagAggregateRoot aggregateRoot) {
    this.tagAggregateRootRepository.saveSync(aggregateRoot.id(), aggregateRoot);
  }

  @Override
  public @Nullable TagAggregateRoot unregister(final @NotNull String id) {
    return this.tagAggregateRootRepository.deleteSync(id);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> delete(final @NotNull String id) {
    return this.tagAsyncAggregateRootRepository.deleteAsync(id);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> save(final @NotNull TagAggregateRoot aggregateRoot) {
    return this.tagAsyncAggregateRootRepository.saveAsync(aggregateRoot);
  }
}
