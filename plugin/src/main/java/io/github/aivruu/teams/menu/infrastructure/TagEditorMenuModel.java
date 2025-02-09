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
import io.github.aivruu.teams.config.infrastructure.object.TagEditorMenuConfigurationModel;
import io.github.aivruu.teams.menu.application.MenuModelContract;
import io.github.aivruu.teams.menu.infrastructure.shared.MenuConstants;
import io.github.aivruu.teams.minimessage.application.MiniMessageHelper;
import io.github.aivruu.teams.tag.application.TagModificationContainer;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class TagEditorMenuModel implements MenuModelContract {
  private final TagModificationContainer tagModificationContainer;
  private final ActionManager actionManager;
  private ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;
  private ConfigurationContainer<TagEditorMenuConfigurationModel> tagsMenuModelConfiguration;
  private BaseGui gui;

  public TagEditorMenuModel(
    final @NotNull TagModificationContainer tagModificationContainer,
    final @NotNull ActionManager actionManager,
    final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer,
    final @NotNull ConfigurationContainer<TagEditorMenuConfigurationModel> tagEditorMenuModelConfiguration) {
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
  public @NotNull String id() {
    return MenuConstants.TAGS_EDITOR_ID;
  }

  @Override
  public void build() {
    final TagEditorMenuConfigurationModel menu = this.tagsMenuModelConfiguration.model();
    this.gui = Gui.gui(GuiType.CHEST)
      .disableAllInteractions()
      .title(MiniMessageHelper.text(menu.title))
      .rows(menu.rows)
      .create();
    for (final TagEditorMenuConfigurationModel.ItemSection itemSection : menu.items) {
      if (itemSection.slots.length == 1) {
        this.configureItem(itemSection, itemSection.slots[0]);
        continue;
      }
      for (final byte slot : itemSection.slots) {
        this.configureItem(itemSection, slot);
      }
    }
    this.gui.update();
  }

  private void configureItem(final @NotNull TagEditorMenuConfigurationModel.ItemSection itemSection, final byte slot) {
    gui.addSlotAction(slot, event -> {
      final ItemStack clickedItem = event.getCurrentItem();
      if (clickedItem == null || !clickedItem.getPersistentDataContainer().has(MENU_ITEM_NBT_KEY)) {
        return;
      }
      if (itemSection.checkCustomModelData && clickedItem.getItemMeta().getCustomModelData() != itemSection.data) {
        return;
      }
      event.setCancelled(true);
      this.processInput((Player) event.getWhoClicked(), itemSection, event.getClick());
    });
    gui.setItem(slot, this.buildSlotItem(itemSection));
  }

  private void processInput(
    final @NotNull Player player,
    final @NotNull TagEditorMenuConfigurationModel.ItemSection itemSection,
    final @NotNull ClickType clickType
  ) {
    if (!this.processActionType(player, itemSection, clickType)) {
      return;
    }
    // Just close the gui if the click-type was for a tag-selection.
    this.gui.close(player);
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
    if (this.tagsMenuModelConfiguration.model().useOpenActions) {
      for (final String action : this.tagsMenuModelConfiguration.model().openActions) {
        this.actionManager.execute(player, action);
      }
    }
    this.gui.open(player);
  }

  @Override
  public void close(final @NotNull Player player) {
    this.gui.close(player, false);
  }

  private @NotNull GuiItem buildSlotItem(final @NotNull TagEditorMenuConfigurationModel.ItemSection itemSection) {
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
