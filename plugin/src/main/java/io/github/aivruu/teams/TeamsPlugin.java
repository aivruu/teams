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
package io.github.aivruu.teams;

import io.github.aivruu.teams.action.application.ActionManager;
import io.github.aivruu.teams.action.application.ActionModelContract;
import io.github.aivruu.teams.action.application.ActionBarActionModel;
import io.github.aivruu.teams.action.application.BroadcastMessageActionModel;
import io.github.aivruu.teams.action.application.CommandActionModel;
import io.github.aivruu.teams.action.application.MessageActionModel;
import io.github.aivruu.teams.action.application.SoundActionModel;
import io.github.aivruu.teams.action.application.TitleActionModel;
import io.github.aivruu.teams.command.application.RegistrableCommandContract;
import io.github.aivruu.teams.command.application.MainCommand;
import io.github.aivruu.teams.command.application.TagsCommand;
import io.github.aivruu.teams.config.infrastructure.ConfigurationContainer;
import io.github.aivruu.teams.config.infrastructure.object.ConfigurationConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.TagEditorMenuConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.TagsMenuConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.logger.application.DebugLoggerHelper;
import io.github.aivruu.teams.menu.application.MenuManagerService;
import io.github.aivruu.teams.menu.infrastructure.TagEditorMenuModel;
import io.github.aivruu.teams.menu.infrastructure.TagSelectorMenuModel;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.packet.application.PacketAdaptationModule;
import io.github.aivruu.teams.persistence.infrastructure.InfrastructureRepositoryController;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.player.application.PlayerTagSelectorManager;
import io.github.aivruu.teams.player.application.listener.PlayerRegistryListener;
import io.github.aivruu.teams.player.application.registry.PlayerAggregateRootRegistryImpl;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.registry.PlayerAggregateRootRegistry;
import io.github.aivruu.teams.player.domain.repository.PlayerAggregateRootRepository;
import io.github.aivruu.teams.player.infrastructure.PlayerCacheAggregateRootRepository;
import io.github.aivruu.teams.shared.infrastructure.ExecutorHelper;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.application.TagModificationContainer;
import io.github.aivruu.teams.tag.application.TagModifierService;
import io.github.aivruu.teams.tag.application.listener.TagModificationChatInputListener;
import io.github.aivruu.teams.tag.application.modification.TagModificationProcessor;
import io.github.aivruu.teams.tag.application.registry.TagAggregateRootRegistryImpl;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import io.github.aivruu.teams.tag.domain.repository.TagAggregateRootRepository;
import io.github.aivruu.teams.tag.infrastructure.TagCacheAggregateRootRepository;
import io.github.aivruu.teams.tag.infrastructure.modification.SimpleTagModificationProcessor;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class TeamsPlugin extends JavaPlugin implements Teams {
  private final ComponentLogger logger = super.getComponentLogger();
  private final PacketAdaptationContract packetAdaptation = new PacketAdaptationModule();
  private ConfigurationContainer<ConfigurationConfigurationModel> configurationModelContainer;
  private ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;
  private ConfigurationContainer<TagsMenuConfigurationModel> tagsMenuModelContainer;
  private ConfigurationContainer<TagEditorMenuConfigurationModel> tagEditorMenuModelContainer;
  private TagAggregateRootRepository tagAggregateRootRepository;
  private InfrastructureRepositoryController infrastructureRepositoryController;
  private TagAggregateRootRegistry tagAggregateRootRegistry;
  private TagManager tagManager;
  private TagModifierService tagModifierService;
  private PlayerAggregateRootRepository playerAggregateRootRepository;
  private PlayerAggregateRootRegistry playerAggregateRootRegistry;
  private PlayerManager playerManager;
  private PlayerTagSelectorManager playerTagSelectorManager;
  private ActionManager actionManager;
  private MenuManagerService menuManagerService;
  private TagModificationContainer tagModificationContainer;
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
  public @NotNull TagModifierService tagModifierService() {
    if (this.tagModifierService == null) {
      throw new IllegalStateException("The tags' modifier-service has not been initialized yet.");
    }
    return this.tagModifierService;
  }

  @Override
  public @NotNull TagModificationContainer tagModificationContainer() {
    if (this.tagModificationContainer == null) {
      throw new IllegalStateException("The tags' modification-container has not been initialized yet.");
    }
    return this.tagModificationContainer;
  }

  @Override
  public @NotNull TagModificationProcessor tagModificationProcessor() {
    if (this.tagModificationProcessor == null) {
      throw new IllegalStateException("The tags' modification-processor has not been initialized yet.");
    }
    return this.tagModificationProcessor;
  }

  @Override
  public @NotNull PlayerAggregateRootRepository playerCacheRepository() {
    if (this.playerAggregateRootRepository == null) {
      throw new IllegalStateException("The players' cache-repository has not been initialized yet.");
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
      throw new IllegalStateException("The players' tag-selector manager has not been initialized yet.");
    }
    return this.playerTagSelectorManager;
  }

  @Override
  public @NotNull MenuManagerService menuManagerService() {
    if (this.menuManagerService == null) {
      throw new IllegalStateException("The menu-manager service has not been initialized yet.");
    }
    return this.menuManagerService;
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
    final Path dataFolder = super.getDataPath();
    this.configurationModelContainer = ConfigurationContainer.of(dataFolder, "config", ConfigurationConfigurationModel.class);
    this.messagesModelContainer = ConfigurationContainer.of(dataFolder, "messages", MessagesConfigurationModel.class);
    this.tagsMenuModelContainer = ConfigurationContainer.of(dataFolder, "selector_menu", TagsMenuConfigurationModel.class);
    this.tagEditorMenuModelContainer = ConfigurationContainer.of(dataFolder, "editor_menu", TagEditorMenuConfigurationModel.class);
    if (this.configurationModelContainer == null || this.messagesModelContainer == null ||
      this.tagsMenuModelContainer == null || this.tagEditorMenuModelContainer == null) {
      this.logger.error("The configurations couldn't be loaded correctly, the plugin won't keep the start-up process.");
      return;
    }
    final ConfigurationConfigurationModel config = this.configurationModelContainer.model();
    DebugLoggerHelper.enable(config.debugMode);
    ExecutorHelper.createPool(config.threadPoolSize);
    this.infrastructureRepositoryController = new InfrastructureRepositoryController(dataFolder, this.configurationModelContainer.model());
    if (!this.infrastructureRepositoryController.selectAndInitialize()) {
      this.logger.error("The infrastructure repository-controller couldn't be initialized correctly, the plugin won't start correctly.");
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
      this.tagAggregateRootRepository, this.infrastructureRepositoryController.tagInfrastructureAggregateRootRepository());
    this.tagModificationContainer = new TagModificationContainer();

    this.tagManager = new TagManager(this.tagAggregateRootRegistry, this.packetAdaptation);
    ((TagCacheAggregateRootRepository) this.tagAggregateRootRepository).buildCache(this.tagManager);
    this.tagModifierService = new TagModifierService(this.packetAdaptation);
    this.tagModificationProcessor = new SimpleTagModificationProcessor(this, this.tagAggregateRootRegistry, this.tagModifierService, this.messagesModelContainer);

    this.logger.info("Initializing player-management services and registries.");
    this.playerAggregateRootRegistry = new PlayerAggregateRootRegistryImpl(
      this.playerAggregateRootRepository, this.infrastructureRepositoryController.playerInfrastructureAggregateRootRepository());

    this.playerManager = new PlayerManager(this.playerAggregateRootRegistry);
    this.playerTagSelectorManager = new PlayerTagSelectorManager(
      this.playerAggregateRootRegistry, this.tagAggregateRootRegistry, this.packetAdaptation);
    this.logger.info("Initializing action-manager and action-types registering.");

    this.actionManager = new ActionManager();
    this.registerActions(
      new ActionBarActionModel(),
      new BroadcastMessageActionModel(),
      new CommandActionModel(),
      new MessageActionModel(),
      new SoundActionModel(),
      new TitleActionModel()
    );
    this.logger.info("Initializing menu-manager service for menu-types building and registering.");
    this.menuManagerService = new MenuManagerService();
    this.menuManagerService.register(new TagSelectorMenuModel(this.actionManager, this.messagesModelContainer,
      this.tagsMenuModelContainer, this.playerTagSelectorManager));
    this.menuManagerService.register(new TagEditorMenuModel(this.tagModificationContainer, this.actionManager,
      this.messagesModelContainer, this.tagEditorMenuModelContainer));
    this.logger.info("Registered menus successfully.");

    this.logger.info("Registering plugin event-listener and commands.");
    // Commands and listener registration and API initialize.
    final PluginManager pluginManager = super.getServer().getPluginManager();
    pluginManager.registerEvents(new PlayerRegistryListener(this.playerManager, this.tagModificationContainer), this);
    pluginManager.registerEvents(new TagModificationChatInputListener(this.tagModificationContainer, this.tagModificationProcessor), this);
    this.registerCommands(
      new MainCommand(this, this.messagesModelContainer),
      new TagsCommand(this.messagesModelContainer, this.tagManager, this.menuManagerService, this.playerTagSelectorManager,
        this.tagModificationContainer)
    );
    TeamsProvider.set(this);
    this.logger.info("The plugin has been enabled successfully!");
  }

  public boolean reload() {
    final ConfigurationContainer<ConfigurationConfigurationModel> updatedConfigurationContainer = this.configurationModelContainer.reload().join();
    final ConfigurationContainer<MessagesConfigurationModel> updatedMessagesContainer = this.messagesModelContainer.reload().join();
    final ConfigurationContainer<TagsMenuConfigurationModel> updatedSelectorMenuContainer = this.tagsMenuModelContainer.reload().join();
    final ConfigurationContainer<TagEditorMenuConfigurationModel> updatedEditorMenuContainer = this.tagEditorMenuModelContainer.reload().join();
    if (updatedConfigurationContainer == null || updatedMessagesContainer == null ||
      updatedSelectorMenuContainer == null || updatedEditorMenuContainer == null) {
      this.logger.error("Failed to reload the configuration files.");
      return false;
    }
    this.configurationModelContainer = updatedConfigurationContainer;
    DebugLoggerHelper.enable(this.configurationModelContainer.model().debugMode);
    this.messagesModelContainer = updatedMessagesContainer;
    this.tagsMenuModelContainer = updatedSelectorMenuContainer;
    this.tagEditorMenuModelContainer = updatedEditorMenuContainer;
    final TagSelectorMenuModel tagSelectorMenu = (TagSelectorMenuModel) this.menuManagerService.menuModelOf(MenuConstants.TAGS_MENU_ID);
    if (tagSelectorMenu == null) {
      this.logger.info("Tags-selector menu isn't available for reloading, skipping it.");
    } else {
      // Menu's configuration and messages-container update, and re-build menu's GUI's content.
      tagSelectorMenu.messagesConfiguration(this.messagesModelContainer);
      tagSelectorMenu.menuConfiguration(this.tagsMenuModelContainer);
      tagSelectorMenu.build();
    }
    final TagEditorMenuModel tagEditorMenu = (TagEditorMenuModel) this.menuManagerService.menuModelOf(MenuConstants.TAGS_EDITOR_ID);
    if (tagEditorMenu == null) {
      this.logger.info("Tags-editor menu isn't available for reloading, skipping it.");
    } else {
      tagEditorMenu.messagesConfiguration(this.messagesModelContainer);
      tagEditorMenu.menuConfiguration(this.tagEditorMenuModelContainer);
      tagEditorMenu.build();
    }
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

  @Override
  public void onDisable() {
    this.logger.info("Verifying aggregate-root cache-repositories for data saving and clean.");
    if (this.playerAggregateRootRepository != null) {
      for (final PlayerAggregateRoot playerAggregateRoot : this.playerAggregateRootRepository.findAllInCacheSync()) {
        this.playerManager.handlePlayerAggregateRootSave(playerAggregateRoot);
      }
      this.playerAggregateRootRepository.clearAllSync();
    }
    if (this.tagAggregateRootRepository != null) {
      this.tagAggregateRootRepository.clearAllSync();
    }
    if (this.tagModificationContainer != null) {
      this.tagModificationContainer.clearModifications();
    }
    if (this.actionManager != null) {
      this.actionManager.unregisterAll();
    }
    if (this.menuManagerService != null) {
      this.menuManagerService.unregisterAll();
    }
    if (this.infrastructureRepositoryController != null) {
      this.logger.info(Component.text("Closing infrastructure repository-controller.").color(NamedTextColor.YELLOW));
      this.infrastructureRepositoryController.close();
    }
  }
}
