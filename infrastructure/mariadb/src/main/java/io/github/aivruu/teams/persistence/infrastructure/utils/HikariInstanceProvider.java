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
package io.github.aivruu.teams.persistence.infrastructure.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.aivruu.teams.logger.application.DebugLoggerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This utility-class is used as {@link HikariDataSource} instance builder and provider.
 *
 * @since 3.5.1
 */
public final class HikariInstanceProvider {
  private static final String MARIADB_SOURCE_CLASS_NAME = "org.mariadb.jdbc.Driver";
  private static @Nullable HikariDataSource hikariDataSource;

  /**
   * Returns the {@link HikariDataSource} instance if is available.
   *
   * @return The {@link HikariDataSource} or {@code null} if it couldn't be initialized or is closed.
   * @since 3.5.1
   */
  public static @Nullable HikariDataSource get() {
    return (hikariDataSource == null || hikariDataSource.isClosed()) ? null : hikariDataSource;
  }

  /**
   * Returns the connection-pool used by HikariCP for the database.
   *
   * @return The {@link Connection} pool or {@code null} if data-source couldn't be initialized or
   * a {@link SQLException} was thrown when trying to get the connection.
   * @since 3.5.1
   */
  public static @Nullable Connection connection() {
    if (hikariDataSource == null) {
      return null;
    }
    try {
      return hikariDataSource.getConnection();
    } catch (final SQLException exception) {
      DebugLoggerHelper.write("Unexpected exception when trying to retrieve the database's connection.", exception);
      return null;
    }
  }

  /**
   * Creates a new {@link HikariDataSource} instance with the given parameters and assigns it to the
   * {@link #hikariDataSource} field if parameters were valid. If the data-source it's already built, the
   * method will skip the logic.
   *
   * @param server the database's server.
   * @param port the database's port.
   * @param database the database's name.
   * @param user the database's username.
   * @param password the database's password.
   * @since 3.5.1
   */
  public static void buildDataSource(
    final @NotNull String server,
    final int port,
    final @NotNull String database,
    final @NotNull String user,
    final @NotNull String password
  ) {
    if (hikariDataSource != null) return;

    final HikariConfig config = new HikariConfig();
    try {
      config.setDataSourceClassName(MARIADB_SOURCE_CLASS_NAME);
      config.addDataSourceProperty("serverName", server);
      config.addDataSourceProperty("port", port);
      config.addDataSourceProperty("databaseName", database);
      config.addDataSourceProperty("user", user);
      config.addDataSourceProperty("password", password);
      hikariDataSource = new HikariDataSource(config);
    } catch (final IllegalStateException exception) {
      DebugLoggerHelper.write("Unexpected exception when trying to build a new HikariDataSource object.", exception);
    }
  }
}
