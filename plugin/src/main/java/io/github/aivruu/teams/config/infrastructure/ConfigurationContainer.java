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
package io.github.aivruu.teams.config.infrastructure;

import io.github.aivruu.teams.logger.application.DebugLoggerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public record ConfigurationContainer<C>(@NotNull C model, @NotNull HoconConfigurationLoader loader, @NotNull Class<C> modelClass) {
  public @Nullable ConfigurationContainer<C> reload() {
    try {
      final C updatedModel = this.loader.load().get(this.modelClass);
      return (updatedModel == null) ? null : new ConfigurationContainer<>(updatedModel, this.loader, this.modelClass);
    } catch (final ConfigurateException exception) {
      DebugLoggerHelper.write("Unexpected exception during configuration reload: {}", exception);
      return null;
    }
  }

  public static <C extends ConfigurationInterface> @Nullable ConfigurationContainer<C> of(
    final @NotNull Path directory,
    final @NotNull String fileName,
    final @NotNull Class<C> modelClass
  ) {
    final Path path = directory.resolve(fileName + ".conf");
    final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
      .prettyPrinting(true)
      .defaultOptions(opts -> opts
        .header("""
          AldrTeams | Create multiple tags with prefixes and suffixes for your players, and manage them across
          a customizable menu.

          This plugin supports MiniMessage format as well for all features, menu-configuration, tags' prefixes/suffixes
          and more, you can customize these tags as you want to implement custom gradient-prefixes or suffixes for
          special-ranks.

          All this process is handle through a customizable-menu, which provides a highly-customization both for
          tags-selection as for another items, also, you can trigger actions when a player clicks on a specific item
          on the menu, all this indications more detailed in the selector_menu file.""")
        .shouldCopyDefaults(true))
      .path(path)
      .build();
    try {
      final CommentedConfigurationNode commentedNode = loader.load();
      final C config = commentedNode.get(modelClass);
      if (config == null) {
        return null;
      }
      if (Files.notExists(path)) {
        commentedNode.set(modelClass, config);
        loader.save(commentedNode);
      }
      return new ConfigurationContainer<>(config, loader, modelClass);
    } catch (final ConfigurateException exception) {
      exception.printStackTrace();
      return null;
    }
  }
}
