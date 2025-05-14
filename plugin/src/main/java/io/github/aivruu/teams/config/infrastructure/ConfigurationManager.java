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
package io.github.aivruu.teams.config.infrastructure;

import io.github.aivruu.teams.config.infrastructure.object.ConfigurationConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.TagEditorMenuConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.TagsMenuConfigurationModel;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@SuppressWarnings({"ConstantConditions", "rawtypes"})
public final class ConfigurationManager {
  private final Path directory;
  private final ComponentLogger logger;
  private @Nullable ConfigurationContainer @Nullable [] configurations;

  public ConfigurationManager(final @NotNull Path directory, final @NotNull ComponentLogger logger) {
    this.directory = directory;
    this.logger = logger;
  }

  private void buildContainersArray() {
    this.configurations = new ConfigurationContainer[]{
       ConfigurationContainer.of(this.directory, "config", ConfigurationConfigurationModel.class),
       ConfigurationContainer.of(this.directory, "messages", MessagesConfigurationModel.class),
       ConfigurationContainer.of(this.directory, "selector_menu", TagsMenuConfigurationModel.class),
       ConfigurationContainer.of(this.directory, "editor_menu", TagEditorMenuConfigurationModel.class)
    };
  }

  public boolean load() {
    this.buildContainersArray();
    byte i = 0;
    do {
      // we can assume at this point that the array won't be null, maybe its elements.
      if (this.configurations[i++] == null) {
        this.logger.error("A configuration-file could not be loaded.");
        return false;
      }
    } while (i < this.configurations.length);
    // assuming all configuration were loaded successfully.
    return true;
  }

  public boolean reload() {
    if (this.configurations == null) {
      return false;
    }
    final ConfigurationContainer[] newArray = new ConfigurationContainer[4];
    for (byte i = 0; i < this.configurations.length; i++) {
      // As a reminder, this method is called during reload only if the plugin was even enabled,
      // so, by that, we can assume safely that none of array's elements will be null.
      final ConfigurationContainer<?> newContainer =
         (ConfigurationContainer<?>) this.configurations[i].reload().join();
      if (newContainer == null) {
        this.logger.error("Configuration-model '{}' could not be reloaded.",
           this.configurations[i].modelClass().getName());
        return false;
      }
      newArray[i] = newContainer;
    }
    this.configurations = newArray;
    return true;
  }

  public @NotNull ConfigurationConfigurationModel config() {
    return (ConfigurationConfigurationModel) this.configurations[0].model();
  }

  public @NotNull MessagesConfigurationModel messages() {
    return (MessagesConfigurationModel) this.configurations[1].model();
  }

  public @NotNull TagsMenuConfigurationModel selector() {
    return (TagsMenuConfigurationModel) this.configurations[2].model();
  }

  public @NotNull TagEditorMenuConfigurationModel editor() {
    return (TagEditorMenuConfigurationModel) this.configurations[3].model();
  }
}
