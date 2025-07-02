package io.github.aivruu.teams.tag.application.modification.property.type;

import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.property.PropertyProcessorContract;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SuffixPropertyProcessor implements PropertyProcessorContract<Component> {
  INSTANCE;

  @Override
  public @NotNull ModificationContext context() {
    return ModificationContext.SUFFIX;
  }

  @Override
  public @Nullable TagPropertiesValueObject handle(
     final @NotNull String input,
     final @NotNull TagPropertiesValueObject properties,
     final @Nullable Component oldValue) {
    final boolean clear = input.equalsIgnoreCase("clear");
    if (clear && oldValue == null) {
      return null;
    }
    Component newSuffix = null;
    if (!clear) {
      newSuffix = MiniMessageParser.text(input);
    }
    return (newSuffix != null && newSuffix.equals(oldValue))
       ? null
       : new TagPropertiesValueObject(properties.prefix(), newSuffix, properties.color());
  }
}
