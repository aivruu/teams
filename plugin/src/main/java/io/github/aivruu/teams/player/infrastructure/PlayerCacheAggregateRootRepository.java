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
package io.github.aivruu.teams.player.infrastructure;

import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.repository.PlayerAggregateRootRepository;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class PlayerCacheAggregateRootRepository implements PlayerAggregateRootRepository {
  private final Object2ObjectMap<String, PlayerAggregateRoot> cache = new Object2ObjectOpenHashMap<>();

  @Override
  public @Nullable PlayerAggregateRoot findInCacheSync(final @NotNull String id) {
    return this.cache.get(id);
  }

  @Override
  public @NotNull Collection<PlayerAggregateRoot> findAllInCacheSync() {
    return this.cache.values();
  }

  @Override
  public void saveSync(final @NotNull PlayerAggregateRoot aggregateRoot) {
    this.cache.put(aggregateRoot.id(), aggregateRoot);
  }

  @Override
  public @Nullable PlayerAggregateRoot deleteSync(final @NotNull String id) {
    return this.cache.remove(id);
  }

  @Override
  public void clearAllSync() {
    this.cache.clear();
  }
}
