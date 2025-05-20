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
package io.github.aivruu.teams.tag.infrastructure.modification;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.repository.TagModificationRepository;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import io.github.aivruu.teams.tag.infrastructure.modification.cache.TagModificationCacheInvalidationListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * {@link TagModificationRepository} implementation for in-cache modifications management.
 *
 * @since 4.0.0
 */
public final class TagModificationCacheRepository implements TagModificationRepository {
  private Cache<String, ModificationInProgressValueObject> cache;

  public void buildCache(final @NotNull ConfigurationManager configurationManager) {
    if (this.cache != null) {
      return;
    }
    this.cache = Caffeine.newBuilder()
       .expireAfterWrite(15, TimeUnit.SECONDS)
       .scheduler(Scheduler.systemScheduler())
       .removalListener(new TagModificationCacheInvalidationListener(configurationManager))
       .build();
  }

  @Override
  public @Nullable ModificationInProgressValueObject findSync(final @NotNull String id) {
    return this.cache.getIfPresent(id);
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return this.cache.asMap().containsKey(id);
  }

  @Override
  public void saveSync(
     final @NotNull String id,
     final @NotNull ModificationInProgressValueObject object) {
    this.cache.put(id, object);
  }

  @Override
  public <V> void updateSync(final @NotNull String id, @NotNull final V value) {
    // As before update the context we check if there's a modification for this player, we safely
    // know that the modification is still cached and we can proceed.
    this.cache.asMap().compute(id, (key, modification) ->
       // The value always will the context, so we can cast it safely.
       new ModificationInProgressValueObject(modification.tag(), (ModificationContext) value));
  }

  @Override
  public @Nullable ModificationInProgressValueObject deleteSync(final @NotNull String id) {
    final ModificationInProgressValueObject modification = this.cache.getIfPresent(id);
    if (modification != null) {
      this.cache.invalidate(id);
    }
    return modification;
  }

  @Override
  public void clearSync() {
    this.cache.invalidateAll();
  }
}
