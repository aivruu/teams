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
import io.github.aivruu.teams.config.infrastructure.ConfigurationContainer;
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.TagEditorMenuConfigurationModel;
import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.tag.application.modification.repository.TagModificationRepository;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.util.PlaceholderParser;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
  private final TagModificationRepository tagModificationRepository;
  private ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;
  private ConfigurationContainer<TagEditorMenuConfigurationModel> tagsMenuModelConfiguration;

  public TagEditorMenuModel(
    final @NotNull ActionManager actionManager,
    final @NotNull TagModificationRepository tagModificationRepository,
    final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer,
    final @NotNull ConfigurationContainer<TagEditorMenuConfigurationModel> tagEditorMenuModelConfiguration) {
    super(MenuConstants.TAGS_EDITOR_ID, actionManager);
    this.tagModificationRepository = tagModificationRepository;
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
    super.inventory = Bukkit.createInventory(this, menu.rows * 9,
       PlaceholderParser.parseBoth(null, menu.title));
    for (final TagEditorMenuConfigurationModel.ItemSection itemSection : menu.items) {
      for (final byte slot : itemSection.slots) {
        super.inventory.setItem(slot, this.prepareItem(itemSection));
      }
    }
  }

  @Override
  public @Nullable String handleClickLogic(
    final @NotNull Player player, final @Nullable ItemStack clicked, final @NotNull ClickType clickType
  ) {
    final String itemNbtKey = super.handleClickLogic(player, clicked, clickType);
    if (itemNbtKey == null) {
      return null;
    }
    // After that we know the item is valid and has the key assigned.
    final ItemMeta meta = clicked.getItemMeta();
    final int customModelData = meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
    for (final TagEditorMenuConfigurationModel.ItemSection itemSection : this.tagsMenuModelConfiguration.model().items) {
      if (!itemNbtKey.equals(itemSection.id)) {
        continue;
      }
      if (itemSection.checkCustomModelData && customModelData != itemSection.data) {
        continue;
      }
      this.processInput(player, itemSection, clickType);
    }
    return itemNbtKey;
  }

  private void processInput(
    final @NotNull Player player,
    final @NotNull TagEditorMenuConfigurationModel.ItemSection itemSection,
    final @NotNull ClickType clickType
  ) {
    // Execute item's actions and check if it should stop execution-flow.
    super.processItemActions(player, clickType, itemSection.leftClickActions, itemSection.rightClickActions);
    if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
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
    if (!this.tagModificationRepository.existsSync(playerId)) {
      return;
    }
    for (final Component message : MiniMessageParser.array(this.messagesModelContainer.model().enteredEditMode, Placeholder.parsed("type", itemSection.inputTypeRequired.name()))) {
      player.sendMessage(message);
    }
    this.tagModificationRepository.updateSync(playerId, itemSection.inputTypeRequired);
  }

  @Override
  public void open(final @NotNull Player player) {
    final TagEditorMenuConfigurationModel menu = this.tagsMenuModelConfiguration.model();
    if (menu.useOpenActions) {
      for (final String action : menu.openActions) {
        super.actionManager.execute(player, action);
      }
    }
    super.open(player);
  }

  private @NotNull ItemStack prepareItem(final @NotNull TagEditorMenuConfigurationModel.ItemSection itemSection) {
    final ItemStack item = new ItemStack(itemSection.material);
    // Air material-type for an item in the menu shouldn't have any custom-information.
    if (itemSection.material != Material.AIR){
      item.editMeta(meta -> {
        // Provide support for support only for legacy and modern global-placeholders.
        meta.itemName(PlaceholderParser.parseBoth(null, itemSection.displayName));
        meta.lore(Arrays.asList(PlaceholderParser.parseBoth(null, itemSection.lore)));
        if (itemSection.data > 0) {
          meta.setCustomModelData(itemSection.data);
        }
        if (itemSection.glow) {
          meta.addEnchant(Enchantment.LURE, 1, false);
          meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.getPersistentDataContainer().set(AbstractMenuModel.MENU_ITEM_NBT_KEY, PersistentDataType.STRING, itemSection.id);
      });
    }
    return item;
  }
}
