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
package io.github.aivruu.teams.menu.infrastructure.repository;

import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.menu.application.repository.MenuRepository;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MenuCacheRepository implements MenuRepository {
  private final Object2ObjectMap<String, AbstractMenuModel> cache = new Object2ObjectOpenHashMap<>();

  @Override
  public @Nullable AbstractMenuModel findSync(final @NotNull String id) {
    return this.cache.get(id);
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return this.cache.containsKey(id);
  }

  @Override
  public void saveSync(final @NotNull String id, final @NotNull AbstractMenuModel object) {
    this.cache.put(object.id(), object);
  }

  @Override
  public @Nullable AbstractMenuModel deleteSync(final @NotNull String id) {
    return this.cache.remove(id);
  }

  @Override
  public void clearSync() {
    this.cache.clear();
  }
}
