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
package io.github.aivruu.teams;

import io.github.aivruu.teams.action.application.ActionManager;
import io.github.aivruu.teams.action.application.ActionModelContract;
import io.github.aivruu.teams.action.application.type.ActionBarActionModel;
import io.github.aivruu.teams.action.application.type.BroadcastMessageActionModel;
import io.github.aivruu.teams.action.application.type.CommandActionModel;
import io.github.aivruu.teams.action.application.type.MessageActionModel;
import io.github.aivruu.teams.action.application.type.SoundActionModel;
import io.github.aivruu.teams.action.application.type.TitleActionModel;
import io.github.aivruu.teams.action.infrastructure.ActionCacheRepository;
import io.github.aivruu.teams.command.application.RegistrableCommandContract;
import io.github.aivruu.teams.command.infrastructure.MainCommand;
import io.github.aivruu.teams.command.infrastructure.TagsCommand;
import io.github.aivruu.teams.command.application.suggestion.AvailableTagSuggestionProvider;
import io.github.aivruu.teams.config.infrastructure.object.ConfigurationConfigurationModel;
import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.menu.infrastructure.repository.MenuCacheRepository;
import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.tag.infrastructure.modification.TagModificationProcessorImpl;
import io.github.aivruu.teams.util.application.Debugger;
import io.github.aivruu.teams.menu.application.MenuManager;
import io.github.aivruu.teams.menu.application.listener.MenuInteractionListener;
import io.github.aivruu.teams.menu.infrastructure.TagEditorMenuModel;
import io.github.aivruu.teams.menu.infrastructure.TagSelectorMenuModel;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.packet.application.PacketAdaptationModule;
import io.github.aivruu.teams.persistence.infrastructure.InfrastructureRepositoryController;
import io.github.aivruu.teams.placeholder.application.PlaceholderHookContract;
import io.github.aivruu.teams.placeholder.application.impl.MiniPlaceholdersHookImpl;
import io.github.aivruu.teams.placeholder.application.impl.PlaceholderAPIHookImpl;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.player.application.PlayerTagSelectorManager;
import io.github.aivruu.teams.player.application.listener.PlayerRegistryListener;
import io.github.aivruu.teams.player.application.registry.PlayerAggregateRootRegistryImpl;
import io.github.aivruu.teams.player.domain.registry.PlayerAggregateRootRegistry;
import io.github.aivruu.teams.player.domain.repository.PlayerAggregateRootRepository;
import io.github.aivruu.teams.player.infrastructure.PlayerCacheAggregateRootRepository;
import io.github.aivruu.teams.util.application.PluginExecutor;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.application.modification.repository.TagModificationRepository;
import io.github.aivruu.teams.tag.application.listener.TagModificationChatInputListener;
import io.github.aivruu.teams.tag.application.modification.TagModificationProcessor;
import io.github.aivruu.teams.tag.application.TagAggregateRootRegistryImpl;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import io.github.aivruu.teams.tag.domain.repository.TagAggregateRootRepository;
import io.github.aivruu.teams.tag.infrastructure.TagCacheAggregateRootRepository;
import io.github.aivruu.teams.tag.infrastructure.modification.TagModificationCacheRepository;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class TeamsPlugin extends JavaPlugin implements Teams {
  private final ComponentLogger logger = super.getComponentLogger();
  private final PacketAdaptationContract packetAdaptation = new PacketAdaptationModule();
  private final ConfigurationManager configurationManager =
     new ConfigurationManager(super.getDataPath(), this.logger);
  private TagAggregateRootRepository tagAggregateRootRepository;
  private InfrastructureRepositoryController infrastructureRepositoryController;
  private TagAggregateRootRegistry tagAggregateRootRegistry;
  private TagManager tagManager;
  private PlayerAggregateRootRepository playerAggregateRootRepository;
  private PlayerAggregateRootRegistry playerAggregateRootRegistry;
  private PlayerManager playerManager;
  private PlayerTagSelectorManager playerTagSelectorManager;
  private ActionManager actionManager;
  private MenuManager menuManager;
  private TagModificationRepository tagModificationRepository;
  private TagModificationProcessor tagModificationProcessor;

  @Override
  public @NotNull TagAggregateRootRepository tagCacheRepository() {
    if (this.tagAggregateRootRepository == null) {
      throw new IllegalStateException("The tags' cache-repository has not been initialized yet.");
    }
    return this.tagAggregateRootRepository;
  }

  @Override
  public @NotNull TagAggregateRootRegistry tagsRegistry() {
    if (this.tagAggregateRootRegistry == null) {
      throw new IllegalStateException("The tags' registry has not been initialized yet.");
    }
    return this.tagAggregateRootRegistry;
  }

  @Override
  public @NotNull TagManager tagManager() {
    if (this.tagManager == null) {
      throw new IllegalStateException("The tags' manager has not been initialized yet.");
    }
    return this.tagManager;
  }

  @Override
  public @NotNull TagModificationRepository tagModificationRepository() {
    if (this.tagModificationRepository == null) {
      throw new IllegalStateException(
         "The tags-modification-repository has not been initialized yet.");
    }
    return this.tagModificationRepository;
  }

  @Override
  public @NotNull TagModificationProcessor tagModificationProcessor() {
    if (this.tagModificationProcessor == null) {
      throw new IllegalStateException(
         "The tags' modification-processor has not been initialized yet.");
    }
    return this.tagModificationProcessor;
  }

  @Override
  public @NotNull PlayerAggregateRootRepository playerCacheRepository() {
    if (this.playerAggregateRootRepository == null) {
      throw new IllegalStateException(
         "The players' cache-repository has not been initialized yet.");
    }
    return this.playerAggregateRootRepository;
  }

  @Override
  public @NotNull PlayerAggregateRootRegistry playersRegistry() {
    if (this.playerAggregateRootRegistry == null) {
      throw new IllegalStateException("The players' registry has not been initialized yet.");
    }
    return this.playerAggregateRootRegistry;
  }

  @Override
  public @NotNull PlayerManager playerManager() {
    if (this.playerManager == null) {
      throw new IllegalStateException("The players' manager has not been initialized yet.");
    }
    return this.playerManager;
  }

  @Override
  public @NotNull PlayerTagSelectorManager playerTagSelectorManager() {
    if (this.playerTagSelectorManager == null) {
      throw new IllegalStateException(
         "The players' tag-selector manager has not been initialized yet.");
    }
    return this.playerTagSelectorManager;
  }

  @Override
  public @NotNull MenuManager menuManagerService() {
    if (this.menuManager == null) {
      throw new IllegalStateException("The menu-manager service has not been initialized yet.");
    }
    return this.menuManager;
  }

  @Override
  public @NotNull ActionManager actionManager() {
    if (this.actionManager == null) {
      throw new IllegalStateException("The action-manager has not been initialized yet.");
    }
    return this.actionManager;
  }

  @Override
  public void onLoad() {
    if (!this.configurationManager.load()) {
      this.logger.error("The configurations couldn't be loaded correctly, the plugin won't keep " +
         "the start-up process.");
      return;
    }
    final ConfigurationConfigurationModel config = this.configurationManager.config();
    Debugger.enable(config.debugMode);
    PluginExecutor.build(config.threadPoolSize);
    this.infrastructureRepositoryController = new InfrastructureRepositoryController(
       super.getDataPath(), this.configurationManager);
    if (!this.infrastructureRepositoryController.selectAndInitialize()) {
      this.logger.error("One, or both infrastructure-repositories couldn't be initialized " +
         "correctly, the plugin won't continue with start-up process!");
      return;
    }
    this.playerAggregateRootRepository = new PlayerCacheAggregateRootRepository();
  }

  @Override
  public void onEnable() {
    if (this.infrastructureRepositoryController == null || this.playerAggregateRootRepository == null) {
      return;
    }
    this.logger.info("Initializing tags-management services and registries.");
    this.tagAggregateRootRepository = new TagCacheAggregateRootRepository();
    this.tagAggregateRootRegistry = new TagAggregateRootRegistryImpl(
       this.tagAggregateRootRepository,
       this.infrastructureRepositoryController.tagInfrastructureAggregateRootRepository());
    this.tagModificationRepository = new TagModificationCacheRepository();
    ((TagModificationCacheRepository) this.tagModificationRepository).buildCache(this.configurationManager);

    this.tagManager = new TagManager(this.tagAggregateRootRegistry, this.packetAdaptation);
    ((TagCacheAggregateRootRepository) this.tagAggregateRootRepository).buildCache(this.tagManager);
    this.tagModificationProcessor = new TagModificationProcessorImpl(
       this, this.tagAggregateRootRegistry, this.tagManager, this.configurationManager);

    this.logger.info("Initializing player-management services and registries.");
    this.playerAggregateRootRegistry = new PlayerAggregateRootRegistryImpl(
       this.playerAggregateRootRepository,
       this.infrastructureRepositoryController.playerInfrastructureAggregateRootRepository());

    this.playerManager = new PlayerManager(this.playerAggregateRootRegistry);
    this.playerTagSelectorManager = new PlayerTagSelectorManager(
       this.playerAggregateRootRegistry, this.tagAggregateRootRegistry, this.packetAdaptation);
    this.logger.info("Initializing action-manager and action-types registering.");

    this.actionManager = new ActionManager(new ActionCacheRepository());
    this.registerActions(
       new ActionBarActionModel(),
       new BroadcastMessageActionModel(),
       new CommandActionModel(),
       new MessageActionModel(),
       new SoundActionModel(),
       new TitleActionModel()
    );
    this.logger.info("Initializing menu-manager service for menu-types building and registering.");
    this.menuManager = new MenuManager(new MenuCacheRepository());
    this.menuManager.register(new TagSelectorMenuModel(this.actionManager, this.playerManager,
       this.playerTagSelectorManager, this.configurationManager));
    this.menuManager.register(new TagEditorMenuModel(this.actionManager,
       this.tagModificationRepository, this.configurationManager));
    this.logger.info("Registered menus successfully.");

    this.logger.info("Registering plugin event-listener and commands.");
    // Commands, listeners and hooks registration and API initialization.
    final PluginManager pluginManager = super.getServer().getPluginManager();
    pluginManager.registerEvents(new PlayerRegistryListener(this.playerManager,
       this.tagModificationRepository), this);
    pluginManager.registerEvents(new TagModificationChatInputListener(
       this.tagModificationRepository, this.tagModificationProcessor), this);
    pluginManager.registerEvents(new MenuInteractionListener(), this);
    this.registerCommands(
       new MainCommand(this, this.configurationManager),
       new TagsCommand(this.configurationManager, this.tagManager, this.menuManager,
          this.playerTagSelectorManager,
          this.tagModificationRepository, new AvailableTagSuggestionProvider(this.tagManager))
    );
    this.registerHooks(
       new PlaceholderAPIHookImpl(this.playerManager, this.packetAdaptation),
       new MiniPlaceholdersHookImpl(this.playerManager, this.packetAdaptation));
    TeamsProvider.set(this);
    this.logger.info("The plugin has been enabled successfully!");
  }

  public boolean reload() {
    if (!this.configurationManager.reload()) {
      this.logger.error("Failed to reload the configuration files.");
      return false;
    }
    Debugger.enable(this.configurationManager.config().debugMode);
    final AbstractMenuModel selectorMenu = this.menuManager.menuModelOf(MenuConstants.TAGS_MENU_ID);
    if (selectorMenu == null) {
      return false;
    }
    selectorMenu.build();
    final AbstractMenuModel editorMenu = this.menuManager.menuModelOf(MenuConstants.TAGS_EDITOR_ID);
    if (editorMenu == null) {
      return false;
    }
    editorMenu.build();
    return true;
  }

  private void registerActions(final @NotNull ActionModelContract... actionModelContracts) {
    for (final ActionModelContract actionModel : actionModelContracts) {
      this.actionManager.register(actionModel);
      this.logger.info("Registered '{}' action-type correctly", actionModel.id());
    }
  }

  @SuppressWarnings("UnstableApiUsage")
  private void registerCommands(final @NotNull RegistrableCommandContract... registrableCommands) {
    super.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, eventHandler -> {
      final Commands commands = eventHandler.registrar();
      for (final RegistrableCommandContract command : registrableCommands) {
        commands.register(command.register());
        this.logger.info("Registered '{}' command", command.id());
      }
    });
  }

  private void registerHooks(final @NotNull PlaceholderHookContract... placeholderHooks) {
    for (final PlaceholderHookContract placeholderHook : placeholderHooks) {
      if (!placeholderHook.hook()) continue;
      this.logger.info("Hooked {} successfully", placeholderHook.hookName());
    }
  }

  @Override
  public void onDisable() {
    this.logger.info("Clearing cache-repositories and unregistering objects.");
    if (this.playerAggregateRootRepository != null) {
      this.playerAggregateRootRepository.clearSync();
    }
    if (this.tagAggregateRootRepository != null) {
      this.tagAggregateRootRepository.clearSync();
    }
    if (this.tagModificationRepository != null) {
      this.tagModificationRepository.clearSync();
    }
    if (this.actionManager != null) {
      this.actionManager.unregisterAll();
    }
    if (this.menuManager != null) {
      this.menuManager.unregisterAll();
    }
    this.logger.info("Closing infrastructure-repositories.");
    if (this.infrastructureRepositoryController != null) {
      this.infrastructureRepositoryController.close();
    }
  }
}
