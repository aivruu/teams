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

public final class HikariInstanceProvider {
  private static HikariDataSource hikariDataSource;

  public static @Nullable HikariDataSource get() {
    return hikariDataSource;
  }

  public static void buildDataSource(
    final @NotNull String server,
    final int port,
    final @NotNull String database,
    final @NotNull String user,
    final @NotNull String password
  ) {
    if (hikariDataSource != null) {
      return;
    }
    final HikariConfig config = new HikariConfig();
    try {
      config.setDataSourceClassName("org.mariadb.jdbc.Driver");
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
