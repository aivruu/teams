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

import io.github.aivruu.teams.tag.application.TagModificationContainer;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import io.github.aivruu.teams.tag.application.modification.TagModificationProcessor;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TagModificationChatInputListener implements Listener {
  private final TagModificationContainer tagModificationContainer;
  private final TagModificationProcessor tagModificationProcessor;

  public TagModificationChatInputListener(
    final @NotNull TagModificationContainer tagModificationContainer,
    final @NotNull TagModificationProcessor tagModificationProcessor) {
    this.tagModificationContainer = tagModificationContainer;
    this.tagModificationProcessor = tagModificationProcessor;
  }

  @EventHandler
  public void onAsyncChat(final @NotNull AsyncChatEvent event) {
    final ModificationInProgressValueObject modificationOnCurse = this.tagModificationContainer.unregisterModification(event.getPlayer().getUniqueId().toString());
    if (modificationOnCurse == null) {
      return;
    }
    event.setCancelled(true);
    // Delegate input-processing logic for validation before actual tag's property-modification.
    this.tagModificationProcessor.process(event.getPlayer(), modificationOnCurse, event.message());
  }
}
