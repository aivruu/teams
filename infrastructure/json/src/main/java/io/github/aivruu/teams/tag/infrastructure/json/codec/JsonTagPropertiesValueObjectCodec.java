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
package io.github.aivruu.teams.tag.infrastructure.json.codec;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.aivruu.teams.plain.application.PlainComponentHelper;
import io.github.aivruu.teams.shared.infrastructure.adapter.JsonCodecAdapterContract;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public enum JsonTagPropertiesValueObjectCodec implements JsonCodecAdapterContract<TagPropertiesValueObject> {
  INSTANCE;

  @Override
  public @NotNull Class<TagPropertiesValueObject> forClass() {
    return TagPropertiesValueObject.class;
  }

  @Override
  public @NotNull TagPropertiesValueObject deserialize(final JsonElement json, final Type type, final JsonDeserializationContext ctx) throws JsonParseException {
    final JsonObject jsonObject = json.getAsJsonObject();
    final String prefix = jsonObject.get("prefix").getAsString();
    final String suffix = jsonObject.get("suffix").getAsString();
    return new TagPropertiesValueObject(
      prefix.isEmpty() ? null : PlainComponentHelper.modern(prefix),
      suffix.isEmpty() ? null : PlainComponentHelper.modern(suffix),
      // This will never be null.
      NamedTextColor.namedColor(jsonObject.get("color-value").getAsInt())
    );
  }

  @Override
  public @NotNull JsonElement serialize(final TagPropertiesValueObject properties, final Type type, final JsonSerializationContext ctx) {
    final JsonObject jsonObject = new JsonObject();
    final Component prefix = properties.prefix();
    jsonObject.addProperty("prefix", (prefix == null) ? "" : PlainComponentHelper.plain(prefix));
    final Component suffix = properties.suffix();
    jsonObject.addProperty("suffix", (suffix == null) ? "" : PlainComponentHelper.plain(suffix));
    jsonObject.addProperty("color-value", properties.color().value());
    return jsonObject;
  }
}
