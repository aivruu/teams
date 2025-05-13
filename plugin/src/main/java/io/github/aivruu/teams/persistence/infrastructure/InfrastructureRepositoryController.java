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
package io.github.aivruu.teams.persistence.infrastructure;

import com.mongodb.client.MongoClient;
import io.github.aivruu.teams.config.infrastructure.object.ConfigurationConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.persistence.infrastructure.utils.HikariInstanceProvider;
import io.github.aivruu.teams.persistence.infrastructure.utils.MongoClientHelper;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.infrastructure.json.PlayerJsonInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.player.infrastructure.json.codec.JsonPlayerAggregateRootCodec;
import io.github.aivruu.teams.player.infrastructure.mariadb.PlayerMariaDBInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.player.infrastructure.mongodb.PlayerMongoInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.InfrastructureAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.util.JsonCoder;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.infrastructure.json.TagJsonInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.tag.infrastructure.json.codec.JsonTagAggregateRootCodec;
import io.github.aivruu.teams.tag.infrastructure.json.codec.JsonTagPropertiesValueObjectCodec;
import io.github.aivruu.teams.tag.infrastructure.mariadb.TagMariaDBInfrastructureAggregateRootRepository;
import io.github.aivruu.teams.tag.infrastructure.mongodb.TagMongoInfrastructureAggregateRootRepository;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.sql.Connection;

public final class InfrastructureRepositoryController {
  private final Path dataFolder;
  private final ConfigurationManager configurationManager;
  private InfrastructureAggregateRootRepository<PlayerAggregateRoot> playerInfrastructureAggregateRootRepository;
  private InfrastructureAggregateRootRepository<TagAggregateRoot> tagInfrastructureAggregateRootRepository;

  public InfrastructureRepositoryController(
     final @NotNull Path dataFolder,
     final ConfigurationManager configurationManager) {
    this.dataFolder = dataFolder;
    this.configurationManager = configurationManager;
  }

  public boolean selectAndInitialize() {
    final ConfigurationConfigurationModel config = this.configurationManager.config();
    if (config.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.JSON ||
        config.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.JSON) {
      JsonCoder.buildWithAdapters(
        JsonTagAggregateRootCodec.INSTANCE, JsonTagPropertiesValueObjectCodec.INSTANCE, JsonPlayerAggregateRootCodec.INSTANCE);
    }
    // Database clients initialization if repositories require it.
    if (config.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MONGODB ||
        config.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MONGODB
    ) {
      Debugger.write("Initializing mongo-client instance with configuration's parameters.");
      MongoClientHelper.buildClient(config.host, config.username, config.database, config.password);
      // Check if parameters are valid and client was initialized correctly.
      if (MongoClientHelper.client() == null) {
        Debugger.write("Mongo-client couldn't be initialized correctly, stopping infrastructure repositories initialization.");
        return false;
      }
    }
    if (config.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MARIADB ||
        config.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MARIADB
    ) {
      Debugger.write("Initializing mongo-client instance with configuration's parameters.");
      HikariInstanceProvider.buildDataSource(config.host, config.mariaDbPort, config.username,
         config.database, config.password);
      // Check if parameters are valid.
      if (HikariInstanceProvider.get() == null) {
        Debugger.write("HikariDataSource couldn't be initialized correctly, stopping infrastructure repositories initialization.");
        return false;
      }
    }
    return this.determineAndInitializeInfrastructureTypes(config);
  }

  private boolean determineAndInitializeInfrastructureTypes(
     final @NotNull ConfigurationConfigurationModel config) {
    final Connection connection = HikariInstanceProvider.connection();
    final MongoClient client = MongoClientHelper.client();
    this.playerInfrastructureAggregateRootRepository = switch (config.playerInfrastructureRepositoryType) {
      case JSON -> new PlayerJsonInfrastructureAggregateRootRepository(
        this.dataFolder.resolve(config.playerCollectionAndDirectoryName));
      // Note: The client-instance could be null, but it won't throw a NullPointerException as this method is called
      // only when MongoDB infrastructure is required, at that point, the client, or it was initialized already, or
      // parameters were invalid and repository won't be initialized.
      case MONGODB -> new PlayerMongoInfrastructureAggregateRootRepository(
        client, config.database, config.playerCollectionAndDirectoryName);
      case MARIADB -> new PlayerMariaDBInfrastructureAggregateRootRepository(
        // HikariDataSource instance shouldn't be null if repository-type is for MariaDB.
        connection, config.playerCollectionAndDirectoryName);
    };
    this.tagInfrastructureAggregateRootRepository = switch (config.tagInfrastructureRepositoryType) {
      case JSON -> new TagJsonInfrastructureAggregateRootRepository(
        this.dataFolder.resolve(config.tagCollectionAndDirectoryName));
      case MONGODB -> new TagMongoInfrastructureAggregateRootRepository(
        client, config.database, config.tagCollectionAndDirectoryName);
      case MARIADB -> new TagMariaDBInfrastructureAggregateRootRepository(
        connection, config.tagCollectionAndDirectoryName);
    };
    return this.playerInfrastructureAggregateRootRepository.start()
       && this.tagInfrastructureAggregateRootRepository.start();
  }

  public void close() {
    if (this.playerInfrastructureAggregateRootRepository != null) {
      this.playerInfrastructureAggregateRootRepository.close();
    }
    if (this.tagInfrastructureAggregateRootRepository != null) {
      this.tagInfrastructureAggregateRootRepository.close();
    }
  }

  public @NotNull InfrastructureAggregateRootRepository<PlayerAggregateRoot> playerInfrastructureAggregateRootRepository() {
    return this.playerInfrastructureAggregateRootRepository;
  }

  public @NotNull InfrastructureAggregateRootRepository<TagAggregateRoot> tagInfrastructureAggregateRootRepository() {
    return this.tagInfrastructureAggregateRootRepository;
  }
}
