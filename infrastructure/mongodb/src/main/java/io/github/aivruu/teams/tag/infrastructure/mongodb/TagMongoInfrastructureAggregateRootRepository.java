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
package io.github.aivruu.teams.tag.infrastructure.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.aivruu.teams.shared.infrastructure.ExecutorHelper;
import io.github.aivruu.teams.shared.infrastructure.InfrastructureAggregateRootRepository;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class TagMongoInfrastructureAggregateRootRepository implements InfrastructureAggregateRootRepository<TagAggregateRoot> {
  private final MongoClient client;
  private final String databaseName;
  private final String collectionName;
  private MongoCollection<TagAggregateRoot> tagAggregateRootMongoCollection;

  public TagMongoInfrastructureAggregateRootRepository(
    final MongoClient client,
    final String databaseName,
    final String collectionName) {
    this.client = client;
    this.databaseName = databaseName;
    this.collectionName = collectionName;
  }

  @Override
  public boolean start() {
    try {
      final MongoDatabase database = this.client.getDatabase(this.databaseName);
      this.tagAggregateRootMongoCollection = database.getCollection(this.collectionName, TagAggregateRoot.class);
      return true;
    } catch (final IllegalArgumentException exception) {
      return false;
    }
  }

  @Override
  public void close() {
    this.client.close();
  }

  @Override
  public @NotNull CompletableFuture<@Nullable TagAggregateRoot> findInPersistenceAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() ->
      this.tagAggregateRootMongoCollection.find(new Document("id", id)).first(), THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> existsAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() ->
      this.tagAggregateRootMongoCollection.find(new Document("id", id)).first() != null, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> saveAsync(final @NotNull TagAggregateRoot aggregateRoot) {
    return CompletableFuture.supplyAsync(() ->
      this.tagAggregateRootMongoCollection.insertOne(aggregateRoot).wasAcknowledged(), THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> deleteAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() ->
      this.tagAggregateRootMongoCollection.deleteOne(new Document("id", id)).wasAcknowledged(), THREAD_POOL);
  }
}
