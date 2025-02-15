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
import io.github.aivruu.teams.config.infrastructure.object.TagEditorMenuConfigurationModel;
import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.minimessage.application.MiniMessageHelper;
import io.github.aivruu.teams.placeholder.application.PlaceholderHelper;
import io.github.aivruu.teams.tag.application.TagModificationContainer;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import net.kyori.adventure.text.Component;
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

public final class TagEditorMenuModel extends AbstractMenuModel {
  private final TagModificationContainer tagModificationContainer;
  private final ActionManager actionManager;
  private ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;
  private ConfigurationContainer<TagEditorMenuConfigurationModel> tagsMenuModelConfiguration;

  public TagEditorMenuModel(
    final @NotNull TagModificationContainer tagModificationContainer,
    final @NotNull ActionManager actionManager,
    final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer,
    final @NotNull ConfigurationContainer<TagEditorMenuConfigurationModel> tagEditorMenuModelConfiguration) {
    super(MenuConstants.TAGS_EDITOR_ID);
    this.tagModificationContainer = tagModificationContainer;
    this.actionManager = actionManager;
    this.messagesModelContainer = messagesModelContainer;
    this.tagsMenuModelConfiguration = tagEditorMenuModelConfiguration;
  }

  public void messagesConfiguration(final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer) {
    this.messagesModelContainer = messagesModelContainer;
  }

  public void menuConfiguration(final @NotNull ConfigurationContainer<TagEditorMenuConfigurationModel> menuModelConfiguration) {
    this.tagsMenuModelConfiguration = menuModelConfiguration;
  }

  @Override
  public void build() {
    final TagEditorMenuConfigurationModel menu = this.tagsMenuModelConfiguration.model();
    super.inventory = Bukkit.createInventory(this, menu.rows * 9, PlaceholderHelper.parseBoth(null, menu.title));
    for (final TagEditorMenuConfigurationModel.ItemSection itemSection : menu.items) {
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
    for (final TagEditorMenuConfigurationModel.ItemSection itemSection : this.tagsMenuModelConfiguration.model().items) {
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
    final @NotNull TagEditorMenuConfigurationModel.ItemSection itemSection,
    final @NotNull ClickType clickType
  ) {
    if (!this.processActionType(player, itemSection, clickType)) {
      return;
    }
    super.close(player);
    // Check if clicked-item is decorative, or due to some reason the item's context in configuration isn't correctly defined.
    if (itemSection.inputTypeRequired == ModificationContext.NONE || itemSection.inputTypeRequired == ModificationContext.CANCELLED ||
      itemSection.inputTypeRequired == ModificationContext.FAILED || itemSection.inputTypeRequired == ModificationContext.CLEARED) {
      return;
    }
    final String playerId = player.getUniqueId().toString();
    // May modification have expired before player have selected an editor?
    if (!this.tagModificationContainer.isModifying(playerId)) {
      return;
    }
    for (final Component message : MiniMessageHelper.array(this.messagesModelContainer.model().enteredEditMode, Placeholder.parsed("type", itemSection.inputTypeRequired.name()))) {
      player.sendMessage(message);
    }
    this.tagModificationContainer.updateModificationContext(playerId, itemSection.inputTypeRequired);
  }

  private boolean processActionType(
    final @NotNull Player player,
    final @NotNull TagEditorMenuConfigurationModel.ItemSection itemSection,
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

  @Override
  public void open(final @NotNull Player player) {
    final TagEditorMenuConfigurationModel menu = this.tagsMenuModelConfiguration.model();
    if (menu.useOpenActions) {
      for (final String action : menu.openActions) {
        this.actionManager.execute(player, action);
      }
    }
    super.open(player);
  }

  private @NotNull ItemStack prepareItem(final @NotNull TagEditorMenuConfigurationModel.ItemSection itemSection) {
    final ItemStack item = new ItemStack(itemSection.material);
    item.editMeta(meta -> {
      meta.itemName(PlaceholderHelper.parseBoth(null, itemSection.displayName));
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
