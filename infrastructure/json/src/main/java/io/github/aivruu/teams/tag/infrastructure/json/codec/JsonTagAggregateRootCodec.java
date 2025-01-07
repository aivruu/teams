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
import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagModelEntity;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public enum JsonTagAggregateRootCodec implements JsonSerializer<TagAggregateRoot>, JsonDeserializer<TagAggregateRoot> {
  INSTANCE;

  @Override
  public @NotNull TagAggregateRoot deserialize(final JsonElement json, final Type type, final JsonDeserializationContext ctx) throws JsonParseException {
    final JsonObject jsonObject = json.getAsJsonObject();
    final String id = jsonObject.get("id").getAsString();
    return new TagAggregateRoot(id, new TagModelEntity(id, ctx.deserialize(jsonObject.get("properties"), TagPropertiesValueObject.class)));
  }

  @Override
  public @NotNull JsonElement serialize(final TagAggregateRoot tagAggregateRoot, final Type type, final JsonSerializationContext ctx) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", tagAggregateRoot.id());
    jsonObject.add("properties", ctx.serialize(tagAggregateRoot.tagModel().tagComponentProperties()));
    return jsonObject;
  }
}
