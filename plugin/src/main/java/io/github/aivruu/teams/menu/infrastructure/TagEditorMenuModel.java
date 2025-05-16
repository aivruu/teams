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
package io.github.aivruu.teams.menu.infrastructure;

import io.github.aivruu.teams.action.application.ActionManager;
import io.github.aivruu.teams.config.infrastructure.object.TagEditorMenuConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.item.MenuItemSection;
import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.menu.application.ProcessedMenuItemValueObject;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.menu.infrastructure.util.MenuItemSetter;
import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.tag.application.modification.repository.TagModificationRepository;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.util.PlaceholderParser;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TagEditorMenuModel extends AbstractMenuModel {
  private final TagModificationRepository tagModificationRepository;
  private final ConfigurationManager configurationManager;

  public TagEditorMenuModel(
     final @NotNull ActionManager actionManager,
     final @NotNull TagModificationRepository tagModificationRepository,
     final @NotNull ConfigurationManager configurationManager) {
    super(MenuConstants.TAGS_EDITOR_ID, actionManager);
    this.tagModificationRepository = tagModificationRepository;
    this.configurationManager = configurationManager;
  }

  @Override
  public void build() {
    final TagEditorMenuConfigurationModel menu = this.configurationManager.editor();
    super.inventory = Bukkit.createInventory(this, menu.rows * 9,
       PlaceholderParser.parseBoth(null, menu.title));
    MenuItemSetter.placeItems(super.inventory, menu.items);
  }

  @Override
  public @Nullable ProcessedMenuItemValueObject handleClickLogic(
     final @NotNull Player player,
     final @Nullable ItemStack clicked,
     final @NotNull ClickType clickType) {
    final ProcessedMenuItemValueObject processedMenuItem = super.handleClickLogic(player, clicked,
       clickType);
    if (processedMenuItem == null) {
      return null;
    }
    // After that we know the item is valid and has the key assigned.
    final int customModelData = processedMenuItem.meta().hasCustomModelData()
       ? processedMenuItem.meta().getCustomModelData() : 0;
    final String id = processedMenuItem.id();
    MenuItemSection itemInformation;
    for (final MenuItemContract menuItem : this.configurationManager.selector().items) {
      itemInformation = menuItem.itemInformation();
      if (!id.equals(itemInformation.id)) {
        continue;
      }
      if (itemInformation.checkCustomModelData && customModelData != itemInformation.data) {
        continue;
      }
      this.processInput(player, (TagEditorMenuConfigurationModel.MenuItemImpl) menuItem, clickType);
    }
    return processedMenuItem;
  }

  private void processInput(
     final @NotNull Player player,
     final @NotNull TagEditorMenuConfigurationModel.MenuItemImpl itemSection,
     final @NotNull ClickType clickType) {
    // Execute item's actions and check if it should stop execution-flow.
    super.processItemActions(player, clickType, itemSection.itemInformation.leftClickActions,
       itemSection.itemInformation.rightClickActions);
    if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
      return;
    }
    super.close(player);
    // Check if clicked-item is decorative, or due to some reason the item's context in
    // configuration isn't correctly defined.
    if (itemSection.inputTypeRequired == ModificationContext.NONE) {
      return;
    }
    final String playerId = player.getUniqueId().toString();
    // May modification have expired before player have selected an editor?
    if (!this.tagModificationRepository.existsSync(playerId)) {
      return;
    }
    for (final Component message : MiniMessageParser.array(
       this.configurationManager.messages().enteredEditMode,
       Placeholder.parsed("type", itemSection.inputTypeRequired.name()))) {
      player.sendMessage(message);
    }
    this.tagModificationRepository.updateSync(playerId, itemSection.inputTypeRequired);
  }

  @Override
  public void open(final @NotNull Player player) {
    final TagEditorMenuConfigurationModel menu = this.configurationManager.editor();
    if (menu.useOpenActions) {
      for (final String action : menu.openActions) {
        super.actionManager.execute(player, action);
      }
    }
    super.open(player);
  }
}
