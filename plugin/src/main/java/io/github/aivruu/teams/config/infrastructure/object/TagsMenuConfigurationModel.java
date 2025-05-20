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

import io.github.aivruu.teams.config.application.ConfigurationInterface;
import io.github.aivruu.teams.config.infrastructure.object.item.MenuItemSection;
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
  public MenuItem[] items = { new MenuItem() };

  @ConfigSerializable
  public static class MenuItem {
    @Comment("Contains the general-information for this item.")
    public MenuItemSection itemInformation = new MenuItemSection();

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
  }
}
