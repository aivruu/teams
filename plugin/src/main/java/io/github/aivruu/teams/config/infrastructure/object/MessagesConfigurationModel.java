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
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class MessagesConfigurationModel implements ConfigurationInterface {
  public String[] help = {
    "<gradient:blue:green>AldrTeams | Available Commands Guide:",
    "",
    "<gray> - <yellow>/aldrteams help</yellow> Shows this messages.",
    "<gray> - <yellow>/aldrteams reload</yellow> Reloads the plugin's configurations.",
    "<gray> - <yellow>/tags selector</yellow> Opens the tags-selector menu.",
    "<gray> - <yellow>/tags unselect</yellow> Unselects the current tag.",
    "<gray> - <yellow>/tags create <id> <prefix> <suffix></yellow> Creates a new tag with the given properties.",
    "<gray> - <yellow>/tags delete <id></yellow> Deletes the tag with that ID.",
    "<gray> - <yellow>/tags edit <id></yellow> Opens the editor-menu for the specified tag."
  };

  public String modifyUsage = "<blue>[AldrTeams] <dark_gray><b>></b> <red>Correct usage for modifications: <id> <prefix> <suffix><br><yellow>You can let the fields empty if you want to modify just one value.";

  public String reloadSuccess = "<blue>[AldrTeams] <dark_gray><b>></b> <gradient:green:yellow>The plugin has been reloaded correctly.";

  public String reloadError = "<blue>[AldrTeams] <dark_gray><b>></b> <red>An error occurred during configurations reloading or selector-menu updating.";

  public String playerUnknownInfo = "<blue>[AldrTeams] <dark_gray><b>></b> <red>Seems your information isn't available.";

  public String unknownTag = "<blue>[AldrTeams] <dark_gray><b>></b> <red>The specified tag doesn't exist.";

  public String openedMenu = "<blue>[AldrTeams] <dark_gray><b>></b> <gradient:green:yellow>The menu has been opened.";

  public String[] enteredEditMode = {
    "<green>You have enabled the edit-mode for the selected tag, insert the input that will be used for the property",
    "<gray>Currently, you're modifying the property <green><type></green>.",
    "<gray>If you want to cancel the modification, type <red>cancel",
    "<gray>Type <white>clear</white> to clear the property-to-modify current content, only for <white>prefix & suffix</white>."
  };

  public String cancelledEditMode = "<yellow>The tag-modification has been cancelled by the user.";

  public String alreadyInModification = "<blue>[AldrTeams] <dark_gray><b>></b> <red>You're already in a modification process, wait 15 seconds.";

  public String clearedTagProperty = "<blue>[AldrTeams] <dark_gray><b>></b> <gradient:green:yellow>The property's content has been cleared.";

  public String modifiedTagProperty = "<blue>[AldrTeams] <dark_gray><b>></b> <gradient:green:yellow>The tag's property <property> has been modified successfully.";

  public String tagModifyError = "<blue>[AldrTeams] <dark_gray><b>></b> <red>Something went wrong during property-modification, may event was cancelled, or value is already set.";

  public String deleted = "<blue>[AldrTeams] <dark_gray><b>></b> <red>The tag <tag-id> has been deleted.";

  public String created = "<blue>[AldrTeams] <dark_gray><b>></b> <gradient:green:yellow>The tag <tag-id> has been created.";

  public String alreadyExists = "<blue>[AldrTeams] <dark_gray><b>></b> <red>That tag already exists.";

  public String alreadySelected = "<blue>[AldrTeams] <dark_gray><b>></b> <red>You've already selected that tag.";

  public String selected = "<blue>[AldrTeams] <dark_gray><b>></b> <gradient:green:yellow>The tag <tag-id> has been selected.";

  public String noSelectedTag = "<blue>[AldrTeams] <dark_gray><b>></b> <red>You've no a tag selected.";

  public String unselected = "<blue>[AldrTeams] <dark_gray><b>></b> <gradient:green:yellow>Your current tag has been unselected.";
}
