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
package io.github.aivruu.teams.tag.infrastructure.modification;

import io.github.aivruu.teams.TeamsPlugin;
import io.github.aivruu.teams.minimessage.application.MiniMessageHelper;
import io.github.aivruu.teams.config.infrastructure.ConfigurationContainer;
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.tag.application.TagModifierService;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.TagModificationProcessor;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SimpleTagModificationProcessor extends TagModificationProcessor {
  private final ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;

  public SimpleTagModificationProcessor(
    final @NotNull TeamsPlugin plugin,
    final @NotNull TagAggregateRootRegistry tagAggregateRootRegistry,
    final @NotNull TagModifierService tagModifierService,
    final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer) {
    super(plugin, tagAggregateRootRegistry, tagModifierService);
    this.messagesModelContainer = messagesModelContainer;
  }

  @Override
  public @NotNull ModificationContext process(final @NotNull Player player, final @NotNull ModificationInProgressValueObject modificationOnCurse, final @NotNull Component message) {
    final ModificationContext processedModificationContext = super.process(player, modificationOnCurse, message);
    final MessagesConfigurationModel messages = this.messagesModelContainer.model();
    switch (processedModificationContext) {
      case CANCELLED -> player.sendMessage(MiniMessageHelper.text(messages.cancelledEditMode));
      case FAILED -> player.sendMessage(MiniMessageHelper.text(messages.tagModifyError));
      case CLEARED -> player.sendMessage(MiniMessageHelper.text(messages.clearedTagProperty));
      case PREFIX -> player.sendMessage(MiniMessageHelper.text(messages.modifiedTagPrefix));
      case SUFFIX -> player.sendMessage(MiniMessageHelper.text(messages.modifiedTagSuffix));
      case COLOR -> player.sendMessage(MiniMessageHelper.text(messages.modifiedTagColor));
    }
    return processedModificationContext;
  }
}
