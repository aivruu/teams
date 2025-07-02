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
package io.github.aivruu.teams.tag.infrastructure.modification;

import io.github.aivruu.teams.config.infrastructure.ConfigurationManager;
import io.github.aivruu.teams.config.infrastructure.object.MessagesConfigurationModel;
import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.tag.application.TagManager;
import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import io.github.aivruu.teams.tag.application.modification.TagModificationProcessor;
import io.github.aivruu.teams.tag.application.modification.context.ProcessedContextResultValueObject;
import io.github.aivruu.teams.tag.application.modification.property.type.ColorPropertyProcessor;
import io.github.aivruu.teams.tag.application.modification.property.type.PrefixPropertyProcessor;
import io.github.aivruu.teams.tag.application.modification.property.type.SuffixPropertyProcessor;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class TagModificationProcessorImpl extends TagModificationProcessor {
  private final TagManager tagManager;
  private final ConfigurationManager configurationManager;
  private final PacketAdaptationContract packetAdapter;

  public TagModificationProcessorImpl(
     final @NotNull JavaPlugin plugin,
     final @NotNull TagAggregateRootRegistry tagAggregateRootRegistry,
     final @NotNull TagManager tagManager,
     final @NotNull ConfigurationManager configurationManager,
     final @NotNull PacketAdaptationContract packetAdapter) {
    super(plugin, tagAggregateRootRegistry);
    this.tagManager = tagManager;
    this.configurationManager = configurationManager;
    this.packetAdapter = packetAdapter;
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public @NotNull ProcessedContextResultValueObject process(
     final @NotNull Player player,
     final @NotNull ModificationInProgressValueObject modification,
     final @NotNull String input) {
    final MessagesConfigurationModel messages = this.configurationManager.messages();
    final ProcessedContextResultValueObject processedContextResult = super.process(player,
       modification, input);
    if (processedContextResult.cancelled()) {
      player.sendMessage(MiniMessageParser.text(messages.cancelledEditMode));
      return processedContextResult;
    }
    // This means that the cause might be an unknown-tag, or modify-event was cancelled.
    if (!processedContextResult.pending()) {
      player.sendMessage(MiniMessageParser.text(messages.tagModifyError));
      return processedContextResult;
    }
    final TagAggregateRoot tagAggregateRoot = processedContextResult.tagAggregateRoot();
    final TagPropertiesValueObject properties = tagAggregateRoot.tagModel().tagComponentProperties();
    final TagPropertiesValueObject newProperties = switch (modification.context()) {
      case PREFIX -> PrefixPropertyProcessor.INSTANCE.handle(input, properties, properties.prefix());
      case SUFFIX -> SuffixPropertyProcessor.INSTANCE.handle(input, properties, properties.suffix());
      case COLOR -> ColorPropertyProcessor.INSTANCE.handle(input, properties, properties.color());
      case NONE -> null; // non-reachable case at this point.
    };
    if (newProperties == null) {
      player.sendMessage(MiniMessageParser.text(messages.valueIsSame));
    } else {
      tagAggregateRoot.tagComponentProperties(newProperties);
      // update properties for the tag's scoreboard-team.
      this.packetAdapter.updateTeamAttributes(tagAggregateRoot.id(), newProperties);
      // Save tag's information as it was modified.
      this.tagManager.handleTagAggregateRootSave(tagAggregateRoot);
      player.sendMessage(MiniMessageParser.text(messages.modifiedTagProperty,
         Placeholder.parsed("property", modification.context().name().toLowerCase(Locale.ROOT))));
    }
    return processedContextResult;
  }
}
