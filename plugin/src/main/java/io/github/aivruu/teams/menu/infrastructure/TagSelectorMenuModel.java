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
package io.github.aivruu.teams.menu.infrastructure;

import io.github.aivruu.teams.action.application.ActionManager;
import io.github.aivruu.teams.config.infrastructure.ConfigurationContainer;
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.TagsMenuConfigurationModel;
import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.minimessage.application.MiniMessageHelper;
import io.github.aivruu.teams.placeholder.application.PlaceholderHelper;
import io.github.aivruu.teams.player.application.PlayerTagSelectorManager;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class TagSelectorMenuModel extends AbstractMenuModel {
  private final ActionManager actionManager;
  private final PlayerTagSelectorManager playerTagSelectorManager;
  private ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;
  private ConfigurationContainer<TagsMenuConfigurationModel> tagsMenuModelConfiguration;

  public TagSelectorMenuModel(
    final @NotNull ActionManager actionManager,
    final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer,
    final @NotNull ConfigurationContainer<TagsMenuConfigurationModel> tagsMenuModelConfiguration,
    final @NotNull PlayerTagSelectorManager playerTagSelectorManager) {
    super(MenuConstants.TAGS_MENU_ID);
    this.actionManager = actionManager;
    this.messagesModelContainer = messagesModelContainer;
    this.tagsMenuModelConfiguration = tagsMenuModelConfiguration;
    this.playerTagSelectorManager = playerTagSelectorManager;
  }

  public void messagesConfiguration(final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer) {
    this.messagesModelContainer = messagesModelContainer;
  }

  public void menuConfiguration(final @NotNull ConfigurationContainer<TagsMenuConfigurationModel> menuModelConfiguration) {
    this.tagsMenuModelConfiguration = menuModelConfiguration;
  }

  @Override
  public void build() {
    final TagsMenuConfigurationModel menu = this.tagsMenuModelConfiguration.model();
    super.inventory = Bukkit.createInventory(this, menu.rows * 9, PlaceholderHelper.parseBoth(null, menu.title));
    for (final TagsMenuConfigurationModel.ItemSection itemSection : menu.items) {
      for (final byte slot : itemSection.slots) {
        super.inventory.setItem(slot, this.prepareItem(itemSection));
      }
    }
  }

  @Override
  public boolean handleClickLogic(final @NotNull Player player, final @Nullable ItemStack clicked, final @NotNull ClickType clickType) {
    if (!super.handleClickLogic(player, clicked, clickType)) {
      return false;
    }
    // At this point the item won't be null as null-checks and pdc-checks are made on superclass' base method-logic.
    final ItemMeta meta = clicked.getItemMeta();
    final String itemKey = clicked.getPersistentDataContainer().get(AbstractMenuModel.MENU_ITEM_NBT_KEY, PersistentDataType.STRING);
    for (final TagsMenuConfigurationModel.ItemSection itemSection : this.tagsMenuModelConfiguration.model().items) {
      if (!itemKey.equals(itemSection.id)) {
        continue;
      }
      if (itemSection.checkCustomModelData && meta.getCustomModelData() != itemSection.data) {
        continue;
      }
      this.processInput(player, itemSection, clickType);
    }
    return true;
  }

  private void processInput(
    final @NotNull Player player,
    final @NotNull TagsMenuConfigurationModel.ItemSection itemSection,
    final @NotNull ClickType clickType
  ) {
    if (!this.processActionType(player, itemSection, clickType)) {
      return;
    }
    // Just close the gui if the click-type was for a tag-selection.
    super.close(player);
    if (itemSection.tag.isEmpty()) {
      return;
    }
    if (!itemSection.permission.isEmpty() && !player.hasPermission(itemSection.permission)) {
      player.sendMessage(MiniMessageHelper.text(itemSection.permissionMessage));
      return;
    }
    this.processTagSelection(player, itemSection);
  }

  private boolean processActionType(
    final @NotNull Player player,
    final @NotNull TagsMenuConfigurationModel.ItemSection itemSection,
    final @NotNull ClickType clickType
  ) {
    if (clickType != ClickType.LEFT && clickType != ClickType.RIGHT) {
      return false;
    }
    if (clickType == ClickType.LEFT) {
      for (final String action : itemSection.leftClickActions) {
        this.actionManager.execute(player, action);
      }
      return true;
    }
    for (final String action : itemSection.rightClickActions) {
      this.actionManager.execute(player, action);
    }
    return false;
  }

  private void processTagSelection(
    final @NotNull Player player,
    final @NotNull TagsMenuConfigurationModel.ItemSection itemSection
  ) {
    final MessagesConfigurationModel messages = this.messagesModelContainer.model();
    // Process status-code provided by the select-operation.
    switch (this.playerTagSelectorManager.select(player, itemSection.tag)) {
      case PlayerTagSelectorManager.PLAYER_IS_NOT_ONLINE ->
        player.sendMessage(MiniMessageHelper.text(messages.playerUnknownInfo));
      case PlayerAggregateRoot.TAG_IS_ALREADY_SELECTED ->
        player.sendMessage(MiniMessageHelper.text(messages.alreadySelected));
      case PlayerTagSelectorManager.TAG_SPECIFIED_NOT_EXIST ->
        player.sendMessage(MiniMessageHelper.text(messages.unknownTag));
      case PlayerAggregateRoot.TAG_HAS_BEEN_CHANGED ->
        player.sendMessage(MiniMessageHelper.text(messages.selected, Placeholder.parsed("tag-id", itemSection.tag)));
      default -> throw new UnsupportedOperationException("Unexpected status-code result.");
    }
  }

  @Override
  public void open(final @NotNull Player player) {
    final TagsMenuConfigurationModel menu = this.tagsMenuModelConfiguration.model();
    if (menu.useOpenActions) {
      for (final String action : menu.openActions) {
        this.actionManager.execute(player, action);
      }
    }
    super.open(player);
  }

  private @NotNull ItemStack prepareItem(final @NotNull TagsMenuConfigurationModel.ItemSection itemSection) {
    final ItemStack item = new ItemStack(itemSection.material);
    item.editMeta(meta -> {
      meta.displayName(PlaceholderHelper.parseBoth(null, itemSection.displayName));
      meta.lore(Arrays.asList(PlaceholderHelper.parseBoth(null, itemSection.lore)));
      if (itemSection.data > 0) {
        meta.setCustomModelData(itemSection.data);
      }
      if (itemSection.glow) {
        meta.addEnchant(Enchantment.LURE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      }
      meta.getPersistentDataContainer().set(AbstractMenuModel.MENU_ITEM_NBT_KEY, PersistentDataType.STRING, itemSection.id);
    });
    return item;
  }
}
