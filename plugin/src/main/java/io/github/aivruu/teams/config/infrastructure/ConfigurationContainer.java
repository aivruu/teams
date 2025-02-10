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

import io.github.aivruu.teams.TeamsPlugin;
import io.github.aivruu.teams.logger.application.DebugLoggerHelper;
import io.github.aivruu.teams.shared.infrastructure.ExecutorHelper;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public record ConfigurationContainer<C>(@NotNull C model, @NotNull HoconConfigurationLoader loader, @NotNull Class<C> modelClass) {
  private static final ComponentLogger LOGGER = TeamsPlugin.getPlugin(TeamsPlugin.class).getComponentLogger();
  private static final String CONFIG_HEADER = """
    AldrTeams | A plugin that gives your players the ability to select customizable-tags and showcase them on
    their name-tag and tab-list.

    This plugin supports MiniMessage format for all features, menu-configuration, tags' prefixes/suffixes
    and more, you can customize these tags as you want to implement custom gradient-prefixes or suffixes for
    special-ranks.

    All this process is handle through a customizable-menu, which provides a highly-customization both for
    tags-selection as for another items, also, you can trigger actions when a player click on a specific item
    on the menu (left and right-click actions), all this indications more detailed in the selector_menu file.

    Almost all plugin's executable-actions requires two or more parameters, which are separated with the ';' char.
    Single-parameter actions doesn't require that.
    Actions:
    - [SOUND] <sound-id>;<volume>;<pitch> - Plays a sound at the action's player-executor's location.
    - [TITLE] <title>;<subtitle>;<fade-in>;<stay>;<fade-out> - Sends a title to the action's player-executor.
    - [ACTION_BAR] <message> - Sends an action-bar message to the action's player-executor.
    - [MESSAGE] <message> - Sends a message to the action's player-executor.
    - [COMMAND] <PLAYER | CONSOLE>;<command> - Executes a command as the action's player-executor.
    - [BROADCAST] <GLOBAL (all server) | LOCAL (world only)>;<message> - Broadcasts a message to all players.

    List of available-colors for tags, these names can be used as input when modifying a tag's color in-game:
    - black
    - dark_blue
    - dark_green
    - dark_aqua
    - dark_red
    - dark_purple
    - gold
    - gray
    - dark_gray
    - blue
    - green
    - aqua
    - red
    - light_purple
    - yellow
    - white""";

  public @NotNull CompletableFuture<@Nullable ConfigurationContainer<C>> reload() {
    return CompletableFuture.supplyAsync(() -> {
      try {
        final C updatedModel = this.loader.load().get(this.modelClass);
        return (updatedModel == null) ? null : new ConfigurationContainer<>(updatedModel, this.loader, this.modelClass);
      } catch (final ConfigurateException exception) {
        DebugLoggerHelper.write("Unexpected exception during configuration reload: {}", exception);
        return null;
      }
    }, ExecutorHelper.pool());
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
        .header(CONFIG_HEADER)
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
      LOGGER.error("Unexpected exception during configuration-file loading/creation.", exception);
      return null;
    }
  }
}
