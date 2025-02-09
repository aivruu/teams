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
package io.github.aivruu.teams.persistence.infrastructure;

import com.mongodb.client.MongoClient;
import io.github.aivruu.teams.config.infrastructure.object.ConfigurationConfigurationModel;
import io.github.aivruu.teams.logger.application.DebugLoggerHelper;
import io.github.aivruu.teams.persistence.infrastructure.utils.MongoClientHelper;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.infrastructure.json.PlayerJsonInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.player.infrastructure.mongodb.PlayerMongoInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.CloseableInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.InfrastructureAggregateRootRepository;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.infrastructure.json.TagJsonInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.tag.infrastructure.mongodb.TagMongoInfrastructureAggregateRootRepository;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class InfrastructureRepositoryController {
  private final Path dataFolder;
  private final ConfigurationConfigurationModel configuration;
  private InfrastructureAggregateRootRepository<PlayerAggregateRoot> playerInfrastructureAggregateRootRepository;
  private InfrastructureAggregateRootRepository<TagAggregateRoot> tagInfrastructureAggregateRootRepository;
  private InfrastructureAggregateRootRepository.Type playerInfrastructureRepositoryType;
  private InfrastructureAggregateRootRepository.Type tagInfrastructureRepositoryType;

  public InfrastructureRepositoryController(final @NotNull Path dataFolder, final @NotNull ConfigurationConfigurationModel configuration) {
    this.dataFolder = dataFolder;
    this.configuration = configuration;
  }

  private void validateInfrastructureType() {
    // Players infrastructure-repository type validation.
    if (this.configuration.playerInfrastructureRepositoryType.equals("JSON")) {
      this.playerInfrastructureRepositoryType = InfrastructureAggregateRootRepository.Type.JSON;
    } else if (this.configuration.playerInfrastructureRepositoryType.equals("MONGODB")) {
      this.playerInfrastructureRepositoryType = InfrastructureAggregateRootRepository.Type.MONGODB;
    } else {
      this.playerInfrastructureRepositoryType = InfrastructureAggregateRootRepository.Type.JSON;
    }
    // Tags infrastructure-repository type validation.
    if (this.configuration.tagInfrastructureRepositoryType.equals("JSON")) {
      this.tagInfrastructureRepositoryType = InfrastructureAggregateRootRepository.Type.JSON;
    } else if (this.configuration.playerInfrastructureRepositoryType.equals("MONGODB")) {
      this.tagInfrastructureRepositoryType = InfrastructureAggregateRootRepository.Type.MONGODB;
    } else {
      this.tagInfrastructureRepositoryType = InfrastructureAggregateRootRepository.Type.JSON;
    }
  }

  public boolean selectAndInitialize() {
    this.validateInfrastructureType();
    MongoClient client = null;
    // MongoClient instance initialization if any repository require it.
    if (this.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MONGODB ||
      this.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MONGODB
    ) {
      DebugLoggerHelper.write("Initializing mongo-client instance with configuration's parameters.");
      MongoClientHelper.buildClient(this.configuration.mongoHost, this.configuration.mongoUsername, this.configuration.mongoDatabase, this.configuration.mongoPassword);
      client = MongoClientHelper.client();
      // Check if parameters are valid and client was initialized correctly.
      if (client == null) {
        DebugLoggerHelper.write("Mongo-client couldn't be initialized correctly, stopping infrastructure repositories initialization.");
        return false;
      }
    }
    this.playerInfrastructureAggregateRootRepository = (this.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.JSON)
      ? new PlayerJsonInfrastructureAggregateRootRepository(this.dataFolder.resolve(this.configuration.playerCollectionAndDirectoryName))
      : new PlayerMongoInfrastructureAggregateRootRepository(client, this.configuration.mongoDatabase, this.configuration.playerCollectionAndDirectoryName);
    this.tagInfrastructureAggregateRootRepository = (this.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.JSON)
      ? new TagJsonInfrastructureAggregateRootRepository(this.dataFolder.resolve(this.configuration.tagCollectionAndDirectoryName))
      : new TagMongoInfrastructureAggregateRootRepository(client, this.configuration.mongoDatabase, this.configuration.tagCollectionAndDirectoryName);
    return this.playerInfrastructureAggregateRootRepository.start() && this.tagInfrastructureAggregateRootRepository.start();
  }

  public void close() {
    if (this.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MONGODB) {
      ((CloseableInfrastructureAggregateRootRepository<PlayerAggregateRoot>) this.playerInfrastructureAggregateRootRepository)
        .close();
    }
    if (this.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MONGODB) {
      ((CloseableInfrastructureAggregateRootRepository<TagAggregateRoot>) this.tagInfrastructureAggregateRootRepository)
        .close();
    }
  }

  public @NotNull InfrastructureAggregateRootRepository<PlayerAggregateRoot> playerInfrastructureAggregateRootRepository() {
    return this.playerInfrastructureAggregateRootRepository;
  }

  public @NotNull InfrastructureAggregateRootRepository<TagAggregateRoot> tagInfrastructureAggregateRootRepository() {
    return this.tagInfrastructureAggregateRootRepository;
  }
}
