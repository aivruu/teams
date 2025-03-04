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

public final class StatementConstants {
  public static final String CREATE_PLAYERS_DATA_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s(UUID varchar(36), tag VARCHAR(10))";
  public static final String CREATE_TAGS_DATA_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s(id varchar(36), properties VARCHAR(255))";
  public static final String FIND_PLAYER_INFORMATION_STATEMENT = "SELECT tag FROM %s WHERE UUID=?";
  public static final String FIND_TAG_INFORMATION_STATEMENT = "SELECT properties FROM %s WHERE id=?";
  public static final String SAVE_TAG_INFORMATION_STATEMENT = "INSERT INTO %s(id, properties) VALUES(?, ?)";
  public static final String SAVE_PLAYER_INFORMATION_STATEMENT = "INSERT INTO %s(UUID, tag) VALUES(?, ?)";
  public static final String DELETE_TAG_INFORMATION_STATEMENT = "DELETE FROM %s WHERE id=?";
  public static final String DELETE_PLAYER_INFORMATION_STATEMENT = "DELETE FROM %s WHERE UUID=?";

  private StatementConstants() {
    throw new UnsupportedOperationException("This class cannot be instantiated.");
  }
}
