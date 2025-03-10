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
package io.github.aivruu.teams.player.infrastructure.json.codec;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.PlayerModelEntity;
import io.github.aivruu.teams.shared.infrastructure.adapter.JsonCodecAdapterContract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public enum JsonPlayerAggregateRootCodec implements JsonCodecAdapterContract<PlayerAggregateRoot> {
  INSTANCE;

  @Override
  public @NotNull Class<PlayerAggregateRoot> forClass() {
    return PlayerAggregateRoot.class;
  }

  @Override
  public @NotNull PlayerAggregateRoot deserialize(final JsonElement json, final Type type, final JsonDeserializationContext ctx) throws JsonParseException {
    final JsonObject jsonObject = json.getAsJsonObject();
    final String id = jsonObject.get("id").getAsString();
    final JsonElement tag = jsonObject.get("selected-tag");
    return new PlayerAggregateRoot(id, new PlayerModelEntity(id, (tag == null) ? null : tag.getAsString()));
  }

  @Override
  public @NotNull JsonElement serialize(final PlayerAggregateRoot playerAggregateRoot, final Type type, final JsonSerializationContext ctx) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", playerAggregateRoot.id());
    jsonObject.addProperty("selected-tag", playerAggregateRoot.playerModel().tag());
    return jsonObject;
  }
}
