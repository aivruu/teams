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
