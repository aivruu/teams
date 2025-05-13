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
package io.github.aivruu.teams.player.infrastructure.json;

import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.shared.infrastructure.json.JsonInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.util.JsonCoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class PlayerJsonInfrastructureAggregateRootRepository
   extends JsonInfrastructureAggregateRootRepository<PlayerAggregateRoot> {
  public PlayerJsonInfrastructureAggregateRootRepository(final @NotNull Path directory) {
    super(directory);
  }

  @Override
  public @NotNull CompletableFuture<@Nullable PlayerAggregateRoot> findAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      final Path file = this.directory.resolve(id + ".json");
      return Files.notExists(file) ? null : JsonCoder.read(file, PlayerAggregateRoot.class);
    }, THREAD_POOL);
  }
}
