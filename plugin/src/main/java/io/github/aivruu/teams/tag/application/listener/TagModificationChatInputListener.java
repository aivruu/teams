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
package io.github.aivruu.teams.tag.application.listener;

import io.github.aivruu.teams.tag.application.modification.repository.TagModificationRepository;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import io.github.aivruu.teams.tag.application.modification.TagModificationProcessor;
import io.github.aivruu.teams.util.application.component.PlainComponentParser;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TagModificationChatInputListener implements Listener {
  private final TagModificationRepository tagModificationRepository;
  private final TagModificationProcessor tagModificationProcessor;

  public TagModificationChatInputListener(
     final @NotNull TagModificationRepository tagModificationRepository,
     final @NotNull TagModificationProcessor tagModificationProcessor) {
    this.tagModificationRepository = tagModificationRepository;
    this.tagModificationProcessor = tagModificationProcessor;
  }

  @EventHandler
  public void onAsyncChat(final @NotNull AsyncChatEvent event) {
    final Player player = event.getPlayer();
    final ModificationInProgressValueObject modification = this.tagModificationRepository.deleteSync(
       player.getUniqueId().toString());
    if (modification == null) {
      return;
    }
    event.setCancelled(true);
    final String inputAsPlainText = PlainComponentParser.plain(event.message());
    // Delegate input-processing logic for validation before actual tag's property-modification.
    if (this.tagModificationProcessor.process(modification, inputAsPlainText).modified()) {
      return;
    }
    player.sendMessage(Component.text("Failed to modify tag's property.").color(NamedTextColor.RED));
  }
}
