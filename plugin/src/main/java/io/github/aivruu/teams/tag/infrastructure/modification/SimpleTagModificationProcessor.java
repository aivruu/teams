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
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.application.TagModifierService;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.TagModificationProcessor;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SimpleTagModificationProcessor extends TagModificationProcessor {
  private final ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer;
  private final TagManager tagManager;

  public SimpleTagModificationProcessor(
    final @NotNull TeamsPlugin plugin,
    final @NotNull TagAggregateRootRegistry tagAggregateRootRegistry,
    final @NotNull TagModifierService tagModifierService,
    final @NotNull ConfigurationContainer<MessagesConfigurationModel> messagesModelContainer,
    final @NotNull TagManager tagManager) {
    super(plugin, tagAggregateRootRegistry, tagModifierService);
    this.messagesModelContainer = messagesModelContainer;
    this.tagManager = tagManager;
  }

  @Override
  public @NotNull ModificationContext process(final @NotNull Player player, final @NotNull ModificationInProgressValueObject modification, final @NotNull Component message) {
    final ModificationContext context = super.process(player, modification, message);
    final MessagesConfigurationModel messages = this.messagesModelContainer.model();
    switch (context) {
      case CANCELLED -> player.sendMessage(MiniMessageHelper.text(messages.cancelledEditMode));
      case FAILED -> player.sendMessage(MiniMessageHelper.text(messages.tagModifyError));
      case CLEARED -> player.sendMessage(MiniMessageHelper.text(messages.clearedTagProperty));
      case PREFIX, SUFFIX, COLOR -> {
        // Write tag's new information to the storage.
        final TagAggregateRoot tagAggregateRoot = this.tagManager.tagAggregateRootOf(modification.tag());
        // That is still in cache?
        if (tagAggregateRoot != null) {
          this.tagManager.handleTagAggregateRootSave(tagAggregateRoot);
        }
        player.sendMessage(MiniMessageHelper.text(messages.modifiedTagProperty, Placeholder.parsed("property", context.name())));
      }
    }
    return context;
  }
}
