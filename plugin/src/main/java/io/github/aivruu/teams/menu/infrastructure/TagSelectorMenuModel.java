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
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.TagsMenuConfigurationModel;
import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.menu.application.ProcessedMenuItemValueObject;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.menu.infrastructure.util.MenuItemSetter;
import io.github.aivruu.teams.player.application.PlayerManager;
import io.github.aivruu.teams.player.application.PlayerTagSelectorManager;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.util.PlaceholderParser;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TagSelectorMenuModel extends AbstractMenuModel {
  private final PlayerManager playerManager;
  private final PlayerTagSelectorManager playerTagSelectorManager;
  private final ConfigurationManager configurationManager;

  public TagSelectorMenuModel(
     final @NotNull ActionManager actionManager,
     final @NotNull PlayerManager playerManager,
     final @NotNull PlayerTagSelectorManager playerTagSelectorManager,
     final @NotNull ConfigurationManager configurationManager) {
    super(MenuConstants.TAGS_MENU_ID, actionManager);
    this.playerManager = playerManager;
    this.configurationManager = configurationManager;
    this.playerTagSelectorManager = playerTagSelectorManager;
  }

  @Override
  public void build() {
    final TagsMenuConfigurationModel menu = this.configurationManager.selector();
    super.inventory = Bukkit.createInventory(this, menu.rows * 9,
       PlaceholderParser.parseBoth(null, menu.title));
    for (final TagsMenuConfigurationModel.MenuItem menuItem : menu.items) {
      MenuItemSetter.placeItem(super.inventory, menuItem.itemInformation);
    }
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
    for (final TagsMenuConfigurationModel.MenuItem menuItem : this.configurationManager.selector().items) {
      if (menuItem.itemInformation.checkCustomModelData
         && customModelData != menuItem.itemInformation.data) {
        continue;
      }
      if (!id.equals(menuItem.itemInformation.id)) {
        continue;
      }
      this.processInput(player, menuItem, clickType);
    }
    return processedMenuItem;
  }

  private void processInput(
     final @NotNull Player player,
     final @NotNull TagsMenuConfigurationModel.MenuItem itemSection,
     final @NotNull ClickType clickType) {
    // Execute item's actions and check if it should stop execution-flow.
    super.processItemActions(player, clickType, itemSection.itemInformation.leftClickActions,
       itemSection.itemInformation.rightClickActions);
    if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
      return;
    }
    // Just close the gui if the click-type was for a tag-selection.
    super.close(player);
    if (itemSection.tag.isEmpty()) {
      return;
    }
    if (!itemSection.permission.isEmpty() && !player.hasPermission(itemSection.permission)) {
      player.sendMessage(MiniMessageParser.text(itemSection.permissionMessage));
      return;
    }
    this.processTagSelection(player, itemSection);
  }

  @SuppressWarnings("ConstantConditions")
  private void processTagSelection(
     final @NotNull Player player,
     final @NotNull TagsMenuConfigurationModel.MenuItem itemSection) {
    final MessagesConfigurationModel messages = this.configurationManager.messages();
    // Process status-code provided by the select-operation.
    switch (this.playerTagSelectorManager.select(player, itemSection.tag)) {
      case PlayerTagSelectorManager.PLAYER_IS_NOT_ONLINE ->
         player.sendMessage(MiniMessageParser.text(messages.playerUnknownInfo));
      case PlayerAggregateRoot.TAG_IS_ALREADY_SELECTED ->
         player.sendMessage(MiniMessageParser.text(messages.alreadySelected));
      case PlayerTagSelectorManager.TAG_SPECIFIED_NOT_EXIST ->
         player.sendMessage(MiniMessageParser.text(messages.unknownTag));
      case PlayerAggregateRoot.TAG_HAS_BEEN_CHANGED -> {
        // Aggregate-root won't be null.
        this.playerManager.handlePlayerAggregateRootSave(this.playerManager.playerAggregateRootOf(
           player.getUniqueId().toString()));
        player.sendMessage(MiniMessageParser.text(messages.selected, Placeholder.parsed("tag-id",
           itemSection.tag)));
      }
      default -> throw new UnsupportedOperationException("Unexpected status-code result.");
    }
  }

  @Override
  public void open(final @NotNull Player player) {
    final TagsMenuConfigurationModel menu = this.configurationManager.selector();
    if (menu.useOpenActions) {
      for (final String action : menu.openActions) {
        this.actionManager.execute(player, action);
      }
    }
    super.open(player);
  }
}
