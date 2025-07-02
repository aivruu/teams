package io.github.aivruu.teams.tag.application.modification.property.type;

import io.github.aivruu.teams.tag.application.modification.ModificationContext;
import io.github.aivruu.teams.tag.application.modification.property.PropertyProcessorContract;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ColorPropertyProcessor implements PropertyProcessorContract<NamedTextColor> {
  INSTANCE;

  @Override
  public @NotNull ModificationContext context() {
    return ModificationContext.COLOR;
  }

  @Override
  public @Nullable TagPropertiesValueObject handle(
     final @NotNull String input,
     final @NotNull TagPropertiesValueObject properties,
     final @Nullable NamedTextColor oldValue) {
    final NamedTextColor newColor = NamedTextColor.NAMES.value(input.toLowerCase(Locale.ROOT));
    return (newColor != null)
       ? new TagPropertiesValueObject(properties.prefix(), properties.suffix(), newColor)
       : null;
  }
}
