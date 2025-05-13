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
package io.github.aivruu.teams.tag.infrastructure.mariadb;

import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.persistence.infrastructure.utils.StatementConstants;
import io.github.aivruu.teams.shared.infrastructure.common.MariaDBInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.util.JsonCoder;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagModelEntity;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public final class TagMariaDBInfrastructureAggregateRootRepository extends MariaDBInfrastructureAggregateRootRepository<TagAggregateRoot> {
  private final Connection connectionPool;
  private final String tableName;

  public TagMariaDBInfrastructureAggregateRootRepository(
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
          StatementConstants.CREATE_TAGS_DATA_TABLE_STATEMENT.formatted(this.tableName))
        ) {
          return statement.execute();
        } catch (final SQLException exception) {
          Debugger.write("Unexpected exception when trying to create the database's tag-data table.", exception);
          return false;
        }
      }, THREAD_POOL)
      .join();
  }

  @Override
  public @NotNull CompletableFuture<@Nullable TagAggregateRoot> findAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      try (final PreparedStatement statement = this.connectionPool.prepareStatement(
        StatementConstants.FIND_TAG_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        statement.setString(1, id);
        try (final ResultSet resultSet = statement.executeQuery()){
          if (!resultSet.next()) {
            return null;
          }
          final TagPropertiesValueObject properties = JsonCoder.readProperties(resultSet.getString("properties"));
          return new TagAggregateRoot(id, new TagModelEntity(id, (properties == null) ? TagPropertiesValueObject.EMPTY : properties));
        }
      } catch (final SQLException exception) {
        Debugger.write("Unexpected exception when trying to retrieve tag's information from database.", exception);
        return null;
      }
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> existsAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      try (final PreparedStatement statement = this.connectionPool.prepareStatement(
        StatementConstants.FIND_TAG_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        statement.setString(1, id);
        try (final ResultSet resultSet = statement.executeQuery()) {
          return resultSet.next();
        }
      } catch (final SQLException exception) {
        Debugger.write("Unexpected exception when trying to verify if tag's data exists in database.", exception);
        return false;
      }
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> saveAsync(final @NotNull TagAggregateRoot aggregateRoot) {
    return CompletableFuture.supplyAsync(() -> {
      try (final PreparedStatement statement = this.connectionPool.prepareStatement(
        StatementConstants.SAVE_TAG_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        statement.setString(1, aggregateRoot.id());
        statement.setString(2, JsonCoder.writeProperties(aggregateRoot.tagModel().tagComponentProperties()));
        return statement.executeUpdate() > 0;
      } catch (final SQLException exception) {
        Debugger.write("Unexpected exception when trying to save tag's data to the database.", exception);
        return false;
      }
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> deleteAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      try (final PreparedStatement statement = this.connectionPool.prepareStatement(
        StatementConstants.DELETE_TAG_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        statement.setString(1, id);
        return statement.executeUpdate() > 0;
      } catch (final SQLException exception) {
        Debugger.write("Unexpected exception when trying to delete tag's data from the database.", exception);
        return false;
      }
    }, THREAD_POOL);
  }
}
