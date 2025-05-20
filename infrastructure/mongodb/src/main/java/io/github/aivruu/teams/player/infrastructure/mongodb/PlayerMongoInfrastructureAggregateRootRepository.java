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
package io.github.aivruu.teams.player.infrastructure.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.shared.infrastructure.mongodb.MongoDBInfrastructureAggregateRootRepository;
import org.jetbrains.annotations.NotNull;

public final class PlayerMongoInfrastructureAggregateRootRepository
   extends MongoDBInfrastructureAggregateRootRepository<PlayerAggregateRoot> {
  public PlayerMongoInfrastructureAggregateRootRepository(
     final @NotNull MongoClient client,
     final @NotNull String databaseName,
     final @NotNull String collectionName) {
    super(client, databaseName, collectionName);
  }

  @Override
  public boolean start() {
    final MongoDatabase database;
    try {
      database = this.client.getDatabase(this.databaseName);
    } catch (final IllegalArgumentException exception) {
      return false;
    }
    super.aggregateRootCollection = database.getCollection(this.collectionName,
       PlayerAggregateRoot.class);
    return true;
  }
}
