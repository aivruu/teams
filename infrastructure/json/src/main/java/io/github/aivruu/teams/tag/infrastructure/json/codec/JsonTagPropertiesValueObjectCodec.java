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
package io.github.aivruu.teams.tag.infrastructure.json.codec;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.aivruu.teams.shared.infrastructure.json.JsonCodecAdapterContract;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.github.aivruu.teams.util.application.component.PlainComponentParser;
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
    final JsonElement prefix = jsonObject.get("prefix");
    final JsonElement suffix = jsonObject.get("suffix");
    return new TagPropertiesValueObject(
      prefix.isJsonNull() ? null : PlainComponentParser.modern(prefix.getAsString()),
      suffix.isJsonNull() ? null : PlainComponentParser.modern(suffix.getAsString()),
      // This will never be null.
      NamedTextColor.namedColor(jsonObject.get("color-value").getAsInt())
    );
  }

  @Override
  public @NotNull JsonElement serialize(final TagPropertiesValueObject properties, final Type type, final JsonSerializationContext ctx) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("prefix", PlainComponentParser.plainOrNull(properties.prefix()));
    jsonObject.addProperty("suffix", PlainComponentParser.plainOrNull(properties.suffix()));
    jsonObject.addProperty("color-value", properties.color().value());
    return jsonObject;
  }
}
