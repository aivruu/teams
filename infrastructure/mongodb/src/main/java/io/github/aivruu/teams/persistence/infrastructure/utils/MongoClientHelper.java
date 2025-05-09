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
package io.github.aivruu.teams.persistence.infrastructure.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.aivruu.teams.player.infrastructure.mongodb.codec.MongoPlayerAggregateRootCodec;
import io.github.aivruu.teams.tag.infrastructure.mongodb.codec.MongoTagAggregateRootCodec;
import io.github.aivruu.teams.tag.infrastructure.mongodb.codec.MongoTagPropertiesValueObjectCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This utility-class is used to provide {@link MongoClient} instances-building easily.
 *
 * @since 1.0.1
 */
public final class MongoClientHelper {
  private static @Nullable MongoClient client;

  private MongoClientHelper() {
    throw new UnsupportedOperationException("This class is for utility and cannot be instantiated.");
  }

  /**
   * Creates a new {@link MongoClient} instance providing a {@link MongoClientSettings} with the given
   * information parameters.
   *
   * @param host the mongo-db server's host.
   * @param username the database username.
   * @param database the database's displayName.
   * @param password the password for authentication.
   * @since 1.0.1
   */
  public static void buildClient(
    final @NotNull String host,
    final @NotNull String username,
    final @NotNull String database,
    final @NotNull String password
  ) {
    if (client != null) {
      return;
    }
    final MongoClientSettings clientSettings = MongoClientSettings.builder()
      .applyConnectionString(new ConnectionString("mongodb://" + host))
      .credential(MongoCredential.createCredential(username, database, password.toCharArray()))
      .codecRegistry(CodecRegistries.fromCodecs(
        MongoPlayerAggregateRootCodec.INSTANCE,
        MongoTagPropertiesValueObjectCodec.INSTANCE,
        MongoTagAggregateRootCodec.INSTANCE
      ))
      .build();
    client = MongoClients.create(clientSettings);
  }

  /**
   * Returns the {@link MongoClient} instance, if {@code client} wasn't built this function will
   * return {@code null}.
   *
   * @return The {@link MongoClient} or {@code null}.
   * @since 1.0.1
   */
  public static @Nullable MongoClient client() {
    return client;
  }
}
