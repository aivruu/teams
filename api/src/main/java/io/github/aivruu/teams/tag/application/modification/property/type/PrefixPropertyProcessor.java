package io.github.aivruu.teams.tag.application.modification.property.type;

import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.property.PropertyProcessorContract;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.util.application.component.MiniMessageParser;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PrefixPropertyProcessor implements PropertyProcessorContract<Component> {
  INSTANCE;

  @Override
  public @NotNull ModificationContext context() {
    return ModificationContext.PREFIX;
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
    Component newPrefix = null;
    if (!clear) {
      newPrefix = MiniMessageParser.text(input);
    }
    return (newPrefix != null && newPrefix.equals(oldValue))
       ? null
       : new TagPropertiesValueObject(newPrefix, properties.suffix(), properties.color());
  }
}
