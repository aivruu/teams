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
package io.github.aivruu.teams.shared.infrastructure.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import io.github.aivruu.teams.shared.infrastructure.InfrastructureAggregateRootRepository;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class MongoDBInfrastructureAggregateRootRepository<A extends AggregateRoot>
   extends InfrastructureAggregateRootRepository<A> {
  protected final MongoClient client;
  protected final String databaseName;
  protected final String collectionName;
  protected MongoCollection<A> aggregateRootCollection;

  public MongoDBInfrastructureAggregateRootRepository(
     final @NotNull MongoClient client,
     final @NotNull String databaseName,
     final @NotNull String collectionName) {
    this.client = client;
    this.databaseName = databaseName;
    this.collectionName = collectionName;
  }

  @Override
  public void close() {
    this.client.close();
  }

  @Override
  public @NotNull CompletableFuture<@Nullable A> findAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() ->
       this.aggregateRootCollection.find(new Document("id", id)).first(), THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> existsAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() ->
       this.aggregateRootCollection.find(new Document("id", id)).first() != null, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> saveAsync(@NotNull final A aggregateRoot) {
    return CompletableFuture.supplyAsync(() -> {
      final UpdateResult result = this.aggregateRootCollection.updateOne(
         new Document("id", aggregateRoot.id()), new Document("$set", aggregateRoot));
      // Check if the row exists (was matched).
      return (result.getMatchedCount() == 0)
         ? this.aggregateRootCollection.insertOne(aggregateRoot).wasAcknowledged()
         : result.wasAcknowledged();
    }, THREAD_POOL);
  }

  @Override
  public @NotNull CompletableFuture<Boolean> deleteAsync(final @NotNull String id) {
    return CompletableFuture.supplyAsync(() ->
       this.aggregateRootCollection.deleteOne(new Document("id", id)).wasAcknowledged(), THREAD_POOL);
  }
}
