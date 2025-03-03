package io.github.aivruu.teams.shared.infrastructure;

public final class StatementConstants {
  public static final String CREATE_PLAYERS_DATA_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s(UUID varchar(36), tag VARCHAR(10))";
  public static final String CREATE_TAGS_DATA_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s(id varchar(36), properties VARCHAR(255))";
  public static final String FIND_PLAYER_INFORMATION_STATEMENT = "SELECT * FROM %s WHERE UUID=?";
  public static final String FIND_TAG_INFORMATION_STATEMENT = "SELECT * FROM %s WHERE id=?";
  public static final String SAVE_TAG_INFORMATION_STATEMENT = "UPDATE %s SET properties=? WHERE id=?";
  public static final String SAVE_PLAYER_INFORMATION_STATEMENT = "UPDATE %s SET id=? WHERE tag=?";

  private StatementConstants() {
    throw new UnsupportedOperationException("This class cannot be instantiated.");
  }
}
