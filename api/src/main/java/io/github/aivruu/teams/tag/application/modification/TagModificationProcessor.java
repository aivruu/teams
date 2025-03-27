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
package io.github.aivruu.teams.tag.application.modification;

import io.github.aivruu.teams.minimessage.application.MiniMessageHelper;
import io.github.aivruu.teams.plain.application.PlainComponentHelper;
import io.github.aivruu.teams.result.domain.ValueObjectMutationResult;
import io.github.aivruu.teams.tag.application.TagModifierService;
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.tag.domain.event.TagPropertyChangeEvent;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used as base-class to proportionate basic and main logic for the modification-input
 * processing based on the given context.
 *
 * @since 2.3.1
 */
public abstract class TagModificationProcessor {
  private final JavaPlugin plugin; // Used for sync event-firing.
  private final TagAggregateRootRegistry tagAggregateRootRegistry;
  private final TagModifierService tagModifierService;

  /**
   * Creates a new {@link TagModificationProcessor} with the provided parameters.
   *
   * @param plugin a {@link JavaPlugin} instance.
   * @param tagAggregateRootRegistry the {@link TagAggregateRootRegistry}.
   * @param tagModifierService the {@link TagModifierService}.
   * @since 2.3.1
   */
  protected TagModificationProcessor(
    final @NotNull JavaPlugin plugin,
    final @NotNull TagAggregateRootRegistry tagAggregateRootRegistry,
    final @NotNull TagModifierService tagModifierService) {
    this.plugin = plugin;
    this.tagAggregateRootRegistry = tagAggregateRootRegistry;
    this.tagModifierService = tagModifierService;
  }

  /**
   * Processes the input (message) given based on the modification-context provided, and returns the context
   * processed.
   *
   * @param player the player who is modifying the tag.
   * @param modification the {@link ModificationInProgressValueObject} for the tag's modification.
   * @param message the edit-mode's input for modification.
   * @return The {@link ModificationContext} processed, which can be.
   * <ul>
   * <li>{@link ModificationContext#FAILED} if the {@link TagPropertyChangeEvent} was cancelled.</li>
   * <li>{@link ModificationContext#CANCELLED} if the player has cancelled the modification.</li>
   * <li>See {@link #processContext(ModificationContext, TagAggregateRoot, String)} for more possible contexts.</li>
   * </ul>
   * @see #processContext(ModificationContext, TagAggregateRoot, String)
   * @since 2.3.1
   */
  public @NotNull ModificationContext process(final @NotNull Player player, final @NotNull ModificationInProgressValueObject modification, final @NotNull Component message) {
    final ModificationContext context = modification.context();
    final String tag = modification.tag();
    final TagPropertyChangeEvent tagPropertyChangeEvent = new TagPropertyChangeEvent(tag, context);
    // Avoid IllegalStateException due to asynchronous event-firing.
    Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getPluginManager().callEvent(tagPropertyChangeEvent));
    if (tagPropertyChangeEvent.isCancelled()) {
      return ModificationContext.FAILED;
    }
    final String input = PlainComponentHelper.plain(message);
    return input.equals("cancel")
      ? ModificationContext.CANCELLED
      // Tag-existing validation is made previously during editor's command-execution, so the tag should exist.
      : this.processContext(context, this.tagAggregateRootRegistry.findInBoth(tag), input);
  }

  /**
   * Process the input based on the given {@link ModificationContext} and provides a final {@link ModificationContext}
   * for the tag-modification.
   *
   * @param context the {@link ModificationContext} to process the input.
   * @param tagAggregateRoot the {@link TagAggregateRoot} to modify.
   * @param input the input to process.
   * @return A {@link ModificationContext} which can be.
   * <ul>
   * <li>{@link ModificationContext#CLEARED} (only for prefix/suffix) if property's content was cleared.</li>
   * <li>{@link ModificationContext#CANCELLED} if the player has cancelled the modification.</li>
   * <li>{@link ModificationContext#FAILED} if the property's content is the same as input.</li>
   * <li>{@link ModificationContext#PREFIX} if the prefix was successfully updated.</li>
   * <li>{@link ModificationContext#SUFFIX} if the suffix was successfully updated.</li>
   * <li>{@link ModificationContext#COLOR} if the color was successfully updated.</li>
   * </ul>
   */
  private @NotNull ModificationContext processContext(
    final @NotNull ModificationContext context, final @NotNull TagAggregateRoot tagAggregateRoot, final @NotNull String input
  ) {
    final boolean mustBeCleared = input.equals("clear");
    return switch (context) {
      case PREFIX -> {
        final ValueObjectMutationResult<TagPropertiesValueObject> mutationResult = this.tagModifierService.updatePrefix(tagAggregateRoot,
          mustBeCleared ? null : MiniMessageHelper.text(input + " "));
        if (mustBeCleared) {
          // Result won't be null if content has been cleared.
          tagAggregateRoot.tagComponentProperties(mutationResult.result());
          yield ModificationContext.CLEARED;
        }
        yield this.validateResult(tagAggregateRoot, mutationResult) ? ModificationContext.PREFIX : ModificationContext.FAILED;
      }
      case SUFFIX -> {
        final ValueObjectMutationResult<TagPropertiesValueObject> mutationResult = this.tagModifierService.updateSuffix(tagAggregateRoot,
          mustBeCleared ? null : MiniMessageHelper.text(" " + input));
        if (mustBeCleared) {
          // Result won't be null if content has been cleared.
          tagAggregateRoot.tagComponentProperties(mutationResult.result());
          yield ModificationContext.CLEARED;
        }
        yield this.validateResult(tagAggregateRoot, mutationResult) ? ModificationContext.SUFFIX : ModificationContext.FAILED;
      }
      case COLOR -> {
        NamedTextColor color = NamedTextColor.NAMES.value(input);
        if (color == null) {
          color = NamedTextColor.WHITE;
        }
        yield this.validateResult(tagAggregateRoot, this.tagModifierService.updateColor(tagAggregateRoot, color))
          ? ModificationContext.COLOR : ModificationContext.FAILED;
      }
      // Below cases are not possible to be reached.
      case FAILED, CANCELLED, CLEARED, NONE -> context;
    };
  }

  /**
   * Validates the mutation-result provided by the internal {@link TagModifierService}'s methods.
   *
   * @param tagAggregateRoot the {@link TagAggregateRoot} to mutate after validation.
   * @param mutationResult the {@link ValueObjectMutationResult}.
   * @return Whether the mutation-result was {@link ValueObjectMutationResult#MUTATED_STATUS}.
   * @see ValueObjectMutationResult#wasUnchanged()
   * @since 2.3.1
   */
  private boolean validateResult(final @NotNull TagAggregateRoot tagAggregateRoot, final @NotNull ValueObjectMutationResult<TagPropertiesValueObject> mutationResult) {
    if (mutationResult.wasUnchanged()) {
      return false;
    }
    tagAggregateRoot.tagComponentProperties(/* Should never be null. */ mutationResult.result());
    return true;
  }
}
