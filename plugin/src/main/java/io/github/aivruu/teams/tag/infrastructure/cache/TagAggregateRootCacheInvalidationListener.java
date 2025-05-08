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
package io.github.aivruu.teams.tag.infrastructure.cache;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public final class TagAggregateRootCacheInvalidationListener implements RemovalListener<String, TagAggregateRoot> {
  private final TagManager tagManager;

  public TagAggregateRootCacheInvalidationListener(final @NotNull TagManager tagManager) {
    this.tagManager = tagManager;
  }

  @Override
  public void onRemoval(
    final @Nullable String key,
    final @Nullable TagAggregateRoot tagAggregateRoot,
    final @NotNull RemovalCause cause
  ) {
    if (key == null || tagAggregateRoot == null) {
      return;
    }
    if (cause != RemovalCause.EXPIRED) {
      return;
    }
    Debugger.write("Invalidating expired tag-aggregate-root with id '{}' from cache after 5 minutes.", key);
    this.tagManager.handleTagAggregateRootSave(tagAggregateRoot);
  }
}
