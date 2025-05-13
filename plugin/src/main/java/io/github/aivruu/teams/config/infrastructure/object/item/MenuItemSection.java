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
package io.github.aivruu.teams.config.infrastructure.object.item;

import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class MenuItemSection {
  @Comment("The id for this item.")
  public String id = "item-1";

  @Comment("The slot (or slots) where to place this item in the menu.")
  public byte[] slots = { 21 };

  @Comment("The material to use for this item.")
  public Material material = Material.EMERALD;

  @Comment("""
      Represents the required input-type that must be given for the defined modification-type for this tag.
      - PREFIX: A new prefix must be given for the tag.
      - SUFFIX: A new suffix must be given for the tag.
      - COLOR: A new color must be for the tag.
      - NONE: No input required, used only for decoration-items (only executes actions).""")
  public ModificationContext inputTypeRequired = ModificationContext.NONE;

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
      These actions will be executed by the plugin firstly, and then, if a input-type is defined, it
      process it for the tag property-mutation.""")
  public String[] leftClickActions = {
     "[sound] entity.experience_orb.pickup;1;1"
  };

  @Comment("""
      The actions to execute when this item is clicked with right-click or shift-right-click.
      Only these actions will be executed, the plugin won't continue to check by the input-type
      specified for the modification, for this, will be necessary use left-click or shift-left-click instead.""")
  public String[] rightClickActions = {
     "[sound] entity.experience_orb.pickup;1;1"
  };
}
