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
package io.github.aivruu.teams.tag.infrastructure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.repository.TagAggregateRootRepository;
import io.github.aivruu.teams.tag.infrastructure.cache.TagAggregateRootCacheInvalidationListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public final class TagCacheAggregateRootRepository implements TagAggregateRootRepository {
  private Cache<String, TagAggregateRoot> cache;

  public void buildCache(final @NotNull TagManager tagManager) {
    this.cache = Caffeine.newBuilder()
      .expireAfterWrite(5, TimeUnit.MINUTES)
      .scheduler(Scheduler.systemScheduler())
      .removalListener(new TagAggregateRootCacheInvalidationListener(tagManager))
      .build();
  }

  @Override
  public @Nullable TagAggregateRoot findInCacheSync(final @NotNull String id) {
    return this.cache.getIfPresent(id);
  }

  @Override
  public @NotNull Collection<TagAggregateRoot> findAllInCacheSync() {
    return this.cache.asMap().values();
  }

  @Override
  public void saveSync(final @NotNull TagAggregateRoot aggregateRoot) {
    this.cache.put(aggregateRoot.id(), aggregateRoot);
  }

  @Override
  public @Nullable TagAggregateRoot deleteSync(final @NotNull String id) {
    final TagAggregateRoot tagAggregateRoot = this.cache.getIfPresent(id);
    if (tagAggregateRoot != null) {
      this.cache.invalidate(id);
    }
    return tagAggregateRoot;
  }

  @Override
  public void clearAllSync() {
    this.cache.invalidateAll();
  }
}

