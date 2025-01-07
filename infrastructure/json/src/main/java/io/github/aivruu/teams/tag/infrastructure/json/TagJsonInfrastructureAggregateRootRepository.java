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
package io.github.aivruu.teams.tag.infrastructure.json;

import io.github.aivruu.teams.persistence.infrastructure.utils.JsonCodecHelper;
import io.github.aivruu.teams.shared.infrastructure.InfrastructureAggregateRootRepository;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class TagJsonInfrastructureAggregateRootRepository implements InfrastructureAggregateRootRepository<TagAggregateRoot> {
  private final Path directory;

  public TagJsonInfrastructureAggregateRootRepository(final @NotNull Path directory) {
    this.directory = directory;
  }

  @Override
  public boolean start() {
    if (Files.exists(this.directory)) {
      return true;
    }
    try {
      Files.createDirectory(this.directory);
      return true;
    } catch (final IOException exception) {
      return false;
    }
  }

  @Override
  public void close() {
    // Json implementations doesn't require to close or perform anything logic here.
  }

  @Override
  public @NotNull CompletableFuture<@Nullable TagAggregateRoot> findInPersistenceAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      final Path file = this.directory.resolve(id + ".json");
      return Files.notExists(file) ? null : JsonCodecHelper.read(file, TagAggregateRoot.class);
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> existsAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> Files.exists(this.directory.resolve(id + ".json")), THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> saveAsync(final @NotNull TagAggregateRoot aggregateRoot) {
    return CompletableFuture.supplyAsync(() -> {
      final Path file = this.directory.resolve(aggregateRoot.id() + ".json");
      if (Files.notExists(file)) {
        try {
          Files.createFile(file);
        } catch (final IOException exception) {
          return false;
        }
      }
      JsonCodecHelper.write(file, aggregateRoot);
      return true;
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> deleteAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      final Path file = this.directory.resolve(id + ".json");
      try {
        Files.delete(file);
        return true;
      } catch (final IOException exception) {
        return false;
      }
    }, THREAD_POOL);
  }
}
