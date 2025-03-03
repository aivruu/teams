package io.github.aivruu.teams.player.infrastructure.mariadb;

import com.zaxxer.hikari.HikariDataSource;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.PlayerModelEntity;
import io.github.aivruu.teams.shared.infrastructure.CloseableInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.StatementConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public final class PlayerMariaDBInfrastructureAggregateRootRepository implements CloseableInfrastructureAggregateRootRepository<PlayerAggregateRoot> {
  private final HikariDataSource hikariDataSource;
  private final String tableName;

  public PlayerMariaDBInfrastructureAggregateRootRepository(
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
            StatementConstants.CREATE_PLAYERS_DATA_TABLE_STATEMENT.formatted(this.tableName));
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
  public @NotNull CompletableFuture<@Nullable PlayerAggregateRoot> findInPersistenceAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() -> {
      try (
        final Connection connection = this.hikariDataSource.getConnection();
        final PreparedStatement statement = connection.prepareStatement(
          StatementConstants.FIND_PLAYER_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        final ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
          return null;
        }
        final String playerId = resultSet.getString("UUID");
        final String selectedTag = resultSet.getString("tag");
        return new PlayerAggregateRoot(playerId, new PlayerModelEntity(playerId, selectedTag.isEmpty() ? null : selectedTag));
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
  public @NotNull CompletableFuture<Boolean> saveAsync(final @NotNull PlayerAggregateRoot aggregateRoot) {
    return CompletableFuture.supplyAsync(() -> {
      try (
        final Connection connection = this.hikariDataSource.getConnection();
        final PreparedStatement statement = connection.prepareStatement(
          StatementConstants.SAVE_PLAYER_INFORMATION_STATEMENT.formatted(this.tableName))
      ) {
        final String tag = aggregateRoot.playerModel().tag();
        statement.setString(1, aggregateRoot.id());
        statement.setString(2, (tag == null) ? "" : tag);
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
