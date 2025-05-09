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
package io.github.aivruu.teams.shared.infrastructure;

import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import io.github.aivruu.teams.aggregate.domain.repository.AsyncAggregateRootRepository;
import io.github.aivruu.teams.util.application.PluginExecutor;

import java.util.concurrent.ExecutorService;

/**
 * An infrastructure aggregate-root repository model.
 *
 * @param <A> an aggregate-root type.
 * @since 0.0.1
 */
public abstract class InfrastructureAggregateRootRepository<A extends AggregateRoot> implements AsyncAggregateRootRepository<A> {
  /** The plugin's thread-pool used for this async-operations. */
  public static final ExecutorService THREAD_POOL = PluginExecutor.get();

  /**
   * Executes this repository its start-up logic.
   *
   * @return Whether the infrastructure-repository was successfully started.
   * @since 0.0.1
   */
  public abstract boolean start();

  /**
   * Closes the global thread-pool.
   *
   * @since 4.0.0
   */
  public void close() {
    THREAD_POOL.close();
  }

  /**
   * An enum representing the type of infrastructure-repositories available.
   *
   * @since 0.0.1
   */
  public enum Type {
    JSON, MONGODB, MARIADB
  }
}
