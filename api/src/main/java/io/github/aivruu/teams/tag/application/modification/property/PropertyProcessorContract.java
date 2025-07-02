package io.github.aivruu.teams.tag.application.modification.property;

import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface-contract that defines the methods required for tag-properties processing.
 *
 * @param <T> the property's type.
 * @since 4.1.0
 */
public interface PropertyProcessorContract<T> {
  /**
   * Returns the context of the modification for this property.
   *
   * @return The {@link ModificationContext} for the property.
   * @since 4.1.0
   */
  @NotNull ModificationContext context();

  /**
   * Handles the processing-logic for the specified tag-property with the given input.
   *
   * @param input      the input for the property.
   * @param properties the current properties of the tag.
   * @param oldValue   the old-value for the property.
   * @return A new {@link TagPropertiesValueObject} or {@code null} if the property was not
   * modified, or is the same as the old value.
   * @since 4.1.0
   */
  @Nullable TagPropertiesValueObject handle(
     final @NotNull String input,
     final @NotNull TagPropertiesValueObject properties,
     final @Nullable T oldValue);
}
