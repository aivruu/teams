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
  private final ConfigurationConfigurationModel configuration;
  private InfrastructureAggregateRootRepository<PlayerAggregateRoot> playerInfrastructureAggregateRootRepository;
  private InfrastructureAggregateRootRepository<TagAggregateRoot> tagInfrastructureAggregateRootRepository;

  public InfrastructureRepositoryController(final @NotNull Path dataFolder, final @NotNull ConfigurationConfigurationModel configuration) {
    this.dataFolder = dataFolder;
    this.configuration = configuration;
  }

  public boolean selectAndInitialize() {
    if (this.configuration.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.JSON ||
        this.configuration.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.JSON) {
      JsonCoder.buildWithAdapters(
        JsonTagAggregateRootCodec.INSTANCE, JsonTagPropertiesValueObjectCodec.INSTANCE, JsonPlayerAggregateRootCodec.INSTANCE);
    }
    // Database clients initialization if repositories require it.
    if (this.configuration.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MONGODB ||
        this.configuration.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MONGODB
    ) {
      Debugger.write("Initializing mongo-client instance with configuration's parameters.");
      MongoClientHelper.buildClient(
        this.configuration.host, this.configuration.username, this.configuration.database, this.configuration.password
      );
      // Check if parameters are valid and client was initialized correctly.
      if (MongoClientHelper.client() == null) {
        Debugger.write("Mongo-client couldn't be initialized correctly, stopping infrastructure repositories initialization.");
        return false;
      }
    }
    if (this.configuration.playerInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MARIADB ||
        this.configuration.tagInfrastructureRepositoryType == InfrastructureAggregateRootRepository.Type.MARIADB
    ) {
      Debugger.write("Initializing mongo-client instance with configuration's parameters.");
      HikariInstanceProvider.buildDataSource(
        this.configuration.host, this.configuration.mariaDbPort, this.configuration.username,
        this.configuration.database, this.configuration.password
      );
      // Check if parameters are valid.
      if (HikariInstanceProvider.get() == null) {
        Debugger.write("HikariDataSource couldn't be initialized correctly, stopping infrastructure repositories initialization.");
        return false;
      }
    }
    return this.determineAndInitializeInfrastructureTypes();
  }

  private boolean determineAndInitializeInfrastructureTypes() {
    final Connection connection = HikariInstanceProvider.connection();
    final MongoClient client = MongoClientHelper.client();
    this.playerInfrastructureAggregateRootRepository = switch (this.configuration.playerInfrastructureRepositoryType) {
      case JSON -> new PlayerJsonInfrastructureAggregateRootRepository(
        this.dataFolder.resolve(this.configuration.playerCollectionAndDirectoryName));
      // Note: The client-instance could be null, but it won't throw a NullPointerException as this method is called
      // only when MongoDB infrastructure is required, at that point, the client, or it was initialized already, or
      // parameters were invalid and repository won't be initialized.
      case MONGODB -> new PlayerMongoInfrastructureAggregateRootRepository(
        client, this.configuration.database, this.configuration.playerCollectionAndDirectoryName);
      case MARIADB -> new PlayerMariaDBInfrastructureAggregateRootRepository(
        // HikariDataSource instance shouldn't be null if repository-type is for MariaDB.
        connection, this.configuration.playerCollectionAndDirectoryName);
    };
    this.tagInfrastructureAggregateRootRepository = switch (this.configuration.tagInfrastructureRepositoryType) {
      case JSON -> new TagJsonInfrastructureAggregateRootRepository(
        this.dataFolder.resolve(this.configuration.tagCollectionAndDirectoryName));
      case MONGODB -> new TagMongoInfrastructureAggregateRootRepository(
        client, this.configuration.database, this.configuration.tagCollectionAndDirectoryName);
      case MARIADB -> new TagMariaDBInfrastructureAggregateRootRepository(
        connection, this.configuration.tagCollectionAndDirectoryName);
    };
    return this.playerInfrastructureAggregateRootRepository.start() && this.tagInfrastructureAggregateRootRepository.start();
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
