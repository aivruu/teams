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
package io.github.aivruu.teams.shared.infrastructure;

import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import io.github.aivruu.teams.aggregate.domain.repository.AsyncAggregateRootRepository;

import java.util.concurrent.Executor;

/**
 * An infrastructure aggregate-root repository model.
 *
 * @param <A> an aggregate-root type.
 * @since 0.0.1
 */
public interface InfrastructureAggregateRootRepository<A extends AggregateRoot> extends AsyncAggregateRootRepository<A> {
  /** The plugin's thread-pool used for this async-operations. */
  Executor THREAD_POOL = ExecutorHelper.pool();

  /**
   * Executes this repository its start-up logic.
   *
   * @return Whether the infrastructure-repository was successfully started.
   * @since 0.0.1
   */
  boolean start();

  /**
   * An enum representing the type of infrastructure-repositories available.
   *
   * @since 0.0.1
   */
  enum Type {
    JSON, MONGODB, MARIADB
  }
}
