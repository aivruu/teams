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
package io.github.aivruu.teams.config.infrastructure.object;

import io.github.aivruu.teams.config.infrastructure.ConfigurationInterface;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class TagsMenuConfigurationModel implements ConfigurationInterface {
  @Comment("The title for this menu.")
  public String title = "<dark_gray>Menu > Tags > Management";

  @Comment("Whether the open-actions should be triggered.")
  public boolean useOpenActions = true;

  @Comment("The actions to execute when a player opens this menu.")
  public String[] openActions = {
    "[SOUND] minecraft:block.note_block.pling;1;1"
  };

  @Comment("The amount of rows that this GUi will have.")
  public byte rows = 5;

  @Comment("The items that this gui will contain.")
  public ItemSection[] items = { new ItemSection() };

  @ConfigSerializable
  public static class ItemSection {
    @Comment("The id for this item.")
    public String id = "item-1";

    @Comment("The slot (or slots) where to place this item in the menu.")
    public byte[] slots = { 21 };

    @Comment("The material to use for this item.")
    public Material material = Material.EMERALD;

    @Comment("The tag that this item will take, empty if the item no requires this.")
    public String tag = "vip";

    @Comment("""
      The permission required for this tag usage (if this item is a tag's icon), otherwise,
      let this field empty.""")
    public String permission = "tag.vip";

    @Comment("""
      The message to send to the player if this tag-requires permissions (if 'permission' field
      is defined).""")
    public String permissionMessage = "<red>You don't have permission to use this tag.";

    @Comment("The display-name for this item.")
    public String displayName = "<gray>Tag: <green>VIP</green> | <white>Click to select it.";

    @Comment("The item's lore.")
    public String[] lore = {
      "", "", ""
    };

    @Comment("Enables the glow-effect on this item.")
    public boolean glow = true;

    @Comment("Means that besides the item's nbt-key, the plugin will check its custom-model-data.")
    public boolean checkCustomModelData = false;

    @Comment("The custom-model-data for this item. '0' to disable it.")
    public int data = 0;

    @Comment("""
      The actions to execute when this item is clicked with left-click or shift-left-click.
      These actions will be executed by the plugin firstly, and then, if a tag is assigned, will
      process it for the tag-selection.""")
    public String[] leftClickActions = {
      "[sound] entity.experience_orb.pickup;1;1"
    };

    @Comment("""
      The actions to execute when this item is clicked with right-click or shift-right-click.
      Only these actions will be executed, the plugin won't continue to check if a tag is assigned
      for selection-process, for this, will be necessary use left-click or shift-left-click instead.""")
    public String[] rightClickActions = {
      "[sound] entity.experience_orb.pickup;1;1"
    };
  }
}
