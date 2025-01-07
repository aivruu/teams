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
package io.github.aivruu.teams.tag.domain;

import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link AggregateRoot} implementation for {@link TagModelEntity}.
 *
 * @since 0.0.1
 */
public final class TagAggregateRoot extends AggregateRoot {
  private final TagModelEntity tagModel;

  /**
   * Creates a new {@link TagAggregateRoot} with the provided parameters.
   *
   * @param id the aggregate-root's id.
   * @param tagModel this aggregate-root's {@link TagModelEntity} object.
   * @since 0.0.1
   */
  public TagAggregateRoot(final @NotNull String id, final @NotNull TagModelEntity tagModel) {
    super(id);
    this.tagModel = tagModel;
  }

  /**
   * Returns the {@link TagModelEntity}.
   *
   * @return This aggregate-root's {@link TagModelEntity}.
   * @since 0.0.1
   */
  public @NotNull TagModelEntity tagModel() {
    return this.tagModel;
  }

    /**
     * Sets new properties for the tag-model.
     *
     * @param tagComponentProperties the tag's new {@link TagPropertiesValueObject}.
     * @since 0.0.1
     */
  public void tagComponentProperties(final @NotNull TagPropertiesValueObject tagComponentProperties) {
    this.tagModel.tagComponentProperties(tagComponentProperties);
  }
}
