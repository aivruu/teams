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
package io.github.aivruu.teams.action.infrastructure;

import io.github.aivruu.teams.action.application.ActionModelContract;
import io.github.aivruu.teams.action.application.repository.ActionRepository;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public final class ActionCacheRepository implements ActionRepository {
  private final Object2ObjectMap<String, ActionModelContract> cache = new Object2ObjectOpenHashMap<>();
  private final Collection<ActionModelContract> valuesView = new ArrayList<>();

  @Override
  public @Nullable ActionModelContract findSync(final @NotNull String id) {
    return this.cache.get(id);
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return this.cache.containsKey(id);
  }

  @Override
  public @NotNull Collection<ActionModelContract> findAllSync() {
    return this.valuesView;
  }

  @Override
  public void saveSync(final @NotNull String id, final @NotNull ActionModelContract object) {
    this.cache.put(id, object);
    this.valuesView.add(object);
  }

  @Override
  public @Nullable ActionModelContract deleteSync(final @NotNull String id) {
    final ActionModelContract action = this.cache.remove(id);
    if (this.valuesView == null) {
      return null;
    }
    this.valuesView.remove(action);
    return action;
  }

  @Override
  public void clearSync() {
    this.cache.clear();
    this.valuesView.clear();
  }
}
