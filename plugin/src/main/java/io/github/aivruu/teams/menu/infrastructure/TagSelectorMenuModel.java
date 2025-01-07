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

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import io.github.aivruu.teams.action.application.ActionManager;
import io.github.aivruu.teams.config.infrastructure.ConfigurationContainer;
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.config.infrastructure.object.TagsMenuConfigurationModel;
import io.github.aivruu.teams.menu.application.MenuModelContract;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.minimessage.application.MiniMessageHelper;
import io.github.aivruu.teams.player.application.PlayerTagSelectorManager;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class TagSelectorMenuModel implements MenuModelContract {
  private final ActionManager actionManager;
  private final PlayerTagSelectorManager playerTagSelectorManager;
  private ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;
  private ConfigurationContainer<TagsMenuConfigurationModel> tagsMenuModelConfiguration;
  private BaseGui gui;

  public TagSelectorMenuModel(
    final @NotNull ActionManager actionManager,
    final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer,
    final @NotNull ConfigurationContainer<TagsMenuConfigurationModel> tagsMenuModelConfiguration,
    final @NotNull PlayerTagSelectorManager playerTagSelectorManager) {
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
  public @NotNull String id() {
    return MenuConstants.TAGS_MENU_ID;
  }

  @Override
  public void build() {
    final TagsMenuConfigurationModel menu = this.tagsMenuModelConfiguration.model();
    this.gui = Gui.gui(GuiType.CHEST)
      .disableAllInteractions()
      .title(MiniMessageHelper.text(menu.title))
      .rows(menu.rows)
      .create();
    for (final TagsMenuConfigurationModel.ItemSection itemSection : menu.items) {
      if (itemSection.slots.length == 1) {
        this.configureItem(itemSection, itemSection.slots[0]);
      } else {
        for (final byte slot : itemSection.slots) {
          this.configureItem(itemSection, slot);
        }
      }
    }
  }

  private void configureItem(final @NotNull TagsMenuConfigurationModel.ItemSection itemSection, final byte slot) {
    gui.addSlotAction(slot, event -> {
      final ItemStack clickedItem = event.getCurrentItem();
      if (clickedItem == null || clickedItem.getPersistentDataContainer().get(MENU_ITEM_NBT_KEY, PersistentDataType.STRING) == null) {
        return;
      }
      event.setCancelled(true);
      this.processInput((Player) event.getWhoClicked(), itemSection, event.getClick());
    });
    gui.setItem(slot, this.buildSlotItem(itemSection));
  }

  private void processInput(
    final @NotNull Player player,
    final @NotNull TagsMenuConfigurationModel.ItemSection itemSection,
    final @NotNull ClickType clickType
  ) {
    if (!this.processActionType(player, itemSection, clickType)) {
      return;
    }
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
    if (clickType == ClickType.LEFT) {
      for (final String action : itemSection.leftClickActions) {
        this.actionManager.execute(player, action);
      }
      return true;
    } else if (clickType == ClickType.RIGHT) {
      for (final String action : itemSection.rightClickActions) {
        this.actionManager.execute(player, action);
      }
      return false;
    }
    return false;
  }

  private void processTagSelection(
    final @NotNull Player player,
    final @NotNull TagsMenuConfigurationModel.ItemSection itemSection
  ) {
    final MessagesConfigurationModel messages = this.messagesModelContainer.model();
    final byte selectResult = this.playerTagSelectorManager.select(player, itemSection.tag);
    switch (selectResult) {
      case PlayerTagSelectorManager.PLAYER_IS_NOT_ONLINE ->
        player.sendMessage(MiniMessageHelper.text(messages.playerUnknownInfo));
      case PlayerTagSelectorManager.TAG_IS_ALREADY_SELECTED ->
        player.sendMessage(MiniMessageHelper.text(messages.alreadySelected));
      case PlayerTagSelectorManager.TAG_SPECIFIED_NOT_EXIST ->
        player.sendMessage(MiniMessageHelper.text(messages.unknownTag));
      case PlayerTagSelectorManager.TAG_SELECTED_CORRECTLY ->
        player.sendMessage(MiniMessageHelper.text(messages.selected, Placeholder.parsed("tag-id", itemSection.tag)));
      default -> throw new UnsupportedOperationException("Unexpected status-code result.");
    }
  }

  @Override
  public void open(final @NotNull Player player) {
    for (final String action : this.tagsMenuModelConfiguration.model().openActions) {
      this.actionManager.execute(player, action);
    }
    this.gui.open(player);
  }

  @Override
  public void close(final @NotNull Player player) {
    this.gui.close(player, false);
  }

  private @NotNull GuiItem buildSlotItem(final @NotNull TagsMenuConfigurationModel.ItemSection itemSection) {
    final ItemBuilder itemBuilder = ItemBuilder.from(itemSection.material)
      .name(MiniMessageHelper.text(itemSection.displayName).decoration(TextDecoration.ITALIC, false))
      .lore(MiniMessageHelper.array(itemSection.lore))
      .glow(itemSection.glow)
      .pdc(persistentDataContainer ->
        persistentDataContainer.set(MENU_ITEM_NBT_KEY, PersistentDataType.STRING, itemSection.id));
    if (itemSection.data > 0) {
      itemBuilder.model(itemSection.data);
    }
    return new GuiItem(itemBuilder.build());
  }
}
