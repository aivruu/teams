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
package io.github.aivruu.teams.tag.domain.repository;

import io.github.aivruu.teams.aggregate.domain.repository.AggregateRootRepository;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link AggregateRootRepository} interface-implementation for {@link TagAggregateRoot}s.
 *
 * @since 0.0.1
 */
public interface TagAggregateRootRepository extends AggregateRootRepository<TagAggregateRoot> {
  @Override
  default <V> void updateSync(final @NotNull String id, final @NotNull V value) {
    throw NOT_IMPLEMENTED_EXCEPTION;
  }
}
