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
package io.github.aivruu.teams;

import io.github.aivruu.teams.action.application.ActionManager;
import io.github.aivruu.teams.menu.application.MenuManager;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.player.application.PlayerTagSelectorManager;
import io.github.aivruu.teams.player.domain.registry.PlayerAggregateRootRegistry;
import io.github.aivruu.teams.player.domain.repository.PlayerAggregateRootRepository;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.application.modification.repository.TagModificationRepository;
import io.github.aivruu.teams.tag.application.modification.TagModificationProcessor;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import io.github.aivruu.teams.tag.domain.repository.TagAggregateRootRepository;
import org.jetbrains.annotations.NotNull;

/**
 * An interface-contract with the methods required to access to the API classes'
 * instances.
 *
 * @since 0.0.1
 */
public interface Teams {
  /**
   * Returns the {@link TagAggregateRootRepository} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link TagAggregateRootRepository}.
   * @since 0.0.1
   */
  @NotNull TagAggregateRootRepository tagCacheRepository();

  /**
   * Returns the {@link TagAggregateRootRegistry} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link TagAggregateRootRegistry}.
   * @since 0.0.1
   */
  @NotNull TagAggregateRootRegistry tagsRegistry();

  /**
   * Returns the {@link TagManager} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link TagManager}.
   * @since 0.0.1
   */
  @NotNull TagManager tagManager();

  /**
   * Returns the {@link TagModificationRepository} instance, will throw an
   * {@link IllegalStateException} if the instance is not initialized.
   *
   * @return The {@link TagModificationRepository}.
   * @since 2.3.1
   */
  @NotNull TagModificationRepository tagModificationRepository();

  /**
   * Returns the {@link TagModificationProcessor} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link TagModificationProcessor}.
   * @since 2.3.1
   */
  @NotNull TagModificationProcessor tagModificationProcessor();

  /**
   * Returns the {@link PlayerAggregateRootRepository} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link PlayerAggregateRootRepository}.
   * @since 0.0.1
   */
  @NotNull PlayerAggregateRootRepository playerCacheRepository();

  /**
   * Returns the {@link PlayerAggregateRootRegistry} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link PlayerAggregateRootRegistry}.
   * @since 0.0.1
   */
  @NotNull PlayerAggregateRootRegistry playersRegistry();

  /**
   * Returns the {@link PlayerManager} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link PlayerManager}.
   * @since 0.0.1
   */
  @NotNull PlayerManager playerManager();

  /**
   * Returns the {@link PlayerTagSelectorManager} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link PlayerTagSelectorManager}.
   * @since 0.0.1
   */
  @NotNull PlayerTagSelectorManager playerTagSelectorManager();

  /**
   * Returns the {@link MenuManager} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link MenuManager}.
   * @since 0.0.1
   */
  @NotNull MenuManager menuManagerService();

  /**
   * Returns the {@link ActionManager} instance, will throw an {@link IllegalStateException}
   * if the instance is not initialized.
   *
   * @return The {@link ActionManager}.
   * @since 0.0.1
   */
  @NotNull ActionManager actionManager();
}
