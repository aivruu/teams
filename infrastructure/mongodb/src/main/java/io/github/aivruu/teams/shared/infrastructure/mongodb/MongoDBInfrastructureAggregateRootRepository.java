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
