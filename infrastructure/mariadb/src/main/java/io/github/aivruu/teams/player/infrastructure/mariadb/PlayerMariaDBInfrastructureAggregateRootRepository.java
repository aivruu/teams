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
package io.github.aivruu.teams.player.infrastructure.mariadb;

import io.github.aivruu.teams.logger.application.DebugLoggerHelper;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.PlayerModelEntity;
import io.github.aivruu.teams.persistence.infrastructure.utils.StatementConstants;
import io.github.aivruu.teams.shared.infrastructure.common.CommonMariaDBInfrastructureAggregateRootRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public final class PlayerMariaDBInfrastructureAggregateRootRepository extends CommonMariaDBInfrastructureAggregateRootRepository<PlayerAggregateRoot> {
  private final Connection connectionPool;
  private final String tableName;

  public PlayerMariaDBInfrastructureAggregateRootRepository(
    final @NotNull Connection connectionPool,
    final @NotNull String tableName) {
    this.connectionPool = connectionPool;
    this.tableName = tableName;
  }

  @Override
  public boolean start() {
    return CompletableFuture
      .supplyAsync(() -> {
        try (final PreparedStatement statement = this.connectionPool.prepareStatement(
          StatementConstants.CREATE_PLAYERS_DATA_TABLE_STATEMENT.formatted(this.tableName))
        ) {
          return statement.execute();
        } catch (final SQLException exception) {
          DebugLoggerHelper.write("Unexpected exception when trying to create the database's player-data table.", exception);
          return false;
        }
      }, THREAD_POOL)
      .join();
  }

  @Override
  public @NotNull CompletableFuture<@Nullable PlayerAggregateRoot> findInPersistenceAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      try (final PreparedStatement statement = this.connectionPool.prepareStatement(
        StatementConstants.FIND_PLAYER_INFORMATION_STATEMENT.formatted(this.tableName));
      ) {
        statement.setString(1, id);
        try (final ResultSet resultSet = statement.executeQuery()) {
          if (!resultSet.next()) {
            return null;
          }
          final String selectedTag = resultSet.getString(1);
          return new PlayerAggregateRoot(id, new PlayerModelEntity(id, selectedTag.isEmpty() ? null : selectedTag));
        }
      } catch (final SQLException exception) {
        DebugLoggerHelper.write("Unexpected exception when trying to retrieve player's information from database.", exception);
        return null;
      }
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> existsAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      try (final PreparedStatement statement = this.connectionPool.prepareStatement(
        StatementConstants.FIND_PLAYER_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        statement.setString(1, id);
        try (final ResultSet resultSet = statement.executeQuery()) {
          return resultSet.next();
        }
      } catch (final SQLException exception) {
        DebugLoggerHelper.write("Unexpected exception when trying to verify if player's data exists in database.", exception);
        return false;
      }
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> saveAsync(final @NotNull PlayerAggregateRoot aggregateRoot) {
    return CompletableFuture.supplyAsync(() -> {
      try (final PreparedStatement statement = this.connectionPool.prepareStatement(
        StatementConstants.SAVE_PLAYER_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        final String tag = aggregateRoot.playerModel().tag();
        statement.setString(1, aggregateRoot.id());
        statement.setString(2, (tag == null) ? "" : tag);
        return statement.executeUpdate() > 0;
      } catch (final SQLException exception) {
        DebugLoggerHelper.write("Unexpected exception when trying to save player's data to the database.", exception);
        return false;
      }
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> deleteAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      try (final PreparedStatement statement = this.connectionPool.prepareStatement(
        StatementConstants.DELETE_PLAYER_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        statement.setString(1, id);
        return statement.executeUpdate() > 0;
      } catch (final SQLException exception) {
        DebugLoggerHelper.write("Unexpected exception when trying to delete player's data from the database.", exception);
        return false;
      }
    }, THREAD_POOL);
  }
}
