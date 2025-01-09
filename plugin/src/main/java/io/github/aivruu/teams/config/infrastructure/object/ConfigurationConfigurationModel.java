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
package io.github.aivruu.teams.config.infrastructure.object;

import io.github.aivruu.teams.config.infrastructure.ConfigurationInterface;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class ConfigurationConfigurationModel implements ConfigurationInterface {
  @Comment("""
    Represents the amount of threads that plugin's Executor will be able to use, this threads
    are used for asynchronous-operations for the plugin's infrastructure, such as load or save information into
    persistent-storage. If you don't know about this, don't touch it""")
  public int threadPoolSize = 2;

  @Comment("""
    Means that during plugin's internal processes such as infrastructure-initialization and management as well
    in-cache information handling, the plugin will send debug-messages to the console informing about these
    processes, this only should be used to debug the plugin on errors or inconsistencies-searching and if
    the developer require it.""")
  public boolean debugMode = false;

  @Comment("""
    The infrastructure-type to use for the players' information storage, there are two options:
    - MONGODB: Uses the database to store the information.
    - JSON: Uses json-files for information storing at pre-defined directories.""")
  public String playerInfrastructureRepositoryType = "JSON";

  @Comment("""
    The infrastructure-type to use for the tags' information storage, there are two options:
    - MONGODB: Uses the database to store the information.
    - JSON: Uses json-files for information storing at pre-defined directories.""")
  public String tagInfrastructureRepositoryType = "JSON";

  @Comment("""
    The name of the MongoDB's database's collection or the directory's name where the players' information
    will be stored by the plugin.""")
  public String playerCollectionAndDirectoryName = "players";

  @Comment("""
    The name of the MongoDB's database's collection or the directory's name where the tags' information
    will be stored by the plugin.""")
  public String tagCollectionAndDirectoryName = "tags";

  @Comment("The mongo-db's host to connect to.")
  public String mongoHost = "localhost";

  @Comment("The mongo-db's port to connect to.")
  public String mongoDatabase = "database";

  @Comment("The mongo-db's username to connect to.")
  public String mongoUsername = "username";

  @Comment("The mongo-db's password to connect to.")
  public String mongoPassword = "password";
}
