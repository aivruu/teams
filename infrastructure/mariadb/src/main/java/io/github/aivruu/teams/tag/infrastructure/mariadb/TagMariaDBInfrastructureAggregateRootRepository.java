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
package io.github.aivruu.teams.tag.infrastructure.mariadb;

import com.zaxxer.hikari.HikariDataSource;
import io.github.aivruu.teams.shared.infrastructure.CloseableInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.StatementConstants;
import io.github.aivruu.teams.shared.infrastructure.util.JsonCodecHelper;
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

public final class TagMariaDBInfrastructureAggregateRootRepository implements CloseableInfrastructureAggregateRootRepository<TagAggregateRoot> {
  private final HikariDataSource hikariDataSource;
  private final String tableName;

  public TagMariaDBInfrastructureAggregateRootRepository(
    final @NotNull HikariDataSource hikariDataSource,
    final @NotNull String tableName) {
    this.hikariDataSource = hikariDataSource;
    this.tableName = tableName;
  }

  @Override
  public boolean start() {
    return CompletableFuture
      .supplyAsync(() -> {
        try (
          final Connection connection = this.hikariDataSource.getConnection();
          final PreparedStatement statement = connection.prepareStatement(
            StatementConstants.CREATE_TAGS_DATA_TABLE_STATEMENT.formatted(this.tableName))
        ) {
          return statement.execute();
        } catch (final SQLException exception) {
          exception.printStackTrace();
          return false;
        }
      }, THREAD_POOL)
      .join();
  }

  @Override
  public void close() {
    this.hikariDataSource.close();
  }

  @Override
  public @NotNull CompletableFuture<@Nullable TagAggregateRoot> findInPersistenceAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      try (
        final Connection connection = this.hikariDataSource.getConnection();
        final PreparedStatement statement = connection.prepareStatement(
          StatementConstants.FIND_TAG_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        final ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
          return null;
        }
        final TagPropertiesValueObject properties = JsonCodecHelper.readProperties(resultSet.getString("properties"));
        if (properties == null) {
          return null;
        }
        final String tag = resultSet.getString("id");
        return new TagAggregateRoot(tag, new TagModelEntity(tag, properties));
      } catch (final SQLException exception) {
        exception.printStackTrace();
        return null;
      }
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> existsAsync(final @NotNull String id) {
    return null;
  }

  @Override
  public @NotNull CompletableFuture<Boolean> saveAsync(final @NotNull TagAggregateRoot aggregateRoot) {
    return CompletableFuture.supplyAsync(() -> {
      try (
        final Connection connection = this.hikariDataSource.getConnection();
        final PreparedStatement statement = connection.prepareStatement(
          StatementConstants.SAVE_TAG_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        final TagModelEntity tagModel = aggregateRoot.tagModel();
        statement.setString(1, JsonCodecHelper.writeProperties(tagModel.tagComponentProperties()));
        statement.setString(2, aggregateRoot.id());
        return statement.executeUpdate() >= 1;
      } catch (final SQLException exception) {
        exception.printStackTrace();
        return false;
      }
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> deleteAsync(final @NotNull String id) {
    return null;
  }
}
